var global = {
	markers : {}	
};

function addMarker(map, id, lat1, lng1, lat2, lng2) {
	if (global.markers[id] == null) {
		var color = "green";

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
			var initialLocation = new google.maps.LatLng(
					position.coords.latitude, position.coords.longitude);
			map.setCenter(initialLocation);
			
		});
	} else {
		var initialLocation = new google.maps.LatLng(36.719852, -4.419936);
		map.setCenter(initialLocation);
		
	}
}

function initMarkers(map, position, fromLat, fromLon, toLat, toLon) {
	dbClient = new HttpClient();
	dbClient.get(SERVICE_CONTEXT + "/route/from/" + fromLat + "/"
			+ fromLon + "/to/" + toLat + "/" + toLon, function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(map, obj["id"], obj["latitude1"], obj["longitude1"],
					obj["latitude2"], obj["longitude2"]);
		}
	})
}


function getRoute() {
	var map = document.getElementById('map').gMap;
	var fromLat = document.getElementById("latitude1").value;
	fromLat = parseFloat(fromLat).toFixed(6);
	var fromLon = document.getElementById("longitude1").value;
	fromLon = parseFloat(fromLon).toFixed(6);
	var toLat = document.getElementById("latitude2").value;
	toLat = parseFloat(toLat).toFixed(6);
	var toLon = document.getElementById("longitude2").value;
	toLon = parseFloat(toLon).toFixed(6);
	var position = new google.maps.LatLng(fromLat, fromLon);
	
	initMarkers(map, position, fromLat, fromLon, toLat, toLon);
}
