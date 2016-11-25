var global = {
	lastUpdate:0,
	markers:{}
};

function addMarker(lat, lng, mac) {
	// new google.maps.LatLng(37.7699298, -122.4469157);
	map = document.getElementById('map').gMap	
	var location = new google.maps.LatLng(lat, lng);	
	
	if(mac=="F4:09:D8:79:3F:76") {
		var marker = new google.maps.Marker({
			position : location,
			map : map,
			icon: 'resources/img/blue_MarkerA.png'
		});
	} else if(mac=="f8:e0:79:c8:63:95") {
		var marker = new google.maps.Marker({
			position : location,
			map : map,
			icon: 'resources/img/brown_MarkerA.png'
		});
	} else {
		var marker = new google.maps.Marker({
			position : location,
			map : map
		});
	}
	var key = lat+":"+lng;
	global.markers[key] = marker;
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
	dbClient.get(SERVICE_CONTEXT+"/spot/active", function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);			
			var obj = JSON.parse(jsonData[i]);			
			addMarker(obj["latitude"], obj["longitude"], obj["mac"]);
		}		
	})
}

function loadMarkers() {
	dbClient = new HttpClient();
	// first we release spots
	dbClient.get(SERVICE_CONTEXT+"/spot/release/"+global.lastUpdate, function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);			
			var obj = JSON.parse(jsonData[i]);			
			var key = obj["latitude"]+":"+obj["longitude"];
			if( key in global.markers ) {
				global.markers[key].setMap(null);
			}
		}		
	})
	// the we add the new spots
	dbClient.get(SERVICE_CONTEXT+"/spot/active/"+global.lastUpdate, function(response) {
		var jsonData = JSON.parse(response);
		for ( var i in jsonData) {
			// console.log(jsonData[i] + " " + i);			
			var obj = JSON.parse(jsonData[i]);			
			addMarker(obj["latitude"], obj["longitude"]);
		}		
	})
	global.lastUpdate = Date.now();
}

setInterval ( loadMarkers, 60000 );