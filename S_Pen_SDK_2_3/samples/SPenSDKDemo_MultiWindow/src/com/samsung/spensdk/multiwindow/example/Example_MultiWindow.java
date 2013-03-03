
package com.samsung.spensdk.multiwindow.example;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.samsung.spen.lib.multiwindow.SMultiWindowManager;
import com.samsung.spensdk.multiwindow.R;
import com.samsung.spensdk.multiwindow_applistener.SMultiWindowDropListener;


public class Example_MultiWindow extends Activity {

	private List<ActivityInfo> mActivityInfo = null; 	
	private List<ActivityManager.RunningTaskInfo> mRunningTaskList = null;
	private SMultiWindowManager mMWM;	
	private View mDecorView;
	private Context mContext;
	private int mScreenWidth;
	private int mScreenHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.general_purpose_multiwindow);	
		
		mContext = this;
		
		mMWM = new SMultiWindowManager(this);
		if(mMWM!=null)
			mActivityInfo = mMWM.getRegisteredMultiWindowAppList();		
		
		Window gW = getWindow();
		if(gW!=null)
			mDecorView = gW.getDecorView();	

		Display screenSize = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		mScreenWidth = screenSize.getWidth();
		mScreenHeight = screenSize.getHeight();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		printClipData(null);

		View linearLayout = findViewById(R.id.linearLayout);

		// Create OnLongClickListener
		View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				Log.d(getLocalClassName(), "onLongClick View : " + v.toString());

				String[] mimeTypes = {"text/plain", "text/uri-list"};

				ClipData dragData = new ClipData(getLocalClassName(), mimeTypes, new ClipData.Item("Drag & Drop"));

				ClipData.Item uriItem = new ClipData.Item(Uri.parse("http://www.samsung.net/"));
				dragData.addItem(uriItem);

				DragShadowBuilder dragShadowBuilder = new DragShadowBuilder();

				return v.startDrag(dragData, dragShadowBuilder, null, 0);
			}
		};
		// Register OnLongClickListener to view to receive long click event 
		linearLayout.setOnLongClickListener(onLongClickListener);

		// Create SPenMWDropListener
		SMultiWindowDropListener dl = new SMultiWindowDropListener() {
			@Override
			public void onDrop(DragEvent event) {
				Log.d(getLocalClassName(), "onDrop");
				printClipData(event.getClipData());
			}
		};
		// Register SPenMWDropListener to decorView to receive drop event view
		mDecorView.setOnDragListener(dl);


		View splitButton = findViewById(R.id.buttonSplit);
		View.OnClickListener vcl = new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d(getLocalClassName(), "onClick which : " + which);
						selectMultiWindowApplication(which);
						// dialog.dismiss();
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

				builder.setTitle("Registered App List");
				builder.setIcon(R.drawable.icon);				
				if(mActivityInfo.size() <= 0) {
					builder.setTitle("Registered App List : Empty");
				}
				else {
					builder.setAdapter(new MultiWindowAppListAdapter(v.getContext(), R.layout.general_purpose_multiwindow_list_adapter, mActivityInfo, null), cl);
				}
				builder.setNegativeButton("Cancel", null);                
				builder.show();
			}
		};
		splitButton.setOnClickListener(vcl);

		View runningButton = findViewById(R.id.buttonRunning);
		runningButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d(getLocalClassName(), "onClick which : " + which);	
						selectRunningApplication(which);
						dialog.cancel();
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

				builder.setTitle("Running App List");
				builder.setIcon(R.drawable.icon);
				if(mRunningTaskList!=null)
					mRunningTaskList.clear();
				mRunningTaskList = mMWM.getRunningMultiWindowAppList(SMultiWindowManager.FREE_STYLE);				
				if(mRunningTaskList.size() <= 0) {
					builder.setTitle("Running App List : Empty");
				}
				else {
					builder.setAdapter(new MultiWindowAppListAdapter(v.getContext(), R.layout.general_purpose_multiwindow_list_adapter, null, mRunningTaskList), cl);				
				}
				builder.setNegativeButton("Cancel", null);
				builder.show();             
			}
		});       

		View pinupButton = findViewById(R.id.buttonPinup);
		pinupButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!SMultiWindowManager.isFreeStyleMultiWindowSupport(mContext)){
					Toast.makeText(mContext, "Pinup is not supported.", Toast.LENGTH_LONG).show();
					return;				
				}
				
				DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d(getLocalClassName(), "onClick which : " + which);	                        
						dialog.cancel();
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

				builder.setTitle("Pinup App List");
				builder.setIcon(R.drawable.icon);	   
				if(mRunningTaskList!=null)
					mRunningTaskList.clear();
				mRunningTaskList = mMWM.getRunningMultiWindowAppList(SMultiWindowManager.PINUP_STYLE);
				if(mRunningTaskList.size() <= 0) {
					builder.setTitle("Pinup App List : Empty");
				}
				else {
					builder.setAdapter(new MultiWindowAppListAdapter(v.getContext(), R.layout.general_purpose_multiwindow_list_adapter, null, mRunningTaskList), cl);
				}

				builder.setNegativeButton("Cancel", null);
				builder.show();
			}
		});
	}

	

	private void selectMultiWindowApplication(int index) {
		ActivityInfo activityInfo = mActivityInfo.get(index);		
		if (activityInfo != null) {		
			Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			//FreeStyle, PinupStyle window is supported only Galaxy Note 10.1
			if(SMultiWindowManager.isFreeStyleMultiWindowSupport(mContext)){				
				Rect multiWindowRect = getMultiWindowRect();
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.FREE_AND_PINUP_STYLE, multiWindowRect));
			} 
			//Split mode window is supported Galaxy Note2 & Galaxy Note 10.1
			else if(SMultiWindowManager.isSplitMultiWindowSupport(mContext)){
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.SPLIT_ZONE_A, null));
			}
			
			startActivity(intent);
		}
	}
	
	private void selectRunningApplication(int index) {		
		ActivityManager.RunningTaskInfo runningTaskInfo = mRunningTaskList.get(index);		
		if (runningTaskInfo != null) {				
			String packageName = runningTaskInfo.baseActivity.getPackageName();					
			PackageManager pm = getPackageManager();
			Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
//			if(launchIntent==null)
//				return;
			
			String className;
			if(launchIntent!=null){
				List<ResolveInfo> list = pm.queryIntentActivities(launchIntent, 0);
				boolean bLaunchResolved = false;
				if(list!=null && list.size()>0)
					bLaunchResolved = true;
						
				// if not resolved
				if(bLaunchResolved==false){			
					String shortClassName = runningTaskInfo.topActivity.getShortClassName();
					if(shortClassName.startsWith(".")) className = packageName + shortClassName;
					else className = shortClassName;
				}
				// register as class name of LauchActivity
				else{
					ComponentName cnLaunch = launchIntent.getComponent();				
					className = cnLaunch.getClassName();	
				}			
			}
			else{
				String shortClassName = runningTaskInfo.topActivity.getShortClassName();
				if(shortClassName.startsWith(".")) className = packageName + shortClassName;
				else className = shortClassName;
			}
				
			Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(packageName, className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						
			//FreeStyle, PinupStyle window is supported only Galaxy Note 10.1
			if(SMultiWindowManager.isFreeStyleMultiWindowSupport(mContext)){
				
				Rect multiWindowRect = getMultiWindowRect();
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.FREE_AND_PINUP_STYLE, multiWindowRect));
			} 
			//Split mode window is supported Galaxy Note2 & Galaxy Note 10.1
			else if(SMultiWindowManager.isSplitMultiWindowSupport(mContext)){
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.SPLIT_ZONE_A, null));
			}
			
			intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.SPLIT_ZONE_A, null));
			startActivity(intent);			
		}
	}

	private Rect getMultiWindowRect() {
		Rect tmpRect = new Rect();
		tmpRect.left = mScreenWidth/4;
		tmpRect.top =  mScreenHeight/4;
		tmpRect.right = 3*mScreenWidth/4;
		tmpRect.bottom = 3*mScreenHeight/4;		
		return tmpRect;		
	}



	private void printClipData(ClipData clipData) {
        StringBuffer s = new StringBuffer();                
        if (clipData != null) {
            int count = clipData.getItemCount();
            s.append((String) clipData.getDescription().getLabel() + "");           
            for (int index = 0; index < count; ++index) {
                ClipData.Item item = clipData.getItemAt(index);

                s.append("\n[" + index + ":" + count + ", " + clipData.getDescription().getMimeType(index) + "] ");              
                if (item.getText() != null)
                    s.append(item.getText());
                if (item.getUri() != null)
                    s.append(item.getUri().toString());
                if (item.getIntent() != null)
                    s.append(item.getIntent().toString());
            }
        }
        else
        	s.append("No data");

        TextView textView = (TextView) findViewById(R.id.textViewClipData);
        textView.setText("Clip Data : " + s.toString());
        s.delete(0, s.length());
    }


	// ListAdapter
	public class MultiWindowAppListAdapter extends BaseAdapter {
		private Context mContext;
		private List<ActivityInfo> mList;
		private List<ActivityManager.RunningTaskInfo> mTaskList;
		private int mResource;
		private LayoutInflater mInflater;

		// Constructor.
		public MultiWindowAppListAdapter(Context context, int layoutResource, List<ActivityInfo> list, List<ActivityManager.RunningTaskInfo> taskInfo) {			
			this.mContext = context;
			this.mResource = layoutResource;
			this.mList = list;			
			this.mTaskList = taskInfo;
			this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			int count = 0;

			if (mList != null) {
				count = mList.size();
			}
			if (mTaskList != null) {
				count = mTaskList.size();
			}

			return count;
		}

		public Object getItem(int position) {
			Object obj = null;

			if (mList != null) {
				obj = mList.get(position);
			}

			if (mTaskList != null) {
				obj = mTaskList.get(position);
			}

			return obj;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(mResource, null);
			}

			if(mList != null) {
				ActivityInfo info = mList.get(position);
				if (info != null)
				{
					ImageView icon = (ImageView) convertView.findViewById(R.id.icon);					
					icon.setVisibility(View.VISIBLE);
					icon.setImageDrawable(info.loadIcon(mContext.getPackageManager()));				
					
					TextView name = (TextView) convertView.findViewById(R.id.name);
					String label = info.loadLabel(mContext.getPackageManager()).toString();
					// Set text
					name.setText(label);
					name.setTextColor(Color.WHITE);
				}
			}

			if(mTaskList != null) {
				ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
				icon.setVisibility(View.GONE);
				ActivityManager.RunningTaskInfo info = mTaskList.get(position);
				TextView name = (TextView) convertView.findViewById(R.id.name);						

				name.setText(info.baseActivity.toString());
				name.setTextSize(20f);
				name.setTextColor(Color.WHITE);				
			}

			return convertView;
		}
	}
}
