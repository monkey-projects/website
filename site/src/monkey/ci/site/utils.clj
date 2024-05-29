(ns monkey.ci.site.utils)

(defn code-lines [n]
  (->> (range n)
       (map (comp (partial conj [:span.code-editor-line-numbers-item]) inc))
       (into [:div.code-editor-line-numbers])))

(defn code-editor [lines opts]
  [:div.code-editor.mx-auto
   opts
   [:div.code-editor-container (code-lines (count lines))]])
