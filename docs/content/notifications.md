{:title "Notifications"
 :category :extending
 :index 30
 :related [["plugins" "Plugins"]
           ["deps" "3rd party libraries"]]}

Often it is desirable to receive notifications on various build events, for example
when a build is finished, or when a new image or library version has been published.

Since there are **lots of ways to do notifications** to many different tools, *MonkeyCI*
does not provide this in the core application.  Instead we're using **plugins** to do
notifications instead.  This means that there is a lot of flexibility when doing
notifications, depending on the plugins you have included in your build.

Suppose you want to send a [Pushover](https://pushover.net) notification **whenever
a new release has been published**.  In order to do this, you first need to include
the [pushover plugin](https://github.com/monkey-projects/plugin-pushover.git) in
your build by adding it to the `deps.edn`:

```clojure
{:deps {com.monkeyci/plugin-pushover {:mvn/version "0.1.0"}}}
```

Check out [the documentation of the plugin](https://github.com/monkey-projects/plugin-pushover)
for more details, but it provides functionality to add a job to your build that sends
the notification:

```clojure
(ns build
  (:require [monkey.ci.plugin.pushover :as pushover]))

;; Define your jobs here

;; Job list
[...
 (pushover/pushover-msg {:msg "Build finished"})]
```

This is of course a very limited example.  You also need to [set up credentials](params),
and you will probably want to add some [conditions](conditions) as to when the
notification should actually be built.  But it illustrates how you can do build notifications
using *MonkeyCI*.

In essence, these plugins only provide a thin layer that translates the information
provided by *MonkeyCI* into an [action job](jobs) that invokes the underlying notification
API.  Currently there is an [officially supported plugin for
Pushover](https://github.com/monkey-projects/plugin-pushover),
but we are working on adding more, such as e-mail or Slack.  People are of course free to
**write their own plugins** and make them available to the world.