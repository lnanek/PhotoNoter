package co.spark.jaja;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import co.spark.jajasdk.*;


public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private JajaControlConnection conn;

	private Handler handler;
	private DrawingSurface surface;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Button runButton = (Button) findViewById(R.id.run_button);
		final Button stopButton = (Button) findViewById(R.id.stop_button);
		final TextView tv = (TextView) findViewById(R.id.text_view);
		surface = (DrawingSurface) findViewById(R.id.drawing_surface);
		Button clearButton = (Button) findViewById(R.id.clear_button);
		
		surface.setRadius(0);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				String text = "Pressure: "
						+ (conn.isSignalAvailable() ? Double.toString(conn.getSignalValue()) : "---") 
						+ "\nFirst button:"
						+ Boolean.toString(conn.isFirstButtonPressed())
						+ "\nSecond button:"
						+ Boolean.toString(conn.isSecondButtonPressed());
				if (conn.getSignalValue() > 0)
					surface.setRadius((int) Math.max(1,
							Math.round(conn.getSignalValue() * 20)));
				else
					surface.setRadius(0);
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
						// TODO Auto-generated method stub
						
					}

					@Override
					public void jajaControlSignalRestored() {
						Log.e("UI","jajaControlSignalRestored");
						// TODO Auto-generated method stub
						
					}

					@Override
					public void jajaControlError() {
						Log.e("UI","jajaControlError");
						// TODO Auto-generated method stub
						
					}
				});
				runButton.setEnabled(false);
				stopButton.setEnabled(true);
				try {
					conn.start();
				} catch (ConnectionStartedException e) {
					// TODO Auto-generated catch block
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
}