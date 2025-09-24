{:title "SSH keys"
 :category :builds
 :index 130
 :related [["params" "Parameters"]
           ["security" "Security"]
	   ["labels" "Labels"]]}

If you are working on open source projects, the git repositories that
contain the code will usually be public.  But sometimes you also wish to
enable builds on **private repositories**.  These can only be accessed
if an **SSH key** is provided when pulling the code.

SSH keys are configured on the organization level, similar to [build
parameters](params).  They consist of a **public and private** key,
of which the private part is stored [encrypted](security).

## Configuration

Configuring SSH keys is done at organization level.  Click the "Settings"
button in the organization screen, then go to "SSH Keys".

![ssh keys](/img/ssh-keys-1.png "screenshot")

You need to enter the public and private key, and can also add an optional
description.  [Labels](labels) are used to determine which repository
builds gain access to the keys.

## Usage

Repositories automatically use the configured SSH keys that have matching
[labels](labels) with those configured on the repository itself.  A build
can use multiple SSH keys, if more than one matches the labels.  When
cloning the code, *MonkeyCI* will **check each of the keys** to gain access
to the remote repository.

If no keys give access, then the build will fail with a security exception.