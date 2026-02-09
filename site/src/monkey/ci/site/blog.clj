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

(defn- apply-template [conf p]
  [:html
   (head (assoc conf :title "MonkeyCI - Blog"))
   (vec
    (concat
     [:body
      (tc/header-light conf)
      [:div.container.content-space-1
       [:div.w-lg-75.mx-lg-auto p]]]
     (t/footer conf)
     [(tc/script (tc/script-url conf "highlight.min.js"))
      (tc/script (tc/script-url conf "clojure.min.js"))
      ;; Activate syntax highlighting
      [:script "hljs.highlightAll();"]]))])

(defn generate-blog [conf f]
  (-> (md/parse f)
      (assoc :src f)
      (update :contents (partial apply-template conf))))

(defn write-blog [dest entry]
  (let [f (fs/path dest (fs/strip-ext (fs/file-name (:src entry))))]
    (fs/create-dirs f)
    (assoc entry :dest (tb/generate f (constantly (:contents entry))))))

(defn blog-pages [{{:keys [src dest]} :blog :as conf}]
  (->> (fs/list-dir src)
       (map (partial generate-blog conf))
       (map (partial write-blog dest))
       (doall)))
