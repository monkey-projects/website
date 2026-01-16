{:title "Build jobs"
 :category :builds
 :index 30
 :related [["builds" "Builds"]
           ["artifacts" "Artifacts"]
	   ["caching" "Caching"]
	   ["conditions" "Conditional jobs"]
	   ["blocking" "Blocked jobs"]]}

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

Action jobs are in essence **Clojure functions** that are called inside the build script.
They receive build context as an argument, which can be used to fetch [build parameters](params),
or get more information about the build trigger, such as the current branch or tag, or
modified files.

The function is supposed to return a value that indicates whether the job was successful
or not.  A return value of `nil` is interpreted as a success.  In order to indicate a
failure, the job should return the `failure` value from the build api.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

;; This job will return `nil`, which means it succeeds
(def success-job
  (m/action-job
    "my-action"
    (fn [ctx]
      (println "This job is executed with this context:" ctx))))

;; This job will fail
(def failing-job
  (m/action-job
    "failing"
    (fn [ctx]
      (-> m/failure
          (m/with-message "The job has failed because I wanted it to")))))

;; As usual, return all jobs in a list
[success-job
 failing-job]
```

A message is not required on an error result, but it is more user-friendly.

### Working Directory

Each job has a working directory, which by default is the build checkout directory.
This is the location where the repository is checked out.  File-related functions
usually need this location to do their work.  But be careful, **the working directory
is not always the current directory!**  So you can't just start creating or
modifying files using relative file paths assuming you will be in the right
directory.  To ensure you always use the correct paths, use the `in-work` function
provided by the api.  This creates an absolute path from any path relative to the
current job working directory.

```clojure
(def file-creating-job
  (-> (m/action-job "create-file"
        (fn [ctx]
	  ;; This won't necessarily write the file in the right location
	  (spit "test.txt" "This will probably not work")
	  ;; This will work because it uses an absolute path
	  (spit (m/in-work ctx "test.txt") "This will most certainly work")))
      (m/work-dir "subdir")))
```

In the above example, the first `spit` will write information to a file called `test.txt`,
but this may be in another location, most likely the `.monkeyci` directory of your build,
which is probably not what you want.  The second `spit` uses the `in-work` function,
which will correctly calculate the absolute path to use for the job, given the
build checkout directory and the "subdir" subdirectory the job runs in.

## Container Jobs

As the name suggests, container jobs are **executed in their own container**.  You
need to indicate which image will be used for the job, and one or more script
lines.  For example:

```clojure
(def list-files-job
  (-> (m/container-job "list-files")
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

### Resources

By default container jobs use **1 CPU and 2 GB of memory**, but you can override
this by specifying the `:cpus` and `:memory` options when creating the job, or you
can use the `cpus` and `memory` functions:

```clojure
(def heavy-job
  (-> (container-job "heavy-lifting" {:cpus 2 :memory 16})
      (image "...")
      ;; other properties here
      )

(def another-heavy-job
  (-> (container-job "more-heavy-lifting")
      (image "...")
      (cpus 2)
      (memory 16)))
```
This will create a container job that uses 2 cpu cores and 16GB of memory.  This will
of course count towards the [consumed credits](pricing).  They also have maximum
values, as shown in this table:

|Resource|Default|Minimum|Maximum|
|---|---|---|---|
|CPU|1|1|16|
|Memory|2 GB|1 GB|64 GB|

Note that configuring cpu's or memory **on an action job does not have any effect**.
Also, setting the required memory for a job too low may result in job failure due to
*out-of-memory*.

The **available disk space** for a job, and also for the build script itself, is **50GB
per job**.  This is currently not configurable.

### CPU Architectures

Currently, *MonkeyCI* offers two possible architectures to run your containers on: `ARM`
and `AMD`.  This does depend on the availability with our cloud partner, and it may also
happen that the architecture is emulated on another platform.  But normally you can
choose which one you want.  By default, this is `AMD`, which is also the most widely
used architecture, but you could switch to `ARM` by specifying the `:arch` property
in your job:

```clojure
(-> (container-job "arm-job" {:arch :arm})
    (image "docker.io/alpine:latest")
    (script ["echo \"I'm running on ARM!\""]))
```

This could be useful to build container images for multiple platforms, for example.
To determine at build time which architectures are available to your build script,
you can retrieve that information from the job context, using the `archs` function.
This may be useful if you want to build a multi-platform container image, for
example.
```clojure
(defn image-job [ctx]
  ;; Create a job for each available architecture
  (map (fn [arch]
         (-> (container-job (str "job-for-arch-" (name arch)) {:arch arch})
 	     (image "docker.io/alpine:latest")
	     (script [(format "echo \"I'm running on %s!\"" (name arch))])))
       (m/archs ctx)))
```
Assuming the available architectures are `:arm` and `:amd`, the above example
creates two jobs, called `job-for-arch-arm` and `job-for-arch-amd`, respectively.

## Startup Times

Since action jobs are run inside the build script environment, and container jobs
require starting a new container, action jobs have **very low latency** compared
to container jobs.  The downside of action jobs is then that you cannot choose
which tools are available, and they may "contaminate" each other, when multiple
action jobs are run that have some kind of persistent impact.

Starting a container job may incur a latency of up to a minute.  This is however
**not charged** towards your credit consumption.  Only the seconds that the container
script is actually running is taken into account.

The time a container job spends in the pending queue can also increase if there is **heavy
load** and resources are unavailable.  We try to keep this to a minimum, but be aware
that it may happen.