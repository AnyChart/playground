var page = new WebPage(), address, output, stop_message;
page.settings.resourceTimeout = 10000; 
var fs = require('fs');
var system = require('system');

page.viewportSize = {
    width: 868, //1736,
    height: 420//840
};

page.clearMemoryCache();

//310x150

/* phantom.exit(code)
          1: Invalid args was given to phantomjs
         10: Ok
         13: Chart throw smth unexpected in console
         14: Chart has some error and doesn't even render
         404: unknown our error - file not generated, but everything seems ok
*/


if (system.args.length != 4) {
    phantom.exit("01");
}else{
    address = system.args[1];
    output = system.args[2];
    stop_message = system.args[3];

    var code = '14';
    var triesCount = 0;
    var maxTriesCount = 5;

    var tryGenerate = function() {
        console.log('generating');
        if (page.render(output)) {
            phantom.exit('0');
        } else {
            triesCount++;
            if (triesCount < maxTriesCount) {
                window.setTimeout(tryGenerate, 1000);
            } else {
                phantom.exit('404');
            }
        }
    };

    page.onConsoleMessage = function(msg) {
        console.log('on console:', msg);
        if (msg.indexOf("chart draw") == 0) {
            tryGenerate();
        }
    };

    if (code=='14'){
		window.setTimeout(
		    function(){
		        console.log('timeout');
		        phantom.exit(code);
		    }, 12000);
	}
    console.log("open", address);

    // page.open(address);
    // cause .open fails with timeout sometimes
	var content = fs.read(address);
	page.setContent(content, "");
}