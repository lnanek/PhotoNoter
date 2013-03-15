
// Call init function once done loading.
document.addEventListener('deviceready', init, true);

function init () {

	document.getElementById("eventSubmit").onclick = startEventReadout;

	document.getElementById("paintingViewSubmit").onclick = startPainting;

	document.getElementById("canvasSubmit").onclick = startCanvas;
	
	// Start check if we're on a device with the HTC Scribe pen.
	window.plugins.penPlugin.isPenSupported(receiveIsPenSupportedResult, receiveIsPenSupportedError);
}

function receiveIsPenSupportedError(error) {
	console.log("error calling window.plugins.penPlugin.isPenSupported: " + error);
	receiveIsPenSupportedResult(false);
}

function receiveIsPenSupportedResult(result) {
	if (result) {
		// Enable buttons for HTC Scribe only functionality.
		document.getElementById("eventSubmit").disabled = false;
		document.getElementById("paintingViewSubmit").disabled = false;
	} else {
		// Show messages letting user know they can run it on another device for cool extras!
		document.getElementById("paintingViewNotSupportedMessage").style.display = "";
		document.getElementById("penEventNotSupportedMessage").style.display = "";	
	}	
}

function startCanvas() {
	
	window.location.href = "canvas.html";
	
	return false;
}

function startPainting() {
	
	window.plugins.penPlugin.startPaintingActivity(function(r) {
		console.log("result of window.plugins.penPlugin.startPenCaptureActivity: " + r)
	}, function(e) {
		console.log("error calling window.plugins.penPlugin.startPenCaptureActivity: " + e)
	});
	
	return false;
}

function startEventReadout() {
	
	window.plugins.penPlugin.startPenCaptureActivity(function(r) {
		console.log("result of window.plugins.penPlugin.startPenCaptureActivity: " + r)
	}, function(e) {
		console.log("error calling window.plugins.penPlugin.startPenCaptureActivity: " + e)
	});
	
	return false;
}
