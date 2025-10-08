{:title "Tutorial"
 :category :getting-started
 :index 25
 :related [["intro/basic-example" "A basic example"]
           ["intro/useful-example" "A more useful example"]]}

Get started using *MonkeyCI* by following this tutorial.  It explains:

 - Writing the first [build script](builds)
 - Running it locally using the [CLI](cli)
 - Registering a [new account](registration)
 - Setting up your [first repo](repos)
 - Automatically [build it online](triggers)
 - Make your script more powerful by [including plugins](plugins)

## Getting Started

You can use *MonkeyCI* without any limitations on your own system if you
install [the CLI](cli).  You can do this by running this script:

```shell
$ wget https://monkeyci-artifacts.s3.fr-par.scw.cloud/install-cli.sh -O - | bash
```

After that, you can run the *MonkeyCI* `cli` by simply running `monkeyci`.  See
the [cli page](cli) for more details.  Now, since there is nothing to run it on,
you first need to write an initial script.

## Setting up the Project

Let's assume we're building a Java application using [Maven](https://maven.apache.org/).
We'll use the [archetype plugin](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html)
to generate the initial project files:

```shell
$ mvn archetype:generate -B \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=com.monkeyci \
  -DartifactId=tutorial \
  -Dversion=0.1-SNAPSHOT \
  -Dpackage=com.monkeyci.tutorial
```

This will create a `tutorial` directory with a basic Java project.  We're assuming
you know how Maven works, so you can tailor this for your own needs.  You can check
if it builds by running Maven manually:
```shell
$ cd tutorial
[tutorial] $ mvn verify
```

Eventually you should see something like this:
![initial verify](/img/tutorial-2.png "screenshot 2")

## The Initial Script

*MonkeyCI* assumes your build scripts are in the `.monkeyci/` directory, although
you can override this.  In this tutorial, we'll use the standard settings though.
So let's assume we're putting this tutorial in the `tutorial` directory.  Create
the directory, and also the `.monkeyci` subdirectory:

```shell
[tutorial] $ mkdir .monkeyci
```

We will add a single job to the script, that initially just prints a message.
Now put this into the `./.monkeyci/build.yaml` file:

```yaml
- id: first-job
  image: docker.io/alpine:latest
  script:
    - 'echo "This my first MonkeyCI script!"'
```

Note that *MonkeyCI* also supports [EDN](https://github.com/edn-format/edn) and
`JSON`, in addition to the powerful [Clojure](https://clojure.org) language, but
we'll explain the latter later on.  Now run the build (make sure you're in the
`tutorial` directory):
```shell
[tutorial] $ monkeyci build run
```

In the background, this will start a Java process that loads and runs the build
script jobs.  In this case, there is only one container job, so it will also start
a container using [podman](https://podman.io).

The result should look something like this:
![first build](/img/tutorial-1.png "screenshot 1")

It works!  The output is not displayed, but you can look it up in the output
logs, as printed by the `CLI`.  Ok, now let's make it actually *do something*!

# Expanding the Script
