{:title "Using 3rd Party Libraries"
 :tags ["libraries"]}

*MonkeyCI* uses the [Clojure CLI](https://clojure.org/reference/clojure_cli) in order
to execute the build scripts.  This CLI allows to configure dependencies to include
other libraries in your code, and so it's also possible to do this in your build scripts.
These dependencies are configure in a file called `deps.edn` which should be put next to
your `build.clj`, in the `.monkeyci/` directory.  Although the CLI provides [a lot of
functionality](https://clojure.org/reference/deps_edn) with this file, we use it mainly
to declare dependencies, like so:

```clojure
;; Example deps.edn file
;; Add the maven plugin library
{:deps {com.monkeyci/plugin-mvn {:mvn/version "1.0.0"}}}
```

This is required if you want to use any [plugins](/pages/plugins/) in your build script.
But you can also use it to **include any 3rd party library** in your script, which you
can then use in action jobs.

*MonkeyCI* will automatically include a reference to itself in the dependencies, so
you don't need to do that yourself.  This is necessary to be able to access the basic
core functionality of *MonkeyCI*.  But apart from that you're free to add any `Java` or
`Clojure` library in your build script.