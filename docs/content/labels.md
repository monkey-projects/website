{:title "Labels"
 :related [["params" "Build parameters"]
           ["customer" "Customers"]
	   ["ssh-keys" "SSH keys"]]}

Labels are used extensively throughout *MonkeyCI* to group several kinds of objects
for various purposes.  The main applications for labels are:

 - To allow users to group [repositories](repos) for displaying.
 - To determine which [builds](builds) have access to which [parameters](params).
 - To assign [SSH keys](ssh-keys) when checking out code.

## Group Repositories

Over time, the number of repositories that a customer has can become very large.
In order to allow for users to be able to have an overview over these repositories,
the user interface allows to group them according to their assigned labels.  The
*group by* dropdown in the repositories overview screen will display any of the
defined labels.  If you select one of the labels, the repositories will be grouped
according to their values.

## Access to Build Parameters

As described in the [build parameters](params) page, builds often need some kind of
parameterization. In order to determine **which builds have access to which parameters**,
labels are used.  The **label filter** which you can change in the parameter editing
screen is used for this purpose.  Builds for repositories that match the filter are
**granted access** to the parameters when they retrieve them through the API.

Depending on the kind of filter, builds can see the parameters if all of them match
(when an `AND` is used), or any of them (in case of an `OR`).  You can also combine
these.

It is possible to have multiple parameters with the same name configured.  This **should
be avoided** however, because in that case the results are undefined.  It is advised to
declare your labels accordingly to avoid such collisions.

## Assign SSH Keys

Sometimes a repository is private, and it can only be checked out **if a matching SSH key
is provided**.  In order to be able to build from these repositories, *MonkeyCI* allows you
to configure [SSH keys](ssh-keys) on the customer level.  Builds for repositories with
the **matching labels**, similar to build parameters as described above, are assigned those
keys when checking out the code.

## Defining Labels

Defining and assigning labels to repositories is easy.  All you need to do is **edit
the repository** in question, and in the "labels" section add the labels and their
values as you see fit.  You can use **any text** as both the label and the value, but
we advise to keep them as short and descriptive as possible.  After saving the repository,
the labels are assigned to it and will be used by *MonkeyCI* for any of the operations
described above.

Note that both labels and values are **treated case-sensitive** by the application.