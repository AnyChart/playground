(ns playground.views.prev-next-buttons)


(defn prev-button [id page url]
  [:a.prev-button.btn.btn-default {:id    id
                                   :style (str "display: inline-block;")
                                   :href  (str url page)
                                   :title (str "Prev page, " page)}
   [:span.glyphicon.glyphicon-arrow-left {:aria-hidden true}]
   " Prev"])


(defn next-button [id page url]
  [:a.next-button.btn.btn-default {:id    id
                                   :style (str "display: inline-block;")
                                   :href  (str url (-> page inc inc))
                                   :title (str "Next page, " (-> page inc inc))}
   "Next "
   [:span.glyphicon.glyphicon-arrow-right {:aria-hidden true}]])


(defn buttons [prev-id next-id page end url]
  [:div#prev-next-buttons.prev-next-buttons
   (when (pos? page)
     (prev-button prev-id page url))
   (when-not end
     (next-button next-id page url))])