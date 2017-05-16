(ns playground.views.marketing.roadmap-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container
        [:h1.page-caption "Roadmap"]
        [:p.page-caption-desc "Тут бутет информация о грядущих обновлениях и возможность зареквестить фичу."]
        [:div.row
         [:h3 "Version 0.1.0 - First release (beta)"]
         [:ul
          [:li "Прячем все что не работает"
           [:ul
            [:li "регистарцию/авторизацию"]
            [:li "лайки"]
            [:li "прайсинг"]
            [:li "все маркетинговые страницы которые не готовы к релизу"]]]
          [:li "При помощи автоматической утилиты ENV-544, переделываем все примеры на новый формат"]
          [:li "Проставляем теги, как минимум для галереи, как максимум еще и для доков"]
          ]
         ]
        [:div.row
         [:h3 "Features list"]
         [:div.col-md-3
          [:h5 "Export and Embeding"]
          [:ul
           [:li "Copy btn for HTML/CSS/JS/Result windows"]
           [:li "Download button"]
           [:li "Embed as iframe link"]
           [:li "Embed as Script with code"]
           [:li "Embed as Script with link"]
           ]]]
        ]]

      (page/footer (:repos data) (:tags data))]]))