{:title "Supported Platforms"
 :category :getting-started
 :index 120
 :related [["builds" "Builds"]
           ["repos" "Repositories"]]}

*MonkeyCI* tries to support the most popular platforms, and we plan to add more.
Currently we support these `git` repository hosts:

 - [Github](https://github.com)
 - [BitBucket](https://bitbucket.org)
 - [Codeberg](https://codeberg.org) *(partially supported)*

In the short term, we are also planning to add support for [Gitlab](https://gitlab.com)
and expand Codeberg support.

## Github

*MonkeyCI* interfaces with Github using a Github app.  In order to be able to add
your repos to *MonkeyCI*, you first have to [install the MonkeyCI app](https://github.com/apps/monkeyci-app)
in your organization.  This requires just a few clicks.  The app requires minimal permissions,
in order to be able to **list your repositories and install a webhook in them**.  This webhook
is then called by Github to notify *MonkeyCI* whenever a push is performed on a registered
repository.

So if you want to use *MonkeyCI* with Github, please [install the MonkeyCI app](https://github.com/apps/monkeyci-app/installations/new) first.

## Bitbucket

Bitbucket does not support apps like Github does, so instead *MonkeyCI* requires the
permissions to install webhooks when you authenticate.  From then on, you should be able
to start watching [Bitbucket repos](repos) and *MonkeyCI* will **automatically manage the
webhooks for you**.

On each push to a registered repository, Bitbucket will notify *MonkeyCI* using these
webhooks.  Note that Bitbucket does not send changed file information along in the
webhook call, so *MonkeyCI* is currently not able to determine that information, but
we're working on that!  This means that the various [changeset tests](commit-changes),
like `added?`, `removed?`, `modified?` and `touched?` will **always return false**
for Bitbucket builds, until we resolve this.

## Codeberg

[Codeberg](https://codeberg.org) is a free Europe-based Git server, ideally suited for
open source projects.  In order to use it from *MonkeyCI*, you can add your [repositories](repos)
manually.  You will need to create [webhooks](triggers) manually for now.  We're planning
on adding an automatic integration for that in the future!  When you add a webhook, just
use the **forgejo** style.  This is compatible with Github, which is already supported
by *MonkeyCI*.  Have it point to the url as returned when you create the webhook and
make sure to specify the correct secret.  From then on your build will be [triggered](triggers)
**whenever you push to your Codeberg repository**.

## Other Platforms

We are constantly working on adding more platforms.  If your platform is not supported,
please [create an issue](https://github.com/monkey-projects/monkeyci/issues) (assuming
one does not already exist) and as soon as we have time, we will take care of it.  Of
course, you're always welcome to **provide a PR** of your own.