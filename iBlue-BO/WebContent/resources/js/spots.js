var global = {
	lastUpdate : 0,
	markers : {},
	mac : {}
};

function addMarker(lat, lng, mac) {
	// new google.maps.LatLng(37.7699298, -122.4469157);
	map = document.getElementById('map').gMap
	var location = new google.maps.LatLng(lat, lng);

	if (mac == "F4:09:D8:79:3F:76") {
		var marker = new google.maps.Marker({
			position : location,
			map : map,
			icon : BO_CONTEXT + '/resources/img/blue_MarkerA.png'
		});
	} else if (mac == "f8:e0:79:c8:63:95") {
		var marker = new google.maps.Marker({
			position : location,
			map : map,
			icon : BO_CONTEXT + '/resources/img/brown_MarkerA.png'
		});
	} else if (mac == "backoffice") {
		var marker = new google.maps.Marker({
			position : location,
			map : map,
			icon : BO_CONTEXT + '/resources/img/orange_MarkerA.png'
		});
	} else {
		var marker = new google.maps.Marker({
			position : location,
			map : map
		});
	}
	var key = lat + ":" + lng;
	global.markers[key] = marker;
	global.mac[key] = mac;

	google.maps.event.addListener(marker, 'click', function() {
		loadForm(key);
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

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			initialLocation = new google.maps.LatLng(position.coords.latitude,
					position.coords.longitude);
			map.setCenter(initialLocation);

		});
	} else {
		var initialLocation = {
			lat : 36.719852,
			lng : -4.419936
		};
		map.setCenter(initialLocation);
	}

	initMarkers();
	global.lastUpdate = Date.now();
}


function initMarkers() {
	dbClient = new HttpClient();
	dbClient.get(SERVICE_CONTEXT + "/spot/active", function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(obj["latitude"], obj["longitude"], obj["mac"]);
		}
	})
}

/*
function loadMarkers() {
	dbClient = new HttpClient();
	// first we release spots
	dbClient.get(SERVICE_CONTEXT + "/spot/release/" + global.lastUpdate,
			function(response) {
				var jsonData = JSON.parse(response);
				for ( var i in jsonData) {
					// console.log(jsonData[i] + " " + i);
					var obj = JSON.parse(jsonData[i]);
					var key = obj["latitude"] + ":" + obj["longitude"];
					if (key in global.markers) {
						global.markers[key].setMap(null);
					}
				}
			})
	// the we add the new spots
	dbClient.get(SERVICE_CONTEXT + "/spot/active/" + global.lastUpdate,
			function(response) {
				var jsonData = JSON.parse(response);
				for ( var i in jsonData) {
					// console.log(jsonData[i] + " " + i);
					var obj = JSON.parse(jsonData[i]);
					addMarker(obj["latitude"], obj["longitude"]);
				}
			})
	global.lastUpdate = Date.now();
}

setInterval(loadMarkers, 60000);
*/

function loadForm(id) {
	var marker = global.markers[id].getPosition();
	document.getElementById("latitude").value = marker.lat().toFixed(6);
	document.getElementById("longitude").value = marker.lng().toFixed(6);
	document.getElementById("status").checked = true;
	document.getElementById("mac").value = global.mac[id];
}

function updateSpot() {
	var lat = document.getElementById("latitude").value;
	var lon = document.getElementById("longitude").value;
	var status = document.getElementById("status").checked ? 1 : 0;
	var mac = document.getElementById("mac").value;

	var dbClient = new HttpClient();
	var json = "{" + 
		"\"latitude\":\"" + lat + "\"," + 
		"\"longitude\":\"" + lon + "\"," + 
		"\"status\":\"" + status + "\"," + 
		"\"mac\":\"" + mac + "\"" + 
		"}";
	console.log(json);

	dbClient.postJson(SERVICE_CONTEXT + "/spot/set", json, function(response) {
		console.log(response);
	});

	var id = lat + ":" + lon;
	if (global.markers[id] != null) {
		global.markers[id].setMap(null);
	}
	if (status == 1) {
		addMarker(lat, lon, mac);
	}
}
