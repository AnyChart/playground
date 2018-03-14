[<img src="https://cdn.anychart.com/images/logo-transparent-segoe.png?2" width="234px" alt="AnyChart - Robust JavaScript/HTML5 Chart library for any project">](https://anychart.com)

# Playground

[![Build Status](https://travis-ci.com/AnyChart/playground.svg?token=ERMLfyrvWdA8g6gi11Vp&branch=master)](https://travis-ci.com/AnyChart/playground) production

[![Build Status](https://travis-ci.com/AnyChart/playground.svg?token=ERMLfyrvWdA8g6gi11Vp&branch=staging)](https://travis-ci.com/AnyChart/playground) staging

## Architecture
The application consists of three parts:
* Web part - for serving http requets, the main site and the editor
* Generator - for parsing git repositories and put data to database
* Preview generator - generate images for samples

All parts communicate via Redis queues.


## Dependencies
```
sudo apt-get install postgresql postgresql-contrib
sudo apt-get install redis-server
sudo apt-get install phantomjs
sudo apt-get install pngquant
```

## External Resources
For now playground uses some of AnyChart resources, later we'll need to rework it to make Playground standalone project.
  
| Resource | Description |
| ------------- | ------------- |
| Data Sets [source file](https://static.anychart.com/cdn/anydata/common/index.json)  | JSON file what contains lists of available data sets.|
| [Tags list](https://static.anychart.com/utility/tags_list.json)  | JSON file what contains data for tags generating process. |
| [New Gallery](https://github.com/AnyChart/playground.anychart.com-gallery), [Chartopedia](https://github.com/AnyChart/Chartopedia.git), [API Reference](https://github.com/AnyChart/api.anychart.com.git), [Documentation](https://github.com/AnyChart/docs.anychart.com.git), [Templates](https://github.com/AnyChart/playground-templates.git) | Repositories with predefined samples, some of them are private. |
| [Chartopedia](https://github.com/AnyChart/Chartopedia.git) | Also chartopedia repo is used as a source for Chart Types pages. |


## Usage
Sample config file format:

```
[web]
# Web settings
port = 8080
# used for generation zip - for downloading in editor
zip-folder = "/apps/pg/data/zip/"


[db]
# Dababase settings
port = 5432
name = "playground_db"
user = "playground_user"
password = "playground_password"


[redis]
# redis settings are used for communication between web, generator 
# and preview-generator parts
port = 6379
host = "127.0.0.1"
db = 0
queue = "generator-queue-name"
preview-queue = "generator-preview-queue-name"


[notifications.slack]
# Slack notification settings
token = "token"
channel = "#playground_notifications"
username = "playground_bot"
# tag and domain are used in slack messages
domain = "http://playground.example.com/"
tag  = "playground"


[previews]
# settings for preview-generator
cdn-purge = true
cdn-prefix = "/pg/"
url-prefix = "http://playground.anychart.local/previews/pg/"
images-dir = "/apps/pg/data/previews"
phantom-engine = "/usr/bin/phantomjs"
generator = "/apps/pg/data/phantom.js"

[previews.maxcdn]
# maxcdn settings, used for purging images when new ones are generated
alias = "anychart"
key = "key"
secret = "secret"
zone-id = 12345


# Repositories settings
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

## Database
[Scheme](https://github.com/AnyChart/playground/blob/staging/src/sql/scheme_postgre.sql)


AnyChart © 2017
