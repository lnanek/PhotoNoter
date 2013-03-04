package com.newaer.newaersampleapp;

import com.newaer.sdk.NASdkApp;

/**
 * By extending NASdkApp we take care of NAPlatform.init() and destroy();
 *
 * @author Originate
 *
 */
public class SampleApplication extends NASdkApp {

	@Override
	public String getApplicationKey() {
		return "YOUR APPLICATION KEY";
	}

}
