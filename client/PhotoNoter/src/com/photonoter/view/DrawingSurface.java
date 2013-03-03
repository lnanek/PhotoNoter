package com.photonoter.view;

import com.samsung.spen.lib.input.SPenEvent;
import com.samsung.spen.lib.input.SPenLibrary;

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

public class DrawingSurface extends View {

	private static final String LOG_TAG = "DrawingSurface";

	private Paint paint;

	private Circle lastCircle;

	private float Radius = 20;

	float newR;

	float oldR;

	private Bitmap mBitmap;

	private Matrix matrix = new Matrix();

	public DrawingSurface(Context context) {
		super(context);
	}

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
	}

	public synchronized void clear() {
		if (mBitmap == null) {
			invalidate();
			return;
		}
		
		mBitmap = null;
		lastCircle = null;
		invalidate();
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public synchronized void setBitmap(final Bitmap aBitmap) {
			if ( null != mBitmap ) {
				mBitmap.recycle();
				mBitmap = null;
			}
	
			
			if ( null == aBitmap ) {
				return;
			}
			
			Bitmap bitmapResult = aBitmap.createBitmap(aBitmap.getWidth(), aBitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
	        Canvas c = new Canvas(bitmapResult);
	        c.drawBitmap(aBitmap, 0, 0, new Paint());
	        aBitmap.recycle();
	        
			mBitmap = bitmapResult;
	}

	public float getRadius() {
		return Radius;
	}

	public void setRadius(int value) {

		if (Radius != value) {
			// Log.i("rad","value: "+value);
		}
		if (value <= 0) {
			oldR = 0;
		}
		Radius = value;
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

	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {

		Float spenPressure = getPressure(event);
		if (null != spenPressure) {
			Log.i(LOG_TAG, "Setting pressure by SPen: " + spenPressure);
			setRadius((int) Math.max(1, Math.round(spenPressure * 20)));
		}

		if (mBitmap == null) {

			mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					android.graphics.Bitmap.Config.ARGB_8888);
		}

			float currentRadius = this.getRadius();
			if (currentRadius <= 0)
				return true;
			;
			int newX = (int) event.getX();
			int newY = (int) event.getY();
			Canvas canvas = new Canvas(mBitmap);
			if (lastCircle != null) {
				int distance = getDistance(newX, newY, lastCircle.getX(),
						lastCircle.getY());

				if (distance > 0 && currentRadius > 0) {
					newR = this.Radius;

					for (int i = 0; i <= distance; i++) {
						float ratio = 1.0f * i / distance;
						int x = (int) (newX * ratio + (1 - ratio)
								* lastCircle.getX());
						int y = (int) (newY * ratio + (1 - ratio)
								* lastCircle.getY());
						canvas.drawCircle(x, y, (newR * i + oldR
								* (distance - i))
								/ distance, paint);

					}
					oldR = newR;
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
		if (mBitmap != null) {
				canvas.drawBitmap(mBitmap, matrix, paint);
		}
	}

}