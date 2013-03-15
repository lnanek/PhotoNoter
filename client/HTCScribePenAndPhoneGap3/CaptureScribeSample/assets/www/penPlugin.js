/**
 *  
 * @return Object literal singleton instance of PenPlugin
 */
var PenPlugin = function() {
	this.penData = new Object();
	this.penData.isPenEvent = false;
	this.penData.isPenButton1 = false;
	this.penData.isPenEButton2 = false;
	this.penData.penPressure = 0.0;	
};

/**
  * @param successCallback The callback which will be called when the pen action is attempted
  * @param failureCallback The callback which will be called when pen action encounters an error
  */
PenPlugin.prototype.isPenSupported = function(successCallback, failureCallback) {
	
	if ( "Android" != window.device.platform ) {
		successCallback(false);
	}

	return PhoneGap.exec(    
			successCallback,   //Success callback from the plugin
			failureCallback,   //Error callback from the plugin
			'PenPlugin',       //Tell PhoneGap to run "PenPlugin" Plugin
			'isPenSupported',  //Tell plugin, which action we want to perform
			[]);			   //Passing list of args to the plugin
};

/**
 * @param successCallback The callback which will be called when the pen action is attempted
 * @param failureCallback The callback which will be called when pen action encounters an error
 */
PenPlugin.prototype.startPenCaptureActivity = function(successCallback, failureCallback) {

	if ( "Android" != window.device.platform ) {
		successCallback(false);
	}

	return PhoneGap.exec(
			successCallback,            //Success callback from the plugin
			failureCallback,            //Error callback from the plugin
			'PenPlugin',                //Tell PhoneGap to run "PenPlugin" Plugin
			'startPenCaptureActivity',  //Tell plugin, which action we want to perform
			[]);			   			//Passing list of args to the plugin
};

/**
 * @param successCallback The callback which will be called when the pen action is attempted
 * @param failureCallback The callback which will be called when pen action encounters an error
 */
PenPlugin.prototype.startPaintingActivity = function(successCallback, failureCallback) {

	if ( "Android" != window.device.platform ) {
		successCallback(false);
	}

	return PhoneGap.exec(
			successCallback,            //Success callback from the plugin
			failureCallback,            //Error callback from the plugin
			'PenPlugin',                //Tell PhoneGap to run "PenPlugin" Plugin
			'startPaintingActivity',  	//Tell plugin, which action we want to perform
			[]);			   			//Passing list of args to the plugin
};
 
PhoneGap.addConstructor(function() {
	
	PhoneGap.addPlugin("penPlugin", new PenPlugin());

	var receivePenData = function(r) {
		window.plugins.penPlugin.penData = r;
	};
	
	var doNothing = function() {};
	
	return PhoneGap.exec(
			receivePenData,            	//Success callback from the plugin
			doNothing,            		//Error callback from the plugin
			'PenPlugin',                //Tell PhoneGap to run "PenPlugin" Plugin
			'registerForPenEvents',  	//Tell plugin, which action we want to perform
			[]);		

});
