var global = {
	markers : {},
	capacity : {},
	type : {}
};

function capToColor(cap, type) {
	var color = "black";
	if (type == 1) {
		color = "#3A66A7";
	} else if (cap > 0) {
		if (cap <= 2) {
			color = "yellow";
		} else if (cap <= 5) {
			color = "yellowgreen"
		} else {
			color = "green";
		}
	}
	return color;
}

function addMarker(map, id, lat1, lng1, lat2, lng2, cap, type) {
	if (global.markers[id] == null) {
		var color = capToColor(cap, type);

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
		global.type[id] = type;

		google.maps.event.addListener(line, 'click', function() {
			loadForm(id);
		});
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
					obj["latitude2"], obj["longitude2"], obj["capacity"],
					obj["type"]);
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
	document.getElementById("type").value = global.type[id];
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
	var type = document.getElementById("type").value;

	if (id > 0) {
		var dbClient = new HttpClient();
		var json = "{" + "\"latitude1\":\"" + fromLat + "\","
				+ "\"longitude1\":\"" + fromLon + "\"," + "\"latitude2\":\""
				+ toLat + "\"," + "\"longitude2\":\"" + toLon + "\","
				+ "\"status\":\"" + status + "\"," + "\"capacity\":\"" + cap
				+ "\"," + "\"type\":\"" + type + "\"," + "\"id\":\"" + id
				+ "\"" + "}";
		 console.log(json);
		dbClient.postJson(SERVICE_CONTEXT + "/street/set", json, function(
				response) {
			console.log(response);
		});

		global.markers[id].setMap(null);
		if (status == 1) {
			var map = document.getElementById('map').gMap;
			global.markers[id] = null;
			addMarker(map, id, fromLat, fromLon, toLat, toLon, cap, type);
		}
	}
}

function reload() {
	var map = document.getElementById('map').gMap;
	var position = map.getCenter();
	initMarkers(map, position);
}
