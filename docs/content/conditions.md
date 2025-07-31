{:title "Conditional Jobs"
 :category :builds
 :related [["jobs" "Jobs"]
           ["builds" "Builds"]
	   ["plugins" "Plugins"]
	   ["commit-changes" "Changed files"]]}

In many situations your builds will differ depending on the situation.  For example,
you'll only want to deploy building a release, or when building from the `main`
branch, or when files in a specific directory have been touched.  And your deployment
strategies may differ depending on the environment where you'll be deploying (`staging`
vs. `production`).  For this, you'll need **conditions**.

## Types of Conditions

In *MonkeyCI*, there are multiple strategies to handle conditional jobs.

 - Conditionally add buils to the job list
 - Use job functions that return `nil` when you don't want to run them
 - Use [action jobs](jobs) that return `skipped`

In any case, you'll need to use Clojure scripts for conditions, they are not
supported in `YAML` or similar, since they are considered a more advanced feature.

## Job List Manipulation

As mentioned [earlier](intro/basic-example), the last expression in your build
script must be the list of jobs to execute.  But *MonkeyCI* also **accepts a function**
in this case.  So instead of a fixed list, the last expression can also be a
function that, given the build context, returns the list of jobs to run.  This opens
up the possibility to conditionally add jobs to the list depending on the situation,
for example the git `ref` that was pushed to.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(def unit-tests
  "Runs the unit tests"
  (-> (m/container-job "unit-tests")
      (m/image "docker.io/maven:latest")
      (m/script ["mvn verify"])))

(def deploy
  "Deploys the lib"
  (-> (m/container-job "deploy")
      (m/image "docker.io/maven:latest")
      (m/script ["mvn deploy:deploy"])
      (m/depends-on ["unit-tests"])))

(defn jobs
  "This function is the last statement and creates the list of jobs"
  [ctx]
  [unit-tests
   ;; Only deploy when on the main branch
   (when (m/main-branch? ctx)
     deploy)])
```

The above example will always execute the `unit-tests` job, but will only
run the `deploy` job when triggered from the main branch, which is configured
on the [repository](repos).  *MonkeyCI* will automatically skip the `nil`
entries in the job list.

Now this opens up **lots of possibilities**, because you can apply the full [Clojure
API library](https://clojure.org/api/cheatsheet) to that list.  Composing sublists,
reordering, anything you need!

## Job Functions

Although using the job list function is fairly straightforward, it could lead to
a cluttered job list which may be difficult to reason about (although you can still
write [unit tests](tests) for it).  An alternative is using **job functions**.
Instead of defining your jobs directly, you can instead use functions that take
the build context, and return one or more jobs to add to the build.  If a function
returns `nil`, this will simply be skipped and not result in a job in the build.

So the above example may be reworked to look like this:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(def unit-tests
  "Runs the unit tests"
  (-> (m/container-job "unit-tests")
      (m/image "docker.io/maven:latest")
      (m/script ["mvn verify"])))

(defn deploy
  "Deploys the lib when on main branch"
  [ctx]
  (when (m/main-branch? ctx)
    (-> (m/container-job "deploy")
        (m/image "docker.io/maven:latest")
        (m/script ["mvn deploy:deploy"])
        (m/depends-on ["unit-tests"]))))

[unit-tests
 deploy]
```

This makes the job list cleaner, puts all the logic in the `deploy` job.  This
is especially useful if you're writing an [plugin](plugins) with reusable jobs.

## Action Job Return Values

This option is only valid when using [action jobs](jobs).  The status of container
jobs is determined by the exit code of their script, a zero value indicates a
success, any other value is interpreted as a failure.  But with action jobs,
there are more possibilities, because you can **decide which value to return**.
Apart from `success` and `failed`, you can also return `skipped`.  This will be
indicated in the *MonkeyCI* user interface, which may be more informative, as
opposed to not having a job in the build at all.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(def optional-job
  (m/action-job "optional-job"
    (fn [ctx]
      (if (m/main-branch? ctx)
        (println "I should be doing something here")
	m/skipped))))	
```

In this example, when building from the main branch, it will print the message and
return `nil`, which is interpreted as `success`.  In any other case, it will return
`skipped`.  So in any case, the job is executed, but the action itself indicates to
the system that it did anything or not.

## Composed Conditions

As your build script gets more complicated and more situations need to be addressed,
the conditions will become more complicated as well.  Since we're using a programming
language, we have the full freedom to extract those conditions in separate functions,
perhaps **even plugins of their own**!  Checking the conditions then becomes as
simple as calling a function.  For example, we could modify one of the above examples
as follows:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn changed?
  "True if any file under `src/` has been touched"
  [ctx]
  (m/touched? ctx #"src/.*"))

(def should-deploy? m/main-branch?)

(defn unit-tests
  "Runs the unit tests if sources have changed"
  [ctx]
  (when (changed? ctx)
    (-> (m/container-job "unit-tests")
        (m/image "docker.io/maven:latest")
        (m/script ["mvn verify"]))))

(defn deploy
  "Deploys the lib when on main branch"
  [ctx]
  (when (should-deploy? ctx)
    (-> (m/container-job "deploy")
        (m/image "docker.io/maven:latest")
        (m/script ["mvn deploy:deploy"])
        (m/depends-on (when (changed? ctx) ["unit-tests"])))))

[unit-tests
 deploy]
```

We have extracted the condition checks into two functions, `changed?` and `should-deploy?`,
which shows their intent a little better.  Then we have also added a condition on the
`unit-tests` job, which will now only run when the source files have changed.  **Note** that
we have also added that check on the `deploy` dependencies, because otherwise the deploy
job would only run when source files have changed, since it's dependent on the `unit-tests`
job.

## Conclusion

As you can see, conditions give you **a lot of power and flexibility** to adjust the flow of
your builds, or to make them more efficient.  Be sure to use [unit tests](tests) to verify
whether the conditions actually behave the way you have intended.