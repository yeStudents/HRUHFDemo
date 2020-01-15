package com.jf.uhf;

import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.jf.uhf.R;
import com.jf.uhf.setpage.PageReaderBarcode;
import com.jf.uhf.setpage.PageReaderOutPower;
import com.jf.uhf.tagpage.PageInventoryReal;
import com.jf.uhf.tagpage.PageTagAccess;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableRow;

public class Tag extends ScrollView {
	private Context mContext;
	
	private TableRow mInventoryReal6CRow;
	
	private TableRow mReadBarcodeRow;
	
	//private TextView mInventoryReal6CText;
	//private TableRow mInventoryReal6BRow;
	//private TextView mInventoryReal6BText;
	//private TableRow mInventoryBufferRow;
	//private TextView mInventoryBufferText;
	//private TableRow mInventoryFast4antRow;
	//private TextView mInventoryFast4antText;
	private TableRow mAccessTag6CRow;
	//private TextView mAccessTag6CText;
	//private TableRow mAccessTag6BRow;
	//private TextView mAccessTag6BText;
	
	private ReaderBase mReader;
	private ReaderHelper mReaderHelper;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
	
	public Tag(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.tag, this);
		
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

	private void initSettingView() {
		
		mInventoryReal6CRow = (TableRow) findViewById(R.id.table_row_inventory_real);
		mInventoryReal6CRow.setOnClickListener(setTagOnClickListener);
		
		//mInventoryReal6CText = (TextView) findViewById(R.id.text_inventory_real_mode);
		/*
		mInventoryBufferRow = (TableRow) findViewById(R.id.table_row_inventory_buffer);
		mInventoryBufferRow.setOnClickListener(setTagOnClickListener);
		mInventoryBufferText = (TextView) findViewById(R.id.text_inventory_buffer_mode);
		
		mInventoryFast4antRow = (TableRow) findViewById(R.id.table_row_inventory_fast4ant);
		mInventoryFast4antRow.setOnClickListener(setTagOnClickListener);
		mInventoryFast4antText = (TextView) findViewById(R.id.text_inventory_fast4ant_mode);
		
		mInventoryFast4antRow = (TableRow) findViewById(R.id.table_row_inventory_fast4ant);
		mInventoryFast4antRow.setOnClickListener(setTagOnClickListener);
		mInventoryFast4antText = (TextView) findViewById(R.id.text_inventory_fast4ant_mode);
		
		mInventoryReal6BRow = (TableRow) findViewById(R.id.table_row_inventory_real_6b);
		mInventoryReal6BRow.setOnClickListener(setTagOnClickListener);
		mInventoryReal6BText = (TextView) findViewById(R.id.text_inventory_real_mode_6b);
		*/
		mAccessTag6CRow = (TableRow) findViewById(R.id.table_row_access_tag_6c);
		mAccessTag6CRow.setOnClickListener(setTagOnClickListener);
		
		mReadBarcodeRow = (TableRow) findViewById(R.id.table_row_readbarcode);
		mReadBarcodeRow.setOnClickListener(setTagOnClickListener);
		
		//mAccessTag6BRow = (TableRow) findViewById(R.id.table_row_access_tag_6b);
		//mAccessTag6BRow.setOnClickListener(setTagOnClickListener);
		//mAccessTag6CText = (TextView) findViewById(R.id.text_access_tag_6c);
		
		//mAccessTag6BText = (TextView) findViewById(R.id.text_access_tag_6b);
	}

	public void refreshReaderSetting(byte btCmd) {
		switch (btCmd) {
			
		}
	}
	
	private OnClickListener setTagOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = null;
			switch(arg0.getId()) {
			case R.id.table_row_inventory_real:
				intent = new Intent().setClass(mContext, PageInventoryReal.class);
				break;
			case R.id.table_row_access_tag_6c:
				intent = new Intent().setClass(mContext, PageTagAccess.class);
				break;
			case R.id.table_row_readbarcode:
				intent = new Intent().setClass(mContext, PageReaderBarcode.class);
				break;
			default:
				intent = null;
			}
			
			if (intent != null) mContext.startActivity(intent);
		}
	};
}
