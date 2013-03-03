package com.photonoter;

import android.app.Application;
import android.content.Context;

public class PhotoBackWriterApp extends Application {

	public static Integer pickedImageId;
	
	public Prefs mPrefs;

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
