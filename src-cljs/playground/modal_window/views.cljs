(ns playground.modal-window.views
  (:require [playground.data.consts :as consts]
            [re-frame.core :as rf]))


(defn window []
  (when @(rf/subscribe [:modal/show])

    [:div.ac-modal.hide-outside
     [:div.ac-modal-dialog

      [:div.ac-modal-header
       [:button {:type       "button"
                 :class      "close"
                 ;:data-dismiss "modal"
                 :aria-label "Close"
                 :on-click   #(rf/dispatch [:modal/hide])}
        [:span {:aria-hidden "true"} "×"]]

       [:h4.modal-title
        "WARNING"]]

      [:div.ac-modal-body
       consts/modal-window-warning]

      [:div.ac-modal-footer
       [:button.ac-btn.add-btn {:type     "button"
                                :on-click #(rf/dispatch [:modal/hide])} "Close"]
       [:button.ac-btn.remove-btn {:type     "button"
                                   :on-click #(rf/dispatch [:modal/proceed])} "Proceed"]
       ]
      ]
     ]


    ;:div {:class "modal fade" :tabindex "-1" :role "dialog"}
    ;(comment
    ;  [:div.modal
    ;   [:div.modal-dialog {:role "document"}
    ;    [:div.modal-content
    ;     [:div.modal-header
    ;      [:button {:type "button" :class "close" :data-dismiss "modal" :aria-label "Close"}
    ;       [:span {:aria-hidden "true"} "×"]]
    ;      [:h4.modal-title
    ;       "WARNING"]]
    ;     [:div.modal-body
    ;      [:p consts/modal-window-warning]]
    ;     [:div.modal-footer
    ;      [:button.ac-btn.add-btn {:type "button"} "Close"]
    ;      [:button.ac-btn.remove-btn {:type "button"} "Proceed"]
    ;
    ;      ]]]])

    )
  )