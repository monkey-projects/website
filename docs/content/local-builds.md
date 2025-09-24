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