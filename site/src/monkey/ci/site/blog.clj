(ns monkey.ci.site.blog
  "Blog pages for the public website.  These are read from markdown files and
   turned into html pages."
  (:require [babashka.fs :as fs]
            [monkey.ci.template
             [build :as tb]
             [components :as tc]]
            [monkey.ci.site
             [md :as md]
             [template :as t]]))

(defn blog-url [conf path]
  (tc/site-url conf (str "/blog/" path)))

(defn head [conf]
  (into (tc/head conf)
        [(tc/stylesheet (tc/assets-url conf "/css/github-dark.min.css"))]))

(defn- apply-template [contents conf]
  [:html
   (head (assoc conf :title "MonkeyCI - Blog"))
   (vec
    (concat
     [:body
      (tc/header-light conf)
      [:div.container.content-space-1
       [:div.w-lg-75.mx-lg-auto contents]]]
     (t/footer conf)
     [(tc/script (tc/script-url conf "highlight.min.js"))
      (tc/script (tc/script-url conf "clojure.min.js"))
      ;; Activate syntax highlighting
      [:script "hljs.highlightAll();"]]))])

(defn- entry-page [conf entry]
  [:div
   [:h2.text-primary (:title entry)]
   [:p.mb-4 [:em (:author entry) " - " (:date entry)]]
   (:contents entry)
   [:div
    [:a {:href (blog-url conf "archive")} "More blog posts"]]])

(defn generate-blog [conf f]
  (let [p (md/parse f)]
    (assoc p
           :src f
           :name (fs/strip-ext (fs/file-name f))
           :contents (apply-template (entry-page conf p) conf))))

(defn dest-file
  "Generates destination file name for the blog entry, using the original name and date."
  [{:keys [src date]}]
  (fs/strip-ext (fs/file-name src)))

(defn write-blog [dest entry]
  (let [f (fs/path dest (dest-file entry))]
    (fs/create-dirs f)
    (assoc entry :dest (tb/generate f (constantly (:contents entry))))))

(defn copy-home [dest entries]
  (let [{d :dest :as latest} (->> entries
                                  (sort-by :date)
                                  (last))]
    (cond-> entries
      latest (conj {:type :latest
                    :src d
                    :dest (fs/copy d (fs/path dest "index.html") {:replace-existing true})}))))

(defn- archive-entry [conf e]
  [:div.card.card-sm
   [:div.card-body
    [:div.row.align-items-md-center
     [:div.col-md-6.mb-3.mb-md-0
      [:img.img-fluid {:src (tc/site-url conf (:header-img e)) :alt "Blog header image"}]]
     [:div.col-md-6
      [:div.ps-md-5
       [:div.mb-3.mb-md-5
        [:h4
         [:a.text-dark {:href (blog-url conf (:name e))} (:title e)]]
        [:i (:author e) " - " (:date e)]]
       [:p (:summary e)]]]]]])

(defn generate-archive [conf entries]
  [:div
   [:h2.text-primary.mb-3 "MonkeyCI Blog Archive"]
   (->> entries
        (sort-by :date)
        (reverse)
        (map (partial archive-entry conf))
        (into [:div.d-flex.gap-4]))])

(defn write-archive
  "Builds an archive page that holds an overview of all blog entries"
  [conf entries]
  (letfn [(gen-archive []
            (let [f (fs/path (fs/create-dirs (fs/path (get-in conf [:blog :dest]) "archive")) "index.html")
                  c (-> (generate-archive conf entries)
                        (apply-template conf))]
              {:type :archive
               :contents c
               :dest (tb/generate f (constantly c))}))]
    (cond-> entries
      (not-empty entries) (conj (gen-archive)))))

(defn blog-pages [{{:keys [src dest]} :blog :as conf}]
  (->> (fs/list-dir src "*.md")
       (map (partial generate-blog conf))
       (map (partial write-blog dest))
       (write-archive conf)
       (copy-home dest)
       (doall)))
