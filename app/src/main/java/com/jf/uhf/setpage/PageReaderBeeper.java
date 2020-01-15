package com.jf.uhf.setpage;


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

import android.R.integer;
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
import android.widget.RadioGroup;
import android.widget.TextView;

public class PageReaderBeeper extends Activity {
	private LogList mLogList;
	
	private TextView mSet;
	
	private RadioGroup mGroupBeeper;
	
	private ReaderHelper mReaderHelper;
	private ReaderBase mReader;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    
    private LocalBroadcastManager lbm;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_reader_beeper);
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
		mSet = (TextView) findViewById(R.id.set);
		mGroupBeeper =  (RadioGroup) findViewById(R.id.group_beeper);
		
		mSet.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				byte btMode = 0;
				switch (mGroupBeeper.getCheckedRadioButtonId()) {
				case R.id.set_beeper_quiet:
					btMode = 0;
					break;
				case R.id.set_beeper_all:
					btMode = 1;
					break;
				}
				mReader.setBeeperMode(m_curReaderSetting.btReadId, btMode);
				m_curReaderSetting.btBeeperMode = btMode;
			}
		});
		
		lbm  = LocalBroadcastManager.getInstance(this);
		
		IntentFilter itent = new IntentFilter();
		itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
		
		lbm.registerReceiver(mRecv, itent);
		
		updateView();
	}
	
	private void updateView() 
	{
		if (m_curReaderSetting.btBeeperMode == 0) 
		{
			mGroupBeeper.check(R.id.set_beeper_quiet);
		} else if (m_curReaderSetting.btBeeperMode == 1) 
		{
			mGroupBeeper.check(R.id.set_beeper_all);
		}
	}
	
	private final BroadcastReceiver mRecv = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) {
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

