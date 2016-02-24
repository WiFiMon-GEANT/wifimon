/*
 * NetTest v0.9 <http://code.google.com/p/nettest/>
 * Copyright (c) 2008-2009 Artur Janc
 *
 * This software is released under the MIT License, available
 * at http://www.opensource.org/licenses/mit-license.php
 */

var NetTest = new Object();

NetTest.misc = new Object();
NetTest.XHR = new Object();
NetTest.DOM = new Object();
NetTest.URLRequest = new Object();
NetTest.LPF = new Object();

/******************************************/
NetTest.FlashPath = 'nettest.swf';
NetTest.testFilePath = "./files/";
NetTest.XHR.numPingTests = 3;
NetTest.lastResult = undefined;

/******************************************/
// Internal variables, do not meddle.

NetTest.networkReady = true;
NetTest.JSReady = false;
NetTest.ready = false;

NetTest.internalTestQueue = new Array();
NetTest.connectionClose = false;


NetTest.DOM.initialConnectionTime = 0;
NetTest.DOM.containerId = 'image_container';
NetTest.DOM._type = "";

// XHR
NetTest.XHR.downloadStart = undefined;
NetTest.XHR.downloadEnd = undefined;
NetTest.XHR.resourceURL = "";
NetTest.XHR.resourceSize = 0;
NetTest.XHR.pingResults = new Array();
NetTest.XHR.pingConnClose = false;


/* Create our container element */
document.write('<div id="browser_nettest_container"></div>');

// NetTest result object 
NetTest.result = function(params, summary) {
	// public
	this.type = ""; // upload, download, ping
	this.size = -1; // size of requested resource in bytes
	this.start_time = -1; // timestamp when request was made
	this.end_time = -1; // timestamp when reply was received
	this.url = ""; // URL of requested resource

	// method which was used to retrieve the resource. one of:
	// javascript/xhr, javascript/dom, flash/urlrequest, flash/lpf
	this.method = "";
	
	// private
	this._connection_close = false;

	// If an error occurred, this will be set
	this._error = "";
	this._warning = "";
	
	this._fromString = function(params) {
		params = params.split(/&/)

		for (var i = 0; i < params.length; i++) {
			items = params[i].split(/=/);
			if (items.length == 2) {
				this[items[0]] = items[1];
			}
		}
	
		return this;
	}

	if(params) {
		this._fromString(params, summary);
	}

	this.throughput = function() {
		if (this.type != 'download' && this.type != 'upload') {
			return -1;
		} else {
			return parseInt((this.size / 1024) /
			(parseInt(this.end_time - this.start_time) / 1000 ));
		}

	}

	this.units = function () {
		if (this.type != 'download' && this.type != 'upload') {
			return "ms";
		} else {
			return "KB/s"
		}
	}

	this.round_trip = function() {
		var rtt = this.end_time - this.start_time;
		if (this._connection_close) {
			rtt = rtt / 2;
		}

		return rtt;
	}

	this.error = function() {
		return this._error;
	}
	
	this.warning = function() {
		return this._warning;
	}
	
	this.verbose_summary = function() {
		
		var summary = "";
		if (this._connection_close) {
			summary = "* ";
		}

		summary += this.summary();
		
		if (this.warning()) {
			summary += '\nWarning: ' + this.warning();
		}

		return summary;
		
	}

	this.summary = function() {
		if (this.error()) {
			return "Error downloading: " + this.url + ".";
		}

		var summary = "";
		if (this.type == 'download') {
			
			summary += "Download rate from " + NetTest.misc.parseDomain(this.url)
				+ ": " + this.throughput() + " " + this.units() + " (" + this.size
				+ " bytes in " + this.round_trip() + "ms).\n"
		
		} else if (this.type == 'upload') {
			
			summary += "Upload rate to " + NetTest.misc.parseDomain(this.url)
				+ ": " + this.throughput() + " " + this.units() + " (" + this.size
				+ " bytes in " + this.round_trip() + "ms).\n"

		} else if (this.type == 'ping') {
			
			summary += "Round-trip time to " + NetTest.misc.parseDomain(this.url) + ": "
				+ this.round_trip() + this.units() + ".\n";
		}

		return summary;	
	}
	

}

