(ns playground.views.marketing.version-history-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head)
    [:body
     [:div.wrapper.version-history-page.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Version History"]

        [:div.line-container
         [:div
          [:h2 "Version 0.1.0" [:span.muted "First release (beta)"]]
          [:div
           [:p [:span.glyphicon.glyphicon-ok] "Прячем все что не работает"
            [:ul
             [:li "регистарцию/авторизацию"]
             [:li "лайки"]
             [:li "прайсинг"]
             [:li "все маркетинговые страницы которые не готовы к релизу"]]]
           [:p "При помощи автоматической утилиты ENV-544, переделываем все примеры на новый формат"]
           [:p "Имплементим дизайн"]
           [:p "Итерация по SEO: мета теги, sitemap, альты, тайтлы, описания, итд (после дизайна)"]]]

         [:h2 "Version 1.0.0" [:span.muted "Stable release"]]
         [:div
          [:p "Фиксим баги если они есть"]
          [:p [:span.glyphicon.glyphicon-ok] "Берем один - два итема из Features list"]]

         [:h2 "Drop list"]
         [:p "Вещи которые есть на старом PG и их планируется дропнуть"]
         [:div.row
          [:div.col-md-4
           [:p [:span.glyphicon.glyphicon-ok] "Tag Manager (текущий конфиг)"]
           [:ul
            [:li "Кнопка LiveChart'a не уместна"]
            [:li "Holdon Stranger тут не уместен"]
            [:li "Тормозит загрузку страницы"]]]
          [:div.col-md-4
           [:p "Chart related buttons"]
           [:ul
            [:li "PNG/PDF/JPG/SVG"]
            [:li "CSV/XLSX"]
            [:li "Вкладка JSON"]
            [:li "Смена темы"]]]
          [:div.col-md-4
           [:p "Переработано/не актуально"]
           [:ul
            [:li "Список примеров и категорий"]
            [:li "Интеграция с jsfiddle"]]]]


         [:h2 "Features list"]
         [:div.row
          [:div.col-md-3
           [:p [:span.glyphicon.glyphicon-ok] "Embeding"]
           [:ul
            [:li "Embed as iframe"]
            [:li "Embed as Script with code"]
            [:li "Embed as Script with src"]]]
          [:div.col-md-3
           [:p "Export to image"]
           [:ul
            [:li "PNG"]
            [:li "JPG"]
            [:li "PDF"]
            [:li "SVG"]]]
          [:div.col-md-3
           [:p "Sharing"]
           [:ul
            [:li "Facebook"]
            [:li "Twitter"]
            [:li "LinkedIn"]
            [:li "Pinterest"]
            [:li "Instagram"]]]
          [:div.col-md-3
           [:p "Search"]
           [:ul
            [:li "По примерам (имена/описания)"]
            [:li "По тегам"]
            [:li "По датасетам"]
            [:li "По коду"]
            [:li "С фильтрами по проекту/тегу/дата сету"]]]
          [:div.col-md-3
           [:p "User related"]
           [:ul
            [:li "Мои примеры"]
            [:li "Проекты"]
            [:li "Настройки"]
            [:li "Платные вещи + билинг/оплата"]
            [:li "Assets"]]]
          [:div.col-md-3
           [:p [:span.glyphicon.glyphicon-ok] "Разное"]
           [:ul
            [:li "Copy btn for HTML/CSS/JS/Result windows"]
            [:li "Full source as text/gist"]
            [:li "Download button"]
            [:li "Full screen"]
            [:li "Print"]
            [:li "Console"]
            [:li "Keyboard/Keymap"]]]
          [:div.col-md-3
           [:p "Preprocessors"]
           [:ul
            [:li "JS -CofeScript, TypesScript, CljoreScript, more?"]
            [:li "CSS - LESS, SASS, more?"]
            [:li "Markup - Markdown, more?\""]]]]]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))