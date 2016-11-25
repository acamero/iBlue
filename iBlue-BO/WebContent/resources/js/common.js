var BO_CONTEXT = "/iBlue-BO";
var SERVICE_CONTEXT = "/iBlue-Service";

window.onload = function() {
	loc = window.location.pathname;
	loc = loc.replace(BO_CONTEXT + "/", "");
	if (loc.length == 0) {
		loc = "map";
	}
	sel = document.getElementById(loc + "-header-bullet");
	sel.classList.add("selected");
};

function getGreenToRedUsage(usage) {
	var percent = 100*usage;
	g = percent < 50 ? 255 : Math.floor(255 - (percent * 2 - 100) * 255 / 100);
	r = percent > 50 ? 255 : Math.floor((percent * 2) * 255 / 100);	
	return 'rgb(' + r + ',' + g + ',0)';
}