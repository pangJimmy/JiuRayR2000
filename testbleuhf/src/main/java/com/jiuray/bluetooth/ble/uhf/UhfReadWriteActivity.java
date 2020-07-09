package com.jiuray.bluetooth.ble.uhf;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jiuray.bluetooth.ble.uhf.service.BluetoothLeService;
import com.jiuray.bluetooth.ble.uhf.command.UhfBLECommand;

public class UhfReadWriteActivity extends Activity implements OnClickListener{

	private EditText editAccess;
	private Spinner spinnerMembank;
	private EditText editAddr;
	private EditText editLen;
	private Button buttonRead;
	private Button buttonWrite;
	private EditText editRead;
	private EditText editWrite;
	private Button buttonReadTag; // 读标签按钮
	private EditText editTag;

	//UhfMainActivity.mBluetoothLeService.writeCharacteristic(UhfMainActivity.mCharacteristc);
	private String[] membanks;
	private List<String> listMembank = new ArrayList<String>();
	private int membank = 3; // 数据区
	private int addr = 0; // 起始地址
	private int len; // 数据长度word
	private byte[] accessPassword; // 访问密码

	private BluetoothLeService bleService = UhfMainActivity.mBluetoothLeService;
	private BluetoothGattCharacteristic characteristic = UhfMainActivity.mCharacteristc;


	private UhfBLECommand uhf ;

	//消息处理器，用于接收UHF操作返回信息
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case UhfBLECommand.MSG_INVENTORY://读标签返回数据
					byte[] EPCdata = msg.getData().getByteArray(UhfBLECommand.INVENTORY);
					if(EPCdata != null){
						Util.play(1, 0);
						editTag.setText(Tools.Bytes2HexString(EPCdata, EPCdata.length));
					}else{
						Toast.makeText(getApplicationContext(), "未读到标签", Toast.LENGTH_SHORT).show();
					}
					break;
				case UhfBLECommand.MSG_READ_6C:
					byte[] readdata = msg.getData().getByteArray(UhfBLECommand.READ_6C);
					if(readdata != null){
						Log.e("read data", Tools.Bytes2HexString(readdata, readdata.length));
						editRead.setText(Tools.Bytes2HexString(readdata, readdata.length));
						Util.play(1, 0);
					}else{
						Toast.makeText(getApplicationContext(), "未读到数据", Toast.LENGTH_SHORT).show();
					}
					break;
				case UhfBLECommand.MSG_WRITE_6C:
					boolean writeFlag = msg.getData().getBoolean(UhfBLECommand.WRITE_6C);
					if(writeFlag){
						Toast.makeText(getApplicationContext(), "写入数据成功", Toast.LENGTH_SHORT).show();
						Util.play(1, 0);
					}else{
						Toast.makeText(getApplicationContext(), "写入数据失败", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
			}
		};
	};

	private String TAG = "UhfReadWriteActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uhf_read_write);
		initView();
		Util.initSoundPool(this);//初始化声音池
		uhf = new UhfBLECommand(this, characteristic, bleService, mhandler);
	}

	private void initView() {
		editAccess = (EditText) findViewById(R.id.editText_uhf_more_access);
		spinnerMembank = (Spinner) findViewById(R.id.Spinner_uhf_more_access);
		editAddr = (EditText) findViewById(R.id.editText_uhf_more_option_read_addr);
		editLen = (EditText) findViewById(R.id.editText_more_option_read_len);
		buttonRead = (Button) findViewById(R.id.button_more_option_read_data);
		buttonWrite = (Button) findViewById(R.id.button_more_option_write_data);
		editRead = (EditText) findViewById(R.id.editText_more_option_read_data);
		editWrite = (EditText) findViewById(R.id.editText_more_option_write_data);
		buttonReadTag = (Button) findViewById(R.id.button_read_tag_serialport_more);
		editTag = (EditText) findViewById(R.id.editText_tag_serialport_more);
		membanks = getResources().getStringArray(R.array.membanks);
		for (String membankItem : membanks) {
			listMembank.add(membankItem);
		}
		// 设置下拉
		spinnerMembank.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, listMembank));

		buttonReadTag.setOnClickListener(this);
		buttonWrite.setOnClickListener(this);
		buttonRead.setOnClickListener(this);

		spinnerMembank.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				membank = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		uhf.registerReceiver();
//		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		unregisterReceiver(mGattUpdateReceiver);
		uhf.unregisterReceiver();
	}

	@Override
	public void onClick(View v) {
		String addrStr = editAddr.getText().toString();
		String lenStr = editLen.getText().toString();
		String accessStr = editAccess.getText().toString();
		String writeDataStr = editWrite.getText().toString();
		if (null == addrStr || "".equals(addrStr)) {
			Toast.makeText(getApplicationContext(), "起始地址不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		addr = Integer.valueOf(addrStr);
		if (null == accessStr || "".equals(accessStr)
				|| accessStr.length() != 8) {
			Toast.makeText(getApplicationContext(), "访问密码不能为空，且为4个字节",
					Toast.LENGTH_SHORT).show();
			return;
		}
		accessPassword = Tools.HexString2Bytes(accessStr);
		switch (v.getId()) {
			case R.id.button_read_tag_serialport_more:
				uhf.inventoryRealTime();
//			characteristic.setValue(Tools.HexString2Bytes("AA00220000228E"));
//			bleService.writeCharacteristic(characteristic);
//			bleService.setCharacteristicNotification(characteristic, true);
				break;
			case R.id.button_more_option_read_data:// 读数据
				if (null == lenStr || "".equals(lenStr)) {
					Toast.makeText(getApplicationContext(), "数据长度不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				len = Integer.valueOf(lenStr);
				uhf.readFrom6C(membank, addr, len, accessPassword);
				break;
			case R.id.button_more_option_write_data:// 写数据
				if (null == writeDataStr || "".equals(writeDataStr)) {
					Toast.makeText(getApplicationContext(), "写入数据不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 计算写入数据，若长度不为4的整数倍则用0补齐
				int writeLen = writeDataStr.length();
				int temp = writeLen % 4;
				if (temp != 0) {
					for (int i = 0; i < temp; i++) {
						writeDataStr += "0";
					}
				}
				byte[] writeBytes = Tools.HexString2Bytes(writeDataStr);
				uhf.writeTo6C(accessPassword, membank, addr, writeBytes.length, writeBytes);
				//写数据
//			boolean writeFlag = writeData(membank, addr, accessPassword,
//					writeBytes, writeBytes.length);
//			if (writeFlag) {
//				Toast.makeText(getApplicationContext(), "写数据成功",
//						Toast.LENGTH_SHORT).show();
//			} else {
//				Toast.makeText(getApplicationContext(), "写数据失败",
//						Toast.LENGTH_SHORT).show();
//			}
				break;

			default:
				break;
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
