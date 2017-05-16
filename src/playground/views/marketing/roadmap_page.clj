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
          [:li "Имплементим дизайн"]]]
        [:div.row
         [:h3 "Version 1.0.0 - Stable release"]
         [:ul
          [:li "Фиксим баги если они есть"]
          [:li "Берем один - два итема из Features list"]]]
        [:div.row
         [:h3 "Version 1.1.0 - TBA"]
         [:ul
          [:li "Набираем из Features list"]]]
        [:div.row
         [:h3 "Drop list"]
         [:p "Вещи которые есть на старом PG и их планируется дропнуть"]
         [:div.col-md-3
          [:h5 "Tag Manager (текущий конфиг)"]
          [:ul
           [:li "Кнопка LiveChart'a не уместна"]
           [:li "Holdon Stranger тут не уместен"]
           [:li "Тормозит загрузку страницы"]]]
         [:div.col-md-3
          [:h5 "Chart related buttons"]
          [:ul
           [:li "PNG/PDF/JPG/SVG"]
           [:li "CSV/XLSX"]
           [:li "Вкладка JSON"]
           [:li "Смена темы"]]]
         [:div.col-md-3
          [:h5 "Переработано/не актуально"]
          [:ul
           [:li "Список примеров и категорий"]
           [:li "Интеграция с jsfiddle"]]]]
        [:div.row
         [:h3 "Features list"]
         [:div.col-md-3
          [:h5 "Export and Embeding"]
          [:ul
           [:li "Copy btn for HTML/CSS/JS/Result windows"]
           [:li "Full source as text/gist"]
           [:li "Download button"]
           [:li "Embed as iframe link"]
           [:li "Embed as Script with code"]
           [:li "Embed as Script with link"]]]
         [:div.col-md-3
          [:h5 "Share to"]
          [:ul
           [:li "Facebook"]
           [:li "Twitter"]
           [:li "LinkedIn"]
           [:li "Pinterest"]
           [:li "Instagram"]
           [:li "XLSX"]]]
         [:div.col-md-3
          [:h5 "Search"]
          [:ul
           [:li "По примерам (имена/описания)"]
           [:li "По тегам"]
           [:li "По датасетам"]
           [:li "По коду"]
           [:li "С фильтрами по проекту/тегу/дата сету"]]]
         [:div.col-md-3
          [:h5 "Разное"]
          [:ul
           [:li "Full screen"]
           [:li "Print"]]]
         [:div.col-md-3
          [:h5 "Preprocessors"]
          [:ul
           [:li "JS -CofeScript, TypesScript, CljoreScript, more?"]
           [:li "CSS - LESS, SASS, more?"]
           [:li "Markup - Markdown, more?\""]]]]
        ]]

      (page/footer (:repos data) (:tags data))]]))