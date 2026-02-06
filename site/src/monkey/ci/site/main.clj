(ns monkey.ci.site.main
  (:require [monkey.ci.site
             [template :as t]
             [utils :as u]]
            [monkey.ci.template.components :as cc]))

(def input-card
  [:div.text-center.mx-auto.mb-7
   {:style "max-width: 32rem;"}
   ;; Input Card
   [:form#register-form
    [:div.input-card.input-card-sm.mb-3
     [:div.input-card-form
      [:label.form-label.visually-hidden
       {:for "subscribe-email"}
       "Enter email"]
      [:input.form-control.form-control-lg
       {:type "text"
        :id "subscribe-email"
        :placeholder "Enter email"
        :aria-label "Enter email"}]]
     [:button.btn.btn-primary.btn-lg
      {:type "submit"}
      [:i.bi.bi-envelope-at.me-1] "Get Notified"]]
    [:span.small.text-white-50
     "By submitting your email you agree that we may send you updates about MonkeyCI.  "
     "You can unsubscribe at any time."]]
   [:div#registration-ok.text-white.d-none
    "Thanks!  We'll email you with updates from now on."]
   [:div#registration-failed.text-white.d-none
    [:b.me-2 "Whoops, something went wrong."] "Please try again later."]
   [:div.mt-1
    [:a.link.link-light
     {:href "https://app.monkeyci.com"}
     "Sign up for free"
     [:i.bi.bi-chevron-right.small.ms-1]]]])

(def code-fragment
  (letfn [(p [s]
            [:span.text-primary s])
          (c [s]
            [:span.text-muted s])
          (k [s]
            [:span.text-danger s])]
    (u/code-editor
     {:style "width: 46rem;"}
     ;; TODO Auto code formatting
     [[:span "(" (p "ns") " build"]
      [:span.ps-3 "(" (k ":require") " [monkey.ci.api " (k ":as") " m]))"]
      ""
      [:span "(" (p "def") " unit-test"]
      [:span.ps-3 "(-> (m/container-job " (k "\"unit-test\"") ")"]
      [:span.ps-8 "(m/image " (k "\"docker.io/maven:4.5\"") ")"]
      [:span.ps-8 "(m/script [" (k "\"mvn verify\"") "])))"]
      ""
      (c ";; The jobs to execute")
      "[unit-test]"])))

(def clients
  [:div.container.content-space-b-1.content-space-b-md-3
   [:div.w-lg-65.text-center.mx-lg-auto
    [:div.mb-4
     [:h5 "Supports the Major Repository Providers and Platforms"]]
    [:div.row.py-3
     [:div.col.text-center
      [:h4.text-primary [:i.bi.bi-github.me-1]
       [:a {:href "https://github.com"
            :target :_blank}
        "Github"]]]
     [:div.col.text-center
      [:h4.text-primary
       [:a {:href "https://bitbucket.org"
            :target :_blank}
        "Bitbucket"]]]
     [:div.col.text-center
      [:h4.text-primary
       [:a {:href "https://codeberg.org"
            :target :_blank}
        "Codeberg"]]]]]])

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

(defn mockups [config]
  [:div.bg-soft-primary-light {:style {:padding-bottom "20em"}}
   [:div.container.content-space-t-1.content-space-t-md-3
    [:div.w-lg-65.text-center.mx-lg-auto.mb-7
     [:h3 "Built by Developers for Developers"]
     [:p.fs-6
      "We use" [:a.mx-1 {:href "https://clojure.org"} "Clojure"]
      "to enable you to generate build scripts super-fast."
      [:br]
      "Check out our" [:a.mx-1 {:href (cc/docs-url config)} "docs"]
      "for example code and more details on how we do this."]]
    [:div.d-grid.d-sm-flex.justify-content-sm-center.gap-3.mb-7
     [:a.btn.btn-primary {:href (cc/docs-url config) :target :_blank} "Explore our Documentation"]]]])

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
       "Go To Application" [:i.bi.bi-chevron-right.ms-1]]]]]])

(def shape-2
  [:div.shape-container
   [:div.shape.shape-bottom.zi-3
    [:svg {:width 3000
           :height 500
           :viewbox "0 0 3000 500"
           :fill "none"
           :xmlns "http://www.w3.org/2000/svg"}
     [:path {:d "M0 500H3000V0L0 500Z"
             :fill "#fff"}]]]])

