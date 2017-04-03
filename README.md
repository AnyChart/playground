[<img src="https://cdn.anychart.com/images/logo-transparent-segoe.png?2" width="234px" alt="AnyChart - Robust JavaScript/HTML5 Chart library for any project">](https://anychart.com)

# Playground

[![Build Status](https://travis-ci.com/AnyChart/playground.svg?token=ERMLfyrvWdA8g6gi11Vp&branch=master)](https://travis-ci.com/AnyChart/playground)
=========================

## Usage
Config file format:

```
[web]
# Web settings
port = 8080

[db]
# MySQL settings
port = 3306
name = "playground_db"
user = "playground_user"
password = "playground_password"

[notifications.slack]
# Slack notification settings
token = "token"
channel = "#playground_notifications"
username = "playground_bot"
# tag and domain are used in slack messages
domain = "http://playground.example.com/"
tag  = "playground"

# Next you should describe you repositories

[[repositories]]
# Main info
name = "first_repository"
# Folder where the repo will locate
dir = "/playground/data/first_repository"
# Prefix will be used in repository's sample path
url-prefex = "first-repo"
# either generate preview or not
generate-preview = true
# connection type, may be ssh or https
type = "ssh"

# if you choose ssh connection type, describe it
[repositories.ssh]
ssh = "git@github.com:YourCompany/your-repo.git"
secret-key = "/path-to-id_rsa"
public-key = "/path-to-id_rsa.pub"
passphrase = "passphraze"

# or if you choose https
[repositories.https]
https = "https://github.com/YourCompany/your-repo.git"
login = "login"
password = "password"

# Second repository
[[repositories]]
name = "second_repository"
dir = "/playground/data/second_repository"
url-prefex = "second-repo"
generate-preview = true
type = "ssh"

[repositories.ssh]
ssh = "git@github.com:YourCompany/your-repo2.git"
secret-key = "/path-to-id_rsa"
public-key = "/path-to-id_rsa.pub"
passphrase = "passphraze"
```

[TOML](https://github.com/toml-lang/toml) `.sample` sample format:
```
name = "Chart Name"
description = "Chart description"
short_description = "Short chart description" 

[meta]
tags = ["some" "tags"]
export = "chart_name_for_exporting"
show_on_landing = true

[deps]
scripts = ["http://remote-js-dependency.js"]
local-scripts = ["../local-js-dependency.js"]
styles = ["http://remote-css-dependency.css"]

[code]
type = "js"
code = """
// write your sample code here
// anychart.onDocumentReady(function() {
// ...
// });
"""

[style]
type = "css"
code = """
/* write your sample styles here */
"""

[markup]
type = "html"
code = """
<!-- describe your markup here -->
"""
```

HTML `.html` sample format:
```
<html>
<head>
    <meta charset="UTF-8"/>
    <meta name="ac:name" content="Chart name"/>
    <meta name="ac:description" content="Chart description"/>
    <meta name="ac:short_description" content="Chart short description"/>
    <meta name="ac:tags" content="some, tags"/>
    <meta name="ac:show_on_landing" content="true"/>
    <script src="../local-js-dependency.js"></script>
    <script src="http://remote-js-dependency.js" data-export="true"></script>
    <script x-export="chart">
        // write your code here
        // anychart.onDocumentReady(function () {
        // ...
        // });
    </script>
</head>
<body>
<div id="container" style="width: 100%; height: 100%"></div>
</body>
</html>
```

## API
The application uses [Transit](https://github.com/cognitect/transit-format) format.
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


[MySQL Scheme](https://github.com/AnyChart/playground/blob/staging/src/sql/scheme.sql)


AnyChart © 2017
