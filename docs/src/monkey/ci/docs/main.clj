(ns monkey.ci.docs.main
  (:require [monkey.ci.template.components :as tc]))

(defn content [config]
  [:main#content {:role "main"}
   [:div.overflow-hidden
    [:div.bg-primary-dark
     [:div.container.position-relative.zi-2.content-space-b-1.content-space-t-2.content-space-md-3
      [:div.card
       [:div.card-body
        [:h1 "Application Documentation"]
        [:p
         "Welcome to" [:a.ms-1 {:href (tc/app-url config)} "MonkeyCI"]
         "!  So you've finally fed up with tinkering in YAML files and have decided that you want to use"
         [:b.mx-1 "real code"] "to run your build pipelines?  Well, this is the place
         where we will explain all about how to do that."]
        [:p
         "Note that this documentation is still a work in progress.  If you think that anything should"
         "be explained more thoroughly or have encountered an error, you can either"
         [:a.mx-1 {:href "https://github.com/monkey-projects/website/issues" :target :_blank} "create an issue"]
         "or" [:a.mx-1 {:href "https://github.com/monkey-projects/website/pulls" :target :_blank} "fix it yourself"]
         "by creating a pull request."]]]]]]])

(defn main [config]
  [:html
   tc/head
   [:body
    (tc/header config)
    (content config)
    (tc/footer config)
    (tc/script "vendor.min.js")
    (tc/script "theme.min.js")
    (tc/script "site.js")]])
