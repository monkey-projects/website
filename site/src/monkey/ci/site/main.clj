(ns monkey.ci.site.main)

(defn stylesheet [url]
  [:link {:rel "stylesheet" :href url}])

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

(def content
  [:main#content {:role "main"}
   [:div.overflow-hidden
    [:div.bg-primary-dark
     [:div.container.position-relative.zi-2.content-space-b-1.content-space-t-2.content-space-md-3
      ;; Heading
      [:div.w-lg-75.text-center.mx-lg-auto.mb-7
       [:h1.display-3.text-white.mb-md-5
        "The powerful" [:span.ms-1.text-warning "CI/CD pipeline"]]
       [:p.lead.text-white-70
        "A" [:b.mx-1 "no-nonsense"] "CI/CD platform that gives you" [:b.mx-1 "full control"]
        "over your build.  Harness the" [:b.mx-1 "power and flexibility"] "of code to deploy applications."]]
      input-card]]]])

(defn main []
  [:html
   head
   [:body
    header
    content]])