# MonkeyCI Websites

This repository contains the files for the [MonkeyCI website](https://www.monkeyci.com)
and related sites, like [the blog](https://www.monkeyci.com/blog).  Parts of these are
just static `HTML` files, where others are generated from markdown files using [Cryogen](http://cryogenweb.org/)
or just using plain [Hiccup](https://github.com/weavejester/hiccup).

Currently this repository contains these sites:

  - Static [Monkey Projects website](https://www.monkey-projects.be)
  - Static [MonkeyCI website](https://www.monkeyci.com)
  - [Documentation site](https://docs.monkeyci.com), using [Cryogen](https://cryogenweb.org)

The [MonkeyCI application](https://app.monkeyci.com) can be found in [another
repository](https://github.com/monkey-projects/monkeyci).

## Theme

Since we're aiming for all the *MonkeyCI*-related sites to use the same theme, we
have also added a common library called `site-template`, which can be included
in the build for the static sites above, and also in the MonkeyCI GUI site.  It
provides commonly used components that are rendered in hiccup or reagent.

## Building

The CI/CD build for the site is on
[MonkeyCI](https://app.monkeyci.com/c/kKDOAlkrk8S6xNNTeHnHmtAm/r/bzzhuxE5nHGsEQweoUSWd1GT) itself.
It generates any static files, adds the public files and builds an [Nginx](https://nginx.org/)
container to host it.  The infrastructure project uses [ArgoCD](https://argo-cd.readthedocs.io/)
to automatically redeploy any new versions.  To this end the build script pushes an update
to the infra repo on Github with the new version.

## License

Copyright (c) 2024 by [Monkey Projects BV](https://www.monkey-projects.be).

[GPLv3 license](LICENSE)