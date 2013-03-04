package com.newaer.sdk;

import android.content.Context;

import com.newaer.newaersampleapp.R;

/**
 * The status of a device in the system.
 *
 * @author Originate
 *
 */
public enum NADeviceStatus {
	/** The unknown state, if a radio is off or equivalent. **/
	UNKNOWN(R.string.unknown),
	/** The in range state, if a device is visible. **/
	IN_RANGE(R.string.in_range),
	/** Out of range, if a device is not seen in the scans. **/
	OUT_OF_RANGE(R.string.out_of_range);

	private final int stringId;

	private NADeviceStatus(int stringId) {
		this.stringId = stringId;
	}

	/**
	 * Returns a localized version of the status for consumption
	 * by user interfaces.
	 * @param context the context to use to get the localized string.
	 * @return a localized version of the status.
	 */
	public String getName(Context context) {
		return context.getString(stringId);
	}
}
