{:title "Command-line Interface"
 :related [["tests" "Unit tests"]]}

Next to a graphical user interface, *MonkeyCI* also provides a command-line
interface (or **CLI**).  The CLI can be used to trigger builds, run them locally,
verify build scripts or run unit tests.

## Installation

In order to install the CLI, you will first need to install `Java`.  Then
run this script:

```bash
$ TODO
```

It will download the *MonkeyCI* `jar` file, put it in a local directory and
create a binary that allows you to invoke it.

## Invocation

Running the CLI is done by invoking the `monkeyci` command:

```bash
$ monkeyci build --help

NAME:
  build - Build commands

USAGE:
  build [global-options] command [command options] [arguments...]

VERSION:
 0.12.0

COMMANDS:
   run                  Runs build locally
   verify               Verifies local build script
   list                 Lists builds for customer or repo
   watch                Logs build events for customer or repo
   test                 Runs build script unit tests

GLOBAL OPTIONS:
   -s, --server S       Server URL
   -c, --customer-id S  Customer id
   -r, --repo-id S      Repository id
   -?, --help
```

Although there are other commands, they are intended for internal use and so only
the `build` command will be discussed here.

### run

Runs a build locally.  It will load the build script, and execute any jobs according
to the configuration.  For container jobs, [podman](https://podman.io) is used.  Note
that local builds do not count towards your credit consumption, and it will also not
register on the main application.  Artifacts and caches are not downloaded or published.

This can be useful to run a one-of, or try something out.  For actual testing it's better
to write [unit tests](tests).

### verify

When you want to do a quick verification of your build script syntax, the `build verify`
command is ideal.  It does not run any jobs or tests, but it does a static code analysis
of the build script itself, and will flag any syntax errors or warnings.

### test

Runs any unit tests that you may have configured on your build script.  See [unit
tests](tests/) for more details.