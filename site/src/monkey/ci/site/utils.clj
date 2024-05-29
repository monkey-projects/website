(ns monkey.ci.site.utils)

(defn code-lines [n]
  (->> (range n)
       (map (comp (partial conj [:span.code-editor-line-numbers-item]) inc))
       (into [:div.code-editor-line-numbers])))

(defn code-typing-area [lines]
  (->> lines
       (interpose [:br])
       (into [:div.code-editor-typing-area])))

(defn code-editor [opts lines]
  [:div.code-editor.mx-auto
   opts
   [:div.code-editor-container
    (code-lines (count lines))
    (code-typing-area lines)
    [:div.position-absolute.zi-n1 {:style "top: -3rem; left: -5rem;"}
     [:img.img-fluid {:src "./svg/shape-1-soft-light.svg"
                      :style "width: 10rem"}]]]])
