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
          [:li "Имплементим дизайн"]
          [:li "Вещи из старого PG которых не будет в первом релизе"
           [:ul
            [:li "Фул скрин"]
            [:li "Поиск"]
            [:li "Сохранение в картинку через UI плейграунда"]
            ]]
          [:li "Вещи из старого PG которые планируется дропнуть " [:b "совсем"]
           [:ul
            [:li "Интеграция с jsfiddle, теперь незачем"]
            [:li "Кнопка смены темы, это должно делаться через Quick Add и код, но не через кнопку"]
            [:li "Tag manager, я решительно против дефолтного конфига тег менеджера в этом проекте:
            Убивает UX, Тормозит загрузку страницы, за плагин Holdon Stranger в сервисе вообще отдельный котел в аду нужен"]
            ]]
          ]
         ]
        [:div.row
         [:h3 "Version 1.0.0 - Stable release"]
         [:ul
          [:li "Фиксим баги если они есть"]
          [:li "Берем один - два итема из Features list"]
         ]]
        [:div.row
         [:h3 "Version 1.1.0 - TBA"]
         [:ul
          [:li "Набираем из Features list"]
          ]]
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
           ]]
         [:div.col-md-3
          [:h5 "Export to"]
          [:ul
           [:li "PNG"]
           [:li "PDF"]
           [:li "JPG"]
           [:li "SVG"]
           [:li "CSV"]
           [:li "XLSX"]]]
         [:div.col-md-3
          [:h5 "Share to"]
          [:ul
           [:li "Facebook"]
           [:li "Twitter"]
           [:li "LinkedIn"]
           [:li "Pinterest"]
           [:li "Instagram"]
           [:li "XLSX"]]]
         [:div.col-md-3 "Full screen"]
         [:div.col-md-3 "Print"]
         ]
        ]]

      (page/footer (:repos data) (:tags data))]]))