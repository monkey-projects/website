{:title "API reference"
 :category :builds
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more useful example"]]}

This is a listing of the available functions when writing [build scripts](builds).
Since *MonkeyCI* is open source, you can also [check out the code](https://github.com/monkey-projects/monkeyci)
and see for yourself.  The API code is located in the [monkey.ci.build.*](https://github.com/monkey-projects/monkeyci/tree/main/app/src/monkey/ci/build) namespaces.  They cover a range **from low-level
to high-level functions**.  It is advised to use the high-level functions in `monkey.ci.build.v2`,
unless you want to do something very specific, in which case you may also find useful
functions in `monkey.ci.build.core` or any other adjoining namespace.

See also the [cljdoc page](https://cljdoc.org/d/com.monkeyci/app/CURRENT/api/monkey.ci.build.v2)
for the auto-generated documentation with links to the source code.  It's **always up-to-date**
with the latest version.

## action-job

Creates a new action job.

- Arguments: `[id action opts?]`
- Returns: a basic action job with default options.

|Argument|Description|
|---|---|
|`id`|job id, must be unique in the build script|
|`action`|1-arity function that takes the build context, and returns the build result.  A return value of `nil` is interpreted as a success.|
|`opts`|optional extra configuration, specified as a map.  Prefer using the manipulator functions.|

Example:
```clojure
(action-job
  "test-job"
  (fn [ctx]
    (println "This is an action job")))
```

## action-job?

Verifies if argument is an action job.  Useful for testing.

- Arguments: `[obj]`
- Returns: `true` if the argument is an action job, `false` otherwise.

|Argument|Description|
|---|---|
|`obj`|The object to check.|

Example:
```clojure
(is (= (action-job? my-job)))
```

## container-job

Creates a new container job.  Use manipulator functions to configure it.

- Arguments: `[id opts?]`
- Returns: a basic container job with default options.

|Argument|Description|
|---|---|
|`id`|job id, must be unique in the build script|
|`opts`|optional extra configuration, specified as a map.  Prefer using the manipulator functions.|

Example:
```clojure
(container-job "test-job" {:image "docker.io/alpine:latest"})
```

Note that it is preferred to use manipulator functions to configure the job, instead of using
the `opts` map, unless there is a good reason for it.

## container-job?

Verifies if argument is a container job.  Useful for testing.

- Arguments: `[obj]`
- Returns: `true` if the argument is a container job, `false` otherwise.

|Argument|Description|
|---|---|
|`obj`|The object to check.|

Example:
```clojure
(is (= (container-job? my-job)))
```

## dependencies

Gets the list of dependencies of the job.  Useful for testing or conditions.

- Arguments `[job]`
- Returns: the dependencies configured on the argument.

|Argument|Description|
|---|---|
|`job`|The job to get dependencies of.|

Example:
```clojure
(is (not-empty (dependencies test-job)))
```

## depends-on

A manipulator function that adds a dependency to the job.

- Arguments: `[job dependencies]`
- Returns: the updated job.

|Argument|Description|
|---|---|
|`job`|The job to add the dependency to.  Can also be a function that returns a job.|
|`dependencies`|One or more dependencies, can also be a sequence.|

Example:
```clojure
(-> (action-job "test-job" (fn [ctx] (println "This is a test job")))
    (depends-on "other-job" ["another-job" "yet-another-job"])
    (depends-on "even-more-jobs"))
```

You can specify multiple job ids, or a sequence.  They will all be flattened out into a single
list of dependencies.  Invoking `depends-on` multiple times adds to the existing list of
dependencies, it does not replace them.