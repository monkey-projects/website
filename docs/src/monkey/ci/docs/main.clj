(ns monkey.ci.docs.main
  (:require [babashka.fs :as fs]
            [monkey.ci.docs.config :as dc]
            [monkey.ci.template
             [components :as tc]
             [icons :as i]]))

(defn head [config]
  (-> (tc/head (assoc config :title "MonkeyCI: Documentation Center"))
      ;; Syntax highlighting lib
      ;; See https://github.com/highlightjs/highlight.js/tree/main/src/styles for more styles
      (conj (tc/stylesheet "/css/github-dark.min.css"))
      (conj (tc/script "/js/highlight.min.js"))
      (conj (tc/script "/js/clojure.min.js"))
      (conj [:script "hljs.highlightAll();"])))

(defn header
  "Renders docs header"
  [config]
  [:header#header.navbar.navbar-expand-lg.navbar-end.navbar-light.bg-white
   [:div.container
    [:nav.js-mega-menu.navbar-nav-wrap
     ;; Logo
     [:a.navbar-brand
      {:href "/"
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

(defn- search-form []
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
     [:i.bi-search]]]])

(defn search-bar []
  [:div.bg-primary-dark.overflow-hidden
   [:div.container.position-relative.content-space-1
    ;; Search form, does nothing for now, so it's disabled
    #_[:div.w-lg-75.mx-lg-auto
       (search-form)]
    [:div.position-absolute
     {:style {:top "-6rem"
              :left "-6rem"}}
     [:img {:src "/svg/shape-1-soft-light.svg"
            :alt "SVG"
            :width 500
            :style {:width "12rem"}}]]
    [:div.position-absolute
     {:style {:bottom "-6rem"
              :right "-7rem"}}
     [:img {:src "/svg/shape-7-soft-light.svg"
            :alt "SVG"
            :width 250}]]]])

(defn breadcrumb [path conf]
  (letfn [(bc-item [{:keys [path label]}]
            [:li.breadcrumb-item
             [:a {:href path} label]])]
    [:nav
     (->> path
          (map bc-item)
          (into [:ol.breadcrumb.mb-0]))]))

(def short-title (some-fn :short :title))

(defn- related-articles [related conf]
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
                [:a.text-body {:href (dc/apply-prefix path (dc/articles-prefix conf))} lbl]]])]
      [:div.mt-7
       [:div.text-center.mb-7
        [:h4 "Related articles"]]
       [:div.row
        (render-col (map first rows))
        (render-col (map second rows))]])))

(defn- render-md
  "Renders markdown so it can be included in a html page"
  [{:keys [title contents related]} config]
  [:div
   (when title
     [:h2.h3 title])
   contents
   (when (not-empty related)
     (related-articles related config))])

(defn- render-categories [cats]
  (->> cats
       (map (fn [{:keys [label active?] :as c}]
              [:li.nav-item
               [:a.nav-link
                (cond-> {:href (-> c :location last :path)}
                  active? (assoc :class "active"))
                (format "%s (%d)" label (count (:files c)))]]))
       (into 
        [:ul#categories.nav.nav-link-gray.nav-tabs.nav-vertical])))

(defn- add-categories
  "Add categories to the given page"
  [page cats]
  [:div.row
   [:div.col-md-4
    (render-categories cats)]
   [:div.col-md-8
    page]])

(defn- ->page [content bc config]
  [:html
   (head config)
   [:body
    (header config)
    [:main {:role :main}
     (search-bar)
     [:div.border-bottom
      [:div.container.py-4
       [:div.w-lg-75.mx-lg-auto
        bc]]]
     [:div.overflow-hidden
      [:div.d-flex.flex-column.min-vh-100
       [:div.container.content-space-1
        [:div.w-lg-75.mx-lg-auto
         content]]
       [:div.mt-auto
        (tc/footer config)]]]]
    (tc/script (tc/script-url config "vendor.min.js"))
    (tc/script (tc/script-url config "theme.min.js"))]])

(defn md->page
  "Given a parsed markdown structure, renders it into the resulting hiccup structure as a 
   full html page."
  [md config]
  (->page
   (render-md md config)
   (breadcrumb (:location md) config)
   config))

(defn- category-article [c art]
  (let [loc (-> art :location last)]
    [:li
     [:div.d-sm-flex
      [:div.flex-shrink-0.mb-3.mb-sm-0
       [:div.text-primary.h5 (i/icon :question-circle-fill)]]
      [:div.flex-grow-1.ms-sm-3
       [:div.mb-5
        ;; Category label
        [:span.text-cap (:label c)]
        [:a.link-dark {:href (:path loc)} [:h5.article-title (:title art)]]
        [:p (or (:summary art)
                ;; If no summary specified, pick the first paragraph
                (-> art
                    :contents
                    second))]]
       [:a {:href (:path loc)} [:span.me-1 "Read more"] (i/icon :chevron-right)]]]]))

(defn category-page
  "Generates a category page hiccup structure for the given category.  The config should
   contain all categories, and the articles within.  The articles in the category
   are sorted by the `index` property."
  [category config]
  (let [cat-conf (get-in config [:categories category])]
    (-> (->> cat-conf
             :files
             (sort-by :index)
             (map (partial category-article cat-conf))
             (interpose [:li.border-top.my-5])
             (into [:ul.list-unstyled.list-py-2]))
        (add-categories (-> (:categories config)
                            ;; Mark current category as active
                            (assoc-in [category :active?] true)
                            vals))
        (->page
         (breadcrumb (:location cat-conf) config)
         (:config config)))))

(defn index-page
  "Generates an index page using the given markdown.  This is a combination of a category page
   and an article page."
  [md config]
  (-> (render-md md (:config config))
      (add-categories (-> (:categories config)
                          ;; No active category for index page
                          vals))
      (->page
       (breadcrumb (:location md) config)
       (:config config))))