(def feature-1
  [:div.d-flex
   [:div.flex-shrink-0
    [:span.svg-icon.svg-icon-sm.text-primary
     [:svg {:width 24 :height 24 :viewbox "0 0 24 24" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
      [:path {:opacity "0.3"
              :d "M7 20.5L2 17.6V11.8L7 8.90002L12 11.8V17.6L7 20.5ZM21 20.8V18.5L19 17.3L17 18.5V20.8L19 22L21 20.8Z"
              :fill "#035A4B"}]
      [:path {:d "M22 14.1V6L15 2L8 6V14.1L15 18.2L22 14.1Z"
              :fill "#035A4B"}]]]]
   [:div.flex-grow-1.ms-4
    [:h5 "Use libraries from any Maven repository"]
    [:p
     "Publish your own libraries and use them in your scripts, or use already existing libs "
     "to kick-start your builds."]]])

(def feature-2
  [:div.d-flex
   [:div.flex-shrink-0
    [:span.svg-icon.svg-icon-sm.text-primary
     [:svg {:width 24 :height 24 :viewbox "0 0 24 24" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
      [:path {:fill-rule "evenodd"
              :clip-rule "evenodd"
              :d "M15 19.5229C15 20.265 15.9624 20.5564 16.374 19.9389L22.2227 11.166C22.5549 10.6676 22.1976 10 21.5986 10H17V4.47708C17 3.73503 16.0376 3.44363 15.626 4.06106L9.77735 12.834C9.44507 13.3324 9.80237 14 10.4014 14H15V19.5229Z"
              :fill "#035A4B"}]
      [:path {:opacity "0.3"
              :fill-rule "evenodd"
              :clip-rule "evenodd"
              :d "M3 6.5C3 5.67157 3.67157 5 4.5 5H9.5C10.3284 5 11 5.67157 11 6.5C11 7.32843 10.3284 8 9.5 8H4.5C3.67157 8 3 7.32843 3 6.5ZM3 18.5C3 17.6716 3.67157 17 4.5 17H9.5C10.3284 17 11 17.6716 11 18.5C11 19.3284 10.3284 20 9.5 20H4.5C3.67157 20 3 19.3284 3 18.5ZM2.5 11C1.67157 11 1 11.6716 1 12.5C1 13.3284 1.67157 14 2.5 14H6.5C7.32843 14 8 13.3284 8 12.5C8 11.6716 7.32843 11 6.5 11H2.5Z"
              :fill "#035A4B"}]]]]
   [:div.flex-grow-1.ms-4
    [:h5 "Zero Impact"]
    [:p
     "By only provisioning infrastructure as needed and using low-emission datacenters, we aim for a "
     "zero-impact experience.  Any impact we do make is offset by donating to carbon-compensating "
     "organizations."]]])

(def code-fragment-2
  (letfn [(p [s]
            [:span.text-primary s])
          (c [s]
            [:span.text-muted s])
          (k [s]
            [:span.text-danger s])]
    (u/code-editor
     {:class "overflow-hidden"}
     [[:span "(" (k "require") " '[monkey.ci.plugin.clj " (k ":as") " clj]))"]
      ""
      (c ";; Contains both test and publish jobs")
      "(clj/deps-library)"])))

(def features-expanded
  [:div.overflow-hidden
   [:div.container.content-space-1.content-space-b-md-3
    [:div.row.align-items-lg-center
     [:div.col-lg-6.mb-5.mb-lg-0
      [:div.pe-lg-5
       [:div.mb-7
        [:h3 "Compact build scripts"]
        [:p.fs-6
         "Get rid of huge unmaintainable build scripts by using common libraries to make your scripts more compact. "
         [:b "MonkeyCI"] " provides the means to easily achieve this using existing and battle-tested systems."]]
       [:div.d-grid.gap-3
        feature-1
        feature-2]]]
     [:div.col-lg-6
      [:div.tab-content.mb-7
       [:div.tab-pane.fade.show.active
        code-fragment-2]]]]]])

(defn content [config]
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
    t/shape-1
    clients]
   [:div.border-top.mx-auto {:style "max-width: 25rem;"}]
   features
   (mockups config)
   shape-2
   features-expanded
   call-to-action])

(defn main [config]
  (t/wrap-template config
                   (t/header config)
                   (content config)))
