(ns monkey.ci.template.icons)

(defn icon [n]
  [:i {:class (str "bi bi-" (name n))}])
