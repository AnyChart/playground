(ns playground.views.marketing.about-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "About | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body page/body-tag-manager
     [:div.wrapper

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container
        [:h1.page-caption "About"]
        [:h2 "Our Mission"]

        [:p "Миссия нового плйграунда состоит в том, чтобы предоставить быстрый и удобный способ создавать визуализации данных.
        На практике, это значит что " [:b "есть"] " все необходимые инструменты:"
         [:ul
          [:li "Нормальный дизайн с продуманным UI/UX"]
          [:li "Источники данных"]
          [:li "Различные типы графиков"]
          [:li "Обучающие материалы и подсказки"]
          [:li "Community"]
          ]]

        [:p "Это значит что " [:b "нет"] " ничего лишнего, того что мешало бы процессу создания визуализаций:"
         [:ul
          [:li "Рекламы"]
          [:li "Любой другой херни которая мешает"]]]

        [:h2 "Слоган"]
        [:p "AnyChart Playground - the ultimate charts playground. Create, modify, browse, learn and share."]

        [:h2 "Профит для Компании"]
        [:ul
         [:li "SEO, мы буквально заполоним интернет картинками с графиками AnyChart, которые будут искаться по самым разным тегам"]
         [:li "Удобный инструмент для создания примеров в галерею который снимает ограничения в которые мы упираемся уже хуй знает сколько"]
         [:li "Удобный инструмент для того чтобы делать демки для кастомеров, автоматическое сопровожднеие таких демок обучающими материалами: доки, апи, чартопедия"]
         [:li "Платный контент, на сервисе будут платные услуги, но они имеют меньший приоритет перед профитами перечисленными ранее"]]
        ]]

      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))