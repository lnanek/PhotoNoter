package com.newaer.newaersampleapp;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newaer.sdk.NAAction;
import com.newaer.sdk.NAActionPlugin;
import com.newaer.sdk.NADevice;
import com.newaer.sdk.NADeviceType;
import com.newaer.sdk.NAPlatform;
import com.newaer.sdk.NARule;
import com.photonoter.PhotoBackWriterApp;
import com.photonoter.R;

public class CheckIfHomeActivity extends Activity {

	private static final String TAG = "SampleActivity";

	private ScheduledExecutorService service;
	
	private ArrayAdapter<NARule> adapter;
	
	private ProgressDialog dialog;
	
	private String homeNetwork;
	
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			app.mPrefs.setHome(false);
			finish();
			
			Toast.makeText(CheckIfHomeActivity.this, "Home not found in 5 seconds. Assuming away...", Toast.LENGTH_LONG).show();
			
			handler.removeCallbacks(runnable);
			return;
		}
		
	};

	Handler handler = new Handler();
	
	PhotoBackWriterApp app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_if_home);
		

		app = PhotoBackWriterApp.getApp(CheckIfHomeActivity.this);
		app.mPrefs.setHome(false);
		homeNetwork = app.mPrefs.getHomeNetwork();
		

		service = Executors.newScheduledThreadPool(1);
		adapter = new ArrayAdapter<NARule>(this, 0) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView bindView = (TextView) convertView;
				if (bindView == null) {
					bindView = new TextView(CheckIfHomeActivity.this);
				}
				bindView.setText(adapter.getItem(position).getName());

				return bindView;
			}
		};
		
		
		List<NARule> currentRules = NARule.getAll(this);

		
		for( NARule rule : currentRules ) {
			if ( rule.getDevices(this).get(0).getId().equals(homeNetwork) ) {
				app.mPrefs.setHome(true);
				finish();
				
				Toast.makeText(CheckIfHomeActivity.this, "You are home!", Toast.LENGTH_LONG).show();
				
				handler.removeCallbacks(runnable);
				return;
			}
		}
		

		dialog = ProgressDialog.show(this, "Checking if you are home or away using NewAer!", "Scanning...");
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				app.mPrefs.setHome(false);
				finish();
				
				Toast.makeText(CheckIfHomeActivity.this, "Assuming away...", Toast.LENGTH_LONG).show();
				
				handler.removeCallbacks(runnable);
				return;
			}
		});
		

		
		handler.postDelayed(runnable, 5000);
		
		adapter.addAll(currentRules);

		// Enable Scanning for wifi
		NAPlatform.get(this).setEnabled(NADeviceType.WIFI, true);

		// Periodically scan for untagged devices.
		service.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					Log.d(TAG, "Running scan.");
					// Get the list of action plugins;
					List<NAActionPlugin> plugins = NAActionPlugin.getAll(CheckIfHomeActivity.this);
					Log.d(TAG, "Got plugins: " + plugins.size());

					// Get recently seen wifi devices.
					List<NADevice> wifi = NADevice.getAllUntaggedByType(CheckIfHomeActivity.this, NADeviceType.WIFI);
					Log.d(TAG, "Got: " + wifi.size());

					for (final NADevice device : wifi) {
						// Tag the device.
						Log.d(TAG, "Tagging Device.");
						device.setTagged(CheckIfHomeActivity.this, true);

						// Make a rule for this device.
						Log.d(TAG, "Creating Rule: " + device.getName());
						final NARule rule = NARule.create(CheckIfHomeActivity.this, device.getName());

						// Make an action for this rule.
						Log.d(TAG, "Creating Action: " + device.getName());
						NAAction action = NAAction.create(CheckIfHomeActivity.this, device.getName(), plugins.get(0));

						// Add the action to the rule.
						Log.d(TAG, "Adding action to rule.");
						rule.addAction(CheckIfHomeActivity.this, action);

						// Add the device to the rule.
						Log.d(TAG, "Adding device to rule.");
						rule.addDevice(CheckIfHomeActivity.this, device);

						// Tell the adapter about this new list.
						CheckIfHomeActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								
								//TODO check for home
								
								if ( device.getId().equals(homeNetwork) ) {
									app.mPrefs.setHome(true);
									finish();
									
									Toast.makeText(CheckIfHomeActivity.this, "You are home!", Toast.LENGTH_LONG).show();
									
									if (dialog != null ) {
										dialog.dismiss();
									}
									
									handler.removeCallbacks(runnable);
								}
								
								

								
								// Add the rule to our list view.
								Log.d(TAG, "Adding Rule to Adapter.");
								adapter.add(rule);

								Log.d(TAG, "Rule added.");
								adapter.notifyDataSetChanged();
								Log.d(TAG, "Notified.");
							}
						});
					}
				} catch (Exception e) {
					Log.e(TAG, "Exception: ", e);
				}
			}
		}, 1, 30, TimeUnit.SECONDS);


		// Setup the list view.
		ListView listView = (ListView) findViewById(R.id.rules_list);
		listView.setAdapter(adapter);

	}


}
