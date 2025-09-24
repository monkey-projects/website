{:title "Create a New Organization"
 :category :orgs
 :index 10
 :related [["org-join" "Join an Existing Organization"]
           ["params" "Build Parameters"]
	   ["ssh-keys" "SSH keys"]
	   ["pricing" "Pricing"]]}

Creating a new organization is the default action you undertake if you
first log in to *MonkeyCI*.  You just have to specify a name, and save
to create the organization.  You are then also the administrator for
the organization, in charge of approving any [join requests](org-join).

If there is only one organization you are a member of, you will be
**automatically redirected** to that organization on your next login.
Organizations are the core entity of *MonkeyCI*: everything is linked
to them, except for users.  Repositories, build parameters, ssh keys,...
They all "belong" to a single organization.  Should the organization be
deleted, then all those entities will also be removed.

**Credits** are also counted on the organization level.  Each organization
receives **1.000 free credits** per month, and more can be purchased as
needed.  When switching to a [paying tier](pricing), the organization
receives more credits depending on the number of registered users.