package com.newaer.newaersampleapp;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.newaer.sdk.NAAction;
import com.newaer.sdk.NAActionPlugin;
import com.newaer.sdk.NADevice;
import com.newaer.sdk.NADeviceType;
import com.newaer.sdk.NAPlatform;
import com.newaer.sdk.NARule;

public class SampleActivity extends Activity {

	private static final String TAG = "SampleActivity";

	private ScheduledExecutorService service;
	private ArrayAdapter<NARule> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
	}

	@Override
	protected void onStart() {
		super.onStart();

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
		adapter.addAll(NARule.getAll(this));

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
				// Grab all the actions in the rule.

				for (NAAction action : adapter.getItem(position).getActions(SampleActivity.this)) {
					// Configure the action in the rule when they click.
					action.configure(SampleActivity.this);
				}
			}

		});

	}

}
