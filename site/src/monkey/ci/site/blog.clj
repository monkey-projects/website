(ns monkey.ci.site.blog
  "Blog pages for the public website.  These are read from markdown files and
   turned into html pages."
  (:require [babashka.fs :as fs]
            [monkey.ci.template.build :as tb]
            [monkey.ci.site
             [md :as md]
             [template :as t]]))

(defn generate-blog [conf f]
  {:src f
   :contents (->> (slurp (fs/file f))
                  (md/md-page)
                  (t/wrap-template conf))})

(defn write-blog [dest entry]
  (let [f (fs/path dest (fs/strip-ext (fs/file-name (:src entry))))]
    (log/info "Writing blog" (:src entry) "to" f)
    (fs/create-dirs f)
    (assoc entry :dest (tb/generate f (constantly (:contents entry))))))

(defn blog-pages [{{:keys [src dest]} :blog :as conf}]
  (->> (fs/list-dir src)
       (map (partial generate-blog conf))
       (map (partial write-blog dest))
       (doall)))
