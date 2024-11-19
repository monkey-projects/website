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

What makes *MonkeyCI* different from other similar tools?  We allow you to use code
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

### Basic Example

Let's show an example of how a *MonkeyCI* build script could look like:

```clojure
(use 'monkey.ci.build.core)

(action-job
 "test-job"
 (fn [_]
   (println "Hi, I don't do very much.  But it's a start!")))
```

This very basic (and, frankly, useless) build script defines a single job that will
be executed by *MonkeyCI* if you put it in your repository at `/.monkeyci/build.clj`.

Commit the file to your repository, push it to [Github](https://github.com) or
[Bitbucket](https://bitbucket.org) and log in at [MonkeyCI](https://app.monkeyci.com).
If it's your first time, a new user will automatically be created and you can also
create a [new customer](/pages/customer-new/), or [join an existing one](/pages/customer-join/).
Go ahead, it's [free](/pages/pricing/)!

So, let's analyze the above file line by line.
```clojure
(use 'monkey.ci.build.core)
```
This tells the script processor to include the core namespace for *MonkeyCI* builds.  This
is something you will most likely to in every build script.  It contains the most basic
functionality for setting up a build script in *MonkeyCI*.  There are [other
ways](https://clojuredocs.org/clojure_core/clojure.core/require) to include a namespace,
but `use` is the simplest one.  It makes all functions in the target namespace invokable
from your script file.  Although it's advised to use an alias, for clarity, we'll go
without one in this example, because it's such a simple file.  But see the [documentation
for the use expression](https://clojuredocs.org/clojure.core/use) when you have time.

```clojure
(action-job
```
This declares an action job.  There are **two kinds of jobs** in *MonkeyCI*: *action jobs*
and *container jobs*.  The first type just runs an arbitrary function in your script, while
the second type starts a new container job.  Most of the time you will probably use container
jobs, but especially in the more complicated build scripts, action jobs may pop up as well.
They are especially well suited to do some intermediate processing that you would otherwise
have to write a clunky shell script for.  Think of parsing `JSON` documents, composing
`HTTP` requests, and so on.  Note that these action jobs run inside the Clojure process,
so you have access to all functionality available to the build script, and you can even
execute [shell scripts](/pages/shell/) as well!  We'll talk later on how to [include
additional libraries](/pages/deps/) in your build to broaden the scope even further.

```clojure
 "test-job"
```
This is the identifier of that specific job.  Each identifier should be unique over your
build script, and it is used to display information, but also to declare [dependencies](/pages/dependencies/)
between jobs.  We advise to keep the name short but descriptive.  You can include **every
character** in the name, and there is **no real limit** to it's length, but let's say 16MB is
about the practical maximum length, due to database storage limitations.

```clojure
 (fn [_]
   (println "Hi, I don't do very much.  But it's a start!")))
```
This is the actual function that will be executed.  It is passed the [script context](/pages/context/)
as an argument, but since we're not using it here, we're replacing it with an `_`.  This
means more or less *"I know this function receives an argument, but we're not using it right
now."*  In this case, it just prints a message.  This message will be displayed when you
navigate in the application site to that specific job.

## Conclusion

The above example illustrates in a nutshell how *MonkeyCI* builds work.  If this triggers
your curiosity, you can learn more by proceeding to one of the following next steps:

 - [A more useful example](/pages/useful-example/)
 - [How to register as a new user](/pages/registration/)
 - [How MonkeyCI works under the hood](/pages/under-the-hood/)
 - [Our sustainability goals](/pages/sustainability/)

