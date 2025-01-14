(ns monkey.ci.docs.main
  (:require [babashka.fs :as fs]
            [monkey.ci.template
             [components :as tc]
             [icons :as i]]))

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
    ;; Search form, does nothing for now, so it's disabled
    #_[:div.w-lg-75.mx-lg-auto
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

(defn breadcrumb [path]
  (letfn [(bc-item [{:keys [path label]}]
            [:li.breadcrumb-item
             [:a {:href path} label]])]
    [:nav
     (->> path
          (concat [{:path "/"
                    :label "Home"}])
          (map bc-item)
          (into [:ol.breadcrumb.mb-0]))]))

(defn- related-articles [related]
  (let [rows (partition-all 2 related)]
    (letfn [(render-col [items]
              [:div.col-sm-6
               (->> items
                    (remove nil?)
                    (map render-item)
                    (into [:ul.list-unstyled.list-py-2.mb-0]))])
            (render-item [[path lbl]]
              [:li.d-flex
               [:div.flex-shrink-0
                (i/icon :file-earmark)]
               [:div.flex-grow-1.ms-2
                [:a.text-body {:href path} lbl]]])]
      [:div.container.content-space-t-1.mb-7
       [:div.w-lg-75.mx-lg-auto
        [:div.text-center.mb-7
         [:h4 "Related articles"]]
        [:div.row
         (render-col (map first rows))
         (render-col (map second rows))]]])))

(defn- render-md
  "Renders markdown to be included in a html page"
  [{:keys [title contents related]} config]
  [:div
   [:div.container.content-space-1
    [:div.w-lg-75.mx-lg-auto
     (when title
       [:h2.h3 title])
     contents]]
   (when (not-empty related)
     (related-articles related))])

(defn md->page
  "Given a parsed markdown structure, renders it into the resulting hiccup structure"
  [md config]
  [:html
   (tc/head (assoc config :title "MonkeyCI: Documentation Center"))
   [:body
    (header config)
    [:main {:role :main}
     (search-bar)
     [:div.border-bottom
      [:div.container.py-4
       [:div.w-lg-75.mx-lg-auto
        (breadcrumb (:location md))]]]
     [:div.overflow-hidden
      [:div.d-flex.flex-column.min-vh-100
       (render-md md config)       
       [:div.mt-auto
        (tc/footer config)]]]]
    (tc/script (tc/script-url config "vendor.min.js"))
    (tc/script (tc/script-url config "theme.min.js"))]])

