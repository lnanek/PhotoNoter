package com.photonoter;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;
import co.spark.jajasdk.ConnectionStartedException;
import co.spark.jajasdk.JajaControlConnection;
import co.spark.jajasdk.JajaControlListener;

import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

import de.devmil.common.ui.color.ColorSelectorDialog;
import de.devmil.common.ui.color.ColorSelectorDialog.OnColorChangedListener;


public class MainActivity extends Activity {
	
	private static final String LOG_TAG = "MainActivity";
			
	private static final String FILE_URI_PREFIX = "file://"; //$NON-NLS-1$

	private JajaControlConnection conn;

	private Handler handler;
	
	private DrawingSurface surface;
	
	private DrawingSurface surface2;

	private String imagePath;
	
	private ImageView photo;
	
	private ViewAnimator viewAnimator;
	
	private void sendUpdate() {
		handler.sendMessage(new Message());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(conn != null) {
			conn.stop();
		}
	}
	
	OnColorChangedListener colorListener = new OnColorChangedListener() {
		@Override
		public void colorChanged(int color) {
			surface.setColor(color);
			surface2.setColor(color);
		}
	};
	
	private String getCombinedImagePath() {
		File ext = Environment.getExternalStorageDirectory();
		if (!ext.exists()) {
			return null;
		}
		return ext.getPath() 
				+ "/" + PhotoBackWriterApp.pickedImageId + ".jpg";
	}
	
	private String getFrontImagePath() {
		return getFilesDir().getPath() 
				+ PhotoBackWriterApp.pickedImageId + "-front.jpg";
	}
	
