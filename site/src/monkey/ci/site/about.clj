(ns monkey.ci.site.about
  (:require [monkey.ci.site.main :as m]
            [monkey.ci.template
             [components :as tc]
             [svg :as svg]]))

(def title
  [:div.row.justify-content-sm-center
   [:div.col-sm-10.col-md-8.col-lg-7
    [:div.text-center.mb-7
     [:h1.display-4.text-primary
      "Our mission is to make the world a " [:span.text-warning "better"] " place."]
     [:p.lead
      "We build tools that help people do their jobs better and that they" [:i.mx-1 "like"] "to use. "
      "And all that in a sustainable fashion."]]]])

(defn bg-shape-1 [conf]
  [:div.position-absolute.zi-n1
   {:style {:top "-6rem"
            :left "-6rem"}}
   [:img
    {:src (tc/assets-url conf "svg/components/shape-1.svg")
     :width 500
     :style {:width "12rem"}}]])

(defn bg-shape-2 [conf]
  [:div.position-absolute.zi-n1
   {:style {:bottom "-6rem"
            :right "-7rem"}}
   [:img
    {:src (tc/assets-url "svg/components/shape-7.svg")
     :width 150}]])

(defn description [conf]
  (letfn [(entry [icon title body]
            [:div.d-flex
             [:div.flex-shrink-0
              [:span.svg-icon.svg-icon-sm.text-primary
               (svg/include conf (str "vendor/duotone-icons/" icon))]]
             [:div.flex-grow-1.ms-4
              [:h6 title]
              body]])]
    [:div.container.content-space-b-1.content-space-b-md-3
     [:div.row.justify-content-sm-center
      [:div.col-sm-10.col-md-8.col-lg-7
       [:div.mb-7
        [:h3 "Our vision"]
        [:p
         "We people inhabit a truly unique place in the universe.  A planet that can harbor life "
         "and that even has evolved intelligent life.  It's up to us to take care of it, and make "
         "the best of it.  When we humans cooperate, there is no limit to what we can achieve. "
         [:b "Monkey Projects"] " tries to contribute to this in it's own way."]]
       [:div.d-grid.gap-5
        (entry "map/map004"
               "Sustainable Software"
               [:p
                "We design our software to require minimal resources, in terms of memory, cpu or storage. "
                "Not only does this consume less energy and fewer precious materials, but it's also cheaper "
                "in the long run."])
        (entry "teh/teh002"
               "Workable Work"
               [:p
                "Happy people that feel respected will love doing what they do, and they will do it better. "
                "That's why we try to make everybody feel as good as possible around the workplace.  We "
                "respect work-life balance.  After all, the economy should serve mankind, not the other "
                "way around."])
        (entry "fin/fin005"
               "Personal Privacy"
               [:p
                "Privacy is more important than you think.  We may have nothing to hide, but that doesn't "
                "mean we should allow others to access our personal information.  We at Monkey Projects "
                "only keep track of the absolute minimal amount of data and ensure that your information "
                "stays yours.  We will never expose it or sell it in any way and we only host your data "
                "in countries where we're sure it's safe."])]]]]))

(defn content [conf]
  [:main#content {:role :main}
   [:div.overflow-hidden
    [:div.container.content-space-1.content-space-md-2
     title
     (bg-shape-1 conf)
     (bg-shape-2 conf)]]
   (description conf)])

(defn about [config]
  [:html
   (m/head config)
   (vec
    (concat
     [:body
      (tc/header-light config)
      (content config)]
     (m/footer config)))])
