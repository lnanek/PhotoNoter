package com.photonoter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {

	private static final String JAJA_ENABLED_KEY = Prefs.class.getName() + ".JAJA_ENABLED_KEY";

	private static final String IS_HOME_KEY = Prefs.class.getName() + ".IS_HOME_KEY";

	private static final String HOME_NETWORK_KEY = Prefs.class.getName() + ".HOME_NETWORK_KEY";

	private final SharedPreferences mPrefs;
	
	public Prefs(final Context context) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}
	
	public boolean isJajaEnabled() {
		return mPrefs.getBoolean(JAJA_ENABLED_KEY, false);
	}
	
	public boolean setJajaEnabled(final boolean aJajaEnabled) {
		final Editor edit = mPrefs.edit();
		edit.putBoolean(JAJA_ENABLED_KEY, aJajaEnabled);
		edit.commit();
		return aJajaEnabled;
	}
	
	public boolean isHome() {
		return mPrefs.getBoolean(IS_HOME_KEY, false);
	}
	
	public boolean setHome(final boolean aIsHome) {
		final Editor edit = mPrefs.edit();
		edit.putBoolean(IS_HOME_KEY, aIsHome);
		edit.commit();
		return aIsHome;
	}
	
	public String getHomeNetwork() {
		return mPrefs.getString(HOME_NETWORK_KEY, null);
	}
	
	public String setHomeNetwork(final String aHomeNetwork) {
		final Editor edit = mPrefs.edit();
		edit.putString(HOME_NETWORK_KEY, aHomeNetwork);
		edit.commit();
		return aHomeNetwork;
	}
	
}
