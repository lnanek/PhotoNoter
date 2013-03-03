package com.photonoter;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapUtil {
	
	private static final String LOG_TAG = "BitmapUtil";

	private static final int COMPRESSED_QUALITY = 70;

	public static void saveBitmap(final Bitmap aBitmap, final String aPath) {
		try {
			
			FileOutputStream out = new FileOutputStream(aPath);
			aBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, out);
			out.flush();
			out.close();
			
			Log.i(LOG_TAG, "Saved writing to: " + aPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