	private String getBackImagePath() {
		return getFilesDir().getPath() 
				+ PhotoBackWriterApp.pickedImageId + "-back.jpg";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Button runButton = (Button) findViewById(R.id.run_button);
		final Button stopButton = (Button) findViewById(R.id.stop_button);
		final TextView tv = (TextView) findViewById(R.id.text_view);

		surface = (DrawingSurface) findViewById(R.id.drawing_surface);
		surface2 = (DrawingSurface) findViewById(R.id.drawing_surface2);

		final Button shareButton = (Button) findViewById(R.id.share_button);
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final Intent intent = createShareIntent();
				startActivity(intent);
			}
		});
		
		final Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				BitmapUtil.saveBitmap(surface.getBitmap(), getFrontImagePath());
				BitmapUtil.saveBitmap(surface2.getBitmap(), getBackImagePath());
				
				finish();
			}
		});
		
		final Button colors = (Button) findViewById(R.id.color_button);
		colors.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ColorSelectorDialog(MainActivity.this, colorListener, surface.getColor()).show();
			}
		});
		
        viewAnimator = (ViewAnimator)this.findViewById(R.id.viewFlipper);
        final Button flipButton = (Button) findViewById(R.id.flip_button);
        /**
         * Bind a click listener to initiate the flip transitions
         */
        flipButton.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				// This is all you need to do to 3D flip
				AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);
			}
        	
        });
		
		
		if ( null != PhotoBackWriterApp.pickedImageId ) {
			photo = (ImageView) findViewById(R.id.photo);

			// Load image we are writing on.
			imagePath = MediaStoreUtil.getImageFilePath(this, PhotoBackWriterApp.pickedImageId);
			Bitmap bitmap = ImageUtility.getBitmapFromLocalPath(imagePath, 1);
			photo.setImageBitmap(bitmap);
			
			// Load writing on the face of the image.
			final String frontPath = getFrontImagePath();
			final Bitmap front = ImageUtility.getBitmapFromLocalPath(frontPath, 1);
			surface.setBitmap(front);
			
			// Load writing on the back of the image.
			final String backPath = getBackImagePath();
			final Bitmap back = ImageUtility.getBitmapFromLocalPath(backPath, 1);
			surface2.setBitmap(back);
			
		}
		
		Button clearButton = (Button) findViewById(R.id.clear_button);
		
		surface.setRadius(0);
		surface2.setRadius(0);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				String text = "Pressure: "
						+ (conn.isSignalAvailable() ? Double.toString(conn.getSignalValue()) : "---") 
						+ "\nFirst button:"
						+ Boolean.toString(conn.isFirstButtonPressed())
						+ "\nSecond button:"
						+ Boolean.toString(conn.isSecondButtonPressed());
				
				if (conn.getSignalValue() > 0) {
					int newRadius = (int) Math.max(1,
							Math.round(conn.getSignalValue() * 20));
					surface.setRadius(newRadius);
					surface2.setRadius(newRadius);
				} else {
					surface.setRadius(0);
					surface2.setRadius(0);
				}
				
				//Log.i("UI", text);
				tv.setText(text);
			}
		};

		runButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				conn = new JajaControlConnection();
				conn.setJajaControlListener(new JajaControlListener() {

					@Override
					public void signalValueChanged(double value) {
						
						sendUpdate();
					}

					@Override
					public void secondButtonValueChanged(boolean isPressed) {
						Log.i("UI","secondButtonValueChanged: "+isPressed);
						sendUpdate();
					}

					@Override
					public void firstButtonValueChanged(boolean isPressed) {
						Log.i("UI","firstButtonValueChanged: "+isPressed);
						sendUpdate();
					}

					@Override
					public void jajaControlSignalLost() {
						Log.e("UI","jajaControlSignalLost");
						sendUpdate();						
					}

					@Override
					public void jajaControlSignalRestored() {
						Log.e("UI","jajaControlSignalRestored");						
					}

					@Override
					public void jajaControlError() {
						Log.e("UI","jajaControlError");						
					}
				});
				runButton.setEnabled(false);
				stopButton.setEnabled(true);
				try {
					conn.start();
				} catch (ConnectionStartedException e) {
					e.printStackTrace();
				}
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				conn.stop();
				runButton.setEnabled(true);
				stopButton.setEnabled(false);

			}
		});
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				surface.clear();

			}
		});

	}

	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG, "onDestroy - cleaning up bitmaps");
		
		super.onDestroy();
		surface.setBitmap(null);
		surface2.setBitmap(null);
		
		Drawable drawable = photo.getDrawable();
		if (drawable instanceof BitmapDrawable) {
		    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		    Bitmap bitmap = bitmapDrawable.getBitmap();
		    bitmap.recycle();
		}
	}
	
    private Intent createShareIntent() {
    	
		final Intent share = new Intent(Intent.ACTION_SEND);

		final String screenshot = saveImage();
		
		if ( null != screenshot ) {
				share.setType("image/jpeg");
		        //share.setType("image/*");
				share.putExtra(Intent.EXTRA_STREAM,Uri.parse(FILE_URI_PREFIX + screenshot));
				share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
		} else {
			share.setType("text/plain");			
		}
    					
		final String shareText = "My Notes";
		
		share.putExtra(Intent.EXTRA_TEXT, shareText); 
		share.putExtra("sms_body", shareText);   
		share.putExtra(Intent.EXTRA_TITLE, shareText);
		share.putExtra(Intent.EXTRA_SUBJECT, shareText);

        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return share;
    }
	
    public String saveImage() {
    	Bitmap bitmap = drawToBitmap();
    	
    	try {

			BitmapUtil.saveBitmap(bitmap, getCombinedImagePath());
	        
	        return getCombinedImagePath();
	        
    	} catch (final Exception e) {
        	Log.e(LOG_TAG, "Error saving image.", e); //$NON-NLS-1$
        } finally {
            if ( null != bitmap ) {
               	bitmap.recycle();
            }
        }
    	
    	return null;
    }
    
    public Bitmap drawToBitmap() {
    	
    	final Bitmap bitmap = Bitmap.createBitmap(
    			viewAnimator.getWidth(), 
    			viewAnimator.getHeight(),
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        		        
        viewAnimator.draw(canvas);
        return bitmap;
    }
}