{:title "Command-line Interface"
 :category :getting-started
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
 0.19.7.1

COMMANDS:
   run                  Runs build locally
   verify               Verifies local build script
   test                 Runs build script unit tests

GLOBAL OPTIONS:
   -?, --help
```

Although there are other commands, they are intended for internal use and so only
the `build` command will be discussed here.

### Running Builds Locally

Runs a build locally.  It will load the build script, and execute any jobs according
to the configuration.  For container jobs, [podman](https://podman.io) is used.  Note
that local builds do not count towards your credit consumption, and it will also not
register on the main application.  **Artifacts and caches are not downloaded or
published**.

This can be useful to run a one-of, or to try something out.  For actual testing it's better
to write [unit tests](tests).  Below are the possible options you can pass to the command:

```shell
NAME:
  build run - Runs build locally

USAGE:
  build run [command options] [arguments...]

OPTIONS:
   -d, --dir S          .monkeyci  Script location
   -u, --git-url S                 Git repository url
   -b, --branch S                  Repository branch
   -t, --tag S                     Repository tag
       --commit-id S               Commit id
       --sid S                     Repository sid
   -p, --param S                   Build param
       --params-file S             Build params file
   -?, --help
```

Normally, you will navigate to your git repository directory.  There you will run
the build command:
```bash
$ monkeyci build run
```

This will execute the build script found in the local `.monkeyci/` directory.  If you
want to override this, you can use the `-d` switch.  It's also possible to execute a
build directory from a git repository, by passing in the `--git-url`.  If you want to
use a private repository, make sure your SSH-keys are configure correctly (in the `~/.ssh`
directory).

#### Parameters

By default, when running a local build, no parameters will be passed.  In the future,
we will add the possibility to fetch [parameters](params) from the global API, but this
feature is still under development.  You can, however, explicitly specify build parameters
on the command line.  Either by specifying them literally using the `-p` or `--param`
option, or by putting them all in a file, and passing its path using the `--params-file`
option.  Each of these support multiple occurrances.  For example:

```bash
$ monkeyci build test -p PARAM1=value1 -p PARAM2=VALUE2
```

Or, using a file:
```bash
$ monkeyci build test -p PARAM1=value1 --params-file params.edn
```

The `--params-file` option accepts `edn`, `json`, `yaml` or Java Properties files.
A `json` parameters file could look like this:

```json
{
  "username": "testuser",
  "password": "highly-secret-value"
}
```

These parameters are then passed to your build script, where they can be retrieved
using the [build-params](https://cljdoc.org/d/com.monkeyci/app/0.19.7.1/api/monkey.ci.api#build-params) api function.

### Verifying Build Scripts

When you want to do a quick verification of your build script syntax, the `build verify`
command is ideal.  It does not run any jobs or tests, but it does a static code analysis
of the build script itself, and will flag any syntax errors or warnings.

```shell
$ monkeyci build verify

Build script is valid!
```

It will either print out a success message, or list any errors and warnings it encounters.

### Running Unit Tests

Runs any unit tests that you may have configured on your build script.  See [unit
tests](tests/) for more details.