(ns monkey.ci.template.plans)

(def plans
  "Configuration for the various subscription plans (including free plan)"
  [{:amount 0
    :title "Basic"
    :summary "For private use or to try it out."
    :features ["1 organization with 1 user"
               "1.000 monthly credits"
               "Unlimited repos"
               "Access to all plugins"
               "Public and private repo's"]
    :footer "No credit card required."}
   
   {:amount 5
    :title "Startup"
    :summary "For starting businesses."
    :features ["1 organization with 3 users *"
               "5.000 monthly credits"
               "Unlimited repos"
               "Access to all plugins"
               "Public and private repo's"
               "Email support"
               "For commercial purposes"]
    :footer "Cancel anytime"}
   
   {:amount 30
    :title "Professional"
    :summary "For enterprises with large teams."
    :features ["1 organization with unlimited users *"
               "30.000 monthly credits"
               "Unlimited repos"
               "Access to all plugins"
               "Public and private repo's"
               "Email + Slack support"
               "For commercial purposes"]
    :footer "Cancel anytime"}])
