package co.spark.jaja;

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

	public DrawingSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private class Circle {
		private float radius;
		private int x;
		private int y;

		public float getRadius() {

			return radius;
		}

		public void setRadius(float radius) {
			this.radius = radius;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

	Paint paint;

	private Circle lastCircle;

	private float Radius = 20;
	private float PrevRadius = 20;
	float newR;
	float oldR;
	public void clear() {
		if (bitmap == null)
			return;
		synchronized (bitmap) {
			bitmap = null;
			lastCircle = null;
		}
	}
	public float getRadius() {
		

		return Radius;
	}
	public void setRadius(int value) {

		if(Radius != value) {
			//Log.i("rad","value: "+value);
		}
		if(value <=0) {
			oldR = 0;
		}
		PrevRadius = Radius;
		Radius = value;
	}

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
	}

	private Bitmap bitmap;

	private int getDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.round(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
				* (y1 - y2)));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (bitmap == null) {

			bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					android.graphics.Bitmap.Config.ARGB_8888);
		}
		synchronized (bitmap) {

			float currentRadius = this.getRadius();
			if (currentRadius <= 0)
				return true;;
			int newX = (int) event.getX();
			int newY = (int) event.getY();
			Canvas canvas = new Canvas(bitmap);
			if (lastCircle != null) {
				int distance = getDistance(newX, newY, lastCircle.getX(),
						lastCircle.getY());
				
				if (distance > 0 && currentRadius > 0) {
					//newR = Radius;
					newR = this.Radius;
					
					
					
					for (int i = 0; i <= distance; i++ ) {
						float ratio = 1.0f * i / distance;
						int x = (int) (newX * ratio + (1 - ratio)
								* lastCircle.getX());
						int y = (int) (newY * ratio + (1 - ratio)
								* lastCircle.getY());
						canvas.drawCircle(x, y, (newR*i + oldR*(distance - i))/distance, paint);

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
				//Log.e("core","asdf");
				//canvas.drawCircle(circle.getX(), circle.getY(),
					//	circle.getRadius(), paint);
			} else {
				lastCircle = null;
			}

		}
		return true;

	}

	@Override
	protected void onDraw(Canvas canvas)// ����� OnDraw ��������� ���������
										// �����, ����� ����� ���������� ������
										// View
	{

		/*
		 * for (Circle c: drawings) canvas.drawCircle(c.getX(), c.getY(),
		 * c.getRadius(), paint);
		 */
		{

			if (bitmap != null)
				synchronized (bitmap) {
					canvas.drawBitmap(bitmap, new Matrix(), paint);
				}
			invalidate();// invalidate() ����� ��� ����, ����� ����������
							// Android,
							// ��� ����� ��������� ����� OnDraw �����, ��� ����
							// View
							// �� ����� ������c���������.
		}
	}

}
