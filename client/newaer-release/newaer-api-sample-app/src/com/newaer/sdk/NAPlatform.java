package com.newaer.sdk;

import java.util.List;

import com.newaer.sdk.impl.StaticBinder;
import android.app.Application;
import android.content.Context;
import android.database.DataSetObserver;


/**
 * <p>This is the NewAer Proximity Platform. It contains methods for initializing
 * the platform, getting the platform for a given context, enabling, starting and
 * stopping scanning for various device types.
 *
 * <p>It also contains methods for getting and creating devices, rules, actions and plugins.
 * However, we have provided static convenience methods, which are almost always shorter
 * to type, especially if you don't already have a platform instance. We suggest you use
 * those.
 *
 * <p>NewAer uses <a href="http://semver.org/">Semantic Versioning</a>.
 *
 * @author Originate
 *
 */
public abstract class NAPlatform {

	/**
	 * <p>The major version number of the platform.
	 */
	public static final int SDK_VERSION_MAJOR = 1;
	/**
	 * <p>The minor version number of the platform.
	 */
	public static final int SDK_VERSION_MINOR = 0;
	/**
	 * <p>The patch level of the platform.
	 */
	public static final int SDK_VERSION_PATCH = 0;

	/**
	 * Only subclasses may construct.
	 */
	protected NAPlatform() {

	}

	public String getVersion() {
		return SDK_VERSION_MAJOR + "." + SDK_VERSION_MINOR + "." + SDK_VERSION_PATCH;
	}

	/**
	 * <p>Returns the platform associated with this context.
	 *
	 * @throws IllegalStateException if the platform has not yet been initialized.
	 * @param context the context to get a platform for
	 * @return the NAPlatform instance for this context.
	 */
	public static NAPlatform get(Context context) {
		return StaticBinder.getPlatform(context);
	}

	/**
	 * <p>Initializes the platform. All device types are disabled by default.
	 *
	 * <p>This method is most easily called in your {@link Application#onCreate()
	 * Application's onCreate() method to initialize the NewAer Platform.
	 * You can extend {@link NASdkApp} to make it easy to call this
	 * method at the correct time for your application.
	 *
	 * @throws an IllegalStateException if the platform has already been initialized.
	 * @param context the context to initialize in
	 * @param appKey the application key
	 */
	public static void init(Context context, String appKey) {
		StaticBinder.initPlatform(context, appKey, null);
	}

	/**
	 * <p> Initialize the platform with specified device types enabled. Once setEnabled(type)
	 * has been called, that value will be persist.
	 *
	 * @throws IllegalStateException if the platform has already been initialized.
	 * @param context the context to initialize for
	 * @param appKey the application key
	 * @param defaultEnabledTypes device types to enable by default
	 */
	public static void init(Context context, String appKey, List<NADeviceType> defaultEnabledTypes) {
		StaticBinder.initPlatform(context, appKey, defaultEnabledTypes);
	}


	/**
	 * <p> Shut down the platform, stopping all scans and dereferencing. This will throw an exception
	 * if the platform is not current running.
	 *
	 * @throws IllegalStateException If there is no current instance of the platform
	 * @param context that the platform was created in
	 */
	public static void destroy(Context context) {
		StaticBinder.destroyPlatform(context);
	}

	/**
	 * <p> Check to see if an instance of NewAer is currently operation
	 *
	 * @param context that the platform was created in
	 * @return true if the platform exists
	 */
	public static boolean isInitialized(Context context) {
		return StaticBinder.isPlatformInitialized(context);
	}

	//=-=-=
	// SCAN
	//=-=-=

	/**
	 * <p>Returns if scanning is enabled for the given type. This
	 * is not applicable for the NFC radio and will throw an Illegal
	 * Argument exception if passed the NADeviceType NFC.
	 *
	 * @param type the type to check for
	 * @return true if the type is available.
	 */
	public abstract boolean isEnabled(NADeviceType type);


