[<img src="https://cdn.anychart.com/images/logo-transparent-segoe.png?2" width="234px" alt="AnyChart - Robust JavaScript/HTML5 Chart library for any project">](https://anychart.com)

# AnyChart Playground Engine

[AnyChart Playground](https://playground.anychart.com/) is an online tool for testing and showcasing user-created HTML, 
CSS and JavaScript code snippets. 


[![Build Status](https://travis-ci.com/AnyChart/playground.svg?token=ERMLfyrvWdA8g6gi11Vp&branch=master)](https://travis-ci.com/AnyChart/playground) 
[production](http://playground.anychart.com)

[![Build Status](https://travis-ci.com/AnyChart/playground.svg?token=ERMLfyrvWdA8g6gi11Vp&branch=staging)](https://travis-ci.com/AnyChart/playground) 
staging




## Architecture
The application backend is written on Clojure and consists of three parts:
* Web part - for serving http requets, the main site and the editor
* Generator - for parsing git repositories and put data to database
* Preview generator - for generating images for samples

All parts communicate via Redis queues.

The frontend is written on ClojureScript and consists of:
* Site
* Editor

The editor is a single page application built with Reagent and re-frame.
It uses [CodeMirror](https://codemirror.net/) as a code editor and [Tern](http://ternjs.net/) as a 
code-analysis engine for JavaScript.



## Dependencies
```
sudo apt-get install postgresql postgresql-contrib
sudo apt-get install redis-server
sudo apt-get install phantomjs
sudo apt-get install pngquant
# elasticsearch
# logstash
```
### PostgreSQL
PostgreSQL is used as a main storage.

[Database Schema](src/sql/schema_postgre.sql)

### Redis
Redis is used for communications between generators and Web part of application.

### PhantomJS and pngquant
PhantomJS is used for generating previews. Pngquant - for images optimizations.

### ElasticSearch
ElasticSearch is used for samples search.

### Logstash
To synchronize Elastic with Postgres you need to set up Logstash.

```
sudo bin/logstash-plugin install logstash-input-jdbc

sudo systemctl start logstash.service  # start 
sudo systemctl stop logstash.service   # stop
```
Config files are available [here](https://github.com/AnyChart/servers-conf/tree/master/playground-server/logstash/conf.d).




## External Resources
For now playground uses some of AnyChart resources, later we'll need to rework it to make Playground standalone project.
  
| Resource | Description |
| ------------- | ------------- |
| Data Sets [source file](https://static.anychart.com/cdn/anydata/common/index.json)  | JSON file what contains lists of available data sets.|
| [Tags list](https://static.anychart.com/utility/tags_list.json)  | JSON file what contains data for tags generating process. |
| [Gallery](https://github.com/AnyChart/ACDVF-playground-samples), [Chartopedia](https://github.com/AnyChart/Chartopedia.git), [API Reference](https://github.com/AnyChart/api.anychart.com.git), [Documentation](https://github.com/AnyChart/docs.anychart.com.git), [Templates](https://github.com/AnyChart/playground-templates.git) | Repositories with predefined samples, some of them are private. |
| [Chartopedia](https://github.com/AnyChart/Chartopedia.git) | Also chartopedia repo is used as a source for Chart Types pages. |


## Usage

[Application config](doc/config.md)

[Repositories samples format](doc/format.md)

[Editor API](doc/api.md)



## License
If you have any questions regarding licensing - please contact us. <sales@anychart.com>
