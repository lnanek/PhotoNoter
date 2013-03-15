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

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.util.Log;

/**
 * Detects the presence of the HTC Scribe pen API on the device.
 * <p>
 * This will return true for HTC tablets with the pen running Android
 * versions later than Gingerbread.
 *
 */
public class PenDetector {
	
	/**
	 * The tag to be output for logging messages.
	 */
	private static final String LOG_TAG = PenDetector.class.getSimpleName();

    private static final String PEN_EVENT_CLASS_NAME = "com.htc.pen.PenEvent";
    
    private static final String PEN_FEATURE = "android.hardware.touchscreen.pen";

    private static Boolean HAS_PEN_EVENT;

    private static final boolean hasFeature(final Context context) {
        for ( FeatureInfo feature : context.getPackageManager().getSystemAvailableFeatures() ) {
        	if ( feature.name.equals(PEN_FEATURE) ) {
        		return true;
        	}
        }
        return false;
    }

    private static final boolean hasClass(final Context context) {
        try {
            Class.forName(PEN_EVENT_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static final boolean hasPenEvent(final Context context) {
        if ( null == HAS_PEN_EVENT ) {
        	HAS_PEN_EVENT = hasFeature(context) && hasClass(context);
    	}
        if ( Flags.LOG ) {
        	Log.d(LOG_TAG, "hasPenEvent returning: " + HAS_PEN_EVENT);
        }
        return HAS_PEN_EVENT;
    }

}