	/**
	 * <p>Returns true if scans are running for the given device
	 * type, i.e. the system radio is turned on. The type must
	 * also be enabled in order to scan.  See {@link
	 * NAPlatform#setEnabled(NADeviceType, boolean) setEnabled(type)}
	 * and
	 * @param type type to inquire about
	 * @return true if scan is running, false otherwise
	 */
	public abstract boolean isScanning(NADeviceType type);

	/**
	 * <p> Enable or disable scanning for the given device type. This
	 * is not applicable for the NFC radio and will throw an Illegal
	 * Argument exception if passed NADeviceType.NFC.
	 * @param type the device type to set
	 * @param enabled the state to set to
	 */
	public abstract void setEnabled(NADeviceType type, boolean enabled);

	/**
	 * <p> Enable or disable scans for all device types
	 * @param enabled
	 */
	public abstract void setEnabled(boolean enabled);

	//=-=-=-=
	// DEVICE
	//=-=-=-=

	/**
	 * <p>Returns all tagged devices.
	 *
	 * <p>This items in this list are actively managed by the platform. This means that the state
	 * of devices in this list may change at any time. However, all changes to the state of
	 * this list is done on android's ui thread. This means that you can use the list returned
	 * by this method directly in a list adapter and pass the platform the observer to cause
	 * the ui to be refreshed when the list changes.
	 *
	 * <p>The list returned is unmodifiable. It also uses weakly consistent iterators. This means that
	 * an iterator may see devices which are no longer tagged, unless you are
	 * using the iterator on the ui thread. Such a use is not recommended.
	 *
	 * <p>If you need to know when this list or items in this list change you need to register
	 * a DataSetObserver with the platform.
	 *
	 * @return all tagged devices
	 */
	public abstract List<NADevice> getAllTaggedDevices();

	/**
	 * <p>Returns a list with all devices in the requested state.
	 *
	 * <p>This items in this list are actively managed by the platform. This means that the state
	 * of devices in this list may change at any time. However, all changes to the state of
	 * this list is done on android's ui thread. This means that you can use the list returned
	 * by this method directly in a list adapter and pass the platform the observer to cause
	 * the ui to be refreshed when the list changes.
	 *
	 * <p>The list returned is unmodifiable. It also uses weakly consistent iterators. This means that
	 * an iterator may see devices which are no longer in the requested state, unless you are
	 * using the iterator on the ui thread. Such a use is not recommended.
	 *
	 * <p>If you need to know when this list or items in this list change you need to register
	 * a DataSetObserver with the platform.
	 *
	 * @return all in range tagged devices
	 */
	public abstract List<NADevice> getAllTaggedDevicesByStatus(NADeviceStatus status);

	/**
	 * <p>Returns a single tagged device based on the device id.
	 *
	 * <p>If you need to know when this device changes you need to register
	 * a DataSetObserver with the platform, however, that will notify you for
	 * any change to a device within the platform.
	 *
	 * @param deviceId the id of the device to get
	 * @return the device or null if it is not found
	 */
	public abstract NADevice getTaggedDevice(String deviceId);

	/**
	 * <p>Returns a collection of non-tagged devices the platform has recently seen. Devices
	 * in this list may not be in subsequent calls to the method.
	 *
	 * <p>This devices in this list are actively managed by the platform. This means that the state
	 * of devices in this list may change at any time.
	 *
	 * <p>This list is not currently actively managed by the platform, but represents a
	 * snapshot of recently seen devices at the time the call is made. If you would like
	 * to see new untagged devices you will need to request a new list.
	 *
	 * <p>This list is intended to allow applications to display a list of recently seen
	 * devices for tagging.
	 *
	 * @param type the type of devices to get
	 * @return returns all non-tagged devices of the requested type
	 */
	public abstract List<NADevice> getAllUntaggedDevicesByType(NADeviceType type);

	/**
	 * <p>Register an observer to be notified whenever a change
	 * occurs on a device or device list.
	 *
	 * <p>The observer will be called when
	 * any of the get device lists has a change in membership or state
	 * of a member.
	 *
	 * @param observer the observer to register.
	 */
	public abstract void registerDeviceObserver(DataSetObserver observer);

