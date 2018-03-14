## API
For communication between editor and server the application uses [Transit](https://github.com/cognitect/transit-format) format.
```
POST /run 
Request params:
scripts "https://cdn.anychart.com/js/master/anychart-bundle.min.js,https://cdn.anychart.com/js/7.13.0/anychart-ui.min.js"
style "html, body {\r\n    width: 100%;\r\n    height: 100%;\r\n    margin: 0;\r\n    padding: 0;\r\n    background-color: #dddddd;\r\n    color: green;\r\n    font-size: 50px;\r\n}\r\np {\r\n  position: absolute;\r\n  left: 100px;\r\n}"
markup "<p id=\"hello\">Hello Anychart! save ывпыв п длордлролд1235</p>"
code "var left = 0;\r\nsetInterval(function(e){\r\n  var h = document.getElementById(\"hello\");\r\n  left += 2;\r\n  h.style.left = left + \"px\";\r\n  if (left > 200){\r\n  \tleft = 0;\r\n  }\r\n}, 100);"
styles "https://cdn.anychart.com/css/7.13.0/anychart-ui.min.css,http://cdn.anychart.com/fonts/2.7.2/anychart.css"

Response: 
<HTML sample data>


POST /save
Request:
{:description "The text moves from left to right"
 :style_type "css"
 :tags
 ("column charts"
  "bar charts"
  "mixed charts"
  "combo charts"
  "multi-series charts"
  "charts")
 :update_date nil
 :version_id nil
 :name "Text Motion"
 :create_date #inst "2017-04-03T15:40:35.000-00:00"
 :scripts
 ("https://cdn.anychart.com/js/master/anychart-bundle.min.js"
  "https://cdn.anychart.com/js/7.13.0/anychart-ui.min.js")
 :likes 0
 :short_description "Text motion super"
 :markup_type "html"
 :style
 "html, body {\n    width: 100%;\n    height: 100%;\n    margin: 0;\n    padding: 0;\n    background-color: #dddddd;\n    color: green;\n    font-size: 50px;\n}\np {\n  position: absolute;\n  left: 100px;\n}"
 :markup "<p id=\"hello\">Hello Anychart! save ывпыв п длордлролд1235</p>"
 :author "AnyChart Team"
 :exports nil
 :id 391437
 :url "wNtoEHBO"
 :code
 "var left = 0;\nsetInterval(function(e){\n  var h = document.getElementById(\"hello\");\n  left += 2;\n  h.style.left = left + \"px\";\n  if (left > 200){\n  \tleft = 0;\n  }\n}, 100);"
 :styles
 ("https://cdn.anychart.com/css/7.13.0/anychart-ui.min.css"
  "http://cdn.anychart.com/fonts/2.7.2/anychart.css")
 :local_scripts nil
 :code_type "js"
 :version 11
 :show_on_landing false
 :views 0}

Response:
{:status :ok
 :hash "Vasd8412"
 :version 1}
 

POST /fork
Request::
{:description "The text moves from left to right"
 :style_type "css"
 :tags
 ("column charts"
  "bar charts"
  "mixed charts"
  "combo charts"
  "multi-series charts"
  "charts")
 :update_date nil
 :version_id nil
 :name "Text Motion"
 :create_date #inst "2017-04-03T15:40:35.000-00:00"
 :scripts
 ("https://cdn.anychart.com/js/master/anychart-bundle.min.js"
  "https://cdn.anychart.com/js/7.13.0/anychart-ui.min.js")
 :likes 0
 :short_description "Text motion super"
 :markup_type "html"
 :style
 "html, body {\n    width: 100%;\n    height: 100%;\n    margin: 0;\n    padding: 0;\n    background-color: #dddddd;\n    color: green;\n    font-size: 50px;\n}\np {\n  position: absolute;\n  left: 100px;\n}"
 :markup "<p id=\"hello\">Hello Anychart! save ывпыв п длордлролд1235</p>"
 :author "AnyChart Team"
 :exports nil
 :id 391437
 :url "wNtoEHBO"
 :code
 "var left = 0;\nsetInterval(function(e){\n  var h = document.getElementById(\"hello\");\n  left += 2;\n  h.style.left = left + \"px\";\n  if (left > 200){\n  \tleft = 0;\n  }\n}, 100);"
 :styles
 ("https://cdn.anychart.com/css/7.13.0/anychart-ui.min.css"
  "http://cdn.anychart.com/fonts/2.7.2/anychart.css")
 :local_scripts nil
 :code_type "js"
 :version 11
 :show_on_landing false
 :views 0}

Response:
{:status :ok
 :hash "Vasd8412"
 :version 0}
```