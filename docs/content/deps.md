{:title "Using 3rd Party Libraries"
 :category :extending
 :index 20
 :tags ["libraries"]
 :related [["plugins" "Plugins"]
           ["notifications" "Notifications"]]}

*MonkeyCI* uses the [Clojure CLI](https://clojure.org/reference/clojure_cli) in order
to execute the build scripts.  This CLI allows to configure dependencies to include
other libraries in your code, and so it's also possible to do this in your build scripts.
These dependencies are configured in a file called `deps.edn` which should be put next to
your `build.clj`, in the `.monkeyci/` directory.  Although the CLI provides [a lot of
functionality](https://clojure.org/reference/deps_edn) with this file, we use it mainly
to declare dependencies, like so:

```clojure
;; Example deps.edn file
;; Add the maven plugin library
{:deps {com.monkeyci/plugin-mvn {:mvn/version "0.1.0"}}}
```

The file format is [EDN](https://github.com/edn-format/edn), short for *extenisble
data notation*.  It's to Clojure what `JSON` is to JavaScript.

This is required if you want to use any [plugins](plugins/) in your build script.
But you can also use it to **include any 3rd party library** in your script, which you
can then use in action jobs.  These libraries can be native Clojure, but also Java
libraries, so the **entire Java ecosystem** is available in your builds!

*MonkeyCI* will automatically include a reference to itself in the dependencies, so
you don't need to do that yourself.  This is necessary to be able to access the basic
core functionality of *MonkeyCI*.  But apart from that you're free to add any `Java` or
`Clojure` library in your build script, as long as they don't require any native
dependencies.