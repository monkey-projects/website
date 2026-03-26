{:title "Condition Recipes"
 :category :cookbook
 :index 20
 :related [["cookbook/generic" "Generic Recipes"]
           ["conditions" "Conditions"]
           ["jobs" "Jobs"]]}
 
[Conditions](conditions) determine whether a job, or multiple jobs, need to be
executed in a build or not.  These can be **static**, depending on values provided
at trigger time (e.g. which branch we're on), or **dynamic**, determined by the
results of other jobs.

Applying conditions is not possible in `YAML` or the other static configuration 
languages.  You have to use [Clojure](why-clojure) for this.

## Branch Condition on a Container Job

Execute a container job only when on a certain branch:
```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn terraform-job [ctx]
  (when (= "terraform" (m/branch ctx))
    (-> (m/container-job "terraform")
        (m/image "docker.io/hashicorp/terraform:latest")
        (m/script ["terraform apply"]))))
```

## Condition on Multiple Jobs

Execute a list of jobs when on a given branch.  We use a function to generate the list of 
jobs in this case:
```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn mvn-job
  "Utility function that creates a basic maven container job with 
   given id and script"
  [id script]
  (-> (m/container-job id)
      (m/image "docker.io/maven:latest")
      (m/script script)))

(def unit-test
  (mvn-job "unit-test" ["mvn verify"]))

(def install
  (-> (mvn-job "install" ["mvn install"])
      (m/depends-on "unit-test")))

(def publish
  (-> (mvn-job "publish" ["mvn publish"])
      (m/depends-on "unit-test")))

(defn jobs [ctx]
  [unit-test     ; Always run this
   (when (m/main-branch? ctx)
     ;; Only run these when on main branch
     [install
      publish])])
```

## Parameter Condition

Only run a job if a parameter has a specific value.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn param-job [ctx]
  ;; Only run if TEST_VAR=run!
  (when (= "run!" (get (m/params ctx) "TEST_VAR"))
    (-> (m/container-job "some-job")
        (m/image "docker.io/alpine:latest")
        (m/script ["echo 'I am running'"]))))
```

## Run on Changed Files

Run a job if file matching a regex have changed.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn unit-test [ctx]
  ;; Run unit tests if any of the files in `java/` dir have changed
  (when (m/touched? ctx #"^java/.*$")
    (-> (m/container-job "unit-tests")
        (m/image "docker.io/maven:latest")
        ;; Run in the java/ dir
        (m/work-dir "java")
        (m/script ["mvn verify"]))))
```
