[<img src="https://cdn.anychart.com/images/logo-transparent-segoe.png?2" width="234px" alt="AnyChart - Robust JavaScript/HTML5 Chart library for any project">](https://anychart.com)
Playground
=========================

# Usage
## Config file

```
[web]
port = 8080

[db]
port = 3306
name = "pg_stg"
user = "pg_stg_user"
password = "pg_stg_pass"

[notifications.slack]
token = "P8Z59E0kpaOqTcOxner4P5jb"
channel = "#notifications"
samples-channel = "#samples"
username = "playground-engine"
domain = "http://pg.anychart.stg/"
tag  = "stg"


[[repositories]]
name = "api"
dir = "/apps/pg-stg/data/api"
url-prefex = "api"
generate-preview = true
samples-format = "html"
type = "ssh"

[repositories.ssh]
ssh = "git@github.com:AnyChart/api.anychart.com.git"
secret-key = "/apps/keys/id_rsa"
public-key = "/apps/keys/id_rsa.pub"
passphrase = ""

# or you can use https
[repositories.https]
https = "https://github.com/AnyChart/api.anychart.com.git"
login = "login"
password = "password"


[[repositories]]
name = "docs"
dir = "/apps/pg-stg/data/docs"
url-prefex = "docs"
generate-preview = false
samples-format = "html"
type = "https"

[repositories.ssh]
ssh = "git@github.com:AnyChart/docs.anychart.com.git"
secret-key = "/apps/keys/id_rsa"
public-key = "/apps/keys/id_rsa.pub"
passphrase = ""


[[repositories]]
name = "pg-samples"
dir = "/apps/pg-stg/data/gallery"
url-prefex = "pg-samples"
generate-preview = false
samples-format = "html"
type = "ssh"

[repositories.ssh]
ssh = "git@github.com:AnyChart/ACDVF-playground-samples.git"
secret-key = "/apps/keys/id_rsa"
public-key = "/apps/keys/id_rsa.pub"
passphrase = ""

```

## MySQL Scheme

[here](https://github.com/AnyChart/playground/blob/staging/src/sql/scheme.sql)


AnyChart © 2017
