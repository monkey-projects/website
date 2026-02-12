{:title "Your CI/CD Pipeline Deserves Better Than YAML: Introducing MonkeyCI"
 :summary "I built a CI/CD platform where you write your build scripts in Clojure instead of YAML. It's open source, uses an event-driven architecture for durability, and lets you test your pipelines like any other code. Looking for beta testers from the Clojure community."
 :author "Wout Neirynck"
 :date "2026-02-10"
 :header-img "/img/blog-1.png"}

**TL;DR:** I built a CI/CD platform where you write your build scripts in Clojure instead of YAML. It's open source, uses an event-driven architecture for durability, and lets you test your pipelines like any other code. Looking for beta testers from the Clojure community.

## The YAML Problem We've All Lived Through

You know the drill. Your project starts simple: run tests, maybe publish to [Clojars](https://clojars.org).
A few lines of YAML in `.gitlab-ci.yml` or `.github/workflows/`, and you're done.

Then reality hits:
```yaml
# Six months later...
script:
  - if [ "$CI_COMMIT_BRANCH" == "main" ] && [ "$DEPLOY_ENV" == "prod" ]; then
      if grep -q "major" CHANGELOG.md; then
        VERSION=$(cat VERSION | awk -F. '{$1++; $2=0; $3=0; print $1"."$2"."$3}')
      elif grep -q "minor" CHANGELOG.md; then
        # ... 50 more lines of bash-in-yaml-in-strings
```

We've all been there. You're writing bash scripts inside YAML strings, fighting with indentation, and crossing your fingers that the conditions work. No REPL. No tests. No way to run it locally without Docker gymnastics.
I spent too many hours debugging builds at 3 AM, and every time I thought: *"Why aren't we using an actual programming language for this?"*

## Treating Your Pipeline as Code (Actually)

That's why I built [MonkeyCI](https://monkeyci.com). The core idea is simple: your deployment
pipeline is a program, so write it in a programming language you already know.

Here's what a MonkeyCI build script looks like:

```clojure
(ns build
  (:require [monkey.ci.api :as m]))

(def clojure-img "docker.io/clojure:tools-deps-trixie")

(def run-tests
  (-> (m/container-job "test")
      (m/image clojure-img)
      (m/script ["clojure -X:test"])))

(def publish
  (-> (m/container-job "publish")
      (m/image clojure-img)
      (m/script ["clojure -X:publish"])
      (m/depends-on "test")))

;; Return a list of jobs (or a fn that returns jobs based on context)
[run-tests
 publish]
```

This is a real Clojure project. The `.monkeyci/` directory in your repo is just a
Clojure project with a deps.edn. You can:
 - Split your build logic across multiple namespaces 
 - Write unit tests for your build functions 
 - Use any library from Clojars 
 - Refactor with your IDE 
 - Run it in a REPL
 
## Two Job Types: The Best of Both Worlds

MonkeyCI supports two kinds of jobs:

**Container Jobs**: Traditional containerized execution for things like Docker builds, database migrations, or running in specific environments.

**Action Jobs**: Pure Clojure functions that run in your build script's JVM. No container
overhead for simple tasks like posting to Slack, updating a database record, or even
coordinating other jobs.
```clojure
(m/action-job
  "notify-slack"
  (fn [ctx]
    (let [url (get (m/build-params ctx) "slack-webhook")]
      (slack/post! url
                   (format "Build %s completed!" (:build-id ctx))))))
```

## Extend As You Like

A great way to reuse code is to use libraries.  MonkeyCI supports this as well!  You can
include any Java or Clojure lib in your script and call its functions, including your own
libs.

First add the dependency to the `.monkeyci/deps.edn`, in this case the
[clj plugin](https://github.com/monkey-projects/plugin-clj):
```clojure
{:deps {com.monkeyci/plugin-clj {:mvn/version "0.4.0"}}}
```

Then your build script can become even simpler:
```clojure
(ns build
  (:require [monkey.ci.plugin.clj :as clj]))

;; Creates both a test and publish job.  In this case with default settings.
(clj/deps-library)
```

Since a build script is just code, you can do just about anything.  The sky is the limit!

## Features That Matter

### Local Reproducibility

Run your builds locally with the CLI:
```bash
monkeyci build run
```

If it works locally, it works remotely. No more "works on my CI" mysteries.

### Real Conditionals
```clojure
(when (= "main" (m/branch ctx))
  (deploy-job))
```

Not if: `${{ github.ref == 'refs/heads/main' }}`. Just Clojure.

### Parallel Execution with Dependencies
```clojure
[(test-frontend)
 (test-backend)
 (deploy-job ["test-frontend" "test-backend"])]  ;; Waits for both tests
```

MonkeyCI builds a DAG and runs jobs in parallel where possible.

## Event-Driven Architecture

Under the hood, MonkeyCI uses an event system ([NATS](https://nats.io) with JetStream) for
durability. If an agent crashes, another picks up. If your infrastructure reboots, jobs
resume. This isn't fire-and-forget; it's designed for reliability.  This is all built on
top of a custom event handling library called [Mailman](https://github.com/monkey-projects/mailman)
(I gave [a talk](https://youtu.be/9Cr-k6rfhzc?si=2TptGJ1m_HfHyBhb) about it at London
Clojurians last year) that lets you declare event handlers like HTTP routes.

## Why Open Source?

MonkeyCI is open source (under the GPLv3).  A few reasons:
 1. **Trust:** You can inspect the code. No black boxes. 
 2. **Quality:** Open source code is better code (or gets better faster). 
 3. **Community:** If you need a feature, you can contribute it. 
 4. **Philosophy:** Information wants to be free.
    
The repo is at [github.com/monkey-projects/monkeyci](https://github.com/monkey-projects/monkeyci).

## Who's This For?

MonkeyCI is probably overkill if you just need to run `lein test` and deploy a JAR. The existing tools may be good enough for you.
But if you've ever:
 - Spent hours debugging complex CI logic 
 - Wished you could test your pipeline locally 
 - Wanted to refactor your build script without fear 
 - Needed more power than YAML provides
 
...then this might be worth a look.

## Try It Out

I'm actively looking for beta testers from the Clojure community. Here's how to get started:
 1. **Check out the [documentation](https://docs.monkeyci.com)**
 2. **Create an account at:** [app.monkeyci.com](https://app.monkeyci.com)
 3. **Connect a repo:** GitHub and BitBucket are fully supported, but any repo that's accessible over the web will work (private ones as well).
 4. **Give feedback:** Issues, PRs, or just DM me on [Clojurians Slack](https://clojurians.slack.com).

Since my main goal is building a community, there is a free tier of 1.000 monthly credits
to spend on your builds.

## Real Talk

MonkeyCI is rough around the edges. Error messages could be better. Documentation could be more complete. Some features are planned but not implemented.

But the core idea works. I've been using it for my own projects for two years. My libraries build on MonkeyCI. It's event-driven and durable. And writing build scripts in Clojure feels right in a way that YAML never did.

If that resonates with you, I'd love your feedback. Even if it's just "this doesn't work" or "I tried to do X and got confused." That's valuable.

## Questions I Expect

**Q: Why not Babashka?**
<br/>
A: The build scripts run as full Clojure via the CLI, not Babashka. This avoids compatibility issues with libraries. MonkeyCI uses some Babashka libraries internally, but your scripts have access to the entire Clojure ecosystem.  You can still use Babashka in container jobs, just like with any other CI/CD tool.

**Q: Can I self-host?**
<br/>
A: It's possible, but this is not my main priority.  In addition to NATS, MonkeyCI also supports in-memory eventing (mostly for dev/testing purposes) and JMS (e.g. [ActiveMQ](https://activemq.apache.org/)).  But setting up your own build agents and connecting them to MonkeyCI is definitely on the roadmap.

**Q: How does pricing work?**
<br/>
A: First I want to make MonkeyCI better before I charge money.  But it will be probably something
along the lines of $5/user/month for basic usage (e.g. startups, teams up to 3 people), and $30/user/month
for larger enterprises (larger teams, more credits).

**Q: What about [Feature X]?**
<br/>
A: [Tell me about it](https://github.com/monkey-projects/monkeyci/issues)! That's why I'm looking for beta testers.

## Links:
 - GitHub: [github.com/monkey-projects/monkeyci](https://github.com/monkey-projects/monkeyci)
 - App: [app.monkeyci.com](https://app.monkeyci.com)
 - Mailman (event lib): [github.com/monkey-projects/mailman](https://github.com/monkey-projects/mailman)

Looking forward to [hearing from the community](https://clojurians.slack.com/team/U014Z1HN0GP). Let's build something better than YAML together.
