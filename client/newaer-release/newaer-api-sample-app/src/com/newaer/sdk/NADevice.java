package com.newaer.sdk;

import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;

/**
 * <p>The representation of a device detected by the ProxPlatform.
 *
 * <p>This class also provides convenience methods for working with devices
 * the platform knows about.
 *
 * <p>An instance of NADevice is actively backed by the device in the system,
 * so any calls into the device return the current state of the device
 * as known to the system at the time of the call. The state of the device
 * may be updated at any time.
 *
 * <p>If your application needs to know when a device is updated you can
 * register an observer. This observer will be called when a device in the
 * system changes state, however which device is not specified via this interface.
 *
 * <p>The ProxPlatform utilizes the concept of "tagging" devices. Only devices
 * which are tagged can be used to trigger rules. It is possible to fetch recently
 * seen devices which have not yet been untagged, but how long we hold these is
 * unspecified.
 *
 * @author Originate
 *
 */
public abstract class NADevice extends NAObject<NADevice> {

	/**
	 * Only subclass construction.
	 */
	protected NADevice() {

	}

	//=-=-=-=-=-=-=-=-=-=-
	// STATIC CONVENIENCES
	//=-=-=-=-=-=-=-=-=-=-

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllTaggedDevices()
	 * NAPlatform.get(context).getTaggedDevices()}
	 *
	 * @param context the context to work in
	 * @return all tagged devices
	 */
	public static List<NADevice> getAllTagged(Context context) {
		return NAPlatform.get(context).getAllTaggedDevices();
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllTaggedDevicesByStatus(NADeviceStatus)
	 * NAPlatform.get(context).getAllTaggedDevicesByStatus(status)}
	 *
	 * @param context the context we are running in
	 * @param status the status of the devices to fetch
	 * @return all tagged devices with the requested status
	 */
	public static List<NADevice> getAllTaggedByStatus(Context context, NADeviceStatus status) {
		return NAPlatform.get(context).getAllTaggedDevicesByStatus(status);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getTaggedDevice(String)
	 * NAPlatform.get(context).getTaggedDevice(deviceId)}
	 *
	 * @param context the context to work in
	 * @param deviceId the id of the device to get
	 * @return the device or null if it is not found
	 */
	public static NADevice getTagged(Context context, String deviceId) {
		return NAPlatform.get(context).getTaggedDevice(deviceId);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllUntaggedDevicesByType(NADeviceType)
	 * NAPlatform.get(context).getAllUntaggedDevicesByType(type)}
	 *
	 * @param context the context to work in
	 * @param type the type of devices to get
	 * @return returns all non-tagged devices of the requested type
	 */
	public static List<NADevice> getAllUntaggedByType(Context context, NADeviceType type) {
		return NAPlatform.get(context).getAllUntaggedDevicesByType(type);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#registerDeviceObserver(DataSetObserver)
	 * NAPlatform.get(context).registerDeviceObserver(observer)}
	 *
	 * @param context the context to work in.
	 * @param observer the observer to register.
	 */
	public static void registerObserver(Context context, DataSetObserver observer) {
		NAPlatform.get(context).registerDeviceObserver(observer);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#unregisterDeviceObserver(DataSetObserver)
	 * NAPlatform.get(context).unregisterDeviceObserver(observer)}
	 *
	 * @param context the context to work in
	 * @param observer the observer to register.
	 */
	public static void unregisterObserver(Context context, DataSetObserver observer) {
		NAPlatform.get(context).unregisterDeviceObserver(observer);
	}

	//=-=-=-=-=-
	// INSTANCE
	//=-=-=-=-=-

	/**
	 * Returns the rules this device belongs to.
	 * @param context the context to work in.
	 * @return the rules this device belongs to.
	 */
	public abstract List<NARule> getRules(Context context);

	/**
	 * @return the type of the device
	 */
	public abstract NADeviceType getType();

	/**
	 * @return the current signal strength of the device
	 */
	public abstract double getSignalStrength();

	/**
	 * @return the status of the device
	 */
	public abstract NADeviceStatus getStatus();

	/**
	 * Returns a human friendly name for the device.
	 * For BlueTooth devices this returns the friendly name if it is known.
	 * For WiFi devices this returns the SSID if it is known.
	 * For NFC devices the system invents a friendly name.
	 *
	 * @return the name for this device.
	 */
	public abstract String getName();

	/**
	 * Returns true if this device is enabled for triggering rules. If this
	 * device is disabled, it will not be used to trigger in range or
	 * out of range actions.
	 * @return true if this device is enabled for triggering actions.
	 */
	public abstract boolean isEnabled();

	/**
	 * Returns the category of the device. These string are displayable
	 * to the user.
	 * @return a description of the "category" of device if available.
	 */
	public abstract String getCategory();

	/**
	 * Returns the number of power bars to display for this device, from
	 * zero to five.
	 * @return the number of power bars to display, 0 to 5.
	 */
	public abstract int getPowerbars();

	//////////////////////////////////////////////////////////
	// 						TAG
	//////////////////////////////////////////////////////////


	/* TODO: Groups Docs
	 * This
	 * method will throw an IllegatStateException if you attempt to
	 * untag a device that is in a NAGroup. Please remove a device
	 * from all groups before untagging.
	 * See {@link NAPlatform#getAllGroupsByDevice(String)
	 * NAPlatform.getAllGroupsByDevice(deviceId)}
	 */
	/**
	 * Sets this device as tagged, and thus watched for rules.
	 *
	 * @param context the context to work in
	 * @param tagged true if this device is tagged
	 */
	public abstract void setTagged(Context context, boolean tagged);

	/**
	 * Sets this device as enabled or disabled for triggering rules.
	 * @param context the context to work in
	 * @param enabled true if this device should trigger rules
	 */
	public abstract void setEnabled(Context context, boolean enabled);

	/**
	 * This is the default way to compare NADevices when sorting
	 * them in a list. The ordering is determined first by status, with in range
	 * followed by out of range, then unknown. Within the status groups, devices
	 * are sorted by name.
	 */

	@Override
	public int compareTo(NADevice other) {
			int order = this.getName().compareToIgnoreCase(other.getName());

			order = sortByStatus(this, other, order);

			return order;
	}

	/**
	 * Comparator for sorting by signal strength. The devices are first sorted by status,
	 * as with the default comparator, then by signal strength.
	 */
	public static final Comparator<NADevice> SIGNAL_STRENGTH_COMPARATOR = new Comparator<NADevice>() {
		@Override
		public int compare(NADevice lhs, NADevice rhs) {
			int order = rhs.getPowerbars() - lhs.getPowerbars();
			if (order == 0) {
				order = lhs.getName().compareToIgnoreCase(rhs.getName());
			}
			order = sortByStatus(lhs, rhs, order);
			return order;
		}
	};

	private static int sortByStatus(NADevice lhs, NADevice rhs, int order) {
		final int RIGHT_FIRST = 1;
		final int LEFT_FIRST = -1;

		boolean lhsUnknown = (lhs.getStatus() == NADeviceStatus.UNKNOWN);
		boolean rhsUnknown = (rhs.getStatus() == NADeviceStatus.UNKNOWN);
		boolean lhsInRange = (lhs.getStatus() == NADeviceStatus.IN_RANGE);
		boolean rhsInRange = (rhs.getStatus() == NADeviceStatus.IN_RANGE);

		// Both known
		if (!lhsUnknown && !rhsUnknown) {
			// Both disconnected?
			if (!lhsInRange && !rhsInRange) {
				// Only LHS is connected?
			} else if (lhsInRange && !rhsInRange) {
				order = LEFT_FIRST;
				// Only RHS is connected?
			} else if (!lhsInRange && rhsInRange) {
				order = RIGHT_FIRST;
			}
		} else if (lhsUnknown && !rhsUnknown) {
			order = RIGHT_FIRST;
		} else if (!lhsUnknown && rhsUnknown) {
			order = LEFT_FIRST;
		}
		return order;
	}

}
