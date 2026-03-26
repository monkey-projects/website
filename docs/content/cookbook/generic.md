{:title "Generic Recipes"
 :category :cookbook
 :index 10
 :related [["intro/basic-example" "Basic Example"]
           ["intro/useful-example" "A more Useful Example"]
		   ["jobs" "Jobs"]
		   ["cookbook/conditions" "Condition Cookbook"]]}

This page provides some examples for generic commands, either using [containers or actions](jobs).

## Shell Commands in Container

Run some shell commands in a container, using the default shell (`/bin/sh`) and using `YAML`:

```yaml
# Saved in .monkeyci/build.yaml
id: bash-commands
image: docker.io/debian:latest
script:
  - ls -l
  - echo "This is another command"
```

## Container with Environment Variables

Run a shell script in a container with env vars passed in:

```yaml
- id: env-commands
  image: docker.io/debian:latest
  script:
    - echo "Value of env var is $TEST_VAR"
  container/env:
    TEST_VAR: test value
```

Or, using Clojure:
```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(-> (m/container-job "env-commands")
    (m/image "docker.io/debian:latest")
    (m/script ["echo \"Value of env var is $TEST_VAR\""])
    (m/env {"TEST_VAR" "test value"}))
```

## Basic Action Job

Create an action job that executes an arbitrary Clojure function:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(m/action-job "generic-fn"
  (fn [_]
    ;; This returns nil, which is interpreted as a success
    (println "This is a generic Clojure function")))
```

## Check using Build Parameters

A job that succeeds when a build parameter is present, and fails when it's not found.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(m/action-job "check-fn"
  (fn [ctx]
    (if-let [param (get (m/build-params ctx) "TEST_VAR")]
      (println "Build parameter value is" param)
      ;; If param is not found, return failure with an error message
      (-> m/failure
          (m/with-message "Build parameter has not been provided")))))
```

## Only Execute a Job on Main Branch

This job is a [conditional job](conditions) that is only executed when the build
is triggered on the `main` branch:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn main-job [ctx]
  (when (= "main" (m/branch ctx))
    (-> (m/container-job "on-main")
        (m/image "docker.io/alpine:latest")
        (m/script ["echo \"I'm in the main branch!\""]))))
```

If you've configured the *main branch* property in the [repository](repos), you can also just use this:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn main-job [ctx]
  (when (m/main-branch? ctx)
    (-> (m/container-job "on-main")
        (m/image "docker.io/alpine:latest")
        (m/script ["echo \"I'm in the main branch!\""]))))
```

See also the [condition cookbook](cookbook/conditions) for more examples.
