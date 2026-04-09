{:title "Plugins"
 :category :extending
 :index 10
 :tags ["libraries"]}

Plugins are a way to extend *MonkeyCI*'s functionality.  Basically, they are
no more than **regular Clojure libraries** that you can include in your build using
the [deps.edn](deps/).  In order to use a plugin, you have to `require` or
`use` the relevant namespace in your build script, and then invoke the provided
functions.  There is **no magic**, it's simple to use!

## Usage

It depends on the plugin itself how you should use it, and which functions it
provides.  Let's take **for example** the [Kaniko plugin](https://github.com/monkey-projects/plugin-kaniko).
It can be used to build container images in your build scripts without having to
access a [Docker](https://docker.io) or [Podman](https://podman.io) service.
Because of technical and security reasons, this is not possible in *MonkeyCI*.

This plugin provides functionality to create a single image, but also to create
images for multiple platforms.  This requires multiple jobs, as you need to build
the image for each platform separately, and then join them together using a manifest.
The Kaniko plugin takes care of all these things for you and allows you to add that
functionality to your build:

In your `deps.edn`, you need to include the plugin:
```clojure
;; deps.edn
{:deps {com.monkeyci/plugin-kaniko {:mvn/version "..."}}} ; Specify version
```

Then, in your `build.clj`, require the namespace and add the jobs:
```clojure
(ns build
  (:require [monkey.ci.plugin.kaniko :as kaniko]))

;; Other build jobs defined here...

;; Add multiple jobs to the build in order to create the image
(def image-jobs
  (kaniko/multi-platform-image
    {:target-img "docker.io/my-repo/my-img:latest"
     :archs [:arm :amd]}))

;; All jobs to run
[my-custom-jobs ; Define your own
 image-jobs]
```

That's it!  **You have just used a plugin** in your build, and saved yourself a lot of
time having to figure out yourself how to build an image!  *MonkeyCI* will take care of
fetching the library and executing the functionality in order to add the jobs to your
build.  Note that this is a basic example, you'll have to check out this specific
[plugin documentation](https://github.com/monkey-projects/plugin-kaniko) for more details.

Because plugins are no more than regular libraries, you can use any existing tools you
like to create and publish them.  And use *MonkeyCI* to build them, of course.

At the time of writing, several plugins already exist, and more are being created.

 - [clj](https://github.com/monkey-projects/plugin-clj), to build Clojure code.
 - [github](https://github.com/monkey-projects/plugin-github) to create releases in [Github](https://github.com).
 - [kaniko](https://github.com/monkey-projects/plugin-kaniko), as used in the above example.
 - [pushover](https://github.com/monkey-projects/plugin-pushover) to publish notifications to [Pushover](https://pushover.net).
 - [junit](https://github.com/monkey-projects/plugin-junit) to parse [JUnit](https://junit.org) output files and make the results viewable.
 - ...

## Extensions

Where plugins are more or less a way to package and reuse functionality, *MonkeyCI* also
has a concept of **extensions**.
These are ways to perform **actions before and/or after a job**.  A plugin can define an
extension (or even multiple), which is then automatically applied when they are `require`d
in the build script.  An extension can define a `before` or `after` function, which can
influence the way a job is run or take some actions when it is run.  Extensions are typically
used to do something with a job result, but they can also be used to modify the job they
are configured on.

### Usage

Let's illustrate how to use extensions by means of the [JUnit plugin](https://github.com/monkey-projects/plugin-junit).
When a job is running unit tests that publish their result in JUnit format (which is an `xml`
file), you can apply the extension to parse those results and put them in the job result.
*MonkeyCI* is able to process several kinds of information in the results, and show them
to the user in the application.  In this case, unit test results: time spent, errors, warnings,
etc...

Again, it depends on the extension itself, but usually you will need to provide some additional
information in your build configuration that the plugin requires.  Each extension is "linked" to
some **configuration key**.  *MonkeyCI* will try to look up the appropriate extension for the
configuration key and then invoke it's before or after step.  In case of the JUnit plugin, it
is linked to the `:junit` configuration key, where it expects to find the id of the
[artifact](artifacts/) holding the `junit.xml` file, and it's path therein.

For example:
```clojure
;; Register the extension by including the namespace
(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.ext.junit :as junit]))

;; Activate it in the job
(def test-job
  (-> (m/container-job "unit-test")
      (m/image "docker.io/maven:latest")
      (m/script ["mvn verify"])
      ;; Make sure to store the test results as artifact
      (m/save-artifacts [(m/artifact "test-results" "target/junit.xml")])
      ;; Configure junit extension so it can find the artifact
      (junit/junit (junit/artifact "test-results" "target/junit.xml"))))
```
Because the extension is **run after the container has executed**, it can't access the file
directly.  Rather, it has to download it from an artifact.  That's why in this example
the `junit.xml` file is referred twice: once in the artifact configuration, and again
in the junit extension configuration.  This is however an implementation detail: the
plugin has full **access to the job details**, so it may just as well inspect the artifacts,
download them all, and try to find the necessary files automatically.  But the `:junit`
configuration key is still necessary in order to "activate" the extension.

### Creating Extensions

Extensions can be very powerful.  They combine the code-reuse abilities of plugins with
the possibility to manipulate jobs, **even add functionality to jobs** defined in non-code
config files (`json`, `yaml` or `edn`).  Let's explain this using an example.  Suppose
you have a recurring use case in your organization where you repeately need to include
the same environment variables that have been configured in [build parameters](params).

Let's first see how to create an extension, then we'll make it more functional.  You could
write up an extension that looks like this:
```clojure
(ns build
  (:require [monkey.ci.extensions :as ext]))
  
;; Give it a unique id
(def id :my-extension/id)

(defmethod ext/before-job id
  ;; First arg is the id, which we don't need here
  [_ ctx]
  (println "This extension has configuration:" (ext/get-config ctx id))
  ;; Otherwise do nothing (yet)
  ctx)
```

The above is a very basic illustration how an extension that manipulates a job before
it executes looks like.  You implement the [multimethod](https://clojure.org/reference/multimethods)
`before-job` that filters on your unique id.  The id should be unique, because *MonkeyCI*
uses it to determine which code to execute.  The best way to do this is to use a
[namespaced keyword](https://clojure.org/reference/data_structures#Keywords) and use
your company's reverse domain as a namespace, the same you would do to set up your
Java packages, for example.

The `before-job` function receives two arguments: the id (which we already know here)
and the [job context](jobs).  The context is passed to each job function (e.g. in 
[action jobs](jobs#action-jobs)) and contains everything you need to perform operations
on jobs, like the branch that this build is triggered from, changed files, etc...
It should return a new context, which is possibly updated.  Since the context also contains
the current job, you can use this to update the job.  This is exactly what we'll be doing
in this example.

Now, let's add some real functionality to our extension.  What we want to do is update
the job by adding an environment variable that holds the value of a configured
[build parameter](params).  We do this by invoking the `params` function, which also
uses the context.

```clojure
(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.extensions :as ext]))
  
;; Give it a unique id
(def id :my-extension/id)

(defn- add-env
  "Helper function that adds given key/values to the job env vars"
  [job env]
  (m/env job (merge (m/env job) env)))

(defmethod ext/before-job id
  [_ ctx]
  ;; Get the config and retrieve build parameters
  (let [conf (ext/get-config ctx id)
        params (m/params ctx)]
    (ext/update-job ctx add-env (select-keys params conf))))
```

To make it more readable, we've introduced a helper function (`add-env`) that allows us
to update job environment variables with more values.  The `before-job` has been rewritten
to do this:
  1. First read the extension configuration from the job.
  2. Then retrieve the build parameters the job has access to.
  3. Update the current job by adding all configured parameters to its environment variables.
  
This means that when we would apply this to a job, we'd need to pass it a list of parameter
names we want to pass to the job environment.  When the job is actually executed, it will
see those parameters in its environment.  We could apply the extension like this:

```clojure
(def some-job
  (-> (m/container-job "some-job")
      (m/image "docker.io/some-image:latest")
      (m/script ["..."])
      (assoc :my-extension/id ["ENV_VAR_1" "ENV_VAR_2"])))
```

The above assumes you have declared `ENV_VAR_1` and `ENV_VAR_2` as [parameters](params) in your
organization.  This way you can keep your jobs simple, but still add your own custom
behaviour to them.  It's also possible to **cancel** a job at this point by simply replacing
it with `nil`.

**And it gets even better!**  This is also applicable to `yaml` style jobs, like so:
```yaml
- id: some-job
  type: container
  image: "docker.io/some-image:latest"
  script: ["..."]
  my-extension/id: ["ENV_VAR_1" "ENV_VAR_2"]
```

This powerful method gives you the best of both worlds: you can have the comfort of using
`yaml` to declare your jobs *and* use code to add custom functionality.  (Note that this
is similarly applicable if you use `json` or `edn` to declare your jobs.)

In addition to the `before-job` there is also an `after-job` function, which is 
executed when the job has finished.  There you can manipulate or inspect the job results,
send a notification, or anything else you want to.

### Limitations

Extensions are very powerful, but they still have limitations.  This section lists
some of them.  Even though `before-job` extensions can manipulate the current job,
what they can't do is change it's **dependencies**.  This is because those have
already been calculated beforehand, and extensions are only executed when the job
is queued.  This happens *after* it's dependencies have already been executed, so
it's not use changing them at this point.

Similarly, you can't [block](blocking) in a `before-job` handler because this verification
has already been applied when the extension is called.

Even though you can change a job result, it's currently not possible to change 
a job status.  This means you can't make a successful job fail, or vice versa, in
an `after-job` handler.
