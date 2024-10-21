{:title "MonkeyCI Documentation"
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
Why Clojure?  Well, you can read [all about that here](/pages/why-clojure).

## Getting Started

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
create a [new customer](/pages/customer-new), or [join an existing one](/pages/customer-join).
Go ahead, it's [free](/pages/pricing)!