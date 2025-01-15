{:title "Why Clojure?"
 :related [["intro/basic-example" "Basic example"]
           ["intro/useful-example" "A more advanced example"]]}

Why have we chosen [Clojure](https://clojure.org) as our main build pipeline
language and not a more well-known language like Java, C# or Javascript?  Well,
when we started this project, the selection of the build language was the most
important decision to make.  There were several criteria we used to decide:

 - It must be a **mature programming language**.
 - It must be **general-purpose**.
 - It should run on a **widely adopted platform**.
 - It should be **easy to learn**.
 - It must be **easy to use as script**.

Clojure ticks all these boxes.  There are probably other languages out there
that comply with these criteria as well, but since we knew (and admittedly, loved)
Clojure, we decided to go with that.

## Mature

Clojure has existed **since somewhere around 2007**, so that's plenty of time to
work out any kinks it may have.  Moreover, it's based on [Lisp](https://en.wikipedia.org/wiki/Lisp_(programming_language)),
which has been around since the late 1950's.  That makes it the second-oldest
language still in use (after Fortran).  And it still looks good!

## General-Purpose

From the [Clojure website](https://clojure.org):

> Clojure is a dynamic, general-purpose programming language, combining the approachability and interactive development of a scripting language with an efficient and robust infrastructure for multithreaded programming.

## Widely Adopted Platform

Clojure compiles to [Java](https://www.oracle.com/java/) bytecode, so it runs on the
[JVM](https://en.wikipedia.org/wiki/Java_virtual_machine).  This makes it available
on one of the most widely used platforms on the planet.  This also means that all
Java libraries can also be called from Clojure, which is a huge advantage.

Also, there is [ClojureScript](https://clojurescript.org/), which compiles to JavaScript.
We're thinking about using this to our advantage.  We're already doing that in part,
because the entire frontend of *MonkeyCI* is written in ClojureScript!

## Easy To Learn

One of the reasons we like Lisp (and by extension, Clojure) so much is it's rediculously
**simple syntax**.  It has no real keywords, just "special forms", like `if` and `let`.
Just about everything else is a function, and it treats them all the same.  No need
to break your fingers by constantly having to type curly braces.  Clojure code mainly
uses round brackets, with some other type of brackets as needed.  For example:

```clojure
(defn some-function [a b c]
  (println "Hi, I'm a function with these arguments:" a b c))
```

But the purpose of this documentation is not to teach you Clojure.  There are [much
better resources for that](https://clojure.org/guides/learn).

## Easy To Use As Script

What we mean by that is that is should be fairly simple to load the build script
code.  We don't want to have to go through a complicated process of compilation
and linking for the script to run.  Lisp-like languages have a huge advantage over
other languages in this regard: the code is actually a *data structure in itself*!
This opens up a lot of possibilities, and one of them is that you can fairly **easily
load code** in your application.  This is one of the features that pushed us towards
using Clojure as our scripting code.

## Downsides

Of course, everything has a downside.  In the case of Clojure, it is that it is
fairly different from the Algol-like languages out there.  Think Java, C#, C,
C++, well, everything that uses curly braces intensively!  It's a **functional
language**, and that means you need to turn a switch in your head in order to use
it effectively.  Fortunately, many languages have been moving towards functional
programming the last few decades, for example by including **lambda's, streams
and immutability**.  So most functional concepts have become well-known to most
developers.

## No Magic

Through the use of [macros](https://clojure.org/reference/macros), Clojure offers ways to
create your own DSL's.  We could have done this for *MonkeyCI* as well, but we have
chosen to refrain from that for the time being.  This because in our experience using
macros may be useful, but it also adds a kind of "magic" to your code, which is **often
hard to understand** for novice users and developers.  Maybe in the future we will create
a more succinct DSL, but the "pure" Clojure code will always remain available.

## Conclusion

So you see that our choice was not just done using a darts board, but **we really
thought about it**.  For some people it may be a turn-off to have to learn
a new language to use *MonkeyCI*, but we hope to have convinced most of you that it's
not a big leap.  On the contrary, we're sure you love learning new stuff, right?  And
you also want to become more efficient?  Why else would you be here?