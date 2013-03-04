package com.photonoter;

import com.newaer.sdk.NASdkApp;

import android.app.Application;
import android.content.Context;

public class PhotoBackWriterApp extends NASdkApp {

	@Override
	public String getApplicationKey() {
		return "da01a519-14d1-4dc6-b1c0-c05cbe72e32a ";
	}

	
	private static final String NEW_AER_APP_ID = "da01a519-14d1-4dc6-b1c0-c05cbe72e32a";

	public static Integer pickedImageId;
	
	public Prefs mPrefs;
	
	public boolean mCheckedIfHome;

	@Override
	public void onCreate() {
		super.onCreate();
		mPrefs = new Prefs(this);
	}
	
	public static PhotoBackWriterApp getApp(final Context aContext) {
		final Context app = aContext.getApplicationContext();
		return (PhotoBackWriterApp) app;
	}

}