NetTest.download = function(url, size, handler) {
	if (! NetTest.ready) { 
		return setTimeout(function() { NetTest.download(url, size, handler); }, 500);
	}

	NetTest.ready = false;

	var methods;
	if (NetTest.misc.getTargetDomain(url) == document.location.hostname) {
		// All methods could be here
		methods = new Array("XHR");
		if (NetTest.misc.isFlashAvailable()) {
			methods.push("URLRequest");
		}
	} else if (url.match(/\.(jpg|jpeg|png|gif)$/i)) {
		// If it's an image, use only the DOM method and pray.
		methods = new Array("DOM");
	} else {
		methods = new Array("DOM");
		if (NetTest.misc.isFlashAvailable()) {
			methods.push("LPF");
		}
	}

	NetTest.internalTestQueue = methods;
	NetTest.internalTestQueue.reverse();

	NetTest._url = url;
	NetTest._size = size;
	NetTest._handler = handler;
	NetTest._test_type = "download";

	NetTest._conductTest();
}

NetTest.upload = function(url, size, handler) {

	if (! NetTest.ready) { 
		return setTimeout(function() { NetTest.upload(url, size, handler); }, 500);
	}

	NetTest.ready = false;

	var methods;
	if (NetTest.misc.getTargetDomain(url) == document.location.hostname) {
		methods = new Array("XHR");
		
		if (NetTest.misc.isFlashAvailable()) {
			methods.push("URLRequest");
		}
	} else {
		// For cases where crossdomain.xml allows our request
		if (NetTest.misc.isFlashAvailable()) {
			methods = new Array("URLRequest");
		}
	}

	NetTest.internalTestQueue = methods;
	NetTest.internalTestQueue.reverse();

	NetTest._url = url;
	NetTest._size = size;
	NetTest._handler = handler;
	NetTest._test_type = "upload";

	NetTest._conductTest();
}

NetTest.ping = function(url, size, handler, connection_close) {

	if (! NetTest.ready) { 
		return setTimeout(function() { NetTest.ping(url, size, handler, connection_close); }, 500);
	}

	NetTest.ready = false;

	var methods;
	if (NetTest.misc.getTargetDomain(url) == document.location.hostname) {
		// All methods could be here
		methods = new Array("XHR", "DOM");
	} else if (url.match(/\.(jpg|jpeg|png|gif)$/i)) {
		// If it's an image, use only the DOM method and pray.
		methods = new Array("DOM");
	} else {
		methods = new Array("DOM");
		if (NetTest.misc.isFlashAvailable()) {
			methods.push("LPF");
		}
	}

	NetTest.internalTestQueue = methods;
	NetTest.internalTestQueue.reverse();

	NetTest._url = url;
	NetTest._size = size;
	NetTest._handler = handler;
	NetTest._test_type = "ping";

	NetTest.connectionClose = connection_close || false; 

	NetTest._conductTest();
}

NetTest._conductTest = function() {
	if(NetTest.internalTestQueue.length == 0) {
		// console.log('no methods to try');
		NetTest.ready = true;
		return;
	}
	
	var method = NetTest.internalTestQueue.pop();

	NetTest[method][NetTest._test_type](NetTest._url, NetTest._size, NetTest._handler);

}

/*********************************************************************************************/
// Misc. utility functions

NetTest.misc.getTargetDomain = function(url) {
	if (url.match(/^https?:\/\/([a-z0-9.\-]*)/i)) {
		var target_domain = RegExp.$1;
	} else {
		var target_domain = document.location.hostname;
	}

	return target_domain;
}

NetTest.misc.isJavaScriptReady = function() {
	return NetTest.JSReady;
}

NetTest.misc.startup = function() {
	// return NetTest.testAll();
}

NetTest.misc.quietEmbedSWF = function(path) {
	var swf_name = NetTest.misc.fileBasename(path);
	
	NetTest.misc.addInvisibleElement(swf_name);

	var params = { allowScriptAccess : "always", name : swf_name };
	var flashvars = false;
	var attributes = { name : swf_name};

	// TODO: remove this for production
	swfobject.embedSWF(path + '?' + NetTest.misc.getTime(), swf_name, "0", "0", "9.0.0",
		"expressInstall.swf", flashvars, params, attributes);
}

