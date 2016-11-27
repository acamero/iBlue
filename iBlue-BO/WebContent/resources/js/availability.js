var global = {
	lastUpdate : 0,
	location : "",
	markers : {}
};

function addMarker(id, lat1, lng1, lat2, lng2, used, cap) {
	// new google.maps.LatLng(37.7699298, -122.4469157);
	map = document.getElementById('map').gMap

	var color = "black";
	if(cap>0) {
		var usage = used/cap;
		color = getGreenToRedUsage(usage);		
	}
	
	
	var line = new google.maps.Polyline({
		path : [ new google.maps.LatLng(lat1, lng1),
				new google.maps.LatLng(lat2, lng2) ],
		strokeColor : color,
		strokeOpacity : 1.0,
		strokeWeight : 5,
		map : map
	});

	global.markers[id] = line;
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
}

function initMarkers() {
	dbClient = new HttpClient();	
	dbClient.get( SERVICE_CONTEXT + "/map/availability/" + global.location.lat()
			+ "/" + global.location.lng(), function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(obj["id"], obj["latitude1"], obj["longitude1"],
					obj["latitude2"], obj["longitude2"], obj["inUseSpots"],
					obj["capacity"]);
		}
	})
}

