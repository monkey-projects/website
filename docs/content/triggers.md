{:title "Build triggers"
 :category :builds
 :index 40}

In order for a [build](builds) to run, it must first be **triggered**.  *MonkeyCI* has
several ways to do this:

 1. Start it manually via the UI.
 2. Start it via the API.
 3. Start it via a public trigger endpoint.

## Manual Triggers

Now, steps 1 and 2 are basically the same, since the UI eventually calls the API,
with the token that is created when the user authenticates.  If you have such a
[token](api_tokens), you could also trigger a build using tools like `cURL`.  If
you would like to trigger a build manually, you can navigate to the [repository
screen](repos) and then press the **Trigger Build** button.  A form is then
displayed where you can specify which branch or tag to run the build from.  On
confirmation, a new build is started.

## External Triggers

But the most often used way to start a build is automatically, on a push to a
git repository.  *MonkeyCI* supports several [platforms](platforms) that can do
this.  See the [platforms page](platforms) for more details on this.  But for
these triggers to work, you need to tell it where to send the trigger to.  That's
where webhooks come in.  These are essentially HTTP endpoints that accept a `POST`
request with details about the push event.  *MonkeyCI* will then use that information
to start a new build.

### Security

Of course, we don't just allow anybody to trigger events as they please.  There
is a **security check** to see whether the caller is actually permitted to trigger builds.
*MonkeyCI* supports [HMAC checks](https://en.wikipedia.org/wiki/HMAC)
on webhook invocations.  For this, a secret key is required to calculate an HMAC
signature, that is passed along as a request header.  *MonkeyCI* then verifies if
the signature matches the content of the request.

When you create a new webhook, a URL along with the secret is generated and stored
in the database.  **Store this secret in a secure place**, because it's not possible
to retrieve it afterwards.  If it's lost, you will need to recreate the webhook.
The API returns this secret on creation, and it's displayed once in the user
interface.

**Depending on the platform** you generated the webhook for, the url is different.
You will need both the url and the secret to configure the webhook in the
platform that will invoke the webhook.  The contents should be put in the
request body as `JSON`.

When the webhook is correctly configured in the client platform, *MonkeyCI*
will start a new build whenever it receives a correctly formatted (and secured) request.