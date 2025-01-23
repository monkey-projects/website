{:title "Security"
 :category :builds
 :related [["params" "Build parameters"]
           ["ssh-keys" "SSH keys"]]}

For any publicly accessible application, security is important.  *MonkeyCI* is no different.
We generally **advise against using any kind of sensitive or private information** in your builds.
Similarly, it's a bad idea to commit that information in your repositories, especially if
they're public.  But we also realize that in order to do decent integration tests or deployments,
it's often unavoidable to put credentials or ssh keys in build parameters.  *MonkeyCI* ensures
that this information is only visible to the appropriate users.  To this end, the [build
parameter values](params) and [ssh keys](ssh-keys) are **encrypted at rest**.  This means they
are stored in an encrypted manner in the database.  It's only when [editing them](params/) or when
using them in a build that the become decrypted.

The encryption key is safely stored in a vault with strict access restrictions.  In addition,
each customer receives a unique **initialization vector**, which is used to ensure that
encrypted values are also unique and that other customer's users are not able to figure
out your sensitive information.  This initialization vector (and of course the encryption
keys) never leave the backend.  They are not transmitted to clients or other parties in
any way.

This ensures that **your private information remains private**.