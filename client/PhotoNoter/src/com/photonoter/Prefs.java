package com.photonoter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {

	private static final String JAJA_ENABLED_KEY = Prefs.class.getName() + ".JAJA_ENABLED_KEY";

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
	
}
