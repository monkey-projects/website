{:title "Build Script Tests"
 :category :builds
 :related [["cli" "The command-line interface"]]}

As with all coding, or actually, with *everything*, you want to test things
before you roll them out.  Similar to your production code, which *of course*
you write using [TDD](https://en.wikipedia.org/wiki/Test-driven_development),
you'd want to be able to write tests for your build script.  After all, it may
be important.  When you're writing things thay **may deploy directly into production**,
it seems self-evident to test this thoroughly before running it against a live
environment.

To this end, *MonkeyCI* provides a way to add unit tests to your build script.
For this, we use the standard [testing framework already present in
Clojure](https://clojure.github.io/clojure/clojure.test-api.html).  But
in the future we may also support other testing frameworks.  The basic idea
is that next to your build script, located in `.monkeyci/build.clj`, you add
test code, and put it in `.monkeyci/build_test.clj`.  The namespace should be
`build-test`, and it contains the unit tests for your build script.

For example:
```clojure
(ns build-test
  (:require [clojure.test :refer :all]
            [build :as sut]
	    [monkey.ci.api :as m]
	    [monkey.ci.test :as mt]))

(deftest build-job
  (testing "creates an action job"
    ;; Fairly trivial test, but it's a start
    (is (m/action-job? (sut/build-job mt/test-ctx)))))
```

The convention is to alias the namespace you're testing as `sut`, short for "system
under test", but you're really free to choose whatever you want.  In addition we
require everything from the `clojure.test` namespace, which contains the Clojure
test framework.  Each unit test is put in a `deftest` block, and can contain as
many assertions as you like, but there has to be at least one.

In order to run the unit tests, you can run the [CLI](cli/) from your repository
directory:
```bash
$ monkeyci build test
```

This will automatically detect any unit tests in the `.monkeyci` directory and
run them.  The results are printed to the console.

Also not the `monkey.ci.test` namespace.  It contains a set of helper functions that
can be used in unit tests.  In this case, we only use the `test-ctx`, which is a
basic context that you can pass to your jobs for testing purposes.

Explaining all about the `clojure.test` framework is beyond the scope of this
documentation, but you can read more about it in the [official
documentation](https://clojure.github.io/clojure/clojure.test-api.html).

## Watching For Changes

When using TDD, you often will want to constantly run your unit tests as you do
changes to your build script.  This way you get **immediate feedback** which is a major
requirement for doing TDD correctly.  To this end, *MonkeyCI* provides a way to
"watch" for file changes, simply by specifying an extra flag to the CLI:

```bash
$ monkeyci build test --watch
```

This will start the test process, but it will keep on running and every time it
detects a source file change (either of your build code, or your test file), it will
re-run the relevant tests.

## Conclusion

If you have a complicated build script, it's very useful (and reassuring) to also
have a suite of unit tests to verify them.  But it's really a good idea to do it
even for the simple build scripts.  It ensures that **your build script does what you
want it to do**, even in more obscure or less likely situations.  This is especially
important for production builds!