{:title "Getting Started in MonkeyCI"
 :summary "We realize that getting started using a new tool can be daunting.  So to get you on your way, we will be providing you with some examples."
 :author "Wout Neirynck"
 :date "2026-03-04"}

We realize that getting started using a new tool can be daunting.  So to get you on your way, 
we will be providing you with some examples.  You can also check out the 
[getting started section](https://docs.monkeyci.com/categories/getting-started) of our
documentation for even more examples.

Now suppose you're working on a Clojure project, and you want to run your unit tests in a 
*MonkeyCI* build pipeline.  A very basic `build.clj` could look like this:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

;; Single job in this build: run unit tests
(-> (m/container-job "test")
    (m/image "docker.io/clojure:tools-deps-trixie-slim")
    (m/script ["clojure -X:test"]))
```

The above example declares a single job, called `test`, that executes a single command:
`clojure -X:test`.  Very basic.  Now, you could also write this in 
[YAML](https://docs.monkeyci.com/articles/intro/yaml-example/) (or even
[JSON](https://docs.monkeyci.com/articles/intro/json-example)), but we're assuming
one of the reasons you're switching to *MonkeyCI* is because you're sick of being
constrained by YAML limitations!

Save this into `.monkeyci/build.clj` so MonkeyCI can find the script.

## Running the Build Locally

Before you push the code upstream and run it on the [MonkeyCI application](https://app.monkeyci.com),
you can run it on your local machine first, using the [MonkeyCI CLI](https://docs.monkeyci.com/articles/cli).
Just run this command in your shell:

```bash
$ monkeyci build run
```

This will run the build script as it would be run in the online app, using a container. This may make it a little slower than just running the tests, but this way you're sure it will work once you push the code. In a future blog I will explain how to trigger this build online, and how to exploit the **true power** of MonkeyCI by using conditions, plugins, parameters and more.

## Conclusion

In this very short blog post I've illustrated how you can get started with a basic build script
with a single job.  Check out future blog posts for more, but in the meantime you can already read
[our documentation](https://docs.monkeyci.com)!
