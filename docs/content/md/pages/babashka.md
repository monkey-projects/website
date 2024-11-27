{:title "Babashka"}

Some of you may be wondering if there is some overlap with [Babashka](https://babashka.org),
the excellent Clojure implementation for your shell.  *MonkeyCI* is in no way intended to
replace (or be replaced) by Babashka.  On the contrary, both tools complement each other.

Where Babashka is a tool to run Clojure code from the command line, *MonkeyCI* provides
a way to run that code in a controlled environment.  You can start container jobs that
in turn run Babashka tasks.

We are considering if Babashka may be used to run build scripts, instead of starting a
full-blown Clojure JVM, but for now we stick with the Java-based implementation because
it is even more powerful.  But, as stated, this does not mean you should not use Babashka
in your build jobs themselves!