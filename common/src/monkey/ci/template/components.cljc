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

(def site-url (partial make-url {:prefix "www"}))
(def app-url (partial make-url {:prefix "app"}))
(def assets-url (partial make-url {:prefix "assets"}))
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
   [:title "MonkeyCI"]
   (stylesheet (assets-url config "/css/vendor.min.css"))
   (stylesheet (assets-url config "/css/theme.min.css"))
   (stylesheet (assets-url config "/css/bootstrap-icons.min.css"))])

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
        {:src (assets-url config "/img/logo-bw-small.png")
         :alt "Logo"}]
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
        [:a.js-animation-link.d-none.d-lg-inline-block.btn.btn-primary
         {:href (app-url config)
          :role "button"}
         [:i.bi.bi-person-circle.me-1]
         "Sign up"]]]]]]])

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
       [:span.text-cap.text-primary-light header]
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
                 [["Blog" "https://www.monkey-projects.be/blog"]
                  ["Documentation" (docs-url config)]])
     (footer-col "Company"
                 [["About us" (site-url config "/about")]
                  ["Contact" (site-url config "/contact")]])
     (footer-col "Legal"
                 [["Terms of use" "todo"]
                  ["Privacy policy" "todo"]])
     [:div.border-top.border-white-10]]
    [:div.row.align-items-md-end.py-5
     [:div.col-md.mb-3.mb-md-0
      [:p.text-white.mb-0
       copyright " 2024 " [:a.link-light {:href "https://www.monkey-projects.be"} "Monkey Projects"]]]
     [:div.col-md.d-md-flex.justify-content-md-end
      (when-let [v (:version config)]
        [:p.text-primary-light.mb-0.small.me-2.pt-2 "version " v])
      [:ul.list-inline.mb-0
       (social-link :github "https://github.com/monkey-projects/monkeyci")
       (social-link :slack "https://monkeyci.slack.com")]]]]])
