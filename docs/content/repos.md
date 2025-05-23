{:title "Repositories"
 :category :orgs}

Repositories are the central organization entity of *MonkeyCI*.  Repositories
(or *repos* in short) are linked to a [organization](orgs), but also to
some **external Git repository**.  This repository could be hosted anywhere, but
it will most likely be one of the supported platforms ([Github](https://github.com),
[BitBucket](https://bitbucket.org)).

Repositories also can have [labels](labels) attached to them.  These
labels are used to **organize the repos**, but also to give [builds](builds)
for these repos access to various [parameters](params).  The same label
can be assigned multiple times, with different values.  This allows you **a lot of
flexibility** to group your repos.

Builds are run for one specific repository, and when navigating to the repo, you
get an **overview of all builds** that have been executed for that repo.  By default,
they are sorted most-recent-first, but you can change that by clicking on the table
column headers.