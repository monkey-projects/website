{:title "Build jobs"
 :category :builds
 :related [["builds" "Builds"]
           ["artifacts" "Artifacts"]
	   ["caching" "Caching"]]}

A build script consists of one or more jobs.  There are two kinds of
jobs: **action jobs** and **container jobs**.  A job can be **dependent** on other
jobs, which is indicated in the job configuration.  In addition, jobs can require
or produce [artifacts](artifacts), or they can use [caches](caching).

Jobs are always executed in parallel, up to a maximum (depending on the available
computing capacity), unless they are dependent on another job, in which case they
will only be started one the dependencies have finished.

When a job fails, either because there is an exception, or it returns an error
value, any jobs that are dependent on the failing job are skipped.  Other jobs
will still be executed, but the build itself will be marked as **failed**.

## Action Jobs

Action jobs are in essense Clojure functions that are called inside the build script.
They are passed the build context, which can be used to fetch [build parameters](params),
or get more information about the build trigger, such as the current branch or tag, or
modified files.

The function is supposed to return a value that indicates whether the job was successful
or not.  A return value of `nil` is interpreted as a success.  In order to indicate a
failure, the job should return the `failure` value from the build api.

```clojure
(ns build
  (:require [monkey.ci.build.v2 :as m]))

;; This job will return `nil`, which means it succeeds
(def my-action
  (m/action-job
    "my-action"
    (fn [ctx]
      (printn "This job is executed with this context:" ctx))))

;; This job will fail
(def failing-job
  (m/action-job
    "failing"
    (fn [ctx]
      (-> m/failure
          (m/with-message "The job has failed because I wanted it to")))))
```

A message is not required on an error result, but it is more user-friendly.

## Container Jobs

Container jobs are executed in their own container (as the name suggests).  You
need to indicate which image will be used for the job, and one or more script
lines.  For example:

```clojure
(def some-container-job
  (-> (m/container-job "example-job")
      (m/image "docker.io/debian:latest")
      (m/script ["ls -l"])))
```

A container job requires at least an `image` and a `script`.  It's not required
to add the hostname to the image url, but we advise to do so to avoid any confusion.

You cannot specify an explicit return value for container jobs.  If all script 
lines are executed successfully, the job will succeed.  Otherwise it will fail.
The **output of each line** will be available for viewing in the user interface.

**Note** that each line is **executed in it's own shell**, so you cannot
change directories or set environment variables in between lines.  If you want
to do that, you should put all commands in one line, or explicitly commit a
script file in your repository.  So this will most likely not give the expected
results:

```clojure
(def example-job
  (-> (m/container-job "example-job")
      (m/image "docker.io/debian:latest")
      (m/script ["cd example-dir"
                 "ls -l"])))
```
The above job will not list the files in the `example-dir`, but in the checkout
directory instead.  To alleviate this, you can either set the working directory
for the script, using `work-dir`, or put the commands in one script line.  So
both the examples here would work:

```clojure
(def example-job-1
  (-> (m/container-job "example-job-1")
      (m/image "docker.io/debian:latest")
      (m/script ["ls -l"])
      (m/work-dir "example-dir")))

(def example-job-2
  (-> (m/container-job "example-job-2")
      (m/image "docker.io/debian:latest")
      (m/script ["cd example-dir && ls -l"])))
```

### Environment Variables

You can set environment variables on container jobs, by using the `env` function.
These are passed as-is to the container, and are available in each script line.

```clojure
(def job-with-env
  (-> (m/container-job "env-job")
      (m/image "docker.io/alpine:latest")
      (m/script ["echo \"The value of the environment is $TEST_ENV\""])
      (m/env {"TEST_ENV" "example value"})))
```

When executed, this job will output `The value of the environment is example value`.
Note that we escaped the `"` for the `echo` command with a backslash character.
The value passed to `env` is a regular key-value map, where the key is the name
of each environment variable.

Environment variables an also be constructed from the [build parameters](params).