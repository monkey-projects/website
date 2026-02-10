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

(defn head [conf]
  (into (tc/head conf)
        [(tc/stylesheet (tc/assets-url conf "/css/github-dark.min.css"))]))

(defn- apply-template [entry conf]
  [:html
   (head (assoc conf :title "MonkeyCI - Blog"))
   (vec
    (concat
     [:body
      (tc/header-light conf)
      [:div.container.content-space-1
       [:div.w-lg-75.mx-lg-auto
        [:h2.text-primary (:title entry)]
        [:p.mb-4 [:em (:author entry) " - " (:date entry)]]
        (:contents entry)]]]
     (t/footer conf)
     [(tc/script (tc/script-url conf "highlight.min.js"))
      (tc/script (tc/script-url conf "clojure.min.js"))
      ;; Activate syntax highlighting
      [:script "hljs.highlightAll();"]]))])

(defn generate-blog [conf f]
  (let [p (md/parse f)]
    (assoc p
           :src f
           :contents (apply-template p conf))))

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
                    :dest (fs/copy d (fs/path dest "index.html"))}))))

(defn write-archive
  "Builds an archive page that holds an overview of all blog entires"
  [dest entries]
  (cond-> entries
    (not-empty entries) (conj {:type :archive
                               ;; TODO File contents
                               :dest (fs/create-dirs (fs/path dest "archive"))})))

(defn blog-pages [{{:keys [src dest]} :blog :as conf}]
  (->> (fs/list-dir src "*.md")
       (map (partial generate-blog conf))
       (sort-by :date)
       (map (partial write-blog dest))
       (copy-home dest)
       (write-archive dest)
       (doall)))
