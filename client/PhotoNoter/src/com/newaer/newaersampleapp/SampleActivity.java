package com.newaer.newaersampleapp;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
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

public class SampleActivity extends Activity {

	private static final String TAG = "SampleActivity";

	private ScheduledExecutorService service;
	
	private ArrayAdapter<NARule> adapter;
	
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		
		
		//dialog = ProgressDialog.show(this, "Scanning...", "");

		service = Executors.newScheduledThreadPool(1);
		adapter = new ArrayAdapter<NARule>(this, 0) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView bindView = (TextView) convertView;
				if (bindView == null) {
					bindView = new TextView(SampleActivity.this);
				}
				bindView.setText(adapter.getItem(position).getName());

				return bindView;
			}
		};
		List<NARule> currentRules = NARule.getAll(this);
		if ( currentRules.isEmpty() ) {
			dialog = ProgressDialog.show(this, "Scanning...", "");
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
		}
		adapter.addAll(currentRules);

		// Enable Scanning for wifi
		NAPlatform.get(this).setEnabled(NADeviceType.WIFI, true);

		// Periodically scan for untagged devices.
		service.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					Log.d(TAG, "Running scan.");
					// Get the list of action plugins;
					List<NAActionPlugin> plugins = NAActionPlugin.getAll(SampleActivity.this);
					Log.d(TAG, "Got plugins: " + plugins.size());

					// Get recently seen wifi devices.
					List<NADevice> wifi = NADevice.getAllUntaggedByType(SampleActivity.this, NADeviceType.WIFI);
					Log.d(TAG, "Got: " + wifi.size());

					for (NADevice device : wifi) {
						// Tag the device.
						Log.d(TAG, "Tagging Device.");
						device.setTagged(SampleActivity.this, true);

						// Make a rule for this device.
						Log.d(TAG, "Creating Rule: " + device.getName());
						final NARule rule = NARule.create(SampleActivity.this, device.getName());

						// Make an action for this rule.
						Log.d(TAG, "Creating Action: " + device.getName());
						NAAction action = NAAction.create(SampleActivity.this, device.getName(), plugins.get(0));

						// Add the action to the rule.
						Log.d(TAG, "Adding action to rule.");
						rule.addAction(SampleActivity.this, action);

						// Add the device to the rule.
						Log.d(TAG, "Adding device to rule.");
						rule.addDevice(SampleActivity.this, device);

						// Tell the adapter about this new list.
						SampleActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								
								if (dialog != null ) {
									dialog.dismiss();
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

		// Setup a click listener on the list.
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				
				Object item = adapter.getItem(position);
				
				NARule rule = (NARule) item;
				
				List<NADevice> devices = rule.getDevices(SampleActivity.this);
				
				/*
				new AlertDialog.Builder(SampleActivity.this)
					.setMessage(devices.get(0).getName() +  " set as your home network!")
					.show();
				*/
				
				Toast.makeText(SampleActivity.this, devices.get(0).getName() +  " set as your home network!", Toast.LENGTH_LONG).show();
				
				final PhotoBackWriterApp app = PhotoBackWriterApp.getApp(SampleActivity.this);
				app.mPrefs.setHomeNetwork(devices.get(0).getId());
				app.mPrefs.setHome(true);
				
				finish();

				/*
				// Grab all the actions in the rule.

				for (NAAction action : adapter.getItem(position).getActions(SampleActivity.this)) {
					// Configure the action in the rule when they click.
					action.configure(SampleActivity.this);
				}
				*/
			}

		});		
		
	}

}
