## Application config

Application settings are stored in separate file, that uses [TOML](https://github.com/toml-lang/toml) format.
This is sample application config:

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