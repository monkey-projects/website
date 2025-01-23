{:title "Builds"
 :category :builds
 :related [["repos" "Repositories"]
           ["jobs" "Jobs"]
	   ["artifacts" "Artifacts"]
	   ["reference" "Script reference"]]}

Builds are the central component of *MonkeyCI*.  They are the whole reason you're
using this tool!  Every time **a change is detected** in one of the configured
[repositories](repos), **a build is triggered**.  If you have set up a build
script in the `.monkeyci/` directory, it will be executed as a single build.

## Jobs

A build consists of one or more jobs.  At least one job has to be in the build,
otherwise it won't do anything.  There are two kinds of jobs: **container jobs**
and **action jobs**.  Read the [jobs section](jobs) to learn more about these.

The last expression in the build script should be the list of jobs, or possibly
just a single job.  It could also be **a function** that takes the build context
as an argument and returns the list of jobs.  This gives you even more power
to set up the build pipelines as you want, depending on the context.

By default **all jobs are executed simultaneously**, up to a certiain parallelism
limit.  But often a job can only start if another job has executed previously,
for example to generate some required [artifact](artifacts).  In order to do this,
you need to configure **dependencies** between jobs.  You can do this using the
`depends-on` function.

```clojure
(def first-job
  (-> (container-job "first")
      ;; More job configurations here
      ))

(def second-job
  (-> (container-job "second")
      ;; Event more job configurations here
      (depends-on "first")))
```

You can specify multiple dependencies at once, just pass in a vector to `depends-on`.
Should you accidentally mark a job dependent on a non-existing job, it **will be
skipped**.

## Build Results

If all jobs in the build succeed, then the build is also successful.  If, however
a single job fails, the entire build is marked as failed.  But *MonkeyCI* will
**continue to run other jobs** that are not dependent on the failed job.  Any jobs
that do depend on the failing job **will be skipped**.

**Note** that skipped jobs in itself do not have impact on the build result.  If jobs
are skipped for some reason, the build just continues.  If all executed jobs succeed,
the build is marked as successful.

## Artifacts

Any [artifacts](artifacts) that have been created by jobs in the build can be
downloaded from the job details screen.  Just go to the build in question in the
[web application](https://app.monkeyci.com) and click on the job that created the
artifact, you can download the files in the **Artifacts tab**.