package com.jf.uhf;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.reader.helper.ReaderHelper;
import com.jf.uhf.R;
import com.jf.uhf.serialport.SerialPort;
import com.jf.uhf.serialport.SerialPortFinder;
import com.jf.uhf.spiner.SpinerPopWindow;
import com.jf.uhf.spiner.AbstractSpinerAdapter.IOnItemSelectListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

@SuppressLint("HandlerLeak")
public class ConnectRs232 extends Activity {

	private TextView mConectButton;
	
	private static final int CONNECTING = 0x10;
	private static final int CONNECT_TIMEOUT = 0x100;
	private static final int CONNECT_FAIL = 0x101;
	private static final int CONNECT_SUCCESS = 0x102;
	
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 0;
	
	private ReaderHelper mReaderHelper;
	
	private List<String> mPortList = new ArrayList<String>();
	private List<String> mBaudList = new ArrayList<String>();
	
	private TextView mPortTextView, mBaudTextView;
	private TableRow mDropPort, mDropBaud;
	private SpinerPopWindow mSpinerPort, mSpinerBaud;
	
	private int mPosPort = -1, mPosBaud = -1;
	
	private SerialPortFinder mSerialPortFinder;
	
	String[] entries = null;
	String[] entryValues = null;
	
	private SerialPort mSerialPort = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_rs232);
		
		((UHFApplication) getApplication()).addActivity(this);
		
		mSerialPortFinder = new SerialPortFinder();
		
		entries = mSerialPortFinder.getAllDevices();
        entryValues = mSerialPortFinder.getAllDevicesPath();
		
		mConectButton = (TextView) findViewById(R.id.textview_connect);
		
		mPortTextView =  (TextView) findViewById(R.id.comport_text);
		mBaudTextView =  (TextView) findViewById(R.id.baudrate_text);
		mDropPort = (TableRow) findViewById(R.id.table_row_spiner_comport);
		mDropBaud = (TableRow) findViewById(R.id.table_row_spiner_baudrate);

		mConectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mPosPort < 0 || mPosBaud < 0) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.rs232_error),
							Toast.LENGTH_SHORT).show();
					return ;
				}
				
				try {
				
					mSerialPort = new SerialPort(new File(entryValues[mPosPort]), Integer.parseInt(mBaudList.get(mPosBaud)), 0);
					
					try {
						mReaderHelper = ReaderHelper.getDefaultHelper();
						mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
					} catch (Exception e) {
						e.printStackTrace();
						
						return ;
					}
					
					Intent intent;
					intent = new Intent().setClass(ConnectRs232.this, MainActivity.class);
					startActivity(intent);
					
					finish();
				} catch (SecurityException e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.error_security),
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.error_unknown),
							Toast.LENGTH_SHORT).show();
				} catch (InvalidParameterException e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.error_configuration),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mDropPort.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showPortSpinWindow();
			}
		});
		
		mDropBaud.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showBaudSpinWindow();
			}
		});
		
		
		String[] lists = entries;
		for(int i = 0; i < lists.length; i++){
			mPortList.add(lists[i]);
		}
		
		mSpinerPort = new SpinerPopWindow(this);
		mSpinerPort.refreshData(mPortList, 0);
		mSpinerPort.setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				setPortText(pos);
			}
		});
		
		lists = getResources().getStringArray(R.array.baud_rate);
		for(int i = 0; i < lists.length; i++){
			mBaudList.add(lists[i]);
		}
		
		mSpinerBaud = new SpinerPopWindow(this);
		mSpinerBaud.refreshData(mBaudList, 0);
		mSpinerBaud.setItemListener(new IOnItemSelectListener() {
			public void onItemClick(int pos) {
				setBaudText(pos);
			}
		});
	}
	
	private void showPortSpinWindow() {
		mSpinerPort.setWidth(mDropPort.getWidth());
		mSpinerPort.showAsDropDown(mDropPort);
	}
	
	private void showBaudSpinWindow() {
		mSpinerBaud.setWidth(mDropBaud.getWidth());
		mSpinerBaud.showAsDropDown(mDropBaud);
	}
	
	private void setPortText(int pos){
		if (pos >= 0 && pos < mPortList.size()){
			String value = mPortList.get(pos);
			mPortTextView.setText(value);
			mPosPort = pos;
		}
	}
	
	private void setBaudText(int pos){
		if (pos >= 0 && pos < mBaudList.size()){
			String value = mBaudList.get(pos);
			mBaudTextView.setText(value);
			mPosBaud = pos;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			startActivity(new Intent().setClass(ConnectRs232.this, ConnectActivity.class));
			
			finish();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
