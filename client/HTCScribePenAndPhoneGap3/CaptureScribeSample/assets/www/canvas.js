// Constants
var GREEN = "#00ff00";
var BLUE = "#0000ff";
var WHITE = "#ffffff";
var BLACK = "#000000";
var MIN_PEN_WIDTH = 2;
var MAX_PEN_WIDTH = 40;

// Page info
var canvas;
var context;
var offset;
var currentPenColor = GREEN;
var processedButton1 = false;
var lastX = -1;
var lastY = -1;
var pathStarted = false;
var lastWidth;

// Setup drawing
document.addEventListener('deviceready', init, true);
function init() {	
	window.plugins.penPlugin.isPenSupported(receiveIsPenSupportedResult, receiveIsPenSupportedError);
}

function receiveIsPenSupportedError(error) {
	console.log("error calling window.plugins.penPlugin.isPenSupported: " + error);
	receiveIsPenSupportedResult(false);
}

function receiveIsPenSupportedResult(result) {
	if (result) {
		// Show pen instructions.
		document.getElementById("penInstructions").style.display = "";
	}
	initCanvas();
}

function initCanvas() {

	canvas = document.getElementById('drawingSurface');
	canvas.style.width = '100%'
	canvas.width = canvas.offsetWidth;
	canvas.style.width = '';
	offset = getOffset(canvas);

	context = canvas.getContext('2d');

	canvas.addEventListener('touchmove', touchmove, false);
	canvas.addEventListener('touchend', touchend, false);
}

// No longer drawing a line.
function touchend() {
	lastX = -1;
	lasyY = -1;
	if ( pathStarted ) {
		context.closePath();
		pathStarted = false;
	}
}

// Extend line on touch.
function touchmove(ev) {

	// Prevent default behavior of trying to scroll window (which prevents receiving further events).
	ev.preventDefault();

	// Determine where was touched.
	var targetEvent = ev.touches.item(0);
	var x = targetEvent.clientX - offset.left;
	var y = targetEvent.clientY - offset.top;
	//console.log("x, y, isPenEvent = " + x + ", " + y + ", " + window.plugins.penPlugin.penData.isPenEvent)

	
	if ( -1 == lastX ) {
		lastX = x;
		lastY = y;
		return;
	}
	
	var drawColor;
	var drawWidth;
	
	// Draw a line that varies based on pen data for pen touches.
	if ( window.plugins.penPlugin.penData.isPenEvent ) {
		
		// Change color if first pen button is pressed.
		if ( window.plugins.penPlugin.penData.isPenButton1 ) {
			// If we haven't changed the color yet for this button press.
			if ( !processedButton1 ) {
				if ( GREEN == currentPenColor ) {
					currentPenColor = BLUE;
				} else {
					currentPenColor = GREEN;				
				}
				processedButton1 = true;
			}
		} else {
			// Button no longer being held, reset flag for if we handled button press.
			processedButton1 = false;
		}
		
		// If eraser button is being held down, draw separate white path.
		if ( window.plugins.penPlugin.penData.isPenButton2 ) {
			drawColor = WHITE;
			drawWidth = 20;
		} else {
			drawColor = currentPenColor;
			
			// The harder the pen is pushed against the screen, the larger the line drawn.
			var penThicknessVariability = MAX_PEN_WIDTH - MIN_PEN_WIDTH;
			var extraPenThicknessFromPressure = window.plugins.penPlugin.penData.penPressure * penThicknessVariability;
			var penThickness = MIN_PEN_WIDTH + extraPenThicknessFromPressure;
			drawWidth = penThickness;
		}
		
	// Draw a thick black line for the pen back or a finger.
	} else {
		drawColor = BLACK;
		drawWidth = 10;
	}

	context.strokeStyle = drawColor;
	context.lineWidth = drawWidth;

	if ( !pathStarted ) {
		context.beginPath();
		context.moveTo(lastX, lastY);
		pathStarted = true;
	} else if ( drawWidth != lastWidth ) {
		context.closePath();
		context.beginPath();
		context.moveTo(lastX, lastY);
	}
	context.lineTo(x, y);
	context.lineJoin = "round";
    context.lineCap = "round";
	context.stroke();
	
	lastX = x;
	lastY = y;
	lastWidth = drawWidth;
}

// Get location of an element on the page
function getOffset(el) {
	var _x = 0;
	var _y = 0;
	while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
		_x += el.offsetLeft - el.scrollLeft;
		_y += el.offsetTop - el.scrollTop;
		el = el.offsetParent;
	}
	return {
		top : _y,
		left : _x
	};
}
