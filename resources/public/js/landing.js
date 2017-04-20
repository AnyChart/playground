var landing = {};

landing.offset = 0;
landing.samplesPerPage = 12;
landing.page = 0;

// landing.end = true|false
// landing.version_id = 35|null

landing.initButtons = function(){
    var prevButton = $("#prevButton");
    var nextButton = $("#nextButton");
    prevButton.click(function(){
        console.log("Prev click: " + landing.page);
        landing.page--;
        landing.loadLandingSamples();
        landing.setButtonsVisibility();
    });
    nextButton.click(function(){
        console.log("Next click: " + landing.page);
        landing.page++;
        landing.loadLandingSamples();
        landing.setButtonsVisibility();
    });
}

landing.setButtonsVisibility = function () {
    var prevButton = $("#prevButton");
    var nextButton = $("#nextButton");
    if (landing.page == 0) {
        prevButton.hide();
    } else {
        prevButton.show();
    }
    if (landing.end) {
        nextButton.hide();
    } else {
        nextButton.show();
    }
}

landing.loadLandingSamples = function () {
    $.post("/landing-samples.json",
        {"offset": (landing.page * landing.samplesPerPage)},
        function (data) {
            console.log(data);
            landing.end = data.end;
            landing.setButtonsVisibility();
        }
    )
}

landing.initButtons();