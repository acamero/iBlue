var global = {
	lastUpdate : 0,
	location : "",
	markers : {},
	capacity : {}
};

function capToColor(cap) {
	var color = "black";
	if (cap > 0) {

		if (cap <= 2) {
			color = "red";
		} else if (cap <= 5) {
			color = "yellow"
		} else {
			color = "green";
		}
	}
	return color;
}

function addMarker(id, lat1, lng1, lat2, lng2, cap) {
	// new google.maps.LatLng(37.7699298, -122.4469157);
	map = document.getElementById('map').gMap;

	var color = capToColor(cap);

	var line = new google.maps.Polyline({
		path : [ new google.maps.LatLng(lat1, lng1),
				new google.maps.LatLng(lat2, lng2) ],
		strokeColor : color,
		strokeOpacity : 1.0,
		strokeWeight : 5,
		map : map
	});

	global.markers[id] = line;
	global.capacity[id] = cap;

	google.maps.event.addListener(line, 'click', function() {
		loadForm(id);
	});
}

function initMap() {

	var map = new google.maps.Map(document.getElementById('map'), {
		zoom : 15
	});

	// Get the DOM Element
	var element = document.getElementById('map');
	// Create a random property that reference the map object
	element.gMap = map;

	var initialLocation = new google.maps.LatLng(36.719852, -4.419936);
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			initialLocation = new google.maps.LatLng(position.coords.latitude,
					position.coords.longitude);
			map.setCenter(initialLocation);

		});
	} else {
		map.setCenter(initialLocation);
	}
	global.location = initialLocation;

	initMarkers();
	global.lastUpdate = Date.now();
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

function initMarkers() {
	dbClient = new HttpClient();
	dbClient.get(SERVICE_CONTEXT + "/map/availability/" + global.location.lat()
			+ "/" + global.location.lng(), function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(obj["id"], obj["latitude1"], obj["longitude1"],
					obj["latitude2"], obj["longitude2"], obj["capacity"]);
		}
	})
}


function loadForm(id) {
	var pathArr = global.markers[id].getPath();
	document.getElementById("from-latitude").value = pathArr.getAt(0).lat()
			.toFixed(6);
	document.getElementById("from-longitude").value = pathArr.getAt(0).lng()
			.toFixed(6);
	document.getElementById("to-latitude").value = pathArr.getAt(1).lat()
			.toFixed(6);
	document.getElementById("to-longitude").value = pathArr.getAt(1).lng()
			.toFixed(6);
	document.getElementById("capacity").value = global.capacity[id];
	document.getElementById("street-id").value = id;
}

function updateStreet() {
	var fromLat = document.getElementById("from-latitude").value;
	var fromLon = document.getElementById("from-longitude").value;
	var toLat = document.getElementById("to-latitude").value;
	var toLon = document.getElementById("to-longitude").value;
	var status = document.getElementById("status").checked ? 1 : 0;
	var cap = document.getElementById("capacity").value;
	var id = document.getElementById("street-id").value;

	if (id > 0) {
		var dbClient = new HttpClient();
		var json = "{" + "\"latitude1\":\"" + fromLat + "\","
				+ "\"longitude1\":\"" + fromLon + "\"," + "\"latitude2\":\""
				+ toLat + "\"," + "\"longitude2\":\"" + toLon + "\","
				+ "\"status\":\"" + status + "\"," + "\"capacity\":\"" + cap
				+ "\"," + "\"id\":\"" + id + "\"" + "}";
		// console.log(json);
		dbClient.postJson(SERVICE_CONTEXT + "/street/set", json, function(
				response) {
			console.log(response);
		});

		global.markers[id].setMap(null);
		if (status == 1) {
			addMarker(id, fromLat, fromLon, toLat, toLon, cap);
		}
	}
}
