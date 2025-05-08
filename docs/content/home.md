{:title "MonkeyCI Documentation"
 :short "Home"
 :home? true
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more useful example"]
           ["intro/edn-example" "Edn example"]
           ["intro/json-example" "Json example"]
           ["intro/yaml-example" "Yaml example"]
	   ["registration" "How to register as a new user"]
	   ["under-the-hood" "Under the hood"]
	   ["sustainability" "Our sustainability goals"]
	   ["security" "Security"]]}

Welcome to the [MonkeyCI](https://monkeyci.com) documentation center!

## What is MonkeyCI?

*MonkeyCI* is a [CI/CD](https://en.wikipedia.org/wiki/CI/CD) pipeline runner that allows
you to use code in addition to `yaml`, `json` or [edn](https://github.com/edn-format/edn) for
configuration.  Most other tools only allow `yaml`.  We also support
this, but only for the basic configurations.  For more complex situations, you will
need conditions, maybe even loops.  After a while, your nice yaml script starts to
look more and more like a coding experiment gone wrong.  Our philosophy is: **for
coding, use a programming language**!  And our language of choice is [Clojure](https://clojure.org).
Why Clojure?  Well, you can read [all about that here](why-clojure/).

This opens up a lot of possibilities.  For example, you can write [unit tests](tests)
to verify your build script or create [custom conditions](conditions) to support your
complex workflows.

*MonkeyCI* also allows you to [run your builds locally](local-builds), for those one-off
scenario's that you don't want to use build credits for, or to do some local fine-tuning
that unit tests cannot capture.

## Is MonkeyCI For You?

For any coding project that has more than the most basic build flow, *MonkeyCI* is surely
something you may consider using.  If you also require the **ability to test and extend**
your build scripts, then *MonkeyCI* is most definitely worth a try.

## Getting Started

In *MonkeyCI*, you can create [repositories](repos/), that refer to a
[Git](https://en.wikipedia.org/wiki/Git) repository that resides on one of the [supported
platforms](platforms/).  Whenever a build is [triggered](triggers/), it
will be displayed on the repository page.

### Register

First off, go to [the login page](https://app.monkeyci.com/login) and [register as a new
user](registration/).  Currently, you can only register if you either have a
[GitHub](https://github.com) or a [Bitbucket](https://bitbucket.org) account, but we will
expand this in the future.  Choosing one of these will make it easier for you to start
watching changes in [repositories](repos/) hosted on those respective platforms.

### Add Repositories

*MonkeyCI* uses **webhooks** to get notified of any changes in external repositories.  We
don't host repositories of our own.  If a repository is *being watched* for changes, and it
contains a build script in the `/.monkeyci` directory, *MonkeyCI* will **trigger a build**.
From then on it's totally up to you: what [jobs](jobs/) are in the build, what
[artifacts](artifacts/) are being produced, etc...

### Your First Build Script

Now it's time to write your first build script!  The best way to learn how to do that is
by looking at **some examples**.

First take a look at the [basic example](intro/basic-example/).  After that, you can
move on to a [more advanced example](intro/useful-example/).  *MonkeyCI* also supports
[edn](intro/edn-example), [json](intro/json-example) and [yaml](intro/yaml-example).
We also have a [cookbook](/categories/cookbook/) for common scenarios.
