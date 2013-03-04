package com.newaer.sdk;

import java.util.List;

import android.content.Context;

/**
 * This represents an action taken by a rule.
 *
 * @author Originate
 *
 */
public abstract class NAAction extends NAObject<NAAction> {

	/**
	 * Only subclass construction.
	 */
	protected NAAction() {

	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllActions()
	 * NAPlatform.get(context).getAllActions()}.
	 *
	 * @param context the context to work in.
	 * @return all tagged devices.
	 */
	public static List<NAAction> getAll(Context context) {
		return NAPlatform.get(context).getAllActions();
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#createAction(String, NAActionPlugin)
	 * NAPlatform.get(context).createAction(name, plugin)}.
	 *
	 * @param context the context to work in.
	 * @param name the name for this action.
	 * @param plugin the plugin this action fires.
	 * @return the created action or null if the user canceled action creation.
	 */
	public static NAAction create(Context context, String name, NAActionPlugin plugin) {
		return NAPlatform.get(context).createAction(name, plugin);
	}

	/**
	 * Returns the rules this action is used in.
	 * @param context the context to work in.
	 */
	public abstract List<NARule> getRules(Context context);

	/**
	 * <p>Deletes this action. The action must not be used in any rules.
	 * @throws IllegalStateException if this action is still used by a rule.
	 */
	public abstract void delete(Context context);

	/**
	 * <p>Launches the configuration for this action.
	 *
	 * <p>This will start a new activity with the plugin's configuration UI.
	 * If the user does not finish configuration of the plugin
	 * in that activity then {@link NAAction#isConfigured() isConfigured()} will return false.
	 *
	 * <p> Note that an action can not be fired, even if it is part of rule,
	 * until it has been configured. Developers should reflect this in the
	 * user interface of their applications.
	 *
	 * @param context The context to work in.
	 */
	public abstract void configure(Context context);

	/**
	 * <p>Returns true if this rule is configured. Note that this action
	 * can only be fired if it is configured. If a rule contains this action
	 * and it is not configured it will not be fired when the rule fires.
	 *
	 * @return true if this rule is configured.
	 */
	public abstract boolean isConfigured();

	/**
	 * <p>Returns the {@link NAActionPlugin} for this action.
	 * @param context the context to work in
	 * @return the plugin for this action.
	 */
	public abstract NAActionPlugin getPlugin(Context context);

}
