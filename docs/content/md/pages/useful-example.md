{:title "A more useful example"
 :tags ["examples"]}

While the [tutorial](/) explains a little bit of how build scripts in *MonkeyCI* work,
it's not useful in the real world.  Let's assume you're working on a
[Java](https://en.wikipedia.org/wiki/Java_(programming_language)) project that uses
[Maven](https://en.wikipedia.org/wiki/Apache_Maven) as it's build tool.  In order
to run your unit tests and eventually build your code, you need to run the appropriate
Maven commands, in this case `mvn verify`, or any of it's related commands.

In order to do this automatically using *MonkeyCI*, you will need a [container job](/page/container-jobs).
This will start the container, presumably from one of the [Maven images](https://hub.docker.com/_/maven),
check out the code from Git, and then run the configured commands.  Like this:

```clojure
(use 'monkey.ci.build.core)

(container-job
 "mvn-verify"
 {:image "docker.io/maven:latest"
  :script ["mvn verify"]})
```
This is a very rudimentary job configuration.  It uses the `latest` version of the `maven`
container image from the Docker hub.  It then executes a single-line script that just runs
`mvn verify`.  You will usually want something more than this from your build, but it's a start.

## A Second Job

Suppose we want to be able to download the resulting `jar` files from this build.  In order to
do this, we need to expose them as [artifacts](/pages/artifacts/).  This will allow us to download
them from the *MonkeyCI* application site.  Configuring a job to produce an artifact is simple,
just add a `:save-artifacts` configuration:

```clojure
(container-job
 "mvn-verify"
 {:image "docker.io/maven:latest"
  :script ["mvn verify"]
  :save-artifacts [{:id "packages"
                    :path "target"}]})
```
The `:save-artifacts` configuration holds a list of one or more artifact configurations.
These consist of an `id`, which should be unique over the build, and the `path` where the
files can be found, **relative to the working directory** of the job.  Assuming the files
actually exist, they will become available for download when the job completes succesfully.

Suppose now that you're writing a library, and want to publish it.  In order to do this, you
can set up a second job, that executes the [Maven
deploy](https://maven.apache.org/plugins/maven-deploy-plugin/index.html) plugin.  You don't
want to have to rebuild your code, so you can restore the artifact you previously created.
This requires two things:

 1. **Restore** the artifact from the verify job
 2. Make the publish job **dependent** on the verify job.

It could look like this:

```clojure
(container-job
 "mvn-publish"
 {:image "docker.io/maven:latest"
  :script ["mvn deploy:deploy"]
  :restore-artifacts [{:id "packages"
                       :path "target"}]
  :dependencies ["mvn-verify"})
```

This job looks a lot like the `verify` job, with several differences:

 - The `script` has changed slightly, it now executes `mvn deploy:deploy`.
 - Instead of saving an artifact, it restores said artifact to the same location.
 - It declares the job **dependent on** the verify job.

If you don't declare the dependency, *MonkeyCI* will execute it **concurrently** with the
verify job.  By default, all jobs are run simultaneously, unless you declare them dependent
on other jobs, in which case they will start as soon as the dependencies have (succesfully)
finished.

## Putting it Together

One more thing we need to do is to "return" both jobs.  When loading the script file,
the **last expression determines the jobs that will be executed** when building.  This can
either be a single job, as it was up until now, or a list of jobs, or even a function
that calculates the jobs using the context, but more on that later.  So in order to do
this, we bind each job to a symbol, using [def](https://clojuredocs.org/clojure_core/clojure.core/def)
and at the end of the build script, we refer to them.  The full script then looks like this:

```clojure
(use 'monkey.ci.build.core)

(def verify
  (container-job
   "mvn-verify"
   {:image "docker.io/maven:latest"
    :script ["mvn verify"]
    :save-artifacts [{:id "packages"
                      :path "target"}]}))

(def publish
  (container-job
   "mvn-publish"
   {:image "docker.io/maven:latest"
    :script ["mvn deploy:deploy"]
    :restore-artifacts [{:id "packages"
                         :path "target"}]
    :dependencies ["mvn-verify"}))

;; Last expression holds all the jobs to execute
[verify
 publish]
```

## Cleaning Up

Now if you're a developer, you'll most likely be screaming "remove the duplication"!
And yes, since we're using code, we can do that, so let's proceed.  The two jobs look
very similar, so we can group the common stuff into a new function, and let the two
jobs call that function with some parameters:

```clojure
(use 'monkey.ci.build.core)

(defn mvn-job [id cmd opts]
  (container-job
   id
   (assoc opts
          :image "docker.io/maven:latest"
          :script [(str "mvn " cmd)])))

(def target-artifact
  {:id "packages"
   :path "target"})

(def verify
  (mvn-job
   "mvn-verify"
   "verify"
   {:save-artifacts [target-artifact]}))

(def publish
  (mvn-job
   "mvn-publish"
   "deploy:deploy"
   {:restore-artifacts [target-artifact]
    :dependencies [(job-id verify)}))

;; Put the jobs in the "resulting" list
[verify
 publish]
```

This is a bit cleaner, in terms of coding.  The common parts have been centralized in
the `mvn-job` function, and the artifact configuration has also been given a symbol,
which we refer to in both jobs.  Also, we use the `job-id` function to declare the
dependency between `publish` and `verify`.

## Going Further

Since `Java` and `Maven` are used by millions of developers in countless projects all
over the world, it is a bit silly that they all should have to rewrite the above code
for all their projects.  *MonkeyCI* offers a solution for this in the form of
[plugins](/pages/plugins/)!  With these you can create a **reusable library** that
provides commonly used functionality that can be [included](/pages/deps/) by other
developers in their projects.  And since it uses the same infrastructure as any other
Java library, you can use any existing tools that help with that.  Assuming such a
plugin already exists (which we're working on), the above 30-line script can become
*a lot* shorter, like this:

```clojure
(require '[monkey.ci.plugin.mvn])

(mvn-lib)
```

That's it!  Really?  Well, it takes a little bit more than that, because in order to
publish a library, you will probably need credentials of some sort.  That's where
[build parameters](/pages/params/) come in.