/*
 * Copyright (C) 2011 HTC Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.sample.pen.phonegap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import co.spark.jajasdk.ConnectionStartedException;
import co.spark.jajasdk.JajaControlConnection;
import co.spark.jajasdk.JajaControlListener;

import com.htc.pen.PenEvent;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

/**
 * PhoneGap plugin which can be involved in following manner from Javascript
 * <p>
 * Result example on an HTC tablet with HTC Scribe pen technology -
 * true
 * </p>
 * <p>
 * Result example on any other device -
 * false
 * </p>
 * 
 * <pre>
 * {@code
 * 
 * var successCallback = function(result){
 *     //result is a boolean
 * }
 * var failureCallback = function(error){
 *     //error is error message
 * }
 * PenPlugin.isPenSupported(successCallback, failureCallback);
 * 
 * PenPlugin.startPenCaptureActivity(successCallback, failureCallback);
 * 
 * }
 * </pre>
 * 
 * <p>
 * The AndroidManifest.xml file in your PhoneGap project also needs this 
 * Activity reference added inside the application element:
 * <p>
 * 
 * <pre>
 * {@code
 * 
 * <activity android:name="com.htc.sample.pen.phonegap.PenCaptureActivity" />
 * 
 * }
 * </pre>
 */
public class PenPlugin extends Plugin {

	private static final String LOG_TAG = "PenPlugin";

	/**
	 * Action used to just check if the HTC Scribe pen is supported by the current device.
	 */
	public static final String IS_PEN_SUPPORTED_ACTION = "isPenSupported";
	
	public static final String REGISTER_ACTION = "registerForPenEvents";

	/**
	 * Action used to start the native Android Activity that uses the HTC Scribe pen API.
	 */
	public static final String START_PEN_CAPTURE_ACTIVITY_ACTION = "startPenCaptureActivity";

	/**
	 * Action used to start the native Android Activity that uses the HTC Scribe pen API.
	 */
	public static final String START_PAINTINT_ACTIVITY_ACTION = "startPaintingActivity";

    public boolean isJajaStarted;

	public JajaControlConnection jajaConnection;

	private Handler handler;
    
	/*
	 * (non-Javadoc)
	 * @see com.phonegap.api.Plugin#execute(java.lang.String,
	 * org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(final String action, final JSONArray data, final String callbackId) {

		Log.d(LOG_TAG, "Plugin Called");

		final boolean isPenSupportedAction = IS_PEN_SUPPORTED_ACTION.equals(action);
		final boolean isStartPenCaptureActivityAction = START_PEN_CAPTURE_ACTIVITY_ACTION.equals(action);
		final boolean isStartPaintingActivityAction = START_PAINTINT_ACTIVITY_ACTION.equals(action);
		final boolean isRegisterAction = REGISTER_ACTION.equals(action);

		final PluginResult result;
		// If one of our two valid actions was requested...
		if (isPenSupportedAction || isStartPenCaptureActivityAction || isStartPaintingActivityAction || isRegisterAction) {			

			// Check if the pen is supported.
			boolean isPenSupported = PenFeatureDetector.hasPenEvent(ctx);
			if (isPenSupported) {
				
				// If it is and starting the native Activity was requested, do so now.
				if (isStartPenCaptureActivityAction) {
					Intent intent = new Intent(ctx, PenCaptureActivity.class);
	            	ctx.startActivity(intent);			
				} else if (isStartPaintingActivityAction) {
					Intent intent = new Intent(ctx, DrawSignatureActivity.class);
	            	ctx.startActivity(intent);			
				} else if (isRegisterAction) {
					
					startJaja(callbackId);
					
					final View.OnTouchListener penListener = new View.OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							final boolean isPenEvent = PenEvent.isPenEvent(event);
							final int action = PenEvent.PenButton(event);
							final boolean isPenButton1 = (PenEvent.PEN_BUTTON1 == action);
							final boolean isPenButton2 = (PenEvent.PEN_BUTTON2 == action);
							final float penPressure = event.getPressure();
											
							sendPenEventToJavascript(callbackId, isPenEvent,
									isPenButton1, isPenButton2, penPressure); 
							
							return false;
						}

					};
					
					webView.setOnTouchListener(penListener);
										
					 PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT); 
					    pluginResult.setKeepCallback(true); 
					    return pluginResult; 					
				}
			}			
			// Return if the pen was supported.
			result = new PluginResult(Status.OK, isPenSupported);

		// Otherwise it is an error.
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
			Log.d(LOG_TAG, "Invalid action : " + action + " passed");
		}

		return result;

	}
	
	private void sendUpdate() {
		handler.sendMessage(new Message());
	}
	
	private void startJaja(final String callbackId) {
		Log.i(LOG_TAG, "startJaja()");
		if ( isJajaStarted ) {
			return;
		}
		isJajaStarted = true;
		
		

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				String text = "Pressure: "
						+ (jajaConnection.isSignalAvailable() ? Double.toString(jajaConnection.getSignalValue()) : "---") 
						+ "\nFirst button:"
						+ Boolean.toString(jajaConnection.isFirstButtonPressed())
						+ "\nSecond button:"
						+ Boolean.toString(jajaConnection.isSecondButtonPressed());				
				Log.i("PenPlugin JaJa", text);
				
				if ( !jajaConnection.isSignalAvailable() ) {
					sendPenEventToJavascript(callbackId, false,
							false, false, 0f); 		
					return;
				}
				
				final boolean isPenButton1 = jajaConnection.isFirstButtonPressed();
				final boolean isPenButton2 = jajaConnection.isSecondButtonPressed();
				final float penPressure = (float) jajaConnection.getSignalValue();
				final boolean isPenEvent = isPenButton1 || isPenButton2 || penPressure > 0;
								
				sendPenEventToJavascript(callbackId, isPenEvent,
						isPenButton1, isPenButton2, penPressure); 

			}
		};		
		
		
		jajaConnection = new JajaControlConnection();
		jajaConnection.setJajaControlListener(new JajaControlListener() {

			@Override
			public void signalValueChanged(double value) {
				
				sendUpdate();
			}

			@Override
			public void secondButtonValueChanged(boolean isPressed) {
				Log.i("UI","secondButtonValueChanged: "+isPressed);
				sendUpdate();
			}

			@Override
			public void firstButtonValueChanged(boolean isPressed) {
				Log.i("UI","firstButtonValueChanged: "+isPressed);
				sendUpdate();
			}

			@Override
			public void jajaControlSignalLost() {
				Log.e("UI","jajaControlSignalLost");
				sendUpdate();						
			}

			@Override
			public void jajaControlSignalRestored() {
				Log.e("UI","jajaControlSignalRestored");						
			}

			@Override
			public void jajaControlError() {
				Log.e("UI","jajaControlError");						
			}
		});
		try {
			jajaConnection.start();
		} catch (ConnectionStartedException e) {
			e.printStackTrace();
		}
	}
	
	private void sendPenEventToJavascript(
			final String callbackId,
			final boolean isPenEvent,
			final boolean isPenButton1,
			final boolean isPenButton2,
			final float penPressure) {
		JSONObject penData = new JSONObject();							
		try {
			penData.put("isPenEvent", isPenEvent);
			penData.put("isPenButton1", isPenButton1);
			penData.put("isPenButton2", isPenButton2);
			penData.put("penPressure", penPressure);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		 PluginResult result = new PluginResult(PluginResult.Status.OK, penData); 
		    result.setKeepCallback(true); 
		    success(result, callbackId);
	}

}
