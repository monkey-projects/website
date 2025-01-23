{:title "Caching"
 :category :builds
 :related [["artifacts" "Artifacts"]
           ["intro/useful-example" "A more useful example"]]}

Where [artifacts](artifacts) can be used to pass files between jobs, caching
is meant to pass files **between the same job, over different builds**.  Jobs
that specify a caching entry with the same id will restore the files pointed to
in their configuration before they start, and store it back after they have
finished.

This is particularly useful to save time when certain processed need to download
the same files every time.  For example a dependency cache.  This can become
fairly large very quickly, even for simple projects.  So it makes sense to cache
that between builds.  The [storage cost](pricing) is much lower than the cost
for cpu cycles, so it makes sense to exchange cpu cycles for storage.

## Enabling Caches

In order to configure a job to use caching, you can use the `caches` function.
For example:

```clojure
(def build-job
  (-> (container-job "build")
      (image "docker.io/maven:latest")
      (script ["mvn verify"])
      (caches (cache "mvn-cache" ".m2"))))
```

In this example, the job that compiles your Java code using Maven (in this case)
will save any files that are written into the `.m2` directory, and store it using
the `mvn-cache` id.  The `cache` function works similar to the `artifact` function,
it defines a cache with the specified id at the given path.  The path is relative
to the job working directory.

There is no strict upper limit to the size of your cache, although smaller is of
course cheaper (and faster).  Only the size of the ephemeral disk that is assigned
to your container job matters.  This is currently 50GB.

Note that it is possible to "share" the same cache over different jobs within the
same build, but beware that **this can cause problems** if those jobs are configure to
run in parallel.  So it's advised to assign a unique id (within the build script) to
each job.

It's also possible to **assign multiple caches to one job**.  In this case you need
to pass a vector of multiple cache configurations to the `caches` function:

```clojure
(def build-job
  (-> (container-job "build")
      ;; ...
      (caches [(cache "mvn-cache" ".m2")
               (cache "other-cache" "other-dir")])))
```

## Controlling Cache Renewal

Currently it's **not yet possible** to control when a cache is updated.  In the future we
will add the possibility to add a condition to determine when a cache should be updated.
A common use case is calculating a hash based on the contents of a file, and compare
that to the hash of the stored cache.