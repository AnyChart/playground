## Repositories samples format
Such projects, like [Gallery](https://github.com/AnyChart/ACDVF-playground-samples),
[Chartopedia](https://github.com/AnyChart/Chartopedia.git), 
[API Reference](https://github.com/AnyChart/api.anychart.com.git),
[Documentation](https://github.com/AnyChart/docs.anychart.com.git),
[Templates](https://github.com/AnyChart/playground-templates.git)
use the format describing below for samples:

### TOML
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

### HTML
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