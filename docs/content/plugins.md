{:title "Plugins"
 :category :extending
 :index 10
 :tags ["libraries"]}

Plugins are a way to extend *MonkeyCI*'s functionality.  Basically, they are
no more than **regular Clojure libraries** that you can include in your build using
the [deps.edn](deps/).  In order to use a plugin, you have to `require` or
`use` the relevant namespace in your build script, and then invoke the provided
functions.  There is **no magic**, it's simple to use!

It depends on the plugin itself how you should use it, and which functions it
provides.  Let's take **for example** the [Kaniko plugin](https://github.com/monkey-projects/plugin-kaniko).
It can be used to build container images in your build scripts without having to
access a [Docker](https://docker.io) or [Podman](https://podman.io) service.
Because of technical and security reasons, this is not possible in *MonkeyCI*.

This plugin provides functionality to create a single image, but also to create
images for multiple platforms.  This requires multiple jobs, as you need to build
the image for each platform separately, and then join them together using a manifest.
The Kaniko plugin takes care of all these things for you and allows you to add that
functionality to your build:

In your `deps.edn`, you need to include the plugin:
```clojure
;; deps.edn
{:deps {com.monkeyci/plugin-kaniko {:mvn/version "..."}}} ; Specify version
```

Then, in your `build.clj`, require the namespace and add the jobs:
```clojure
(require '[monkey.ci.plugin.kaniko :as kaniko])

;; Other build jobs defined here...

;; Add multiple jobs to the build in order to create the image
(def image-jobs
  (kaniko/multi-platform-image
    {:target-img "docker.io/my-repo/my-img:latest"
     :archs [:arm :amd]}))

;; All jobs to run
[my-custom-jobs ; Define your own
 image-jobs]
```

That's it!  **You have just used a plugin** in your build, and saved yourself a lot of
time having to figure out yourself how to build an image!  *MonkeyCI* will take care of
fetching the library and executing the functionality in order to add the jobs to your
build.  Note that this is a basic example, you'll have to check out this specific
[plugin documentation](https://github.com/monkey-projects/plugin-kaniko) for more details.

Because plugins are no more than regular libraries, you can use any existing tools you
like to create and publish them.  And use *MonkeyCI* to build them, of course.

At the time of writing, several plugins already exist, and more are being created.

 - [clj](https://github.com/monkey-projects/plugin-clj), to build Clojure code.
 - [github](https://github.com/monkey-projects/plugin-github) to create releases in [Github](https://github.com).
 - [kaniko](https://github.com/monkey-projects/plugin-kaniko), as used in the above example.
 - [pushover](https://github.com/monkey-projects/plugin-pushover) to publish notifications to [Pushover](https://pushover.net).
 - [junit](https://github.com/monkey-projects/plugin-junit) to parse [JUnit](https://junit.org) output files and make the results viewable.
 - ...

## Extensions

Next to the plain functions you can invoke, *MonkeyCI* also has a concept of **extensions**.
These are ways to perform **actions before and/or after a job**.  A plugin can define an
extension (or even multiple), which are then automatically applied when they are `require`d
in the build script.  An extension can define a `before` or `after` function, which can
influence the way a job is run or take some actions when it is run.  Extensions are typically
used to do something with a job result.

The [JUnit plugin](https://github.com/monkey-projects/plugin-junit) is a good example of this.
When a job is running unit tests that publish their result in JUnit format (which is an `xml`
file), you can apply the extension to parse those results and put them in the job result.
*MonkeyCI* is able to process several kinds of information in the results, and show them
to the user in the application.  In this case, unit test results: time spent, errors, warnings,
etc...

Again, it depends on the plugin itself, but usually you will need to provide some additional
information in your build configuration that the plugin requires.  Each extension is "linked" to
some **configuration key**.  *MonkeyCI* will try to look up the appropriate extension for the
configuration key and then invoke it's before or after step.  In case of the JUnit plugin, it
is linked to the `:junit` configuration key, where it expects to find the id of the
[artifact](artifacts/) holding the `junit.xml` file, and it's path therein.

For example:
```clojure
;; Register the extension by including the namespace
(use 'monkey.ci.ext.junit)

;; Activate it in the job
(def test-job
  (container-job
    "unit-test"
    {:image "docker.io/maven:latest"
     :script ["mvn verify"]
     ;; Make sure to store the test results as artifact
     :save-artifacts [{:id "test-results"
                       :path "target/junit.xml"}]
     ;; Configure junit extension so it can find the artifact
     :junit {:artifact-id "test-results"
             :path "junit.xml"}}))
```
Because the extension is **run after the container has executed**, it can't access the file
directly.  Rather, it has to download it from an artifact.  That's why in this example
the `junit.xml` file is referred twice: once in the artifact configuration, and again
in the junit extension configuration.  This is however an implementation detail: the
plugin has full **access to the job details**, so it may just as well inspect the artifacts,
download them all, and try to find the necessary files automatically.  But the `:junit`
configuration key is still necessary in order to "activate" the extension.