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