	/**
	 * Unregister a device observer.
	 *
	 * @param observer the observer to register.
	 */
	public abstract void unregisterDeviceObserver(DataSetObserver observer);

	//=-=-=-
	// GROUP
	//=-=-=-
// TODO: GROUPS DISABLED
//
//	/**
//	 * Constructs a new group and returns it.
//	 *
//	 * @param name to be set for the group.
//	 * @return The generated id(unique) for this group in the db, -1 if something went wrong.
//	 */
//	public abstract NAGroup createGroup(String name);
//
//	/**
//	 * Finds a group given the group name.
//	 *
//	 * @param name the name of the group to find.
//	 * @return the requested group or null if it is not found
//	 */
//	public abstract NAGroup getGroup(String name);
//
//	/**
//	 * Returns all groups known to the platform.
//	 *
//	 * @return a list of all groups the platform knows about.
//	 */
//	public abstract List<NAGroup> getAllGroups();
//
//	/**
//	 * Register an observer for changes to groups.
//	 * This does not include changes to devices within those groups.
//	 * To be notified about device changes, register with
//	 * {@link NAProxPlatform#registerDeviceObserver(DataSetObserver)}.
//	 *
//	 * @param observer the DataSetObserver
//	 */
//	public abstract void registerGroupObserver(DataSetObserver observer);
//
//	/**
//	 * Unregister a group observer.
//	 *
//	 * @Param observer the DataSetObserver
//	 */
//	public abstract void unregisterGroupObserver(DataSetObserver observer);
//
//	/**
//	 * Fetch all groups for the given device.
//	 * @param deviceId
//	 * @return all groups that contain the given device
//	 */
//	public abstract List<NAGroup> getAllGroupsForDevice(String deviceId);

	//=-=-=
	// RULE
	//=-=-=

	/**
	 * <p>Constructs a new rule with the given name.
	 *
	 * @param name the name for this rule.
	 * @return the new rule
	 */
	public abstract NARule createRule(String name);

	/**
	 * Returns all rules known to the platform.
	 *
	 * @return a list of rules.
	 */
	public abstract List<NARule> getAllRules();

	/**
	 * <p>Returns all rules registered for the given device.
	 *
	 * @param device the device to get rules for
	 * @return a list of rules for the given device
	 */
	public abstract List<NARule> getAllRulesByDevice(NADevice device);

	/**
	 * <p>Returns all rules registered for the given device and event type.
	 *
	 * @param device the device to get rules for
	 * @param eventType the event type to get rules for
	 * @return a list of rules for the given device and event type.
	 */
	public abstract List<NARule> getAllRulesByDeviceAndType(NADevice device, NAEventType eventType);

	/**
	 * Returns an {@link NARule} for the requested rule identifier.
	 * @param ruleId the requested rule id.
	 * @return the {@link NARule} for the requested id.
	 */
	public abstract NARule getRule(String ruleId);

	//=-=-=-=
	// PLUGIN
	//=-=-=-=

	/**
	 * Get a list of plugins known to the system.
	 *
	 * @return the list of plugins known to the system.
	 */
	public abstract List<NAActionPlugin> getAllPlugins();

	//=-=-=-=
	// ACTION
	//=-=-=-=

	/**
	 * Get a list of Actions known to the system
	 *
	 * @return the list of Actions in the system
	 */
	public abstract List<NAAction> getAllActions();

	/**
	 * Get the Action associated with the given action ID
	 *
	 * @param actionId The ID of the action to be retrieved
	 * @return The Action associated with the given ID
	 */
	public abstract NAAction getAction(String actionId);

	/**
	 * Create a new Action for the specified Plugin.
	 *
	 * @param actionName The name of the new Action
	 * @param plugin The Plugin to be associated with this Action
	 * @return The newly created Action
	 */
	public abstract NAAction createAction(String actionName, NAActionPlugin plugin);

}
