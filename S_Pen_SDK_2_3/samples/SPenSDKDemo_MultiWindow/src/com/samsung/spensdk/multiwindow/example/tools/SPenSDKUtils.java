package com.samsung.spensdk.multiwindow.example.tools;

import java.io.IOException;


import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;



public class SPenSDKUtils {

	/**
	 * Get the real file path from URI registered in media store 
	 * @param contentUri URI registered in media store 
	 */		
	public static   String getRealPathFromURI(Activity activity, Uri contentUri) { 		

		String releaseNumber = Build.VERSION.RELEASE;

		if(releaseNumber!=null){
			/* ICS Version */
			if(releaseNumber.length()>0 && releaseNumber.charAt(0)=='4'){
				String[] proj = { MediaStore.Images.Media.DATA };
				String strFileName="";
				CursorLoader cursorLoader = new CursorLoader(activity, contentUri, proj, null, null,null); 
				Cursor cursor = cursorLoader.loadInBackground();			
				if(cursor!=null)
				{
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);			
					cursor.moveToFirst();
					if(cursor.getCount()>0)
						strFileName=cursor.getString(column_index);

					cursor.close();
				}
				return strFileName; 	
			}			
			/* GB Version */
			else if(releaseNumber.startsWith("2.3")){
				String[] proj = { MediaStore.Images.Media.DATA };
				String strFileName="";
				Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null); 
				if(cursor!=null)
				{
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

					cursor.moveToFirst();
					if(cursor.getCount()>0)
						strFileName=cursor.getString(column_index);

					cursor.close();				
				}
				return strFileName; 	
			}	
		}

		//---------------------
		// Undefined Version
		//---------------------
		/* GB, ICS Common */
		String[] proj = { MediaStore.Images.Media.DATA }; 
		String strFileName="";
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null); 
		if(cursor!=null)
		{
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);			

			// Use the Cursor manager in ICS         
			activity.startManagingCursor(cursor);

			cursor.moveToFirst();
			if(cursor.getCount()>0)
				strFileName=cursor.getString(column_index);

			//cursor.close(); // If the cursor close use , This application is terminated .(In ICS Version)
			activity.stopManagingCursor(cursor);
		}
		return strFileName; 		
	}
	

	// Check whether valid image file or not
	public static boolean isValidImagePath(String strImagePath){		
		if(strImagePath==null){			
			return false;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strImagePath, options);

		return (options.outMimeType != null);	
	}

	// Check whether valid file name or not
	public static  boolean isValidSaveName(String fileName) {

		int len = fileName.length();
		for (int i = 0; i < len; i++) {
			char c = fileName.charAt(i);

			if(c== '\\'|| c== ':' || c== '/' || c== '*' || c== '?' || c== '"'  
				|| c== '<' || c== '>' || c== '|' || c== '\t'|| c== '\n') {
				return false;
			}
		}
		return true;
	}
	/****************************************************************************************************************
	 * Get the image bitmap that resizing to maximum size of limit.
	 * Parameter :
	 *  - context : Context 
	 *  - uri : Image URI
	 *  - bContentStreamImage : Gallery contents stream file(true)/file path(false)
	 *  - nMaxResizedWidth : The maximum allowable width of resizing image.
	 *  - nMaxResizedHeight : The maximum allowable height of resizing image.
	 * Return :
	 *  - Resizing bitmap
	 */
	public static Bitmap getSafeResizingBitmap(	
			String strImagePath, 
			int nMaxResizedWidth,	
			int nMaxResizedHeight, 
			boolean checkOrientation)
	{
		//==========================================
		// Bitmap Option
		//==========================================
		BitmapFactory.Options options = getBitmapSize(strImagePath);
		if(options == null)		
			return null;		

		//==========================================
		// Bitmap Scaling
		//==========================================
		int nSampleSize;
		int degree = 0;
		if(checkOrientation){
			degree = getExifDegree(strImagePath);
		}
		
		if(degree==90 || degree==270){
			nSampleSize = getSafeResizingSampleSize(options.outHeight, options.outWidth, nMaxResizedWidth, nMaxResizedHeight);
		}
		else{
			nSampleSize = getSafeResizingSampleSize(options.outWidth, options.outHeight, nMaxResizedWidth, nMaxResizedHeight);
		}
		
		
		//==========================================
		// Load the bitmap including actually data.
		//==========================================
		options.inJustDecodeBounds = false;
		options.inSampleSize = nSampleSize;
		options.inDither = false;		
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;	
		options.inPurgeable = true;

		Bitmap photo = BitmapFactory.decodeFile(strImagePath, options);
		if(checkOrientation 
		&& (degree==90 || degree==270) ){
			return getRotatedBitmap(photo, degree);
		}
		return photo;		
	}
	
	
	
	public static Bitmap decodeImageFile(String strImagePath, BitmapFactory.Options options, boolean checkOrientation){
		if(checkOrientation==false){
			return BitmapFactory.decodeFile(strImagePath, options);
		}
		else{
			Bitmap bm = BitmapFactory.decodeFile(strImagePath, options);
			int degree = getExifDegree(strImagePath);
			return getRotatedBitmap(bm, degree);
		}
	}
	
	public static BitmapFactory.Options getBitmapSize(String strImagePath)
	{
		//==========================================
		// 크기 획득을  위한 임시 비트맵 로드 
		//==========================================
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//Bitmap photo = BitmapFactory.decodeFile(strPath, options);
		BitmapFactory.decodeFile(strImagePath, options);
		
		return options;
	}
	
	public static BitmapFactory.Options getBitmapSize(String strImagePath, boolean checkOrientation)
	{
		//==========================================
		// 크기 획득을  위한 임시 비트맵 로드 
		//==========================================
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//Bitmap photo = BitmapFactory.decodeFile(strPath, options);
		BitmapFactory.decodeFile(strImagePath, options);
		
		if(checkOrientation){
			int degree = getExifDegree(strImagePath);
			if(degree==90 || degree==270){
				int temp = options.outWidth;
				options.outWidth = options.outHeight; 
				options.outHeight = temp;
			}
		}
		
		return options;		
	}

	/****************************************************************************************************************
	 * Get the sampling size for load the bitmap. (If you load the bitmap file of big size, This application is terminated.)
	 * Parameter :
	 *  - nOrgWidth	: The width of the original image (Value of outWidth of BitmapFactory.Options)
	 *  - nOrgHeight : The height of the original image  (Value of outHeight of BitmapFactory.Options)
	 *  - nMaxWidth : The width of the image of maximum size.  (width under 3M. ex.3000)
	 *  - nMaxHeight : The height of the image of maximum size.   (height under 3M. ex.1000)	 *  
	 * Return :
	 *  - Sampling size (If no need to resize, return 1). Throttled much larger.
	 *  - If more than x.5 times , divide x+1 times.
	 */
	public static int getSafeResizingSampleSize(
			int nOrgWidth, 
			int nOrgHeight,
			int nMaxWidth, 
			int nMaxHeight)
	{
		int size = 1;
		float fsize;
		float fWidthScale = 0;
		float fHeightScale = 0;

		if(nOrgWidth > nMaxWidth  || nOrgHeight > nMaxHeight ) 
		{
			if(nOrgWidth > nMaxWidth) 
				fWidthScale = (float)nOrgWidth / (float)nMaxWidth;					
			if(nOrgHeight > nMaxHeight) 
				fHeightScale = (float)nOrgHeight / (float)nMaxHeight;			

			if(fWidthScale >= fHeightScale) fsize = fWidthScale;
			else fsize= fHeightScale;

			size = (int)fsize;			
		}

		return size;
	}
	
	public static int getExifDegree(String filepath){
	    int degree = 0;
	    ExifInterface exif;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	    
	    
	    if (exif != null){
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	        
	        if (orientation != -1){
	            switch(orientation)	            {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	        }
	    }	    
	    return degree;
	}
	
	public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)	{
	    if ( degrees != 0 && bitmap != null ) {
	        Matrix m = new Matrix();
	        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
	        try {
	            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
	            if (bitmap != b2){
	            	bitmap.recycle();
	            	bitmap = b2;
	            }
	        } 
	        catch (OutOfMemoryError ex){
	        	// TODO Auto-generated catch block
	        	ex.printStackTrace();
	        }
	    }
	    return bitmap;
	}
}
