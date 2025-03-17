{:title "YAML example"
 :category :getting-started
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more useful example"]
           ["intro/edn-example" "Edn example"]
           ["intro/json-example" "Json example"]
	   ["jobs" "Jobs"]
	   ["builds" "Builds"]
	   ["artifacts" "Artifacts"]]}

Most CI/CD tools use `YAML` in order to configure build scripts.  `YAML` was
designed as a way to create structured information, readable to both humans and
machines.  And indeed it's great for simple configurations.

*MonkeyCI* also supports `YAML` out of the box, but only for simple scripts, just
like [EDN](intro/edn-example) and [JSON](intro/json-example).  These three formats
are mostly interchangeable so you can use either.  You can even combine them, as
*MonkeyCI* will read all files with supported extensions in the `.monkeyci/` directory.

Below is a basic [build](/builds) configuration for a single [job](/jobs), stored in
`.monkeyci/build.yaml`:

```yaml
id: echo-job
image: docker.io/alpine:latest
script:
  - 'echo "Hi there, this is a test"'
```

Of course, multiple jobs are also possible:

```yaml
- id: test
  image: docker.io/maven:latest
  script:
    - mvn verify
  save-artifacts:
    - id: target
      path: target/

- id: publish
  image: docker.io/maven:latest
  script:
    - mvn deploy:deploy
  dependencies:
    - test
  restore-artifacts:
    - id: target
      path: target/
```
This build script declares two jobs: `test`, which runs Java unit tests using
[Apache Maven](https://maven.apache.org), and `publish`, which publishes the
artifacts and which is [dependent](builds) on the `test` job for the binaries
exposed as [artifacts](artifacts) in order to execute.

As stated, this is **only useful for the simplest build scripts**.  As soon as you need
[conditions](conditions) or you want to run [action jobs](jobs), you'll need to
code your scripts using [Clojure](why-clojure).  Don't forget you can also combine
`YAML` with Clojure code!