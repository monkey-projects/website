{:title "API keys"
 :category :orgs
 :index 50
 :related [["local-builds" "Local builds"]
           ["cli" "Command-line interface"]
	   ["platforms" "Supported platforms"]]}

When you use the user interface, it accesses the *MonkeyCI* REST API using a
token that is generated when you authenticate using one of the [supported
platforms](platforms).  However, when you're using the [command line interface](cli),
or want to access the API using a script, you usually do not have a token available.

For this, you can create *API keys*.  These are tokens that are **linked to an
organization or a user**.

## Organization Keys

Organization keys can access **one single organization**.  They are useful for
automated scripts, or maybe for running [local builds](local-builds).

In order to create an organization key, navigate to the **organization settings
&gt; API keys** screen.  There you can manage the available keys.  Each key can
have a description and an optional valid-until date.  If specified, the token will
not be usable after that date (including it).  When creating the key, you will
be able to copy the token value.  **Note that you only have one chance** to copy
the value.  Afterwards, it cannot be retrieved, since it is stored in a hashed
manner.  Should you ever lose the token, you will need to recreate the key, so
guard it well.

For safety reasons, we do advise to **set an expiration date on the key**, and
refresh it regularly.  Should it ever fall into the wrong hands, its use will be
limited.

## User Keys

User keys are linked to a specific user, and keys generated this way have the
same access level as the user.  This means that they can access any organization
[as the user](org-join).

This has **currently not been implemented** in the user interface, but we are working
on it.

## Using API Keys

In order to use an API key, you can either pass it on the command line, or specify
it in a configuration file.

On the command line:
```shell
$ monkeyci build --api-key <key-value> ...
```

In a config file:
```yaml
account:
  token: <key-value>
```

See the [CLI configuration section](cli) for more details.