NetTest.misc.addInvisibleElement = function(id, type) {
	if (!document.body) {
		return setTimeout(function() { NetTest.misc.addInvisibleElement(id, type); }, 500);
	}

	if (document.getElementById(id)) {
		return false;
	}

	if (!type) {
		type = 'div';
	}

	var container_el = document.getElementById('browser_nettest_container');
	if (!container_el) {
		// console.log('creating container');
		var container_el = document.createElement('div');
		container_el.id = 'browser_nettest_container';
		container_el.style.display = 'none';

		document.body.appendChild(container_el);
		// var container_el = document.getElementById('browser_nettest_container');
	}
	var container_el = document.getElementById('browser_nettest_container');

	// console.log('creating: ' + id);
	var el = document.createElement(type);
	el.id = id;
	el.style.display = 'none';
	container_el.appendChild(el);

	return true;
}

NetTest.misc.fileBasename = function(path) {
	var s = path.replace(/.*\//, '').replace(/.swf/i, '');
	return s;
}

NetTest.misc.writeResultToTextarea = function(textarea_el, str) {
	var d = new Date();
	var h = d.getHours() >= 10 ? d.getHours() : "0" + d.getHours().toString();
	var m = d.getMinutes() >= 10 ? d.getMinutes() : "0" + d.getMinutes().toString();
	var s = d.getSeconds() >= 10 ? d.getSeconds() : "0" + d.getSeconds().toString();
	var nice_date = "(" + h + ":" + m + ":" + s + ") ";
	textarea_el.value += nice_date + str;
	textarea_el.scrollTop = textarea_el.scrollHeight;
}

NetTest.misc.getMovie = function(movieName) { 
 
	if (navigator.appName.indexOf("Microsoft") != -1) { 
		return window[movieName];
	} else { 
		return document[movieName]; 
	} 
} 

NetTest.misc.handleResult = function(resultObj) {

	if (NetTest.connectionClose) {
		resultObj._connection_close = true;
	}

	NetTest.lastResult = resultObj;

	// Warn about ping times with large resources
	if (resultObj.type == "ping" && resultObj.size > 1454) {
		resultObj._warning = "Object used for measuring ping is too large, real ping time might be lower.";
	}
	
	// If we're using LPF, issue a warning.
	if (resultObj.method.match(/lpf/i)) {
		resultObj._warning = "Could not determine if request succeeded, result may be invalid.";
	}

	// Clear queue of other methods if 
	if (!resultObj.error()) {
		NetTest.ready = true;
		NetTest.internalTestQueue = new Array();
		
		// Execute user function on object here
		if (NetTest._handler) {
			NetTest._handler(resultObj);
		}

	} else if (NetTest.internalTestQueue.length) {
		// There was an error, but continue to next method
		return NetTest._conductTest();
	} else {
		// nothing worked, return last result
		NetTest.ready = true;

		// Execute user function on object here
		if (NetTest._handler) {
			NetTest._handler(resultObj);
		}
	}
}

// This function gets called from ActionScript after the download is finished
NetTest.misc.sendToJavaScript = function(params) {
	// console.log(params);

	NetTest.networkReady = true;

	if (!params.match('flash/lpf')) {
		//summary_line = summary_line.replace(/url=/, document.location.hostname);
	} else {
		//summary_line = summary_line.replace('http://', '').replace('/:', ':');
		//summary_line = summary_line.replace('..:', document.location.hostname + ':');
	}
	
	if (!params.match('flash/lpf')) {
		// params = params.replace(/url=/, 'url=http://' + document.location.hostname);
	}
	
	result = new NetTest.result(params);
	NetTest.misc.handleResult(result);
}

NetTest.misc.sendTimestampToFlash = function () {
	var timestamp = new Date().getTime() + "";
	NetTest.misc.getMovie("nettest").uploadTimestamp(timestamp);
}

NetTest.misc.createXMLHttpRequest = function() {
    if (typeof XMLHttpRequest != "undefined") {
        return new XMLHttpRequest();
    } else if (typeof ActiveXObject != "undefined") {
        return new ActiveXObject("Msxml2.XMLHTTP");
    } else {
        throw new Error("XMLHttpRequest not supported");
    }
}

NetTest.misc.randomString = function(length)
{
	function generateBuffer1000() {

		var randomstring = "";

		for (var i=0; i<20; i++)
		{
			randomstring += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
			//var rnum = Math.floor(Math.random() * chars.length);
			//randomstring += chars.substring(rnum,rnum+1);
		}

		return randomstring;
	}

	var str = generateBuffer1000();
//	console.log(str.length);
	
	while (str.length < length) {
		str += (str + str + str + str);
	}

//	console.log(str.length);

	return str.substring(0, length);
}


NetTest.misc.addOnloadEvent = function(new_func) { 
	var original_onload = window.onload; 

	if (typeof window.onload != 'function') { 
		window.onload = new_func; 
	} else { 
		window.onload = function() { 
			if (original_onload) { original_onload(); } 
			new_func(); 
		} 
	} 
}

NetTest.misc.parseDomain = function(url) {
	if (!url) {
		return location.host
	} else if (url.match(/http:/)) {
		url.match(/(?:http:\/\/)?(.*?)\//);
		return RegExp.$1;
	} else {
		return location.host;
	}
}

NetTest.misc.getTime = function() {
	return (new Date()).getTime();
}

NetTest.misc.sendToActionScript = function(command, url, size, handler) {
	if (!NetTest.networkReady) {
		return setTimeout(function() { NetTest.misc.sendToActionScript(command, url, size, handler)}, 500);
	}

	if (!NetTest.LPF.ready() || !NetTest.URLRequest.ready()) {
		return setTimeout(function() { NetTest.misc.sendToActionScript(command, url, size, handler)}, 500);
	}

	if (handler) {
		NetTest._handler = handler;
	}
	
	NetTest.networkReady = false;

	NetTest.misc.getMovie("nettest").sendToActionScript(command, url, size);
}

NetTest.misc.isFlashAvailable = function() {
	if (navigator.mimeTypes && navigator.mimeTypes["application/x-shockwave-flash"])
		return true;
	else if (NetTest.URLRequest.ready())
		return true;
	else if (window.ActiveXObject) {
		for (x = 7; x <= 11; x++) {
			try {
				oFlash = eval("new ActiveXObject('ShockwaveFlash.ShockwaveFlash." + x + "');");
				if (oFlash) {
					return true;
				}
			}
			catch(e) { }
		}
	}
	else
		return false;
}

NetTest.misc.addOnloadEvent(function() {
	NetTest.misc.addInvisibleElement('image_container');
	NetTest.misc.quietEmbedSWF(NetTest.FlashPath);
	
	NetTest.networkReady = true;
	NetTest.JSReady = true;
	NetTest.ready = true;
	
	NetTest.misc.startup();
	});


/*****************************************************************************************/
// URLRequest

NetTest.URLRequest.ready = function() {
	var m = NetTest.misc.getMovie("nettest");
	return m && m.sendToActionScript;
}

NetTest.URLRequest.download = function (url, size, handler) {
	NetTest.misc.sendToActionScript('runURLRequestDownloadTest', url, size, handler);
}

NetTest.URLRequest.upload = function (url, size, handler) {
	NetTest.misc.sendToActionScript('runURLRequestUploadTest', url, size, handler);
}

NetTest.URLRequest.ping = function (url, size, handler) {
	NetTest.misc.sendToActionScript('runURLRequestPingTest', url, size, handler);
}

/*****************************************************************************************/



/*********************************************************************************************/
// LPF 
NetTest.LPF.ready = function() {
	var m = NetTest.misc.getMovie("nettest");
	return m && m.sendToActionScript;
}

NetTest.LPF.download = function(url, size, handler) {
	NetTest.misc.sendToActionScript('runLPFDownloadTest', url, size, handler);
}

NetTest.LPF.ping = function(url, size, handler) {
	NetTest.misc.sendToActionScript('runLPFPingTest', url, size, handler);
}


/*********************************************************************************************/

/*********************************************************************************************/
// DOM
NetTest.DOM.loadInitialImage = function (url, size) {
	NetTest.networkReady = false;

	var container_el = document.getElementById(NetTest.DOM.containerId);
	
	NetTest.DOM.initialConnectionTime = (new Date().getTime());

	var image_html = '<img src="' + url + '?' + (new Date().getTime()) +
		'" onload="NetTest.DOM.loadImage(\'' + url + '\', ' + size + ');" ' +
		'" onerror="NetTest.DOM.initialImageFail(this);">';

	container_el.innerHTML = image_html;
}

NetTest.DOM.initialImageFail = function(el) {
	NetTest.networkReady = true;

	var params = 
		'type=' + "ping" +
		'&start_time=' + NetTest.DOM.downloadStart +
		'&end_time=' + NetTest.DOM.downloadEnd +
		'&url=' + el.src +
		'&method=' + "javascript/dom";
		
	var resultObj = new NetTest.result(params)
	resultObj._error = true;

	return NetTest.misc.handleResult(resultObj);
	
}

NetTest.DOM.loadImage = function (url, size) {
	NetTest.networkReady = false;

	var container_el = document.getElementById(NetTest.DOM.containerId);

	NetTest.DOM.resourceURL = url;
	NetTest.DOM.resourceSize = size;
	
	NetTest.DOM.downloadStart = new Date().getTime();
	
	container_el.innerHTML = '<img src="' + url + '?' + NetTest.DOM.downloadStart +
		'" onload="NetTest.DOM.loadingComplete(this, \'load\');" ' +
		'" onerror="NetTest.DOM.loadingComplete(this, \'error\');" />';
}

NetTest.DOM.download = function(url, size, handler) {
	if (!NetTest.networkReady) { return setTimeout(function() { NetTest.DOM.download(url, size, handler); }, 500); }
	
	if (handler) {
		NetTest._handler = handler;
	}
	
	NetTest.DOM._type = "download";
	NetTest.DOM.loadImage(url, size);
}

NetTest.DOM.ping = function(url, size, handler) {
	if (!NetTest.networkReady) { return setTimeout(function() { NetTest.DOM.ping(url, size, handler); }, 500); }
	
	if (handler) {
		NetTest._handler = handler;
	}

	NetTest.DOM._type = "ping";
	NetTest.DOM.loadInitialImage(url, size);
}

NetTest.DOM.loadingComplete = function (el, res) {
	
	NetTest.networkReady = true;
	NetTest.DOM.downloadEnd = (new Date()).getTime();
	
	if (res == 'load') {
		var report = "";
		
		var bandwidth = parseInt((NetTest.DOM.resourceSize / 1024) /
			(parseInt(NetTest.DOM.downloadEnd - NetTest.DOM.downloadStart) * 0.001 )).toFixed(1);
		
		if (NetTest.DOM.resourceSize < 1454) {
			var type = NetTest.DOM._type;	
			
			report = "Round-trip time to " + NetTest.misc.parseDomain(NetTest.DOM.resourceURL) + ": "
				+ (NetTest.DOM.downloadEnd - NetTest.DOM.downloadStart) + "ms\n";
		} else {
			var type = NetTest.DOM._type;
			
			report = "Download rate from " + NetTest.misc.parseDomain(NetTest.DOM.resourceURL)
				+ ": " + bandwidth + " KB/s (" + NetTest.DOM.resourceSize + " bytes in "
				+ (NetTest.DOM.downloadEnd - NetTest.DOM.downloadStart) + "ms)\n"
		}
	
		// If the first request 
		if ((NetTest.DOM.downloadStart - NetTest.DOM.initialConnectionTime) <
				(NetTest.DOM.downloadEnd - NetTest.DOM.downloadStart)) {
			NetTest.DOM.downloadEnd = NetTest.DOM.downloadStart;
			NetTest.DOM.downloadStart = NetTest.DOM.initialConnectionTime;
		}

		
		var params = 
			'type=' + type +
			'&size=' + NetTest.DOM.resourceSize +
			'&start_time=' + NetTest.DOM.downloadStart +
			'&end_time=' + NetTest.DOM.downloadEnd +
			'&url=' + el.src +
			'&method=' + "javascript/dom";
		
		var resultObj = new NetTest.result(params)

		return NetTest.misc.handleResult(resultObj);


		
		// submitTestResults(log_str);
	} else if (res == 'error') {
		var params = 
			'type=' + 'download' +
			'&start_time=' + NetTest.DOM.downloadStart +
			'&end_time=' + NetTest.DOM.downloadEnd +
			'&url=' + el.src +
			'&method=' + "javascript/dom";
		
		var resultObj = new NetTest.result(params)
		resultObj._error = true;

		return NetTest.misc.handleResult(resultObj);
	}
}


/*****************************************************************************************/

/*****************************************************************************************/
// XHR

NetTest.XHR.download = function(url, size, handler) {
	NetTest.XHR.getURL(url, "download", handler);
}

NetTest.XHR.upload = function(url, size, handler) {

	return NetTest.XHR.postToURL(url, size, handler);
}

NetTest.XHR.ping = function(url, size, handler) {
	
	for (var i = 0; i < NetTest.XHR.numPingTests; i++) {
		NetTest.XHR.getURL(url, "ping_bundle", handler);
	}

	NetTest.XHR.pingResults = new Array();

	var pingResultsReady = function() {
		if(NetTest.XHR.pingResults.length == NetTest.XHR.numPingTests) {
			var total_response_time = 0;
			var min_response_time = 999999;

			var results = new Array();
			for (var i = 1; i < NetTest.XHR.pingResults.length; i++) {
				total_response_time += parseInt(NetTest.XHR.pingResults[i]);
				results.push(parseInt(NetTest.XHR.pingResults[i])); 
				if (parseInt(NetTest.XHR.pingResults[i]) < min_response_time) {
					min_response_time = parseInt(NetTest.XHR.pingResults[i]);
				}
			}

			var avg_response_time = total_response_time / (NetTest.XHR.pingResults.length - 1);

			var params = 
				'type=' + "ping" +
				'&size=' + NetTest.XHR.resourceSize +
				'&start_time=' + NetTest.XHR.downloadStart +
				'&end_time=' + NetTest.XHR.downloadEnd +
				'&url=' + document.location + "/../" + url +
				'&method=' + "javascript/xhr";

			var summary_line = "Round-trip time to " + NetTest.misc.parseDomain() + ": " + avg_response_time + "ms\n"
			var resultObj = new NetTest.result(params)
		
			if (NetTest.XHR.pingConnClose) {
				resultObj._connection_close = true;
			}

			// zero-out the array at the end
			NetTest.XHR.pingResults = new Array();
			NetTest.XHR.pingConnClose = false;
		

			return NetTest.misc.handleResult(resultObj);
		} else {
			setTimeout(function() { pingResultsReady() }, 200); 
		}

	}

	pingResultsReady();
}

NetTest.XHR.postToURL = function(url, size, handler) {

	// var detailed_text = NetTest.misc.getTime() + " Entered postToURL().\n"

	var request = NetTest.misc.createXMLHttpRequest();
	// detailed_text += NetTest.misc.getTime() + " Created XHMLHTTPRequest object.\n"

	if (!NetTest.networkReady) {
		return setTimeout(function() { NetTest.XHR.postToURL(url, size, handler) }, 200);
	} else {
		NetTest.networkReady = false;
	}
	
	if (handler) {
		NetTest._handler = handler;
	}
	
	var params = NetTest.misc.randomString(size);
	// detailed_text += NetTest.misc.getTime() + " Created POST parameters.\n"

	// var stateArray = new Array();
	try {
		request.open("POST", url + '?' + NetTest.misc.getTime(), true);
	} catch(e) {
		var params = '&method=' + "javascript/xhr" + '&url=' + url;

		resultObj = new NetTest.result(params);
		resultObj._error = true;
		NetTest.networkReady = true;
		return NetTest.misc.handleResult(resultObj);
	}

	NetTest.XHR.resourceURL = url;
	NetTest.XHR.resourceSize = size;
	// detailed_text += NetTest.misc.getTime() + " Opened XHMLHTTPRequest connection.\n"

	request.onreadystatechange = function() {
		// detailed_text += NetTest.misc.getTime() + " Received state change event.\n"
	
		if (request.readyState == 4) {
			NetTest.networkReady = true;
			
			if (request.status == 200) {
				
				NetTest.XHR.downloadEnd = NetTest.misc.getTime();
				// detailed_text += NetTest.XHR.downloadEnd + " Got HTTP 200 status.\n"
				
				var bandwidth = (size / 1024) / (parseInt(NetTest.XHR.downloadEnd
									- NetTest.XHR.downloadStart) * 0.001 );
				
				text = "Upload rate to " + NetTest.misc.parseDomain(url) + ": " +
					bandwidth.toFixed(1) + " KB/s (" + size + " bytes in " +
					(parseInt(NetTest.XHR.downloadEnd - NetTest.XHR.downloadStart)) + "ms)\n";
			
				var params = 
					'type=' + "upload" +
					'&size=' + size +
					'&start_time=' + NetTest.XHR.downloadStart +
					'&end_time=' + NetTest.XHR.downloadEnd +
					'&url=' + document.location + "/../" + url +
					'&method=' + "javascript/xhr";
				
				resultObj = new NetTest.result(params)
				return NetTest.misc.handleResult(resultObj);
			} else {
				var params = 
					'type=upload' +
					'&start_time=' + NetTest.XHR.downloadStart +
					'&end_time=' + NetTest.XHR.downloadEnd +
					'&method=' + "javascript/xhr" +
					'&url=' + url;

				// Problem downloading the file
				resultObj = new NetTest.result(params);
				resultObj._error = true;
				return NetTest.misc.handleResult(resultObj);
			}
		}
	}

	NetTest.XHR.downloadStart = NetTest.misc.getTime();
	request.send(params);
	// detailed_text += NetTest.misc.getTime() + " Sent POST parameters.\n"
}

NetTest.XHR.getURL = function(url, measurement_type, handler) {
	if (!NetTest.networkReady) {
		return setTimeout(function() { NetTest.XHR.getURL(url, measurement_type, handler) }, 200);
	}
	
	if (handler) {
		NetTest._handler = handler;
	}
	
	NetTest.networkReady = false;

	var request = NetTest.misc.createXMLHttpRequest();
	NetTest.XHR.pingConnClose = false;

	try {
		request.open("GET", url + '?' + NetTest.misc.getTime(), true);
	} catch(e) {
		var params = '&method=' + "javascript/xhr" + '&url=' + url;

		resultObj = new NetTest.result(params);
		resultObj._error = true;
		NetTest.networkReady = true;
		return NetTest.misc.handleResult(resultObj);
	}

	NetTest.XHR.resourceURL = url;

	request.onreadystatechange = function() {
	
		if (request.readyState == 4) {
			NetTest.networkReady = true;
			
			if (request.status == 200) {
				if(request.getResponseHeader('Connection').match(/close/i)) {
					NetTest.XHR.pingConnClose = true;
				}

				NetTest.XHR.downloadEnd = NetTest.misc.getTime();
				
				NetTest.XHR.resourceSize = request.responseText.length;
			
				if (measurement_type == "download") {
					var bandwidth = (NetTest.XHR.resourceSize / 1024) / 
						(parseInt(NetTest.XHR.downloadEnd - NetTest.XHR.downloadStart) * 0.001 );

					text = "Download rate from " + NetTest.misc.parseDomain(url)
						+ ": " + bandwidth.toFixed(1) + " KB/s (";
					text += NetTest.XHR.resourceSize + " bytes in " +
						(parseInt(NetTest.XHR.downloadEnd - NetTest.XHR.downloadStart)) + "ms)\n"
					
					var params = 
						'type=' + ((measurement_type.match(/ping/)) ? "ping" : "download") + 
						'&size=' + NetTest.XHR.resourceSize +
						'&start_time=' + NetTest.XHR.downloadStart +
						'&end_time=' + NetTest.XHR.downloadEnd +
						'&method=' + "javascript/xhr" +
						'&url=' + document.location + "/../" + url;
					
					resultObj = new NetTest.result(params)
					return NetTest.misc.handleResult(resultObj);
				} else if (measurement_type == "ping_bundle") {
					var time = parseInt(NetTest.XHR.downloadEnd - NetTest.XHR.downloadStart);
					NetTest.XHR.pingResults.push(time);
				}
			} else {
				var type = ((measurement_type.match(/ping/)) ? "ping" : "download");

				// Problem downloading the file
				var params = 
					'type=' + type + 
					'&start_time=' + NetTest.XHR.downloadStart +
					'&end_time=' + NetTest.XHR.downloadEnd +
					'&method=' + "javascript/xhr" +
					'&url=' + url;

				resultObj = new NetTest.result(params);
				resultObj._error = true;

				// Don't show twice for ping tests
				if (type == "ping") {
					if (NetTest.XHR.pingResults.length == 0) {
						NetTest.XHR.pingResults.push(-1);
						return NetTest.misc.handleResult(resultObj);
					}
				} else {
					return NetTest.misc.handleResult(resultObj);
				}
			}
		}
	}

	NetTest.XHR.downloadStart = NetTest.misc.getTime();
	request.send(null);
}

