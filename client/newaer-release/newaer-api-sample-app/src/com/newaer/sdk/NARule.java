package com.newaer.sdk;

import java.util.List;

import android.content.Context;

/**
 * This class represents a rule within the platform which triggers a plugin
 * action when a particular set of device(s) enters a particular state.
 *
 * @author Originate
 *
 */
public abstract class NARule extends NAObject<NARule> {

	/**
	 * Only subclass construction.
	 */
	protected NARule() {

	}

	//=-=-=-=-=-=-=-=-=-=-
	// STATIC CONVENIENCES
	//=-=-=-=-=-=-=-=-=-=-

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#createRule(String)
	 * NAPlatform.get(context).createRule(name)}
	 *
	 * @param name to be set for the rule
	 * @return the created rule.
	 */
	public static NARule create(Context context, String name) {
		return NAPlatform.get(context).createRule(name);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllRules()
	 * NAPlatform.get(context).getAllRules()}.
	 *
	 * @param context the context to work in
	 * @return a list of rules
	 */
	public static List<NARule> getAll(Context context) {
		return NAPlatform.get(context).getAllRules();
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getAllRulesByDeviceAndType(NADevice, NAEventType)
	 * NAPlatform.get(context).getAllRulesByDeviceAndType(device, eventType)}.
	 *
	 * @param context the context to work in
	 * @param device the device to get rules for
	 * @param eventType the event type to get rules for
	 * @return a list of events for the given device and intent type.
	 */
	public static List<NARule> getAllByDeviceAndType(Context context, NADevice device, NAEventType eventType) {
		return NAPlatform.get(context).getAllRulesByDeviceAndType(device, eventType);
	}

	/**
	 * <p>This is a convenience for
	 * {@link NAPlatform#getRule(String)
	 * NAPlatform.get(context).getRule(ruleId)}.
	 *
	 * @param context the context to work in
	 * @param ruleId the id for the rule
	 * @return the rule
	 */
	public static NARule get(Context context, String ruleId) {
		return NAPlatform.get(context).getRule(ruleId);
	}

	//=-=-=-=-=
	// INSTANCE
	//=-=-=-=-=

	/**
	 * <p>Returns the devices which trigger the actions in this rule.
	 *
	 * @return A Set of NADevices
	 */
	public abstract List<NADevice> getDevices(Context context);

	/**
	 * <p>Add a device to this Rule.
	 *
	 * @param device to be added to the set of devices already associated with this Rule.
	 */
	public abstract void addDevice(Context context, NADevice device);

	/**
	 * <p>Remove a device from the set of devices this Rule is active on.
	 *
	 * @return true if the device was removed, false otherwise.
	 */
	public abstract void removeDevice(Context context, NADevice device);

	/**
	 * <p>Delete the rule from the system.
	 *
	 * @param context the context to work in.
	 */
	public abstract void delete(Context context);

	/**
	 * <p>Returns the type of event which triggers the action.
	 *
	 * @return the type of the event
	 */
	public abstract NAEventType getEventType();

	/**
	 * <p>Returns the type of event which triggers the action.
	 *
	 * @return the type of the event
	 */
	public abstract void setEventType(Context context, NAEventType type);

	/**
	 * @return true if this rule is enabled
	 */
	public abstract boolean isEnabled();

	/**
	 * @param enabled true if this rule should be enabled
	 */
	public abstract void setEnabled(Context context, boolean enabled);

	/**
	 * @return The name of this Rule
	 */
	public abstract String getName();

	/**
	 * @param rulename The name to be set for this Rule
	 */
	public abstract void setName(Context context, String rulename);

	/**
	 * @return The RuleType set on this Rule. Default is {@link NARuleType#EVERY NARuleType.EVERY_DEVICE}.
	 */
	public abstract NARuleType getType();

	/**
	 * @param type to be set for this Rule.
	 */
	public abstract void setType(Context context, NARuleType type);

	/**
	 * Returns a list of actions associated with this rule.
	 *
	 * @param context the context to work in.
	 * @return The {@link NAAction NAActions} associated with this rule.
	 */
	public abstract List<NAAction> getActions(Context context);

	/**
	 * <p>Adds an action to this rule.
	 *
	 * @param context the context to work in.
	 * @param action the action to add to this rule.
	 */
	public abstract void addAction(Context context, NAAction action);

	/**
	 * <p>Removes an action from this rule.
	 *
	 * @param context the context to work in
	 * @param action the action to remove from this rule.
	 */
	public abstract void removeAction(Context context, NAAction action);

}
