package com.newaer.sdk;

import android.content.Context;

import com.newaer.newaersampleapp.R;

/**
 * <p>This enum represents the types of rules that we support.
 * <p>Rules fire at different times based on their type. See the
 * documentation on the individual types for more information.
 *
 * @author Originate
 *
 */
public enum NARuleType {
	/**
	 * Rules with the ALL_DEVICES type fire {@link NAEventType#ON_CONNECT} when all
	 * devices in the group come in to range, and fire {@link NAEventType#ON_DISCONNECT}
	 * when the first device goes out of range.
	 */
	ALL(R.string.all_devices),

	/**
	 * <p>Rules with the EVERY_DEVICE type fire {@link NAEventType#ON_CONNECT} when any
	 * device in the group comes in range and fire {@link NAEventType#ON_DISCONNECT
	 * when any device in the group goes out of range.
	 */
	EVERY(R.string.every_device),

	/**
	 * <p>Rules with the FIRST_LAST type fire {@link NAEventType#ON_CONNECT} when
	 * the first device in the group comes in range and fires {@link NAEventType#ON_DISCONNECT}
	 * when all devices in the group (i.e. the last) has gone out of range.
	 */
	FIRST_LAST(R.string.first_last);

	private final int resId;

	private NARuleType(int res) {
		this.resId = res;
	}

	/**
	 * Returns a localized version of the status for consumption
	 * by user interfaces.
	 * @param context the context to work in
	 * @return a localized name for this rule type.
	 */
	public String getName(Context context) {
		return context.getString(resId);
	}

}
