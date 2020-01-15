package com.jf.uhf.setpage;


import com.reader.base.CMD;
import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.jf.uhf.LogList;
import com.jf.uhf.R;
import com.jf.uhf.UHFApplication;
import com.jf.uhf.R.id;
import com.jf.uhf.R.layout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class PageReaderFirmwareVersion extends Activity {
	private LogList mLogList;
	
	private TextView mGet;
	
	private EditText mFirmwareVersionEditText;
	
	private ReaderHelper mReaderHelper;
	private ReaderBase mReader;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    
    private LocalBroadcastManager lbm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_reader_firmware_version);
		((UHFApplication) getApplication()).addActivity(this);
		
		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
			mReader = mReaderHelper.getReader();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_curReaderSetting = mReaderHelper.getCurReaderSetting();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();

		mLogList = (LogList) findViewById(R.id.log_list);
		mGet = (TextView) findViewById(R.id.cancel);
		mFirmwareVersionEditText = (EditText) findViewById(R.id.firmware_version_text);
		
		mGet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mReader.getFirmwareVersion(m_curReaderSetting.btReadId);
			}
		});
		
		lbm  = LocalBroadcastManager.getInstance(this);
		
		IntentFilter itent = new IntentFilter();
		itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
		itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
		lbm.registerReceiver(mRecv, itent);
		
		updateView();
	}
	
	private void updateView() {
		mFirmwareVersionEditText.setText(ReaderHelper.hardwareStr);
	}
	
	private final BroadcastReceiver mRecv = new BroadcastReceiver() 
	{
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING)) 
			{
				byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);
				
				if (btCmd == CMD.GET_FIRMWARE_VERSION) 
				{
					updateView();
				}
			} 
			else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) 
			{
	            mLogList.writeLog((String)intent.getStringExtra("log"), intent.getIntExtra("type", ERROR.SUCCESS));
            }
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mLogList.tryClose()) return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (lbm != null)
			lbm.unregisterReceiver(mRecv);
	}
}

