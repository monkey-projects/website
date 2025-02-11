{:title "EDN example"
 :category :getting-started
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more useful example"]
           ["intro/yaml-example" "Yaml example"]
           ["intro/json-example" "Json example"]
	   ["jobs" "Jobs"]]}

[EDN](https://github.com/edn-format/edn) is to Clojure what `JSON` is to JavaScript.
It's a way to write **declarative information**, but it uses part of the syntax of
Clojure.  *MonkeyCI* build scripts essentially generate jobs that are structures
of maps and lists.  The last expression of the script should be the list of jobs
(or the job, if there is only one) to execute.  Or a function that generates that
list, but that is a more advanced use-case.

Let's take a look at a simple declarative example, stored as `.monkeyci/build.edn`:

```clojure
{:id "compile"
 :image "docker.io/maven:latest"
 :script ["mvn verify"]}
```

This is the **simplest possible configuration** of a [container job](jobs).  It will
start a container containing [Apache Maven](https://maven.apache.org/) and run `mvn
verify` in the build checkout directory.

Now, we previously stated that the last expression should be the job, and that's
exactly what this script does: it **declares a single job**, which is also the
last expression.

## Multiple Jobs

It's also possible to create multiple jobs this way.  All we have to do is put them
in a vector:

```clojure
[{:id "test"
  :image "docker.io/maven:latest"
  :script ["mvn verify"]}
 {:id "publish"
  :image "docker.io/maven:latest"
  :script ["mvn deploy"]
  :dependencies ["test"]}]
```

Again, this is an `edn` structure that contains only a single expression, but this
expression in turn contains **two job definitions**.  A `test` and a `publish` job,
and `publish` [depends on](jobs) `test`, so it will only be executed once the tests
have run successfully.

## Why Use Edn?

Why should you use `edn` instead of, say, `yaml`?  It's completely up to you, but `edn`
is a bit more powerful than `yaml`.  It has a notion of [keywords](https://clojure.org/reference/reader#_literals),
which are heavily used by the *MonkeyCI* for job configuration and allow for namespaces,
which are used by [extensions](extensions).
Also, it may be better suited to do some **whitespace escaping**, which may be difficult
in `yaml`.  Furthermore, `edn` supports [reader tags](https://github.com/edn-format/edn#tagged-elements),
which are used to extend the language in order to process more complex information.
Currently, *MonkeyCI* is not using this in build scripts, but we may do so in the
future.

It's also a small **step up to full Clojure** code, which you will need when you want
to do more complex flows and [conditions](conditions) in your build scripts.  Should
the need arise, you can simply rename your `build.edn` to `build.clj` and go from
there.  With `yaml` you would have to rewrite some of your datastructures in this case.

But note that *MonkeyCI* supports a mixture of **all of the above**.  So you can also write
part of your script in `yaml`, another part in `edn` and still other parts in Clojure!

## Going Further

The above examples are more or less what you would also write in any other `CI/CD`
tool, but in `edn` instead of `yaml`.  Note that *MonkeyCI* also supports `yaml`,
see [this example](intro/yaml-example) and [json](intro/json-example), but it amounts
to the same.  It contains **a lot of repetition** and you need to know the correct names
of all the map keys, which can make it difficult to use.  It's also not possible to
configure [conditional execution](conditions).  *MonkeyCI* can help you with this
in another way.  See [an example using code](intro/basic-example) for this.