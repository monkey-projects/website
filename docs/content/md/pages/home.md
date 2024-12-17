{:title "MonkeyCI Documentation"
 :description "Home"
 :home? true}

Welcome to [MonkeyCI](https://monkeyci.com)!

## What?

Suppose you're working on your newest project.  Of course, at some point you will want
to automatically build it, maybe also deploy it somewhere.  And run your unit tests.
All automated!  To do this, you will need a [CI/CD](https://en.wikipedia.org/wiki/CI/CD)
pipeline tool.  And as it happens, [MonkeyCI](https://monkeyci.com) is such a tool!

## Why?

What makes *MonkeyCI* different from other similar tools?  We allow you **to use code**
to define your build pipelines.  Most other tools only allow `yaml`.  We also support
this, but only for the basic configurations.  For more complex situations, you will
need conditions, maybe even loops.  After a while, your nice yaml script starts to
look more and more like a coding experiment gone wrong.  Our philosophy is: **for
coding, use a programming language**!  And our language of choice is [Clojure](https://clojure.org).
Why Clojure?  Well, you can read [all about that here](/pages/why-clojure/).

## Getting Started

In *MonkeyCI*, you can create [repositories](/pages/repos/), that refer to a
[Git](https://en.wikipedia.org/wiki/Git) repository that resides on one of the [supported
platforms](/pages/platforms/).  Whenever a build is [triggered](/pages/triggers/), it
will be displayed on the repository page.

### Register

First off, go to [the login page](https://app.monkeyci.com/login) and [register as a new
user](/pages/registration/).  Currently, you can only register if you either have a
[GitHub](https://github.com) or a [Bitbucket](https://bitbucket.org) account, but we will
expand this in the future.  Choosing one of these will make it easier for you to start
watching changes in [repositories](/pages/repos/) hosted on those respective platforms.

### Add Repositories

*MonkeyCI* uses **webhooks** to get notified of any changes in external repositories.  We
don't host repositories of our own.  If a repository is *being watched* for changes, and it
contains a build script in the `/.monkeyci` directory, *MonkeyCI* will **trigger a build**.
From then on it's totally up to you: what [jobs](/pages/jobs/) are in the build, what
[artifacts](/pages/artifacts/) are being produced, etc...

### Examples

For more on how to write build scripts, see the [basic example](/pages/basic-example/) or
a [more advanced example](/pages/useful-example/).  We also have a [cookbook](/pages/cookbook/)
for common scenarios.

### Going Further

You can read more by clicking one of the links on the right, or proceed by going to one
of these pages:

 - [Basic example](/pages/basic-example/)
 - [A more useful example](/pages/useful-example/)
 - [How to register as a new user](/pages/registration/)
 - [How MonkeyCI works under the hood](/pages/under-the-hood/)
 - [Our sustainability goals](/pages/sustainability/)
 - [Security](/pages/security/)

