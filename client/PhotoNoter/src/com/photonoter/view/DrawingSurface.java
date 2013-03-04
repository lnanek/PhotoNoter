package com.photonoter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.photonoter.AnnotatePhotoActivity;
import com.samsung.spen.lib.input.SPenEvent;
import com.samsung.spen.lib.input.SPenLibrary;

public class DrawingSurface extends View {

	private static final String LOG_TAG = "DrawingSurface";
	
	private static final int MIN_PAINT_RADIUS = 1;
	
	private static final int MAX_PAINT_RADIUS = 20;
	
	private static final int FINGER_PAINT_RADIUS = 7;
	
	private Paint paint;

	// Last circle, used to interpolate between last sensed location and current.
	private Circle lastCircle;

	private float mNextRadiusToDraw = 20;

	float mLastRadiusDrawn;

	private Bitmap mCachedDrawings;

	private Matrix matrix = new Matrix();
	
	private AnnotatePhotoActivity activity;

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity= (AnnotatePhotoActivity) context;
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
	}

	public synchronized void clear() {
		if (mCachedDrawings == null) {
			invalidate();
			return;
		}
		
		mCachedDrawings = null;
		lastCircle = null;
		invalidate();
	}
	
	public Bitmap getBitmap() {
		return mCachedDrawings;
	}
	
	public synchronized void setBitmap(final Bitmap aBitmap) {
			if ( null != mCachedDrawings ) {
				mCachedDrawings.recycle();
				mCachedDrawings = null;
			}
	
			
			if ( null == aBitmap ) {
				return;
			}
			
			Bitmap bitmapResult = aBitmap.createBitmap(aBitmap.getWidth(), aBitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
	        Canvas c = new Canvas(bitmapResult);
	        c.drawBitmap(aBitmap, 0, 0, new Paint());
	        aBitmap.recycle();
	        
			mCachedDrawings = bitmapResult;
	}

	public void setRadius(int value) {
		if (value <= 0) {
			mLastRadiusDrawn = 0;
		}
		mNextRadiusToDraw = value;
	}

	public int getColor() {
		return paint.getColor();
	}

	public void setColor(final int aColor) {
		paint.setColor(aColor);
	}

	private int getDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.round(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
				* (y1 - y2)));
	}

	private Float getPressure(MotionEvent event) {
		final SPenEvent penEvent = SPenLibrary.getEvent(event);
		if (!penEvent.isPen() && !penEvent.isEraserPen()) {
			return null;
		}

		return penEvent.getPressure();
	}
	
	public void setRadiusByPercent(Float spenPressure) {
		setRadius((int) Math.max(1, Math.round(spenPressure * 20)));		
	}

	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {

		// If SPen
		Float spenPressure = getPressure(event);
		if (null != spenPressure) {
			Log.i(LOG_TAG, "Setting pressure by SPen: " + spenPressure);
			setRadiusByPercent(spenPressure);
		} else if (activity.isJajaStarted && null != activity.jajaConnection && activity.jajaConnection.getSignalValue() > 0 ) {
			// Set by Jaja.
		} else {				
			setRadius(FINGER_PAINT_RADIUS);
		}

		if (mCachedDrawings == null) {

			mCachedDrawings = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					android.graphics.Bitmap.Config.ARGB_8888);
		}

			float currentRadius = mNextRadiusToDraw;
			if (currentRadius <= 0)
				return true;
			;
			int newX = (int) event.getX();
			int newY = (int) event.getY();
			Canvas canvas = new Canvas(mCachedDrawings);
			if (lastCircle != null) {
				int distance = getDistance(newX, newY, lastCircle.getX(),
						lastCircle.getY());

				if (distance > 0 && currentRadius > 0) {

					for (int i = 0; i <= distance; i++) {
						float ratio = 1.0f * i / distance;
						int x = (int) (newX * ratio + (1 - ratio)
								* lastCircle.getX());
						int y = (int) (newY * ratio + (1 - ratio)
								* lastCircle.getY());
						canvas.drawCircle(x, y, (mNextRadiusToDraw * i + mLastRadiusDrawn
								* (distance - i))
								/ distance, paint);

					}
					mLastRadiusDrawn = mNextRadiusToDraw;
				}
			}

			Circle circle = new Circle();
			circle.setX((int) event.getX());
			circle.setY((int) event.getY());
			circle.setRadius(currentRadius);

			if (event.getAction() != MotionEvent.ACTION_UP) {
				lastCircle = circle;
			} else {
				lastCircle = null;
			}

		invalidate();
		return true;

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		if (mCachedDrawings != null) {
				canvas.drawBitmap(mCachedDrawings, matrix, paint);
		}
	}

}
