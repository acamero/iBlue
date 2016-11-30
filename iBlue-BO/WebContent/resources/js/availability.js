var global = {
	markers : {}
};

function addMarker(map, id, lat1, lng1, lat2, lng2, used, cap, type) {
	if (global.markers[id] == null) {
		var color = "black";
		if (cap > 0) {
			var usage = used / cap;
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
}

function initMap() {

	var map = new google.maps.Map(document.getElementById('map'), {
		zoom : 15
	});

	// attach the map to the element
	var element = document.getElementById('map');
	element.gMap = map;

	// set the center and load markers
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			initialLocation = new google.maps.LatLng(position.coords.latitude,
					position.coords.longitude);
			map.setCenter(initialLocation);
			initMarkers(map, initialLocation);
		});
	} else {
		var initialLocation = new google.maps.LatLng(36.719852, -4.419936);
		map.setCenter(initialLocation);
		initMarkers(map, initialLocation);
	}

}

function initMarkers(map, position) {
	dbClient = new HttpClient();
	dbClient.get(SERVICE_CONTEXT + "/map/availability/" + position.lat() + "/"
			+ position.lng(), function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(map, obj["id"], obj["latitude1"], obj["longitude1"],
					obj["latitude2"], obj["longitude2"], obj["inUseSpots"],
					obj["capacity"], obj["type"]);
		}
	})
}

function reload() {
	var map = document.getElementById('map').gMap;
	var position = map.getCenter();
	initMarkers(map, position);
}