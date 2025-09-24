{:title "Clojure Cookbook"
 :category :cookbook
 :index 20
 :related [["intro/basic-example" "Basic Example"]
           ["intro/useful-example" "A More Useful Example"]]}

This is a collection of examples on how to build various kinds of [Clojure](https://clojure.org)
sources.  There is a [Clojure plugin](https://github.com/monkey-projects/plugin-clj.git)
available that provides some functionality for building common Clojure projects.  In order
to use it, include it in your `deps.edn`:

```clojure
{:deps {com.monkeyci/plugin-clj {:mvn/version "0.4.0"}}}
```

The most recent version can be found on [Clojars](https://clojars.org/com.monkeyci/plugin-clj).

[![Clojars Project](https://img.shields.io/clojars/v/com.monkeyci/plugin-clj.svg)](https://clojars.org/com.monkeyci/plugin-clj)

## Leiningen Library

A library using [Leiningen](https://leiningen.org), with default file layout and that's
being published to Clojars:

```clojure
(ns build
  (:require [monkey.ci.plugin.clj :as clj]))

(clj/lein-library {})
```

This assumes [build parameters](params/) exist with `CLOJARS_USERNAME` and `CLOJARS_PASSWORD`.
It creates two jobs, `test` and `publish`.

## CLI Library

A library using the [Clojure CLI](https://clojure.org/guides/deps_and_cli), with default
file layout, and dependencies in `deps.edn`:

```clojure
(ns build
  (:require [monkey.ci.plugin.clj :as clj]))

(clj/deps-library {})
```

The same requirements as for Leiningen apply: credentials need to exist in the build parameters,
and it creates two jobs, called `test` and `publish`.

## Leiningen Unit Tests in subdirectory

Just running unit tests using Leiningen in subdirectory `subdir`.
Only one job is created, called `lein-test`.

```clojure
(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.plugin.clj :as clj]))

;; This will execute 'lein test'
(-> (clj/lein-test {:test-alias "test"
                    :test-job-id "lein-test"})
    (m/work-dir "subdir"))
```

## CLI Unit Tests in subdirectory

Similarly, running unit tests using Clojure CLI in subdirectory `subdir`.
Only one job is created, called `deps-test`.

```clojure
(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.plugin.clj :as clj]))

;; This will execute 'clojure -X:test' in 'subdir'
(-> (clj/deps-test {:test-alias "test"
                    :test-job-id "deps-test"})
    (m/work-dir "subdir"))
```

## Running an Arbitrary Leiningen Command

Running a specific Leiningen alias, in a job named `custom-job`:

```clojure
(ns build
  (:require [monkey.ci.plugin.clj :as clj]))

;; This will execute 'lein custom' in a container with default opts
(clj/clj-lein "custom-job" {} ["lein custom"])
```
## Running an Arbitrary CLI main function

Running a specific CLI main function (using `-M`), in a job named `custom-job`
which is dependent on `other-job`.

```clojure
(ns build
  (:require [monkey.ci.api :as m]
            [monkey.ci.plugin.clj :as clj]))

;; This will execute 'clojure -M:custom' in a container with default opts
(-> (clj/clj-deps "custom-job" {} "-M:custom")
    (m/depends-on "other-job"))
```