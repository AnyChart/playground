(ns playground.views.prev-next-buttons)


(defn prev-button [class page url is-visible]
  [:a.prev-button {:class class
                   :style (str "visibility: " (if is-visible "visible" "hidden") ";")
                   :href  (if is-visible (str url page) "#")
                   :title (when is-visible (str "Prev page, " page))}
   ;[:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
   [:i.fas.fa-arrow-left]
   " Prev"])


(defn next-button [class page url is-visible]
  [:a.next-button {:class class
                   :role  "button"
                   :style (str "visibility: " (if is-visible "visible" "hidden") ";")
                   :href  (if is-visible (str url (-> page inc inc)) "#")
                   :title (when is-visible (str "Next page, " (-> page inc inc)))}
   "Next "
   ;[:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]
   [:i.fas.fa-arrow-right]])


(defn buttons [prev-id next-id page end url]
  [:div#prev-next-buttons.prev-next-buttons
   (prev-button prev-id page url (pos? page))
   (next-button next-id page url (not end))])


;; 4, 15 => [0 nil 3 4 5 nil 15] where nil is for '...'
(defn pagination-list [current-page max-page]
  (let [start (- current-page 3)
        end (+ current-page 3)
        start* (if (> end max-page)
                 (- start (- end max-page)) start)
        end* (if (< start 0)
               (+ end (- 0 start)) end)
        start** (max 0 start*)
        end** (min end* max-page)
        res (vec (range start** (inc end**)))
        res* (if (not= (first res) 0)
               (assoc res 0 0 1 nil)
               res)
        res** (if (not= (last res) max-page)
                (assoc res* (-> res* count dec) max-page
                            (-> res* count dec dec) nil)
                res*)]
    res**))


(defn pagination-markup [page max-page url]
  (let [result-list (pagination-list page
                                     max-page)]
    [:ul.pagination
     (for [key (range (count result-list))
           :let [i (get result-list key)]]
       (if i
         [:li.page-item {:class (if (= i page) "active")}
          [:a.page-link {:href (str url (inc i))}
           (inc i)
           [:span {:class "sr-only"}]]]
         [:li.page-item
          [:a.page-link.dots {:href "#" :rel "nofollow"}
           [:span {:aria-hidden true} "..."]]]))]))


(defn get-max-page [total items-perpage]
  (dec (int (Math/ceil (/ total items-perpage)))))


(defn pagination [prev-class next-class page max-page end url class]
  [:div#prev-next-buttons.prev-next-buttons {:class class}
   (prev-button prev-class page url (pos? page))

   [:div.pagination-box
    (when (pos? max-page)
      (pagination-markup page max-page url))]

   (next-button next-class page url (not end))])