package com.jf.uhf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.jf.uhf.R;
import com.jf.uhf.setpage.PageReaderFirmwareVersion;

import com.jf.uhf.tagpage.PageInventoryReal;
import com.jf.uhf.tagpage.PageTagAccess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class Monitor extends LinearLayout {
	private Context mContext;
	
	private CheckBox mCheckOpenMonitor;
	private TextView mRefreshButton;
	private ListView mMonitorList;
	private ArrayAdapter<CharSequence> mMonitorListAdapter;
	private ArrayList<CharSequence> mMonitorListItem;
	
	private ReaderBase mReader;
	private ReaderHelper mReaderHelper;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
	
	public Monitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.monitor, this);
		
		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
			mReader = mReaderHelper.getReader();
		} catch (Exception e) {
			return ;
		}
		
		m_curReaderSetting = mReaderHelper.getCurReaderSetting();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
	
		mCheckOpenMonitor = (CheckBox) findViewById(R.id.check_open_monitor);
		

		mMonitorList = (ListView)findViewById(R.id.monitor_list_view);
		
		mMonitorListItem = new ArrayList<CharSequence>();
		
		mMonitorListAdapter = new ArrayAdapter<CharSequence>(mContext, R.layout.monitor_list_item, mMonitorListItem);
		
		mMonitorList.setAdapter(mMonitorListAdapter);
		
		mRefreshButton = (TextView) findViewById(R.id.refresh);
		mRefreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMonitorListItem.clear();
				mMonitorListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public final void writeMonitor(String strLog, int type) {
		
		if (!mCheckOpenMonitor.isChecked()) return;
		
		Date now=new Date();
		SimpleDateFormat temp=new SimpleDateFormat("kk:mm:ss");
		SpannableString tSS = new SpannableString(temp.format(now) + ":\n" + strLog);
		tSS.setSpan(new ForegroundColorSpan(type == ERROR.SUCCESS ? Color.BLACK : Color.RED), 0, tSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mMonitorListItem.add(tSS);
		
		if (mMonitorListItem.size() > 1000) mMonitorListItem.remove(0);

		mMonitorListAdapter.notifyDataSetChanged();
	}
	
}
