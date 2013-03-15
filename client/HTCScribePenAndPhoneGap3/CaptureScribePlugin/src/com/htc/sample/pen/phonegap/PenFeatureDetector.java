/*
 * Copyright (C) 2012 HTC Corporation
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

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Detects the presence of the HTC Scribe pen API on the device.
 * <p>
 * This will return true for HTC tablets with the pen running Android versions
 * later than Gingerbread.
 * 
 */
public class PenFeatureDetector {

	/**
	 * Accesses methods that exist only on Android API level 5 or later.
	 * Do not reference this class on earlier versions of Android where
	 * it will cause a VerifyError.
	 */
	private static class SDK5Operations {
		
		/**
		 * Determines if this device has the pen hardware feature.
		 * 
		 * @param context Context used to access PackageManager
		 * @return true if running on a device with pen hardware
		 */
		private static boolean hasPenFeature(final Context context) {
			final PackageManager pm = context.getPackageManager();
			for (final FeatureInfo feature : pm.getSystemAvailableFeatures()) {
				if (PEN_FEATURE_NAME.equals(feature.name)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * The name of a class used in the HTC Scribe pen API.
	 */
	private static final String PEN_EVENT_CLASS_NAME = "com.htc.pen.PenEvent";

	/**
	 * The name of the Android feature that indicates a pen.
	 */
	private static final String PEN_FEATURE_NAME = "android.hardware.touchscreen.pen";

	/**
	 * Determines if this device has support for the HTC Scribe pen.
	 * @param context Context used to access PackageManager
	 * @return true if running on a device with the HTC Scribe pen supported
	 */
	public static boolean hasPenEvent(final Context context) {
		return hasHtcPenEventClass(context) && hasPenFeature(context);
	}

	/**
	 * Determines if this device has the HTC PenEvent class used to provide low level pen data.
	 * 
	 * Warning: as of Sense 3.6 this returns true even on phones without pen support.
	 * 
	 * @param context Context used to access PackageManager
	 * @return true if running on a device with the HTC PenEvent class
	 */
	private static boolean hasHtcPenEventClass(final Context context) {
		try {
			if (null != Class.forName(PEN_EVENT_CLASS_NAME)) {
				return true;
			}
			return false;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Determines if this device has the pen hardware feature.
	 * 
	 * @param context Context used to access PackageManager
	 * @return true if running on a device with pen hardware
	 */
	private static boolean hasPenFeature(final Context context) {
		// Return false if the version of Android doesn't have the needed method.
		if (Integer.parseInt(Build.VERSION.SDK) < 5) {
			return false;
		}
		// Otherwise check for the feature.
		return SDK5Operations.hasPenFeature(context);
	}

}
