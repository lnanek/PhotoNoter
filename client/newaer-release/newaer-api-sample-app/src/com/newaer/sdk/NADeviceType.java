package com.newaer.sdk;

import android.content.Context;

import com.newaer.newaersampleapp.R;

/**
 * This class represents the types of devices that the platform knows how
 * to detect.
 *
 * @author Originate
 *
 */
public enum NADeviceType {
	/** A BlueTooth device. **/
	BLUETOOTH(R.string.bluetooth),
	/** A WiFi device. **/
	WIFI(R.string.wifi),
	/** An NFC device. **/
	NFC(R.string.nfc);

	private final int stringId;

	private NADeviceType(final int stringId) {
		this.stringId = stringId;
	}

	/**
	 * Returns a localized version of the type for consumption
	 * by user interfaces.
	 * @param context the context to use to get the localized name.
	 * @return a localized name for this type
	 */
	public String getName(Context context) {
		return context.getString(stringId);
	}

}
