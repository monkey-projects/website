{:title "Pricing"}

[MonkeyCI](https://monkeyci.com) is free to use up to a certain level.  We strive
to allow our users to be able to use it for non-commercial projects.  Every user
can [create one customer](/pages/customer-new) for free.  And each customer receives
**5.000 credits per month** to spend on their builds.

## Credits

How are credits used?  Credits are **consumed by CPU minutes, memory and storage**.
How many credits are required per minute for a build is determined by the amount
of memory you allocate, the number of CPU's, which architecture you use (ARM or AMD)
and how many data you want to store in [caches](/pages/caching) and [artifacts](/pages/artifacts).

This table gives you a basic overview:

<table class="table table-bordered">
  <thead>
    <tr>
      <th>Resource</th>
      <th>Credits</th>
      <th>Default</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>CPU</td>
      <td>1/minute for ARM, 2/minute for AMD</td>
      <td>1 ARM</td>
    </tr>
    <tr>
      <td>Memory</td>
      <td>1 per GB per minute</td>
      <td>2 GB</td>
    </tr>
    <tr>
      <td>Persistent storage</td>
      <td>1 per GB per month</td>
      <td>(None)</td>
    </tr>
  </tbody>
</table>

So for example, if you have a build that runs for 5 minutes using the default settings
(1 CPU, 2 GB RAM), it would cost you `5 * (1 + 2) = 15` credits.

## Why is ARM cheaper than AMD?

Why do we charge twice the price for AMD CPU's?  Because AMD provides two cores
in their CPU, so you have essentially **double the computing capacity**.  Unless you
[explicitly override](/pages/jobs) the CPU type, *MonkeyCI* will use ARM CPU's.
This makes your builds cheaper and they also consume [less energy](/pages/sustainability).

## Commercial Use

*MonkeyCI* is only **free for non-commercial use**.  Should you want to use it for
commercial purposes (e.g. to build non-free software), you are kindly asked to
switch to a paying plan.  You will **still receive the free 5.000 credits** per month,
and you receive an **additional 2.000 credits per paying user**.