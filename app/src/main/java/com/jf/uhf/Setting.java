package com.jf.uhf;



import com.reader.base.CMD;
import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;



import com.jf.uhf.setpage.PageReaderBeeper;
import com.jf.uhf.setpage.PageReaderFirmwareVersion;

import com.jf.uhf.setpage.PageReaderOutPower;


import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class Setting extends ScrollView {
	private Context mContext;
	
	private TableRow mSettingResetRow;
	private TableRow mSettingReaderAddressRow;
	private TextView mSettingReaderAddressText;
	private TableRow mSettingIdentifierRow;
	private TextView mSettingIdentifierText;
	private TableRow mSettingFirmwareVersionRow;
	private TextView mSettingFirmwareVersionText;
	private TableRow mSettingTemperatureRow;
	private TextView mSettingTemperatureText;
	private TableRow mSettingGpioRow;
	private TextView mSettingGpioText;
	private TableRow mSettingBeeperRow;
	private TextView mSettingBeeperText;
	private TableRow mSettingOutPowerRow;
	private TextView mSettingOutPowerText;
	private TableRow mSettingAntennaRow;
	private TextView mSettingAntennaText;
	private TableRow mSettingReturnLossRow;
	private TextView mSettingReturnLossText;
	private TableRow mSettingAntDetectorRow;
	private TextView mSettingAntDetectorText;
	private TableRow mSettingMonzaRow;
	private TextView mSettingMonzaText;
	private TableRow mSettingRegionRow;
	private TextView mSettingRegionText;
	private TableRow mSettingProfileRow;
	private TextView mSettingProfileText;
	
	private ReaderBase mReader;
	private ReaderHelper mReaderHelper;
	
	private LogList mLogList;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
	
	public Setting(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.setting, this);
		
		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
			mReader = mReaderHelper.getReader();
		} catch (Exception e) {
			return ;
		}
		
		initSettingView();
		
		m_curReaderSetting = mReaderHelper.getCurReaderSetting();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
	}
	
	public void setLogList(LogList logList) {
		mLogList = logList;
	}

	private void initSettingView() {
		
		mSettingResetRow = (TableRow) findViewById(R.id.table_row_setting_reset);
		mSettingResetRow.setOnClickListener(setSettingOnClickListener);
			
		mSettingFirmwareVersionRow = (TableRow) findViewById(R.id.table_row_setting_firmware_version);
		mSettingFirmwareVersionRow.setOnClickListener(setSettingOnClickListener);
		mSettingFirmwareVersionText = (TextView) findViewById(R.id.text_setting_firmware_version);

		
		mSettingBeeperRow = (TableRow) findViewById(R.id.table_row_setting_beeper);
		mSettingBeeperRow.setOnClickListener(setSettingOnClickListener);
		mSettingBeeperText = (TextView) findViewById(R.id.text_setting_beeper);

		
		mSettingOutPowerRow = (TableRow) findViewById(R.id.table_row_setting_out_power);
		mSettingOutPowerRow.setOnClickListener(setSettingOnClickListener);
		mSettingOutPowerText = (TextView) findViewById(R.id.text_setting_out_power);
		
	}
	
	private void writeLog(String strLog, byte type) {
		if (mLogList != null)
			mLogList.writeLog(strLog, type);
	}
	
	public void refreshReaderSetting(byte btCmd) {
		switch (btCmd) {
		
		}
	}
	
	private OnClickListener setSettingOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = null;
			switch(arg0.getId()) {
			case R.id.table_row_setting_reset:
				mReader.reset(m_curReaderSetting.btReadId);
				writeLog(CMD.format(CMD.RESET), ERROR.SUCCESS);
				break;

			case R.id.table_row_setting_firmware_version:
				intent = new Intent().setClass(mContext, PageReaderFirmwareVersion.class);
				break;

			case R.id.table_row_setting_beeper:
				intent = new Intent().setClass(mContext, PageReaderBeeper.class);
				break;
			case R.id.table_row_setting_out_power:
				intent = new Intent().setClass(mContext, PageReaderOutPower.class);
				break;

			default:
				intent = null;
			}
			
			if (intent != null) mContext.startActivity(intent);
		}
	};
}
