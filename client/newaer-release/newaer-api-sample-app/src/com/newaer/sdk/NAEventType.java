package com.newaer.sdk;

import android.content.Context;

import com.newaer.newaersampleapp.R;

/**
 * This class represents the type of events the platform can trigger
 * on devices.
 *
 * @author Originate
 *
 */
public enum NAEventType {
	/** The device came into range. **/
	ON_CONNECT(R.string.on_connect),
	/** The device went out of range. **/
	ON_DISCONNECT(R.string.on_disconnect);

	private final int resId;

	private NAEventType(final int resId) {
		this.resId = resId;
	}

	/**
	 * Returns a localized version of the status for consumption
	 * by user interfaces.
	 * @param context the context to work in
	 * @return a localized name for this event type.
	 */
	public String getName(Context context) {
		return context.getString(resId);
	}

}
