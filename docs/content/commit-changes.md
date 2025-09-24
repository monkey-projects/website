{:title "Changed Commit Files"
 :category :builds
 :index 110
 :related [["conditions" "Conditions"]
 	   ["builds" "Builds"]
	   ["jobs" "Jobs"]]}

A push consists of one or more commits, and each commit is about one or
more changed files.  In *MonkeyCI* it's possible to retrieve which files
have changed in the commit if the [Git platform](platforms) passes this
information in the [webhook](triggers) request.

## Usage

In order to access this information, you can use two kinds of functions
from the api:

 - Retrieve the list of added, removed or modified files.
 - Check if a specific path (or pattern) was touched.

There are four categories of changes: `added`, `removed`, `modified` or
`touched`, the latter being one of the previous three.  You will typically
use the `touched` category of functions, unless in specific situations.

### File Lists

When you need the list of files that have undergone changes, you can use
any of these functions:

 - `files-added`: lists all new files in the push
 - `files-modified`: lists all changed files in the push
 - `files-removed`: lists all removed files

Each of these functions takes the build context as an argument, and returns
a list of file paths.

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(def changes-job
  (m/action-job "file-changes"
    (fn [ctx]
      (println "The list of added files is:" (m/files-added ctx)))))
```

But usually you'll want to use this information in a [condition](conditions),
and for this we have provided other functions, see below.

### File Checks

When you just want to check if a file, or files matching a pattern, have
undergone any changes, you can use any of these file check functions:

 - `added?`
 - `removed?`
 - `modified?`
 - `touched?`

The first three check whether files matching the pattern or path have been
added, removed or modified, respectively.  The last function is actually a
combination of the other three: it returns `true` if any of the previous three
would return `true`.

The checker functions are flexible and take either one or two arguments.  The
2-arity versions take the build context and a predicate.  This predicate can
be one of three things:

 - A fixed path (a string)
 - A regular expression
 - A predicate function, taking a file path and returning a boolean value.

The 1-arity variant is a convenience, that just takes a predicate, and returns
a new function that takes the build context, which can be useful to build
condition functions.

For example:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(defn src-changed?
  "Checks if any files in the src directory have changed"
  [ctx]
  ;; The #"..." construct indicates a regular expression
  (m/touched? ctx #"^src/.*$"))

(def test-changed?
  "Uses 1-arity variant to check if test files have changed"
  (m/touched? #"^test/.*$"))

(defn build-src [ctx]
  ;; Only build if source files have changed
  (when (src-changed? ctx)
    (m/container-job "build-src")
    ...))
```

The previous example shows how you can use file checks to conditionally execute
a build [job](jobs).

The third predicate variant can be used if you need more powerful custom
file checks.  For example, you could use this to check the file contents:

```clojure
(ns build
  (:require [monkey.ci.api :as m]
            [clojure.string :as cs]))

(defn phrase-check [f]
  (cs/includes? (slurp f) "some test phrase"))

(def contains-phrase?
  "Checker that will yield `true` if any of the touched files
   contains the test phrase"
  (m/touched? phrase-check))
```

This illustrates how you could use functions to create custom conditions on files.