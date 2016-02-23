/*Images used for download and upload tests retrieved from
 * Wikimedia Commons (http://commons.wikimedia.org/wiki/File:AsterNovi-belgii-flower-1mb.jpg)
 * under the Creative Commons Attribution-Share Alike 3.0 Unported license
 */

//Declaring global variables
var download_throughput;
var upload_throughput;
var local_ping;
var current_date;
var latitude;
var longitude;
var location_method;
var files_location = "http://62.217.125.88/wifimon/images/";
var pages_location = "http://62.217.125.88/wifimon/util/";
var agent = "http://62.217.125.88:9000/wifimon/";

// NetTest functions (Download-Upload-Ping)
NetTest.testDownload = function(imagePath, imageSize) {

 var handler = function(resultObj) {
  download_throughput = resultObj.throughput();
  NetTest.testUpload(files_location + "medium.jpg", 1271750);
  //NetTest.testPing(files_location + "tiny.gif", 653);
 }
 //NetTest.DOM.download(imagePath, imageSize, handler);
 NetTest.download(imagePath, imageSize, handler);
}

NetTest.testUpload = function(imagePath, imageSize) {
 var handler = function(resultObj) {
  upload_throughput = resultObj.throughput();
  NetTest.testPing(files_location + "tiny.gif", 653);
  //insertToPage();
 }
 NetTest.XHR.upload(imagePath, imageSize, handler);
}

NetTest.testPing = function(imagePath, imageSize) {
 var handler = function(resultObj) {
  local_ping = resultObj.round_trip();
  insertToPage();
 }
 NetTest.ping(imagePath, imageSize, handler, true)
}

// Get location through IP
function geoTest() {
 if (google.loader.ClientLocation) {
  latitude = google.loader.ClientLocation.latitude;
  longitude = google.loader.ClientLocation.longitude;
  location_method = "'Through IP'";
 }
}   

// Save to db
function insertToPage() {
 if (typeof download_throughput === 'undefined' || isNaN(download_throughput) || !download_throughput) {
  download_throughput = "Null";
 }
 if (typeof upload_throughput === 'undefined' || isNaN(upload_throughput) || !upload_throughput) {
  upload_throughput = "Null";
 }
 if (typeof local_ping === 'undefined' || isNaN(local_ping) || !local_ping) {
  local_ping = "Null";
 }
 if (typeof latitude === 'undefined' && typeof longitude === 'undefined') {
    latitude = "Null";
    longitude = "Null";
 }
 if (typeof location_method === 'undefined') {
  location_method = "Null";
 }

var measurement = {downloadThroughput: download_throughput, uploadThroughput: upload_throughput, localPing: local_ping, latitude: latitude, longitude: longitude, locationMethod: location_method};
$.ajax({
	type: "POST",
	data :JSON.stringify(measurement),
	url: agent + "add/",
	contentType: "application/json"
});
}

// Call to previous functions
NetTest.testGeneral = function() {
 geoTest();
 //NetTest.testUpload(files_location + "medium.jpg", 1271750);
 //NetTest.testDownload(files_location + "medium.jpg", 1271750);
 NetTest.testDownload(files_location + "large.jpg", 4811357);
}

//Set and check cookie 
function setCookie(cname,cvalue,exhours) {
 var d = new Date();
 d.setTime(d.getTime() + (exhours*60*60*1000));
 var expires = "expires=" + d.toGMTString();
 document.cookie = cname+"="+cvalue+"; "+expires;
}

function getCookie(cname) {
 var name = cname + "=";
 var ca = document.cookie.split(';');
 for(var i=0; i<ca.length; i++) {
  var c = ca[i];
  while (c.charAt(0)==' ') c = c.substring(1);
  if (c.indexOf(name) == 0) {
   return c.substring(name.length, c.length);
  }
 }
 return "";
}

function checkCookie() {
 var checkTest=getCookie("nettest");
 if (checkTest != "") {
 } else {
    setCookie("nettest", "Test Already Performed", 1.0/3600);
    NetTest.testGeneral(); //Run all functions
 }
}


function runCheckCookie() {
  $.ajax({
        url : pages_location+'validate_ip.php',
        type : 'POST',
  cache: false,
        success : function (result) {
            var check = result;
   if(check == "yes"){
    checkCookie();
   }else{
   }
        }
    })
}

checkCookie();