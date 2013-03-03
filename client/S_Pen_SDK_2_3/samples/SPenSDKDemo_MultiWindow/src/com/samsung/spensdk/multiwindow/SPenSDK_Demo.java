package com.samsung.spensdk.multiwindow;

import com.samsung.spen.lib.multiwindow.SMultiWindowManager;
import com.samsung.spensdk.multiwindow.example.Example_MultiWindow;
import com.samsung.spensdk.multiwindow.example.Example_SPen_MultiWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SPenSDK_Demo extends Activity {

	public static final String MULTIWINDOW_EXAMPLE = "Multi-Window Examples";
	private ListAdapter mListAdapter = null;
	private ListView mListView = null;	
	private Context mContext = null;

	// The item of list
	private static final int 	SDK_MULTIWINDOW			= 0;
	private static final int 	SDK_SPEN_MULTIWINDOW	= 1;		
	private static final int 	TOTAL_LIST_NUM 			= 2;	



	private final String EXAMPLE_NAMES[] = {
			"Editor : Multi-Window",
			"Editor : SPen & Multi-Window",
	};

	@Override 
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spensdk_demo);
		mContext = this;

		createUI();
	}

	private void createUI() {
		TextView textTitle = (TextView)findViewById(R.id.title);
		textTitle.setText(MULTIWINDOW_EXAMPLE);
		textTitle.setTextColor(0xFFFFCCCC);

		mListAdapter = new ListAdapter(this);
		mListView = (ListView)findViewById(R.id.demo_list);
		mListView.setAdapter(mListAdapter);

		mListView.setItemsCanFocus(false);
		mListView.setTextFilterEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// S Pen SDK Demo programs
				if(position == SDK_MULTIWINDOW) {
					if(SMultiWindowManager.isMultiWindowSupport(mContext)) {
						Intent intent = new Intent(SPenSDK_Demo.this, Example_MultiWindow.class);				
						startActivity(intent);		
					}
					else {
						Toast.makeText(mContext, "MultiWindow is not supported.", Toast.LENGTH_LONG).show();
					}
				}				
				else if(position == SDK_SPEN_MULTIWINDOW) {
					if(SMultiWindowManager.isMultiWindowSupport(mContext)) {
						Intent intent = new Intent(SPenSDK_Demo.this, Example_SPen_MultiWindow.class);						
						startActivity(intent);
					}
					else {
						Toast.makeText(mContext, "MultiWindow is not supported.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	//=========================================
	// List Adapter : S Pen SDK Demo Programs   
	//=========================================
	public class ListAdapter extends BaseAdapter {

		public ListAdapter(Context context) {
		}    	

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.spensdk_demolist_item, parent, false);
			}
			// UI Item			
			TextView tvListItemText=  (TextView)convertView.findViewById(R.id.listitemText);
			tvListItemText.setTextColor(0xFFFFFFFF);

			//==================================
			// basic data display 
			//==================================
			if(position < TOTAL_LIST_NUM){
				tvListItemText.setText(EXAMPLE_NAMES[position]);

			}
			return convertView; 
		}

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return TOTAL_LIST_NUM;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

}
