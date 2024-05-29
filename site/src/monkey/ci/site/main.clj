(ns monkey.ci.site.main
  (:require [hiccup2.core :as h]
            [monkey.ci.site.utils :as u]))

(defn stylesheet [url]
  [:link {:rel "stylesheet" :href url}])

(defn script [file]
  [:script {:src (str "./js/" file)}])

(def head
  [:head
   [:meta {:charset "utf-8"}]
   [:meta
    {:name "viewport",
     :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
   [:title "MonkeyCI"]
   (stylesheet "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap")
   (stylesheet "./css/vendor.min.css")
   (stylesheet "./css/theme.min.css?v=1.0")
   (stylesheet "https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css")])

(def header
  [:header#header.navbar.navbar-expand-lg.navbar-end.navbar-absolute-top.navbar-show-hide.navbar-dark
   [:div.container
    [:nav.js-mega-menu.navbar-nav-wrap
     ;; Logo
     [:a.navbar-brand
      {:href "./index.html"
       :aria-label "MonkeyCI"}
      [:h3.text-white
       [:img.navbar-brand-logo
        {:src "./img/logo-bw-small.png"
         :alt "Logo"}]
       "MonkeyCI"]]
     [:div.navbar-absolute-top-scroller
      [:ul.navbar-nav
       ;; Log in button
       [:li.nav-item
        [:a.js-animation-link.btn.btn-ghost-light.btn-no-focus.me-2.me-lg-0
         {:href "https://app.monkeyci.com"
          :role "button"}
         "Log in"]]
       ;; Sign up
       [:li.nav-item
        [:a.js-animation-link.d-none.d-lg-inline-block.btn.btn-primary
         {:href "https://app.monkeyci.com"
          :role "button"}
         [:i.bi.bi-person-circle.me-1]
         "Sign up"]]]]]]])

(def input-card
  [:div.text-center.mx-auto.mb-7
   {:style "max-width: 32rem;"}
   ;; Input Card
   [:form
    [:div.input-card.input-card-sm.mb-3
     [:div.input-card-form
      [:label.form-label.visually-hidden
       {:for "requestDemoForm"}
       "Enter business email"]
      [:input.form-control.form-control-lg
       {:type "text"
        :id "requestDemoForm"
        :placeholder "Enter business email"
        :aria-label "Enter business email"}]]
     [:button.btn.btn-primary.btn-lg
      {:type "button"}
      "Get notified"]]]
   [:a.link.link-light
    {:href "https://app.monkeyci.com"}
    "Create a free account"
    [:i.bi.bi-chevron-right.small.ms-1]]])

(def code-fragment
  (letfn [(p [s]
            [:span.text-primary s])
          (c [s]
            [:span.text-muted s])
          (k [s]
            [:span.text-danger s])]
    (u/code-editor
     {:style "width: 46rem;"}
     [[:span "(" (p "ns") " build-script"]
      [:span.ps-3 "(" (k ":require") " [monkey.ci.build.core " (k ":as") " bc]))"]
      ""
      [:span "(" (p "def") " unit-test"]
      [:span.ps-3 "(bc/container-job"]
      [:span.ps-6 "\"unit-test\""]
      [:span.ps-6 "{" (k ":image") " \"docker.io/maven:4.5\""]
      [:span.ps-7 (k ":script") " [\"mvn verify\"]"]
      ""
      (c ";; The jobs to execute")
      "[unit-test]"])))

(def shape
  [:div.shape-container
   [:div.shape.shape-bottom.zi-1
    [:svg {:viewbox "0 0 3000 600"
           :fill "none"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path {:d "M0 600V350.234L3000 0V600H0Z"
             :fill "#fff"}]]]])

(def clients
  [:div.container.content-space-b-1.content-space-b-md-3
   [:div.w-lg-65.text-center.mx-lg-auto
    [:div.mb-4
     [:h5 "Built by Developers for Developers"]]]])

(defn- feature [img title desc]
  [:div.col-md-6.mb-3.mb-md-7
   [:div.d-sm-flex
    [:div.flex-shrink-0.mb-3.mb-sm-0
     [:img.avatar.avatar-xxl.avatar-4x3
      {:src img}]]
    [:div.flex-grow-1.ms-sm-5
     [:span.text-cap "Features"]
     [:h5 title]
     [:p desc]]]])

(def features
  [:div.container.content-space-1.content-space-md-3
   [:div.row
    (feature "svg/oc-maintenance.svg"
             "Builds as Code"
             "Treat builds as small apps in their own right, with all features that code brings.")
    (feature "svg/oc-to-do.svg"
             "Reduce Debugging Time"
             "Write unit-tests for your build scripts to avoid production issues.")]
   [:div.row
    (feature "svg/oc-collaboration.svg"
             "Easy Extensibility"
             "Include open-source libraries in your builds to expand functionality.")
    (feature "svg/oc-on-the-go.svg"
             "Deploy Without Danger"
             "Simulate builds locally or in unit tests and avoid problems when deploying your app.")]])

(def call-to-action
  [:div.bg-soft-primary-light
   [:div.container.content-space-1.content-space-md-3
    [:div.row
     [:div.col-md-5.col-lg-6.mb-5.mb-lg-0
      [:h3 "Ready to get started?"]
      [:p.fs-6 "Create a new account to start using the free plan.  No credit card required!"]]
     [:div.col-md-7.col-lg-6
      [:a.btn.btn-primary.btn-lg
       {:href "https://app.monkeyci.com"}
       "Go To Application"]]]]])

(def content
  "Main page content"
  [:main#content {:role "main"}
   [:div.overflow-hidden
    [:div.bg-primary-dark
     [:div.container.position-relative.zi-2.content-space-b-1.content-space-t-2.content-space-md-3
      ;; Heading
      [:div.w-lg-75.text-center.mx-lg-auto.mb-7
       [:h1.display-3.text-white.mb-md-5
        "The powerful" [:span.ms-2.text-warning "CI/CD pipeline"]]
       [:p.lead.text-white-70
        "A" [:b.mx-1 "no-nonsense"] "CI/CD platform that gives you" [:b.mx-1 "full control"]
        "over your build.  Harness the" [:b.mx-1 "power and flexibility"] "of code to deploy applications."]]
      input-card
      code-fragment]]
    shape
    clients]
   [:div.border-top.mx-auto {:style "max-width: 25rem;"}]
   features
   call-to-action])

(def copyright (h/raw "&#169;"))

(def footer
  [:footer.bg-primary-dark.border-top.border-white-10
   [:div.container
    [:div.border-top.border-white-10]
    [:div.row.align-items-md-end.py-5
     [:div.col-md.mb-3.mb-md-0
      [:p.text-white.mb-0
       copyright " 2024 " [:a.text-white {:href "https://www.monkey-projects.be"} "Monkey Projects BV"]]]]]])

(defn main []
  [:html
   head
   [:body
    header
    content
    footer
    (script "vendor.min.js")
    (script "theme.min.js")]])
