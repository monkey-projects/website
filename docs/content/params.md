{:title "Build parameters"
 :category :builds
 :related [["labels" "Labels"]
           ["securit" "Security"]]}

Build parameters are a way to configure your builds, without having to put this
configuration in your build script itself.  It is typically used for **sensitive
information** like credentials or signing keys.

## Configuring

Build parameters are linked to a [customer](customer/).  In order to edit
these parameters, go to the customer screen, and click the "Parameters" button.
There you will see an overview of your already configured parameters.  Build
parameters are **grouped in sets**, and each set can contain **multiple parameter
keys and values**.  A set can also have [labels](labels/), which are used
to determine which [repository](repos/) builds have access to which parameters.
Repositories that have **the same values** for each of the labels defined in
the parameter set, will gain access to that set at build time.

If **no labels** are defined on a parameter set, **all builds** for this customer
will have access to that set.

Although it is possible to define the same parameter key multiple times in one
set, it is not advised since in your builds only one will remain, and it is **not
defined** which one.  The same goes for defining the same parameter in multiple sets.
If a build has access to all of those sets, it is **not defined** which parameter
value it will actually get to see.

A parameter name can be up to **100 characters** long, and a value can be up to **16MB
long**.  Although we advise against creating such a large parameter, for performance
reasons.

For security reasons, we advise to set up the **label filters as narrow as possible**,
to avoid exposing sensitive information to any unwanted builds.

## Using

In order to use parameters in your build, you need to explicitly request them when
needed.  You do this by invoking the `build-params` function in the `monkey.ci.build.api`
namespace.  As an example, suppose you have defined a parameter set with a key called
`PASSWORD`.  You can access it in a job like this:

```clojure
;; Example build script that illustrates how to use build parameters
(ns build
  (:require [monkey.ci.build
             [api :as api]
	     [core :as c]]))

(c/action-job
  "use-params"
  (fn [ctx]
    (let [params (api/build-params ctx)]
      (println "The secret password is:" (get params "PASSWORD")))))
```

In your build the parameters will be exposed as **a key/value map**, so you can just
access the value using the [get](https://clojuredocs.org/clojure.core/get) function.
If a parameter is not available to a build, the `get` call will return `nil`.