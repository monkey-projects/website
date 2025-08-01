{:title "Command-line Interface"
 :related [["tests" "Unit tests"]
           ["builds" "Builds"]]}

Next to a graphical user interface, *MonkeyCI* also provides a command-line
interface (or **CLI**).  The CLI can be used to trigger builds, run them locally,
verify build scripts or run unit tests.

## Installation

In order to install the CLI, you will first need to [install Java](https://www.oracle.com/javadownload).  You'll need **Java 21 or later** for the CLI to work correctly.  Then run this script:

```shell
$ wget https://monkeyci-artifacts.s3.fr-par.scw.cloud/install-cli.sh -O - | bash
```

It will download the *MonkeyCI* `jar` file, put it in a local directory and
create a binary that allows you to invoke it.  The binary is installed in `$HOME/bin`,
which usually is in your `PATH`.  If it doesn't work, check if the bin dir has been
added to your path.

## Invocation

Running the CLI is done by invoking the `monkeyci` command:

```shell
$ monkeyci build --help

NAME:
  build - Build commands

USAGE:
  build [global-options] command [command options] [arguments...]

VERSION:
 0.19.4

COMMANDS:
   run                  Runs build locally
   verify               Verifies local build script
   test                 Runs build script unit tests

GLOBAL OPTIONS:
   -?, --help
```

Although there are other commands, they are intended for internal use and so only
the `build` command will be discussed here.

### run

Runs a build locally.  It will load the build script, and execute any jobs according
to the configuration.  For container jobs, [podman](https://podman.io) is used.  Note
that local builds do not count towards your credit consumption, and it will also not
register on the main application.  Artifacts and caches are not downloaded or published.

This can be useful to run a one-of, or to try something out.  For actual testing it's better
to write [unit tests](tests).

### verify

When you want to do a quick verification of your build script syntax, the `build verify`
command is ideal.  It does not run any jobs or tests, but it does a static code analysis
of the build script itself, and will flag any syntax errors or warnings.

### test

Runs any unit tests that you may have configured on your build script.  See [unit
tests](tests/) for more details.