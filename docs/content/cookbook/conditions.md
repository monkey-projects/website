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
  (mvn-job "install" ["mvn install"]))

(def publish
  (mvn-job "publish" ["mvn publish"]))

(defn jobs [ctx]
  [unit-test     ; Always run this
   (when (m/main-branch? ctx)
     ;; Only run these when on main branch
     [install
      publish])])
```
