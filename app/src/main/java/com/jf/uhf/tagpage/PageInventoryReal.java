package com.jf.uhf.tagpage;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.uhf.LogList;
import com.jf.uhf.R;
import com.jf.uhf.R.array;
import com.jf.uhf.R.id;
import com.jf.uhf.R.layout;
import com.jf.uhf.TagRealList;
import com.jf.uhf.UHFApplication;
import com.jf.uhf.spiner.SpinerPopWindow;
import com.reader.base.CMD;
import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.InventoryBuffer.InventoryTagMap;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class PageInventoryReal extends Activity {
	private LogList mLogList;
	
	private TextView mStart;
	private TextView mStop;
	private TextView mSave;
	
	private TextView mRefreshButton;
	
	private List<String> mSessionIdList;
	private List<String> mInventoriedFlagList;
	
	private SpinerPopWindow mSpinerPopWindow1, mSpinerPopWindow2;
	
	private EditText mRealRoundEditText;
	
	private TagRealList mTagRealList;
	
	private ReaderHelper mReaderHelper;
	private ReaderBase mReader;

	private int mPos1 = -1, mPos2 = -1;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    
    private LocalBroadcastManager lbm;
    
    private long mRefreshTime;
    
    public String filename=""; //用来保存存储的文件名
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_inventory_real);
		((UHFApplication) getApplication()).addActivity(this);
		
		try 
		{
			mReaderHelper = ReaderHelper.getDefaultHelper();
			mReader = mReaderHelper.getReader();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mSessionIdList = new ArrayList<String>();
		mInventoriedFlagList = new ArrayList<String>();

		m_curReaderSetting = mReaderHelper.getCurReaderSetting();
		m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();

		mLogList = (LogList) findViewById(R.id.log_list);
		mStart = (TextView) findViewById(R.id.start);
		mStop = (TextView) findViewById(R.id.stop);
		mSave = (TextView) findViewById(R.id.SaveData);
		
		mTagRealList = (TagRealList) findViewById(R.id.tag_real_list);
		
		mRealRoundEditText = (EditText) findViewById(R.id.real_round_text);
		
		mStart.setOnClickListener(setInventoryRealOnClickListener);
		
		mStop.setOnClickListener(setInventoryRealOnClickListener);
		
		mSave.setOnClickListener(setInventoryRealOnClickListener);
		
		lbm  = LocalBroadcastManager.getInstance(this);
		
		IntentFilter itent = new IntentFilter();
		itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
		itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
		lbm.registerReceiver(mRecv, itent);

		mSessionIdList.clear(); mInventoriedFlagList.clear();
		
		String[] lists = getResources().getStringArray(R.array.session_id_list);
		for(int i = 0; i < lists.length; i++)
		{
			mSessionIdList.add(lists[i]);
		}
		lists = getResources().getStringArray(R.array.inventoried_flag_list);
		for(int i = 0; i < lists.length; i++)
		{
			mInventoriedFlagList.add(lists[i]);
		}

		updateView();
		

		
		if (mReaderHelper.getInventoryFlag()) 
		{
			mHandler.removeCallbacks(mRefreshRunnable);
			mHandler.postDelayed(mRefreshRunnable,2000);
		}
		mStop.setEnabled(mReaderHelper.getInventoryFlag());
		mStart.setEnabled(!mReaderHelper.getInventoryFlag());
		
		mRefreshButton = (TextView) findViewById(R.id.refresh);
		mRefreshButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				m_curInventoryBuffer.clearInventoryRealResult();
				refreshList();
				refreshText();
				clearText();
				mRealRoundEditText.setText("65535");
			}
		});
	}
	
	private Handler mHandler = new Handler();
    private Runnable mRefreshRunnable = new Runnable() 
    {
         public void run () 
         {
        	 refreshList();
        	 mHandler.postDelayed(this, 2000); 
      }
    };

	private void updateView() 
	{	
		mRealRoundEditText.setText("65535");
	}

	
	private OnClickListener setInventoryRealOnClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View arg0) 
		{
			
			m_curInventoryBuffer.clearInventoryPar();	
			
			String strInventoryCount = mRealRoundEditText.getText().toString();				
			if (strInventoryCount == null || strInventoryCount.length() <= 0) 
			{
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.repeat_empty),
						Toast.LENGTH_SHORT).show();
				return ;
			}
			m_curInventoryBuffer.Inventory_Count=(int) Integer.parseInt(strInventoryCount);
			if(m_curInventoryBuffer.Inventory_Count<=0)
			{
				return;
			}
			//停止按钮
			if(arg0.getId() == R.id.stop) 
			{
				refreshText();
				mReaderHelper.setInventoryFlag(false);
				m_curInventoryBuffer.bLoopInventoryReal = false;
				
				mStop.setEnabled(false);
				mStart.setEnabled(true);
				mSave.setEnabled(true);
				refreshList();
				return;
			}
			
			if(arg0.getId() == R.id.SaveData) 
			{
				SaveData();
				return;
			}			
			m_curInventoryBuffer.clearInventoryRealResult();
			mReaderHelper.setInventoryFlag(true);
			m_curInventoryBuffer.bLoopInventoryReal = true;			
			mReaderHelper.clearInventoryTotal();			
			refreshText();
			ReaderHelper.RunInventroy_Once();
			mRefreshTime = new Date().getTime();			
			mStop.setEnabled(true);
			mStart.setEnabled(false);
			mSave.setEnabled(false);
			mHandler.removeCallbacks(mRefreshRunnable);
			mHandler.postDelayed(mRefreshRunnable,2000);
		}
	};
	
	private void refreshList() {
		mTagRealList.refreshList();
	}
	
	private void refreshText() {
		mTagRealList.refreshText();
	}
	
	private void clearText() {
		mTagRealList.clearText();
	}
	public void refresh_Inventory_Count() 
	{		
		mRealRoundEditText.setText(String.valueOf(m_curInventoryBuffer.Inventory_Count));
		if(m_curInventoryBuffer.Inventory_Count==0)
		{
			mStop.setEnabled(false);
			mStart.setEnabled(true);
		}
	}
	
	private final BroadcastReceiver mRecv = new BroadcastReceiver() 
	{
		
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL)) 
			{
				byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);
				
				switch (btCmd) 
				{
				case CMD.REAL_TIME_INVENTORY:
					refreshText();
					break;
				case CMD.REFRESH_COUNT:
					refresh_Inventory_Count();
					break;
				}

            } 
			else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) 
			{
            	mLogList.writeLog((String)intent.getStringExtra("log"), intent.getIntExtra("type", ERROR.SUCCESS));
            }
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			if (mLogList.tryClose()) return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (lbm != null)
			lbm.unregisterReceiver(mRecv);
		mHandler.removeCallbacks(mRefreshRunnable);
	}
	
	private void SaveData() 
	{		
		//显示对话框输入文件名
		LayoutInflater factory = LayoutInflater.from(PageInventoryReal.this);  //图层模板生成器句柄
		final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
		new AlertDialog.Builder(PageInventoryReal.this)
		.setTitle("保存文件")
		.setView(DialogView)   //设置视图模板
		.setPositiveButton("确定",
		new DialogInterface.OnClickListener() //确定按键响应函数
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
				filename = text1.getText().toString();  //得到文件名				

				// 准备设置excel工作表的标题
				String[] title = { "ID","TagEpc", "PC", "Conut" ,"RSSI" ,"Time"};
				try
				{
					
					// 获得开始时间
					Calendar ca = Calendar.getInstance();
					int year = ca.get(Calendar.YEAR);//获取年份
					int month=ca.get(Calendar.MONTH);//获取月份
					if(month==0)month=1;
					int day=ca.get(Calendar.DATE);//获取日
					int minute=ca.get(Calendar.MINUTE);//分
					int hour=ca.get(Calendar.HOUR);//小时
					int second=ca.get(Calendar.SECOND);//秒
					//int WeekOfYear = ca.get(Calendar.DAY_OF_WEEK);
					String CurrentDataTime= year +"年"+ month +"月"+ day + "日"+ hour +"时"+ minute +"分"+ second +"秒";
					filename+=CurrentDataTime;
					
					File BuildDir = new File(Environment.getExternalStorageDirectory(), "/UHFData");   //打开UHFData目录，如不存在则生成
					if(BuildDir.exists()==false)BuildDir.mkdirs();
					
					// 创建Excel工作薄
					WritableWorkbook wwb;
					// 在SD卡中，新建立一个jxl文件
					wwb = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UHFData/"+filename+".xls"));
					//Toast.makeText(ComUHF.this, "创建文件成功!", Toast.LENGTH_SHORT).show();

					// 添加第一个工作表并设置第一个Sheet的名字
					WritableSheet sheet = wwb.createSheet("InventoryData", 0);
					Label label;
					
					for (int i = 0; i < title.length; i++) 
					{
						label = new Label(i, 0, title[i]);
						// 将定义好的单元格添加到工作表中
						sheet.addCell(label);
					}
					//list_goods=mDBUtil.queryAll();//导出数据库数据
					/*
					 * 保存数字到单元格，需要使用jxl.write.Number 必须使用其完整路径，否则会出现错误
					 */				
					for(int i = 0 ; i < m_curInventoryBuffer.lsTagList.size(); i++)
					{						
						 InventoryTagMap goods = m_curInventoryBuffer.lsTagList.get(i);
						//添加编号
						jxl.write.Number number = new jxl.write.Number(0, i+1, i+1);
						sheet.addCell(number);
						//添加标签EPC
						label = new Label(1,i+1,goods.strEPC);
						sheet.addCell(label);
						//添加标签PC值
						label = new Label(2,i+1,goods.strPC);
						sheet.addCell(label);
						//添加盘存次数
						jxl.write.Number Countnumber = new jxl.write.Number(3, i+1, goods.nReadCount);
						sheet.addCell(Countnumber);
						//添加RSSI
						label = new Label(4,i+1,goods.strRSSI);
						sheet.addCell(label);
						//添加Time
						label = new Label(5,i+1,goods.strFreq);
						sheet.addCell(label);
					}
					
					
					wwb.write(); //写入数据
					wwb.close(); //关闭文件
					Toast.makeText(PageInventoryReal.this, "文件保存成功!", Toast.LENGTH_SHORT).show();

				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Toast.makeText(PageInventoryReal.this, "文件保存失败!", Toast.LENGTH_LONG).show();
				}
			}
		})
		.setNegativeButton("取消",   //取消按键响应函数,直接退出对话框不做任何处理 
		new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{ 
			}
		}).show();  //显示对话框
	} 
}

