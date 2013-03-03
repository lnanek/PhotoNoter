package com.samsung.spensdk.multiwindow.example;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectText;
import com.samsung.spen.lib.multiwindow.SMultiWindowManager;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.multiwindow.R;
import com.samsung.spensdk.multiwindow.example.tools.SPenSDKUtils;
import com.samsung.spensdk.multiwindow.example.tools.ToolListActivity;
import com.samsung.spensdk.multiwindow_applistener.SMultiWindowDropListener;



public class Example_SPen_MultiWindow extends Activity {

	private final String TAG = "SPenSDK Sample";

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 2;
	private final int APPLICATION_ID_VERSION_MINOR = 2;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";	
	
	private final int MENU_FILE_SAVE = 1000;
	private final int MENU_FILE_LOAD = 1001;
	
	private final int REQUEST_CODE_FILE_SELECT = 10000;
	

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private FrameLayout		mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;		
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
	private ImageView		mMWBtn;

	private List<ActivityInfo> mActivityInfo = null;   
	private SMultiWindowManager mMWM;
	private String  mTempAMSFolderPath = null;
	
	private final String DEFAULT_SAVE_PATH = "SPenSDK2.2";
	private final String DEFAULT_FILE_EXT = ".png";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.example_multiwindow_editor);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mPenBtn.setOnLongClickListener(mBtnLongClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mTextBtn.setOnLongClickListener(mBtnLongClickListener);		

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		mMWBtn = (ImageView) findViewById(R.id.mwBtn);
		mMWBtn.setOnClickListener(mBtnClickListener);

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		mSCanvas = new SCanvasView(mContext);        
		mCanvasContainer.addView(mSCanvas);


		// Full Screen for Galaxy Note 2
		mSCanvas.setSCanvasSize(720, 1056);	
		
		// Full Screen for Galaxy Note 10.1
		//mSCanvas.setSCanvasSize(800, 1138);			
		
		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = getSettingLayoutLocaleResourceMap(true, true, true);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = getSettingLayoutStringResourceMap(true, true, true);
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);


		//------------------------------------
		// Create MultiWindowManager
		//------------------------------------		     
		mMWM = new SMultiWindowManager(this);
		// To get enable application list applied multi-window
		mActivityInfo = mMWM.getRegisteredMultiWindowAppList();



		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		// Callback Listener to inform change of layout by user input
		ViewTreeObserver vto = mCanvasContainer.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        // Get Current Rect (Absolute Location)
		        int [] pos = new int[2];
		        mCanvasContainer.getLocationOnScreen(pos);
		        int newLeft = pos[0]; 		
		        int newTop  = pos[1];
		        
		        // Get Current Window Size
		        int newWidth  = mCanvasContainer.getWidth();	
		        int newHeight = mCanvasContainer.getHeight();
		        
		        // Get Current Rect (Absolute Location)
		        Rect r = new Rect(newLeft, newTop, newLeft + newWidth, newTop + newHeight);

		    }
		});

		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() { 
				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------
				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();


				// Create SPenMWOnDragListener
				SMultiWindowDropListener dl = new SMultiWindowDropListener() {
					@Override
					public void onDrop(DragEvent event) {					
						printClipData(event);						
					}
				};				
				mSCanvas.setOnDragListener(dl);
				mSCanvas.maintainScaleOnResize(true);

				Bitmap bmBG = BitmapFactory.decodeResource(getResources(), R.drawable.scanvas_bg);
				// no save bg image
				mSCanvas.setBackgroundImageExpress(bmBG);
				// Update button state
				updateModeState();				
			}
		});

		//------------------------------------------------
		// History Change Listener
		//------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		});


		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				updateModeState();				
			}

			@Override
			public void onColorPickerModeEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMovingModeEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		File sdcard_path = Environment.getExternalStorageDirectory();
		File default_path =  new File(sdcard_path, DEFAULT_SAVE_PATH);
		if(!default_path.exists()){
			if(!default_path.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}

		mTempAMSFolderPath = default_path.getAbsolutePath();

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);
	}
	
	public boolean onCreateOptionsMenu(Menu menu){		
		menu.add(MENU_FILE_SAVE, MENU_FILE_SAVE, 1, "Save");
		menu.add(MENU_FILE_LOAD, MENU_FILE_LOAD, 2, "Load");

		return super.onCreateOptionsMenu(menu);
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {		
		
		case MENU_FILE_SAVE:
		{			//-------------------------------
			// layout setting
			//-------------------------------
			//check canvas drawing empty
			if(mSCanvas.isCanvasDrawingEmpty(true)){
				Toast.makeText(mContext, "There is no Canvas Drawing", Toast.LENGTH_LONG).show();
				return true;
			}
				
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter filename to save (default: *.png)");
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Save As")
			.setView(textEntryView)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					String strFileName = et.getText().toString();

					// check file name length, invalid characters, overwrite, extension, etc.
					if(strFileName==null)
						return; 
					
					if(strFileName.length()<=0){
						Toast.makeText(mContext, "Enter file name to save", Toast.LENGTH_LONG).show();
						return;
					}
					if(!SPenSDKUtils.isValidSaveName(strFileName)) {						
						Toast.makeText(mContext, "Invalid character to save! Save file name : "+ strFileName, Toast.LENGTH_LONG).show();
						return;
					}
					
					int nExtIndex = strFileName.lastIndexOf(".");	
					if(nExtIndex==-1)	
						strFileName += DEFAULT_FILE_EXT;
					else{
						String strExt = strFileName.substring(nExtIndex + 1);
						if(strExt==null)
							strFileName += DEFAULT_FILE_EXT;
						else{
							if(strExt.compareToIgnoreCase("png")!=0 && strExt.compareToIgnoreCase("jpg")!=0){
						strFileName += DEFAULT_FILE_EXT;
							}							
						}							
					}				
					
					String saveFileName = mTempAMSFolderPath + "/" + strFileName;
					checkSameSaveFileName(saveFileName);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();
		}	
		break;
		case MENU_FILE_LOAD:
		{
			Intent intent = new Intent(this, ToolListActivity.class);
			String [] exts = new String [] { "jpg", "png", "ams" }; // file extension 			
			intent.putExtra(ToolListActivity.EXTRA_LIST_PATH, mTempAMSFolderPath);
			intent.putExtra(ToolListActivity.EXTRA_FILE_EXT_ARRAY, exts);
			intent.putExtra(ToolListActivity.EXTRA_SEARCH_ONLY_SAMM_FILE, true);
			startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
		}
		
		}
		return true;
	}

	private void checkSameSaveFileName(final String saveFileName) {	

		File fSaveFile = new File(saveFileName);
		if(fSaveFile.exists()) {
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Same file name exists! Overwrite?")		
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {				
					
					saveSAMMFile(saveFileName, true);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.show();
		}
		else {
			saveSAMMFile(saveFileName, true);			
		}
	}
	
	boolean saveSAMMFile(String strFileName, boolean bShowSuccessLog){		
		if(mSCanvas.saveSAMMFile(strFileName)){
			if(bShowSuccessLog){
				Toast.makeText(this, "Save AMS File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		else{
			Toast.makeText(this, "Save AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub		
		AlertDialog.Builder ad = new AlertDialog.Builder(mContext);		
		ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
		ad.setTitle(getResources().getString(R.string.app_name))
		.setMessage("Exit this program")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				// finish dialog
				dialog.dismiss();		
				finish();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.show();
		ad = null;		
	} 


	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);					
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			}
			else if(nBtnID == mTextBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}	
			else if(nBtnID == mMWBtn.getId()){
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
				builder.setAdapter(new MultiWindowAppListAdapter(v.getContext(), 
						R.layout.general_purpose_multiwindow_list_adapter, mActivityInfo), cl);
				builder.setNegativeButton("Cancel", null);
				builder.show();
			}	
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			if(data == null)
				return;

			if(requestCode==REQUEST_CODE_FILE_SELECT){
				Bundle bundle = data.getExtras();
				if(bundle == null)
					return;
				String strFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);
				mSCanvas.loadSAMMFile(strFileName, true, true, true);		
				
				Bitmap bmBG = BitmapFactory.decodeResource(getResources(), R.drawable.scanvas_bg);
				// no save bg image
				mSCanvas.setBackgroundImageExpress(bmBG);
			}
		}
	}

	private OnLongClickListener mBtnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){					
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);					
					updateModeState();
				}
				return true;
			}
			else if(nBtnID == mEraserBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
					updateModeState();
				}
				return true;
			}
			else if(nBtnID == mTextBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, true);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
				return true;
			}			

			return false;
		}
	};


	// Update tool button
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mTextBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);				
	}	


	private void selectMultiWindowApplication(int index) {
		ActivityInfo ai = mActivityInfo.get(index);
		if (ai != null) {

			Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(ai.packageName, ai.name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			//FreeStyle, PinupStyle window is supported only Galaxy Note 10.1
			if(SMultiWindowManager.isFreeStyleMultiWindowSupport(mContext)){						
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.FREE_AND_PINUP_STYLE, new Rect(640, 0, 1280, 752)));
			} 
			//Split mode window is supported Galaxy Note2 & Galaxy Note 10.1
			else if(SMultiWindowManager.isSplitMultiWindowSupport(mContext)){
				intent.putExtras(mMWM.makeMultiWindowAppIntent(SMultiWindowManager.SPLIT_ZONE_A, null));
			}
			startActivity(intent);
		}
	}


	// ListAdapter
	public class MultiWindowAppListAdapter extends BaseAdapter {
		private Context mContext;
		private List<ActivityInfo> mList;
		private int mResource;
		private LayoutInflater mInflater;

		// Constructor.
		public MultiWindowAppListAdapter(Context context, int layoutResource, List<ActivityInfo> mActivityInfo) {       	
			this.mContext = context;
			this.mResource = layoutResource;
			this.mList = mActivityInfo;
			this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if(mList==null)
				return 0;

			return mList.size();
		}

		public Object getItem(int position) {
			if(mList==null)
				return null;

			return mList.get(position);
		}

		public long getItemId(int position) {
			if(mList==null)
				return 0;

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(mList==null)
				return null;

			ActivityInfo info = mList.get(position);

			if (convertView == null) {
				convertView = mInflater.inflate(mResource, null);
			}

			if (info != null)
			{
				ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
				TextView name = (TextView) convertView.findViewById(R.id.name);

				if (icon != null) {
					icon.setImageDrawable(info.loadIcon(mContext.getPackageManager()));
				}

				String label = info.loadLabel(mContext.getPackageManager()).toString();
				// set text
				name.setText(label);
				name.setTextColor(Color.WHITE);
			}
			return convertView;
		}
	}


	public HashMap<String, Integer> getSettingLayoutLocaleResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting){
		//----------------------------------------
		// Resource Map for Layout & Locale
		//----------------------------------------
		HashMap<String,Integer> settingResourceMapInt = new HashMap<String, Integer>();
		// Layout 
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_PEN_SPINNER, R.layout.mspinner);
		}		
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER, R.layout.mspinnertext);
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER_TABLET, R.layout.mspinnertext_tablet);
		}

		//----------------------------------------
		// Locale(Multi-Language Support)	
		//----------------------------------------
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_TITLE, R.string.pen_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EMPTY_MESSAGE, R.string.pen_settings_preset_empty);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_TITLE, R.string.pen_settings_preset_delete_title);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_MESSAGE, R.string.pen_settings_preset_delete_msg);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EXIST_MESSAGE, R.string.pen_settings_preset_exist);
		}
		if(bUseEraserSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_TITLE, R.string.eraser_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_CLEARALL, R.string.clear_all);
		}
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TITLE, R.string.text_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_FONT, R.string.text_settings_tab_font);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH, R.string.text_settings_tab_paragraph);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH_ALIGN, R.string.text_settings_tab_paragraph_align);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXTBOX_HINT, R.string.textbox_hint);

			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME1, R.string.user_font_name1);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME2, R.string.user_font_name2);
		}

		// common
		settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_CLOSE_DESCRIPTION, R.string.settingview_close_btn_desc);

		return settingResourceMapInt;
	}

	public HashMap<String, String> getSettingLayoutStringResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting){
		HashMap<String,String> settingResourceMapString = new HashMap<String, String>();
		if(bUseTextSetting){
			// Resource Map for Custom font path
			settingResourceMapString = new HashMap<String, String>();
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH1, "fonts/chococooky.ttf");
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH2, "fonts/rosemary.ttf");
		}	

		return settingResourceMapString;
	}

	private void printClipData(DragEvent event) 
	{
		ClipData clipData = event.getClipData();
		float fX = (float)event.getX();
		float fY = (float)event.getY();		

		if (clipData != null){
			int count = clipData.getItemCount();                		
			for (int index = 0; index < count; index++) 
			{
				ClipData.Item item = clipData.getItemAt(index);  
				if (item.getUri() != null)
				{
					String type = clipData.getDescription().getMimeType(index);		                    	
					if (type.contains("text/uri-list")){

						StringBuffer s = new StringBuffer(); 
						s.append(item.getUri().toString());

						SObjectText sTextData = new SObjectText();
						sTextData.setColor(0xff0000ff);
						sTextData.setStyle(SObjectText.SAMM_TEXT_STYLE_ITALIC);
						sTextData.setSize(7f);
						sTextData.setText(s.toString());							
						sTextData.setRect(getDefaultTextRect(fX, fY));	

						// not selected
						mSCanvas.insertSAMMText(sTextData, true);

						s.delete(0, s.length());
						updateModeState();
					}
					else if (type.contains("image/png") || type.contains("image/jpeg")) 
					{    		
						String imagePath = SPenSDKUtils.getRealPathFromURI(this, item.getUri());						

						// Check Valid Image File
						if(!SPenSDKUtils.isValidImagePath(imagePath)){
							Toast.makeText(this, "Invalid image path" +  imagePath + " or web image", Toast.LENGTH_LONG).show();	
							Log.d( "IFA" , "Invalid image path" +  imagePath + " or web image" );

							return;
						}						

						SObjectImage sImageObject = new SObjectImage();
						sImageObject.setRect(getDefaultImageRect(imagePath, fX, fY));
						sImageObject.setImagePath(imagePath);		

						mSCanvas.insertSAMMImage(sImageObject, true);		            			
						updateModeState();     
					}
				}           
				else{
					if (item.getText() != null){
						StringBuffer s = new StringBuffer(); 

						if (item.getText() != null){							
							s.append(item.getText());   		                		
						}		
						if (item.getIntent() != null)
							s.append(item.getIntent().toString());		

						SObjectText sTextData = new SObjectText();
						sTextData.setColor(0xffff00ff);
						sTextData.setStyle(SObjectText.SAMM_TEXT_STYLE_BOLD);
						sTextData.setSize(7f);
						sTextData.setText(s.toString());						
						sTextData.setRect(getDefaultTextRect(fX, fY));		

						mSCanvas.insertSAMMText(sTextData, true);						
						s.delete(0, s.length());							
						updateModeState();
					}			                
				}       	
			}		            
		}	
	}

	RectF getDefaultTextRect(float fX, float fY)
	{		
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			

		// Text Size : MaxScreenSize/4
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/4 : nScreenWidth/4;	

		// Text position : start from fx, fy
		return new RectF(fX,fY,fX+nBoxRadius,fY+nBoxRadius);	
	}


	RectF getDefaultImageRect(String strImagePath, float fX, float fY)
	{
		// Rect Region : Consider image real size
		BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(strImagePath);
		int nImageWidth = opts.outWidth;
		int nImageHeight = opts.outHeight;
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		// Image Size 
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/4 : nScreenWidth/4;	

		// Image position : considering center of image size, fx and fy
		if(nImageWidth > nImageHeight)
			return new RectF(fX-nBoxRadius,fY-(nBoxRadius*nImageHeight/nImageWidth),fX+nBoxRadius,fY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(fX-(nBoxRadius*nImageWidth/nImageHeight),fY-nBoxRadius,fX+(nBoxRadius*nImageWidth/nImageHeight),fY+nBoxRadius);
	}	
}
