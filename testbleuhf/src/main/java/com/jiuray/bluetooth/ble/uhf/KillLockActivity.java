package com.jiuray.bluetooth.ble.uhf;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
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

public class KillLockActivity extends Activity {

	private Button buttonReadTag; // 读标签按钮
	private EditText editTag; // 标签号输入框
	private Spinner spinnerLockMembank; // 锁定数据区
	private EditText editAccess; // 密码
	private Spinner spinnerLockType; // 锁定类型
	private Button buttonLock; // 锁定
	private EditText editKillPassword ; //销毁密码
	private Button buttonKill; //销毁

	private String[] lockMembanks;
	private String[] lockTypes;
	private List<String> listLockMembanks = new ArrayList<String>();
	private List<String> listLockType = new ArrayList<String>();

	private int lockMem; // 锁定区
	private int lockType; // 锁定类型
	private byte[] accessBytes;// 访问密码
	private byte[] killBytes ;//销毁密码


	private BluetoothLeService bleService = UhfMainActivity.mBluetoothLeService;
	private BluetoothGattCharacteristic characteristic = UhfMainActivity.mCharacteristc;

	private UhfBLECommand uhf ;


	//消息处理器，用于接收UHF操作返回信息
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case UhfBLECommand.MSG_INVENTORY:
					byte[] epc = msg.getData().getByteArray(UhfBLECommand.INVENTORY);
					if(epc != null){
						editTag.setText(Tools.Bytes2HexString(epc, epc.length));
						Util.play(1, 0);
					}else{
						Toast.makeText(getApplicationContext(), "未读到标签", 0).show();
					}
					break;
				case UhfBLECommand.MSG_LOCK://锁定
					boolean lockFlag = msg.getData().getBoolean(UhfBLECommand.LOCK);
					if(lockFlag){
						Util.play(1, 0);
						Toast.makeText(getApplicationContext(), "操作成功", 0).show();
					}else{
						Toast.makeText(getApplicationContext(), "操作失败", 0).show();
					}
					break;
				case UhfBLECommand.MSG_KILL:
					boolean killFlag = msg.getData().getBoolean(UhfBLECommand.KILL);
					if(killFlag){
						Util.play(1, 0);
						Toast.makeText(getApplicationContext(), "操作成功", 0).show();
					}else{
						Toast.makeText(getApplicationContext(), "操作失败", 0).show();
					}
					break;
				default:
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serialport_lock_kill_activity);
		initUI();
		listener();
		Util.initSoundPool(this);
		uhf = new UhfBLECommand(this, characteristic, bleService, mhandler);
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

	private void initUI() {
		buttonReadTag = (Button) findViewById(R.id.button_lock_kill_read_tag);
		editTag = (EditText) findViewById(R.id.editText_lock_kill_tag);
		spinnerLockMembank = (Spinner) findViewById(R.id.spinner_lock_kill_membank);
		editAccess = (EditText) findViewById(R.id.editText_lock_kill_access_password);
		spinnerLockType = (Spinner) findViewById(R.id.spinner_lock_kill_type);
		buttonLock = (Button) findViewById(R.id.button_lock_kill_lock);
		buttonKill = (Button) findViewById(R.id.button_lock_kill_kill);
		editKillPassword = (EditText) findViewById(R.id.editText_lock_kill_access_password);
		lockMembanks = getResources().getStringArray(R.array.lock_membank);
		for (String membank : lockMembanks) {
			listLockMembanks.add(membank);
		}
		lockTypes = getResources().getStringArray(R.array.lock_type);
		for (String lockType : lockTypes) {
			listLockType.add(lockType);
		}
		spinnerLockMembank.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, listLockMembanks));
		spinnerLockType.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, listLockType));

	}

	private void listener() {

		//读标签
		buttonReadTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uhf.inventoryRealTime();

			}
		});
		//锁定区
		spinnerLockMembank.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {

				String mem = listLockMembanks.get(position);
				if("销毁密码".equals(mem)){
					lockMem = UhfBLECommand.LOCK_MEM_KILL;
				}else if("访问密码".equals(mem)){
					lockMem = UhfBLECommand.LOCK_MEM_ACCESS;
				}else if("EPC区".equals(mem)){
					lockMem = UhfBLECommand.LOCK_MEM_EPC;
				}else if("TID区".equals(mem)){
					lockMem = UhfBLECommand.LOCK_MEM_TID;
				}else if("USER区".equals(mem)){
					lockMem = UhfBLECommand.LOCK_MEM_USER;
				}
//				Log.e(TAG, lockMem + "");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		//锁定类型
		spinnerLockType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {
				String type = listLockType.get(position);
				if("开放".equals(type)){
					lockType = UhfBLECommand.LOCK_TYPE_OPEN;
				}else if("永久开放".equals(type)){
					lockType = UhfBLECommand.LOCK_TYPE_PERMA_OPEN;
				}else if("锁定".equals(type)){
					lockType = UhfBLECommand.LOCK_TYPE_LOCK;
				}else if("永久锁定".equals(type)){
					lockType = UhfBLECommand.LOCK_TYPE_PERMA_LOCK;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		//锁定标签
		buttonLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String accessStr = editAccess.getText().toString();
				String epc = editTag.getText().toString();
				if(accessStr == null && accessStr.length() != 8){
					Toast.makeText(getApplicationContext(), "访问密码不能为空,且为8位十六进制", 0).show();
					return;
				}
				accessBytes = Tools.HexString2Bytes(accessStr);
				uhf.lock6C(accessBytes, lockMem, lockType);
				//先选择EPC
//				cmdManager.selectEPC(listTag.get(0).getEpc());
//				//锁定
//				boolean lockFlag = cmdManager.lock6C(accessBytes, lockMem, lockType);
			}
		});
		//销毁标签
		buttonKill.setOnClickListener(new onKillTagListener());
	}


	//销毁标签
	private class onKillTagListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			String killStr = editKillPassword.getText().toString();
			String epc = editTag.getText().toString();
			if(killStr == null && killStr.length() != 8){
				Toast.makeText(getApplicationContext(), "访问密码不能为空,且为8位十六进制", 0).show();
				return;
			}
//			if(epc == null && "".equals(epc) && listTag.isEmpty()){
//				Toast.makeText(getApplicationContext(), "请先选择标签", 0).show();
//				return;
//			}
			killBytes = Tools.HexString2Bytes(killStr);
			uhf.killTag(killBytes);
			//先选择EPC
//			cmdManager.selectEPC(listTag.get(0).getEpc());
//			//销毁
//			boolean lockFlag = cmdManager.killTag(killBytes);
//			if(lockFlag){
//				Toast.makeText(getApplicationContext(), "销毁标签成功", 0).show();
//			}else{
//				Toast.makeText(getApplicationContext(), "销毁标签失败", 0).show();
//			}

		}

	}

}
