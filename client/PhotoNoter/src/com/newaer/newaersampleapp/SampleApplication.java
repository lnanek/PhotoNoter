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
		return "da01a519-14d1-4dc6-b1c0-c05cbe72e32a ";
	}

}
