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
package com.htc.sample.pen.unity;

import android.util.Log;
import android.view.MotionEvent;

import com.unity3d.player.UnityPlayerNativeActivity;

/**
 * Extends the native Unity3D Activity to check for HTC Scribe pen events.
 * To use, follow the instructions under the section called
 * "Extending the UnityPlayerActivity Java Code" here:
 * http://unity3d.com/support/documentation/Manual/Plugins.html
 *
 */
public class PenNativeUnityActivity extends UnityPlayerNativeActivity {
	
	/**
	 * The tag to be output for logging messages.
	 */
	private static final String LOG_TAG = PenNativeUnityActivity.class.getSimpleName();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ( Flags.LOG ) {
        	Log.d(LOG_TAG, "dispatchTouchEvent called");
        }
                
    	// If the current device supports the pen feature...
        if ( PenDetector.hasPenEvent(this) ) {
            // Then translate it or record its value for scripts to access.
            if ( PenLatch.INSTANCE.dispatchTouchEvent(this, ev) ) {
            	// Consume the event.
                if ( Flags.LOG ) {
                	Log.d(LOG_TAG, "dispatchTouchEvent consuming event used by pen logic");
                }
            	return true;
            }
        }
        
        // Otherwise, pass events on.
        return super.dispatchTouchEvent(ev);
    }

}