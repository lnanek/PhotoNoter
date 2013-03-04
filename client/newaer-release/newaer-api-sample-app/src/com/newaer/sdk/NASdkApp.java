package com.newaer.sdk;

import java.util.Arrays;

import android.app.Application;

/**
 * <p>This is a convenience class for Applications which want to make
 * use of the NewAer Proximity Platform. It takes care of initializing
 * the platform for the application.
 * <p>If you don't make use of this you should ensure that your application
 * calls {@link NAPlatform#init(android.content.Context, String) NAPlatform.init()
 * in the {@link Application#onCreate()} method.
 *
 * @author Originate
 *
 */
public abstract class NASdkApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		NAPlatform.init(this, getApplicationKey(), Arrays.asList(NADeviceType.values()));
	}

	/**
	 * @return the applications key provided by NewAer when this application was registered.
	 */
	public abstract String getApplicationKey();

}
