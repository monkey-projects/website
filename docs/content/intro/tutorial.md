{:title "Tutorial"
 :category :getting-started
 :index 25
 :related [["intro/basic-example" "A basic example"]
           ["intro/useful-example" "A more useful example"]]}

Get started using *MonkeyCI* by following this tutorial.  It explains:

 - Setting up your [first repo](repos)
 - Writing the first [build script](builds)
 - Running it locally using the [CLI](cli)
 - Registering a [new account](registration)
 - Automatically [build it online](triggers)
 - Make your script more powerful by [including plugins](plugins)

## Getting Started

You can use *MonkeyCI* without any limitations on your own system if you
install [the CLI](cli).  You can do this by running this script:

```shell
$ wget https://monkeyci-artifacts.s3.fr-par.scw.cloud/install-cli.sh -O - | bash
```

After that, you can run the *MonkeyCI* `cli` by simply running `monkeyci`.  See
the [cli page](cli) for more details.