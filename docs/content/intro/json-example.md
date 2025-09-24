{:title "JSON example"
 :category :getting-started
 :index 40
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more useful example"]
           ["intro/edn-example" "Edn example"]
           ["intro/yaml-example" "Yaml example"]
	   ["jobs" "Jobs"]]}

Next to [EDN](intro/edn-example) and [YAML](intro/yaml-example), *MonkeyCI* also
supports `JSON`.  This is a very widely supported format, and the *lingua franca*
for just about every machine-to-machine communication over the web.

Similar to `EDN` and `YAML`, `JSON` is only usable for simple scripts.  These three
formats are mostly interchangeable so you can use either.  You can even combine them, as
*MonkeyCI* will read all files with supported extensions in the `.monkeyci/` directory.

Below is a basic [build](/builds) configuration for a single [job](/jobs), stored in
`.monkeyci/build.json`:

```json
{
  "id": "echo-job",
  "image": "docker.io/alpine:latest",
  "script": ["echo \"Hi there, this is a test\""]
}
```

Of course, multiple jobs are also possible:

```json
[
  {
    "id": "test",
    "image": "docker.io/maven:latest",
    "script": ["mvn verify"],
    "saveArtifacts":
    [
      { "id": "target", "path": "target/"}
    ]
  },
  {
    "id": "publish",
    "image": "docker.io/maven:latest",
    "script": ["mvn deploy:deploy"],
    "dependencies": ["test"],
    "restoreArtifacts":
    [
      { "id": "target", "path": "target/"}
    ]
  }
]
```
This build script declares two jobs: `test`, which runs Java unit tests using
[Apache Maven](https://maven.apache.org), and `publish`, which publishes the
artifacts and which [is dependent](builds) on the `test` job in order to execute.
The `test` job also [saves artifacts](artifacts) to pass on to the `publish` job.

As stated, this is **only useful for the simplest build scripts**.  As soon as you need
[conditions](conditions) or you want to run [action jobs](jobs), you'll need to
code your scripts using [Clojure](why-clojure).  Don't forget you can also combine
`JSON` with Clojure code!