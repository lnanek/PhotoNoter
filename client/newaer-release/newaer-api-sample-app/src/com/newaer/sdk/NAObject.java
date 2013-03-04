package com.newaer.sdk;

import android.content.Context;

/**
 * <p>All domain objects within the NewAer ProxPlatform
 * have names and identifiers and have natural sort orders.
 *
 * <p>In most cases the sort order is based on the name.
 *
 * @author Originate
 *
 */
public abstract class NAObject<T> implements Comparable<T> {

	/**
	 * Only subclass construction.
	 */
	protected NAObject() {

	}

	/**
	 * <p>Returns the identifier for this object within the platform.
	 *
	 * <p>This identifier is unique on a per object type basis.
	 *
	 * @return the identifier for the object.
	 */
	public abstract String getId();

	/**
	 * Returns the name for this object within the platform.
	 * @return the name for the object.
	 */
	public abstract String getName();

	/**
	 * Change the name for this object.
	 *
	 * @param context the context to work in
	 * @param name the new name for this object.
	 */
	public abstract void setName(Context context, String name);

}
