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


[MySQL Scheme](https://github.com/AnyChart/playground/blob/staging/src/sql/scheme.sql)


AnyChart © 2017
