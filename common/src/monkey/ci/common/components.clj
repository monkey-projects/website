(ns monkey.ci.common.components
  (:require [hiccup2.core :as h]))

(defn- make-url [{:keys [prefix suffix]}
                 {:keys [base-url]
                  :or {base-url "monkeyci.com"}}]
  (cond-> (format "https://%s.%s" prefix base-url)
    suffix (str suffix)))

(def app-url (partial make-url {:prefix "app"}))
(def docs-url (partial make-url {:prefix "docs"}))
(def api-url (some-fn :api-url (partial make-url {:prefix "api" :suffix "/v1"})))

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

(defn header [config]
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
         {:href (str (app-url config) "/login")
          :role "button"}
         "Log in"]]
       ;; Sign up
       [:li.nav-item
        [:a.js-animation-link.d-none.d-lg-inline-block.btn.btn-primary
         {:href (app-url config)
          :role "button"}
         [:i.bi.bi-person-circle.me-1]
         "Sign up"]]]]]]])

(def copyright (h/raw "&#169;"))

(def footer
  [:footer.bg-primary-dark.border-top.border-white-10
   [:div.container
    [:div.border-top.border-white-10]
    [:div.row.align-items-md-end.py-5
     [:div.col-md.mb-3.mb-md-0
      [:p.text-white.mb-0
       copyright " 2024 " [:a.link.link-light {:href "https://www.monkey-projects.be"} "Monkey Projects BV"]]]]]])
