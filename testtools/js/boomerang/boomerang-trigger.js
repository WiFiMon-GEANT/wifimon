// Variables declaration
var html = "";
var local_ping;
var download_throughput;
var upload_throughput;
var latitude;
var longitude;
var location_method;
var images_location;
var application = "boomerang";
var device = "";
var agent_ip = document.getElementById("settings").getAttribute("agentIp");
//--------------------------------------------
//Determine image location

if (typeof document.getElementById("settings").getAttribute("imagesLocation") === 'undefined' || document.getElementById("settings").getAttribute("imagesLocation") == '') {
    images_location = "https://fl-5-205.unil.cloud.switch.ch/wifimon/images/";
} else {
    images_location = document.getElementById("settings").getAttribute("imagesLocation");
}
//------------------------------------------------------
BOOMR.init({

    user_ip: 'agentIP',
    autorun: false, //don't run the page automatically
    BW: {
        base_url: images_location,
        block_beacon: true

    },
    RT: {
        cookie: 'RT',
    }

}); //end of BOOMR.init
//------------------------------------------------------
// Default time for cookie_time
if (typeof document.getElementById("settings").getAttribute("cookieTimeInMinutes") === 'undefined' || document.getElementById("settings").getAttribute("cookieTimeInMinutes") == '') {
    var cookie_time = 1.5;
} else {
    var cookie_time = parseFloat(document.getElementById("settings").getAttribute("cookieTimeInMinutes"));
}
//------------------------------------------------------
// Ports for http or https
if (typeof document.getElementById("settings").getAttribute("hostingWebsite") === 'undefined' || document.getElementById("settings").getAttribute("hostingWebsite") == null || document.getElementById("settings").getAttribute("hostingWebsite") == '' || document.getElementById("settings").getAttribute("hostingWebsite") == 'https') {
    var agent = "https://" + agent_ip + ":8443/wifimon/";
} else {
    var agent = "http://" + agent_ip + ":9000/wifimon/";
}
//-----------------------------------------------------------------------------------------------------------------
// Check for testtool
if (typeof document.getElementById("settings").getAttribute("testtool") === 'undefined' || document.getElementById("settings").getAttribute("testtool") == '') {
    var test_tool = "N/A";
} else {
    var test_tool = document.getElementById("settings").getAttribute("testtool");
}
//-----------------------------------------------------------------------------------------------------------------
//Set and check cookie
function setCookie(cname, cvalue, exhours) {
    var d = new Date();
    d.setTime(d.getTime() + (exhours * 60 * 60 * 1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

//------------------------------------------------------
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

//------------------------------------------------------
function checkCookie() {
    var checkTest = getCookie("Boomerang");
    if (checkTest != "") {
    } else {
        setCookie("Boomerang", "Test Already Performed", cookie_time / 60);
        console.log("DO THE TEST");
        BOOMR.page_ready();  // twra ektelese to test
    }
}

//------------------------------------------------------
//Run checkCookie function

function runCheckCookie() {
    $.ajax({
        url: agent + "subnet/",
        type: "POST",
        contentType: "application/json",
        cache: false,
        success: function (result) {
            var check = result;
            if (check == 'true') {
                checkCookie();
            } else {
            }
        }
    });
}

//------------------------------------------------------
runCheckCookie(); // call runCheckCookie function
//------------------------------------------------------
BOOMR.subscribe('before_beacon', function (o) {

    if (o.lat) {
        local_ping = o.lat;
    }

    if (o.bw) {
        //html += "Your bandwidth to this server is " + parseInt(o.bw/1024) + "kbps (&#x00b1;" + parseInt(o.bw_err*100/o.bw) + "%)<br>";
        console.log("Entering if o.bw");
        download_throughput = (Math.round((o.bw / 1000) * 10) / 10).toFixed(0);

//------------------------------------------------------
// Get location through IP

        function geoTest() {
            if (google.loader.ClientLocation) {
                latitude = google.loader.ClientLocation.latitude;
                longitude = google.loader.ClientLocation.longitude;
                location_method = "'Through IP'";
            }
        }

//------------------------------------------------------
//Device Info
        function detectDevice() {
            if (navigator.userAgent.match(/iPad/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/iPhone/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/iPod/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/Android/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/BlackBerry/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/Windows Phone/i)) {
                device = navigator.platform;
            } else if (navigator.userAgent.match(/webOS/i)) {
                device = navigator.platform;
            } else {
                device = navigator.platform;
            }

            return device; //prepei na perasei kai auto sto measurement
        }


//------------------------------------------------------
        detectDevice();
        geoTest();
//------------------------------------------------------
        postToAgent(local_ping, download_throughput, latitude, longitude, location_method, device, application);
//------------------------------------------------------
// Post measurement to agent

        function postToAgent(local_ping, download_throughput, latitude, longitude, location_method, device, application) {
            console.log("Will post to agent: local_ping=" + local_ping + " download_throughput=" + download_throughput +
                " latitude=" + latitude + " longitude=" + longitude + " location_method=" + location_method +
                " device=" + device + " application=" + application);
            if (typeof download_throughput === 'undefined' || isNaN(download_throughput) || !download_throughput) {
                download_throughput = 0;
            }
            if (typeof upload_throughput === 'undefined' || isNaN(upload_throughput) || !upload_throughput) {
                upload_throughput = 0;
            }
            if (typeof local_ping === 'undefined' || isNaN(local_ping) || !local_ping) {
                local_ping = 0;
            }
            if (typeof latitude === 'undefined' && typeof longitude === 'undefined') {
                latitude = 0;
                longitude = 0;
            }
            if (typeof location_method === 'undefined') {
                location_method = "N/A";
            }

            var measurement = {
                downloadThroughput: download_throughput,
                uploadThroughput: upload_throughput,
                localPing: local_ping,
                latitude: latitude,
                longitude: longitude,
                locationMethod: location_method,
                testTool: test_tool
            };

            $.ajax({
                type: "POST",
                data: JSON.stringify(measurement),
                url: agent + "add/",
                contentType: "application/json"
            });
        }//end postToAgent
//------------------------------------------------------
    }//end if o.bw
});//end BOOMR.subscribe
