(ns monkey.ci.template.components
  (:require [clojure.string :as cs]
            #?(:clj [hiccup2.core :as h]
               :cljs [goog.string :as gstr])
            [monkey.ci.template.icons :as i]))

(defn- make-url [{:keys [prefix suffix]}
                 {:keys [base-url]
                  :or {base-url "monkeyci.com"}}
                 & [path]]
  (cond-> (str "https://" prefix "." base-url)
    suffix (str suffix)
    path (str path)))

(defn assets-url [{:keys [assets-url] :as conf} path]
  (if assets-url
    (str assets-url path)
    (make-url {:prefix "assets"} conf path)))

(def site-url (partial make-url {:prefix "www"}))
(def app-url (partial make-url {:prefix "app"}))
(def docs-url (partial make-url {:prefix "docs"}))
(def api-url (some-fn :api-url (partial make-url {:prefix "api" :suffix "/v1"})))

(defn script-url [config script]
  (assets-url config (str "/js/" script)))

(defn stylesheet [url]
  [:link {:rel "stylesheet" :href url}])

(defn script [url]
  [:script {:src url}])

(defn head [config]
  [:head
   [:meta {:charset "utf-8"}]
   [:meta
    {:name "viewport",
     :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
   [:title (or (:title config) "MonkeyCI")]
   (stylesheet (assets-url config "/css/vendor.min.css"))
   (stylesheet (assets-url config "/css/theme.min.css"))
   (stylesheet (assets-url config "/css/bootstrap-icons.min.css"))])

(defn sign-up-btn [config]
  [:a.js-animation-link.d-none.d-lg-inline-block.btn.btn-primary
   {:href (app-url config)
    :role "button"}
   [:i.bi.bi-person-circle.me-1]
   "Sign up"])

(defn logo-white [config]
  [:img.navbar-brand-logo
   {:src (assets-url config "/img/monkeyci-white.png")
    :alt "Logo"}])

(defn logo-black [config]
  [:img.navbar-brand-logo
   {:src (assets-url config "/img/monkeyci-black.png")
    :alt "Logo"}])

(defn header-dark
  "Renders header with dark background"
  [config]
  [:header#header.navbar.navbar-expand-lg.navbar-end.navbar-absolute-top.navbar-show-hide.navbar-dark
   [:div.container
    [:nav.js-mega-menu.navbar-nav-wrap
     ;; Logo
     [:a.navbar-brand
      {:href "./index.html"
       :aria-label "MonkeyCI"}
      [:h3.text-white
       (logo-white config)
       "MonkeyCI"]]
     [:div.navbar-absolute-top-scroller
      [:ul.navbar-nav
       ;; Log in button
       [:li.nav-item
        [:a.js-animation-link.btn.btn-ghost-light.btn-no-focus.me-2.me-lg-0
         {:href (app-url config "/login")
          :role "button"}
         "Log in"]]
       ;; Sign up
       [:li.nav-item
        (sign-up-btn config)]]]]]])

(def ^:deprecated header header-dark)

(defn header-light
  "Renders generic header with light background"
  [config]
  [:header#header.navbar.navbar-expand-lg.navbar-end.navbar-light.bg-white
   [:div.container
    [:nav.js-mega-menu.navbar-nav-wrap
     ;; Logo
     [:a.navbar-brand
      {:href "/"
       :aria-label "MonkeyCI"}
      (logo-black config)]
     [:div
      [:h1.display-5 "MonkeyCI"]]
     [:div.navbar-absolute-top-scroller
      [:ul.navbar-nav
       [:li.nav-divider]
       ;; Log in button
       [:li.nav-item
        [:a.js-animation-link.btn.btn-ghost-light.btn-no-focus.me-2.me-lg-0.text-primary
         {:href (app-url config "/login")
          :role "button"}
         "Log in"]]
       ;; Sign up
       [:li.nav-item
        (sign-up-btn config)]]]]]])

(def copyright
  #?(:clj (h/raw "&#169;")
     :cljs (gstr/unescapeEntities "&#169;")))

(defn- footer-col [header links]
  (letfn [(footer-link [[lbl url]]
              (let [e? (ext? url)]
                [:li [:a.link-sm.link-light
                      (cond-> {:href url}
                        e? (assoc :target :_blank))
                      lbl
                      (when e?
                        [:small.ms-1 (i/icon :box-arrow-up-right)])]]))
            (ext? [url]
              (and url (cs/starts-with? url "http")))]
      [:div.col-sm.mb-7.mb-sm-0
       [:span.text-cap.text-primary-light header-dark]
       (->> links
            (map footer-link)
            (into [:ul.list-unstyled.list-py-1.mb-0]))]))

(defn- social-link [icon url]
  [:li.list-inline-item
   [:a.btn.btn-icon.btn-sm.btn-soft-light.rounded-circle
    {:href url
     :target :_blank}
    (i/icon icon)]])

(defn footer [config]
  [:footer.footer.bg-primary-dark.border-top.border-white-10
   [:div.container
    [:div.row.content-space-1
     [:div.col-lg-3.mb-5.mb-lg-0
      [:div.mb-5
       [:img {:src (assets-url config "/img/monkeyci-white.png") :width "100px"}]
       [:span.h5.text-light "MonkeyCI"]]]
     (footer-col "Resources"
                 [["Blog" "https://www.monkey-projects.be/blog/"]
                  ["Documentation" (docs-url config)]
                  ["Issues" "https://github.com/monkey-projects/monkeyci/issues"]
                  ["Report an Issue" "https://github.com/monkey-projects/monkeyci/issues/new"]])
     (footer-col "Company"
                 [["About us" (site-url config "/about")]
                  ["Contact" (site-url config "/contact")]])
     (footer-col "Legal"
                 [["Terms of use" (site-url config "/terms-of-use")]
                  ["Privacy policy" (site-url config "/privacy-policy")]])
     [:div.mt-2.border-top.border-white-10]]
    [:div.row.align-items-md-end.py-5
     [:div.col-md.mb-3.mb-md-0
      [:p.text-white.mb-0
       copyright " 2025-2026 " [:a.link-light {:href "https://www.monkey-projects.be"} "Monkey Projects"]]]
     [:div.col-md.d-md-flex.justify-content-md-end
      (when-let [v (:version config)]
        [:p.text-primary-light.mb-0.small.me-2.pt-2 "version " v])
      [:ul.list-inline.mb-0
       (social-link :github "https://github.com/monkey-projects/monkeyci")
       (social-link :slack "https://monkeyci.slack.com")]]]]])

(defn not-found
  "Generates a page not found component"
  [config]
  [:div.container.text-center
   [:img.img-fluid.mb-5
    {:src (assets-url config "/svg/oc-error.svg")
     :alt "Not found image"
     :style {:width "30rem;"}}]
   [:p "The page you are looking for does not exist!"]
   [:a.btn.btn-primary {:href "/"} "Go back home"]])

(defn not-found-page
  "Generates a full html page containing the not-found component.  This is a generic
   page because it is unaware of the context it's displayed in."
  [config]
  [:html
   (head config)
   [:body
    (header-light config)
    [:main {:role :main}
     [:div.overflow-hidden
      [:div.d-flex.flex-column.min-vh-100
       (not-found config)
       [:div.mt-auto
        (footer config)]]]]]])
