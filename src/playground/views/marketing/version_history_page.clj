(ns playground.views.marketing.version-history-page
  (:require [hiccup.page :as hiccup-page]
            [playground.views.common :as page]))

(defn page [{:keys [page] :as data}]
  (hiccup-page/html5
    {:lang "en"}
    (page/head {:title       "Version History | AnyChart Playground"
                :description "The place where all your data visualization dreams come true"})
    [:body page/body-tag-manager
     [:div.wrapper.version-history-page.roadmap-page

      (page/nav (:templates data) (:user data))

      [:div.content
       [:div.container-fluid.content-container

        [:h1.page-caption "Version History"]
        [:p "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Architecto aut culpa deserunt id numquam sed temporibus voluptatem! Animi delectus dolore eos excepturi optio, quasi quibusdam reiciendis suscipit tenetur ullam velit."]

        [:div.line-container
         [:div
          ;region ---- version 1.0.0
          [:h2 "Version 1.0.0" [:span.muted "01 Nov 2017"]]
          [:p "Первая версия плейграунда это начало ахуенного проекта по визуализации данных от anychart (ссылка), он содержит дохера всего интересного (написано ниже), а ждет нас еще больше (сслыка на roadmap)."]]]

        [:div.row
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "Fork Samples!!"]
          [:ul
           [:li "Теперь можно форкать примеры к себе!"]]]
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "View Only mode"]
          [:ul
           [:li "Добавлен отличный режим для простотра примеров"]
           [:li "Содержит описания, теги а так же все зависимости примера"]]]
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "Brand New Editor"]
          [:ul
           [:li "Добавлена возможность использовать не только JavaScript, но и HTML/CSS "]
           [:li "Продуманый UI для работы с dependencies"]
           [:li "Возможность эмбедить созданные примеры к себе на сайт"]]]]

        [:div.row
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "Available Chart Types Page"]
          [:ul
           [:li "Примеры, описания и полезные ресурсы для каждого доступного чарт типа"]
           [:li "Все чарт типы сгруппированы по способу использования"]]]
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "Tags Page"]
          [:ul
           [:li "Каждому пример содержит теги"]
           [:li "Список тегов можно посмотреть на странице тегов"]]]
         [:div.col-md-4
          [:p [:span.glyphicon.glyphicon-ok] "Ready to Use Data Sets"]
          [:ul
           [:li "Мы подготовили для вас несколько десятков Data Set'ов чтобы можно было эксперементировать с разными типами графиков"]
           [:li "Каждый дата сет по мимо его данных, содержит описание и пример использования"]]]]

        ]]
      (page/footer (:repos data) (:tags data) (:data-sets data))]
     [:script {:src "/jquery/jquery.min.js"}]
     [:script {:src "/bootstrap-3.3.7-dist/js/bootstrap.min.js"}]]))