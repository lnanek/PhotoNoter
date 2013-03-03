package com.photonoter;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapUtil {

	private static final String LOG_TAG = "BitmapUtil";

	public static void saveBitmap(final Bitmap aBitmap, final String aPath) {
		try {
			FileOutputStream out = new FileOutputStream(aPath);
			aBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			Log.i(LOG_TAG, "Saved writing to: " + aPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
