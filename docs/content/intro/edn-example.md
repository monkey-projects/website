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
start a container containing `maven` and run `mvn verify` in the build checkout
directory.

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

## Going Further

The above examples are more or less what you would also write in any other `CI/CD`
tool, but in `edn` instead of `yaml`.  Note that *MonkeyCI* also supports `yaml`,
see [this example](intro/yaml-example) and [json](intro/json-example), but it amounts
to the same.  It contains **a lot of repetition** and you need to know the correct names
of all the map keys, which can make it difficult to use.  It's also not possible to
configure [conditional execution](conditions).  *MonkeyCI* can help you with this
in another way.  See [an example using code](intro/basic-example) for this.