package com.photonoter;

public class HttpResult {
	
	public final int mStatusCode;
	
	public final String mResponse;

	public HttpResult(int aStatusCode, String aResponse) {
		mStatusCode = aStatusCode;
		mResponse = aResponse;
	}
	
}
