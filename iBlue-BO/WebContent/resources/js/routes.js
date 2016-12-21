var global = {
	polyline : '',
	toggle : true,
	marker1 : null,
	marker2 : null
};

function addMarker(map, id, lat, lng) {
	var path = global.polyline.getPath();
	path.push(new google.maps.LatLng(lat, lng));
	// console.log(id + " " + lat + " " + lng);

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

	var color = "green";
	var line = new google.maps.Polyline({
		strokeColor : color,
		strokeOpacity : 1.0,
		strokeWeight : 5,
		map : map
	});
	global.polyline = line;

	map.addListener('click', addOrigDest);
}

function addOrigDest(event) {
	
	var map = document.getElementById('map').gMap;	
	
	if(global.toggle) {
		global.toggle = false;
		document.getElementById("latitude1").value = event.latLng.lat().toFixed(6);
		document.getElementById("longitude1").value = event.latLng.lng().toFixed(6);
		if(global.marker1!=null) {
			global.marker1.setMap(null);
		}
		var marker = new google.maps.Marker({
			position : event.latLng,
			map : map,
			icon : BO_CONTEXT + '/resources/img/blue_MarkerA.png'
		});
		global.marker1 = marker;
	} else {
		global.toggle = true;
		document.getElementById("latitude2").value = event.latLng.lat().toFixed(6);
		document.getElementById("longitude2").value = event.latLng.lng().toFixed(6);
		if(global.marker2!=null) {
			global.marker2.setMap(null);
		}
		var marker = new google.maps.Marker({
			position : event.latLng,
			map : map,
			icon : BO_CONTEXT + '/resources/img/brown_MarkerA.png'
		});
		global.marker2 = marker;
	}
	
}

function initMarkers(map, position, fromLat, fromLon, toLat, toLon) {
	dbClient = new HttpClient();
	dbClient.get(SERVICE_CONTEXT + "/route/from/" + fromLat + "/" + fromLon
			+ "/to/" + toLat + "/" + toLon, function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);
			var obj = JSON.parse(jsonData[i]);
			addMarker(map, obj["id"], obj["latitude"], obj["longitude"]);
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

	global.polyline.getPath().clear();
	initMarkers(map, position, fromLat, fromLon, toLat, toLon);
}
