var BO_CONTEXT = "/iBlue-BO";
var SERVICE_CONTEXT = "/iBlue-Service";

window.onload = function() {
	loc = window.location.pathname;
	loc = loc.replace(BO_CONTEXT + "/bo/", "");	
	sel = document.getElementById(loc + "-header-bullet");
	sel.classList.add("selected");
};

function getGreenToRedUsage(usage) {
	var percent = 100*usage;
	g = percent < 50 ? 255 : Math.floor(255 - (percent * 2 - 100) * 255 / 100);
	r = percent > 50 ? 255 : Math.floor((percent * 2) * 255 / 100);	
	return 'rgb(' + r + ',' + g + ',0)';
}

var HttpClient = function() {
	this.get = function(url, callback) {
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				callback(xhttp.responseText);
			}
		}
		xhttp.open("GET", url, true);
		xhttp.send();
	}

	this.postJson = function(url, json, callback) {
		var xhttp = new XMLHttpRequest();
		xhttp.open("post", url, true);
		xhttp.setRequestHeader('Content-Type',
				'application/json; charset=UTF-8');

		// send the collected data as JSON
		xhttp.send(json);

		xhttp.onloadend = callback(xhttp.responseText);
	}
}