{:title "Local Builds"
 :category :builds
 :index 20
 :related [["builds" "Builds"]
           ["jobs" "Jobs"]
	   ["cli" "Command-line interface"]
	   ["tests" "Build script tests"]]}

Sometimes you just need to run something **on your local machine**.  This is of course
not advised for production purposes, but every once in a while, it can be useful
to run the full build pipeline on your own machine.  *MonkeyCI* makes this possible,
as long as you either have [Docker](https://www.docker.com/) or [Podman](https://podman.io/)
on your machine.

First of all, you need to install the [command-line interface](cli).  After that, open
up a command shell and go to your repository directory (the one where `.monkeyci/` is
located).  Then invoke the `build` command:

```shell
$ monkeyci build
```

There are of course **many parameters** you can customize.  But more on that later!  You can
start by asking for help:

```shell
$ monkeyci build --help
```

Needless to say, we advise to **use this feature sparingly** and certainly **not for
production purposes**!  You really wouldn't want to wipe that precious production
database by accident...

## Running a Build Script

In order to run the build script that is present in the local `.monkeyci/` subdirectory,
just execute thie `build run` command:

```shell
$ monkeyci build run
```

This will load the [build scripts](builds), and execute the [jobs](jobs) they declare.
If you are using [parameters](params) in your scripts, they will not have any value
unless you either **explicitly specify** them, or [configure access](api-keys) to
the *MonkeyCI* API to fetch them there.

### Manually Specifying Parameters

To pass parameters on the command line, you can specify literal values using
the `--param` argument (or `-p` in short):

```shell
$ monkeyci build run --param some-param=some-value -p other-param=other-value
```

As you can see, you can specify multiple parameters at once.

Alternatively, you can put multiple parameters into a configuration file and refer
it in your command:

```shell
$ monkeyci build run --param-file path/to/file
```

The file can be a `json`, `yaml`, `edn` or [Java Properties](https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html) file.  For example, using
`yaml`:

```yaml
# Example params.yaml file
some-param: some-value
other-param: other-value
```

Or in `json`:
```json
{
  "some-param": "some-value",
  "other-param": "other-value"
}
```

Similar to literal parameter values, you can specify multiple files at once.  When
there are multiple **conflicting values** (parameters with same name but specified in
multiple sources), the one that is specified **last will have priority**.

### Fetching Parameters from API

In addition, you can fetch the parameters from the *MonkeyCI* REST API.  This
is similar to how [parameters](params) are exposed to builds when executing them
remotely.  Which parameters are available depends on the configured parameters,
their [labels](labels), and the labels configured on the [repository](repos).

In order to allow for this, you need to configure the organization and the
repository the build belongs to, and set up an [API key](api-keys) on that
organization, or on your user account (that needs access to that organization).

See the [CLI configuration section](cli) for more details on where to configure
this.

In addition, you can **specify these values on the command line**, for example:

```shell
$ monkeyci build --api-key mysecretkey run -o monkey-projects -r monkeyci
```

The above example will build the code using the parameters for organization
`monkey-projects` and repository `monkeyci`, using the specified [API key](api-keys).
