(ns monkey.ci.docs.main
  (:require [monkey.ci.template.components :as tc]))

(defn header
  "Renders header with dark background"
  [config]
  [:header#header.navbar.navbar-expand-lg.navbar-end.navbar-light.bg-white
   [:div.container
    [:nav.js-mega-menu.navbar-nav-wrap
     ;; Logo
     [:a.navbar-brand
      {:href "./index.html"
       :aria-label "MonkeyCI"}
      (tc/logo-black config)]
     [:div
      [:h1.display-5 "MonkeyCI"]
      [:p.lead "Documentation Center"]]
     [:div.navbar-absolute-top-scroller
      [:ul.navbar-nav
       [:li.nav-divider]
       ;; Log in button
       [:li.nav-item
        [:a.js-animation-link.btn.btn-ghost-light.btn-no-focus.me-2.me-lg-0.text-primary
         {:href (tc/app-url config "/login")
          :role "button"}
         "Log in"]]
       ;; Sign up
       [:li.nav-item
        (tc/sign-up-btn config)]]]]]])

(defn search-bar []
  [:div.bg-primary-dark.overflow-hidden
   [:div.container.position-relative.content-space-1
    ;; Search form, does nothing for now
    [:div.w-lg-75.mx-lg-auto
     [:form
      [:div.input-card
       [:div.input-card-form
        [:label.form-label.visually-hidden {:for :answers-form}
         "Search for answers"]
        [:input.form-control {:type :text
                              :id :answers-form
                              :placeholder "Search for answers"
                              :aria-label "Search for answers"}]]
       [:button.btn.btn-primary.btn-icon {:type :button}
        [:i.bi-search]]]]]
    [:div.position-absolute
     {:style {:top "-6rem"
              :left "-6rem"}}
     [:img {:src "./svg/shape-1-soft-light.svg"
            :alt "SVG"
            :width 500
            :style {:width "12rem"}}]]
    [:div.position-absolute
     {:style {:bottom "-6rem"
              :right "-7rem"}}
     [:img {:src "./svg/shape-7-soft-light.svg"
            :alt "SVG"
            :width 250}]]]])

(defn content [config]
  [:div.bg-primary-light
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
       "by creating a pull request."]]]]])

(defn main [config]
  [:html
   (tc/head (assoc config :title "MonkeyCI: Documentation"))
   [:body
    (header config)
    [:main {:role :main}
     (search-bar)
     [:div.overflow-hidden
      [:div.d-flex.flex-column.min-vh-100
       [:div.container
        (content config)]
       [:div.mt-auto
        (tc/footer config)]]]]
    (tc/script (tc/script-url config "vendor.min.js"))
    (tc/script (tc/script-url config "theme.min.js"))]])
