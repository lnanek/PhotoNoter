package com.newaer.sdk;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * This class represents an action plugin with the system. Please see
 * com.newaer.sdk.plugins for documentation on how to add a plugin to the
 * platform.
 *
 * @author Originate
 */
public abstract class NAActionPlugin extends NAObject<NAActionPlugin> {

	/**
	 * Only subclasses construction.
	 */
	protected NAActionPlugin() {

	}

	/**
	 * The action used to discover plugins. Your plugin should have an intent filter
	 * with this action and the default category in order to be discovered by
	 * the NewAer SDK.
	 */
	public static final String INTENT_DISCOVER = "com.newaer.plugin.DISCOVER";

	/**
	 * Metadata key in the manifest for the name resource for a plugin.
	 * This will be used to display the plugin in user interfaces.
	 */
	public static final String METADATA_PLUGIN_NAME = "newaer:pluginName";

	/**
	 * Metadata key in the manifest for the receiver class for a plugin.
	 * If this is not provided we expect a static inner class with the
	 * name ActionReceiver to receive the actions for this plugin.
	 */
	public static final String METADATA_PLUGIN_RECEIVER = "newaer:pluginAction";

	/**
	 * Metadata key in the manifest for the icon that will be displayed in
	 * the notification bar. Defaults to the application icon if none is set.
	 */
	public static final String METADATA_NOTIFICATION_ICON = "newaer:notificationIcon";

	/**
	 * Metadata key in the manifest indicating if this plugin can be used by other
	 * applications using the NewAer SDK. Set to "false" to prevent other applications
	 * from calling your plugin. Default: true;
	 */
	public static final String METADATA_PLUGIN_EXPORTED = "newaer:exported";

	/**
	 * This is a convenience for
	 * {@link NAPlatform#getAllPlugins() NAPlatform.get(context).getPlugins()}
	 *
	 * @return the list of plugins known to the system.
	 */
	public static List<NAActionPlugin> getAll(Context context) {
		return NAPlatform.get(context).getAllPlugins();
	}

	/**
	 * <p>Returns the name of the plugin.
	 * <p>This can be used as the name of the plugin in user interfaces. Plugins
	 * are responsible for providing localized names.
	 * @return the name to use to display this plugin to users.
	 */
	public abstract String getName();

	/**
	 * <p>Returns the name of the application which holds this plugin.
	 * <p>This can be used as the name of the application in user interfaces. Plugins
	 * are responsible for providing localized names for their applications.
	 * @return the name to use to display the application this plugin comes from.
	 */
	public abstract String getApplicationName();

	/**
	 * <p>Returns the icon for this plugin.
	 * @return the icon for this plugin.
	 */
	public abstract Drawable getIcon();

	/**
	 * <p>Returns the id for this plugin.
	 * @return the id for this plugin.
	 */
	public abstract String getId();


	/**
	 * <p>Plugins may not be renamed. Sorry. Plugin names are decided by plugin developers.
	 *
	 * @param name the name you wish this plugin had. Tough luck!
	 * @throws UnsupportedOperationException because plugin names are decided by plugin developers.
	 */
	public abstract void setName(Context context, String name);
}
