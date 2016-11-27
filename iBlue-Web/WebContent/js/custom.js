// Offset for Site Navigation
$('#siteNav').affix({
	offset: {
		top: 100
	}
})

function togglePlay(buttonId) {
	var audioId = buttonId.replace('play','audio');	
	audioElement = document.getElementById(audioId);
	if(audioElement.paused) {
		audioElement.play();
		var spanElement = document.getElementById(buttonId).getElementsByTagName('span')[0];
		spanElement.classList.remove("glyphicon-play");
		spanElement.classList.add("glyphicon-pause");
	} else {
		audioElement.pause();
		var spanElement = document.getElementById(buttonId).getElementsByTagName('span')[0];
		spanElement.classList.remove("glyphicon-pause");
		spanElement.classList.add("glyphicon-play");
	}
}
