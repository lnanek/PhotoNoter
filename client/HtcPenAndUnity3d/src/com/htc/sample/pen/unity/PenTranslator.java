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

import com.htc.pen.PenEvent;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Translates pen button presses into key presses.
 *
 */
public class PenTranslator {
	
	/**
	 * The tag to be output for logging messages.
	 */
	private static final String LOG_TAG = PenTranslator.class.getSimpleName();

    public static boolean translateTouchEvent(final Activity activity, final MotionEvent touchEvent) {
        if (!PenEvent.isPenEvent(touchEvent)) {
            if ( Flags.LOG ) {
            	Log.d(LOG_TAG, "translateTouchEvent ignoring non-pen event");
            }
        	return false;
        }
        
        final int penButton = PenEvent.PenButton(touchEvent);
        switch (penButton) {
            case PenEvent.PEN_BUTTON1:
                if ( Flags.LOG ) {
                	Log.d(LOG_TAG, "translateTouchEvent sending menu press");
                }
                simulateKeyPress(activity, KeyEvent.KEYCODE_MENU);
                break;
            case PenEvent.PEN_BUTTON2:
                if ( Flags.LOG ) {
                	Log.d(LOG_TAG, "translateTouchEvent sending back press");
                }
                simulateKeyPress(activity, KeyEvent.KEYCODE_BACK);
                break;
        }

        if ( Flags.LOG ) {
        	Log.d(LOG_TAG, "translateTouchEvent ignoring non-pen button event");
        }        
        return false;
    }

    private static void simulateKeyPress(final Activity activity, final int keyCode) {
        final KeyEvent resultKeyDownEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        activity.dispatchKeyEvent(resultKeyDownEvent);
        final KeyEvent resultKeyUpEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        activity.dispatchKeyEvent(resultKeyUpEvent);
    }

}
