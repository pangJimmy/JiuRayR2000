package com.jiuray.bluetooth.ble.uhf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jiuray.bluetooth.ble.uhf.service.BluetoothLeService;
import com.jiuray.bluetooth.ble.uhf.service.SampleGattAttributes;
import com.jiuray.bluetooth.ble.uhf.command.InventoryInfo;

public class UhfMainActivity extends Activity implements OnClickListener{

	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";

	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
	private String mDeviceAddress;  //蓝牙设备地址
	private String mDeviceName;   //蓝牙设备名称
	private boolean mConnected = false ;  //连接状态

	public static BluetoothGattCharacteristic mCharacteristc  = null;
	private String uuid = "";

	private final String TAG = "MyControl";
	public static BluetoothLeService mBluetoothLeService = null ;  //蓝牙低功耗服务

	private Button buttonReadTag ;  //读标签
	private Button buttonClear ;   //清空
	private ListView listViewEPC ;  //EPC listVIEW
	private Button buttonParaSetting ;  //UHF参数设置
	private Button buttonMoreOption ;  //读写操作
	private EditText editTagCount ;   //标签个数
	private Button buttonLockKill ; //锁定销毁删除
	private Button buttonOther ;//其他功能
	boolean isStop = true;
	boolean isRunning = true;
	boolean isSend = false ;
	List<EPC> listEPC = new ArrayList<EPC>(); //EPC列表

	private SendCmdThread sendCmdThread ;

	private final byte HEAD = (byte)0xAA;
	private final byte END = (byte) 0x8E;

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};


	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
	//                        or notification operations.
	/**
	 * 广播接收者，接收蓝牙连接状态
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				//连接成功

				mConnected = true;
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				//断开连接
				mConnected = false;
				Toast.makeText(getApplicationContext(), R.string.connected_fail, Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				//获取所有服务
				Toast.makeText(getApplicationContext(), R.string.connected_success, Toast.LENGTH_SHORT).show();
				loopcharactics(mBluetoothLeService.getSupportedGattServices());
				setButtonClickable(true);
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				//返回数据
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            	textTips.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
				byte[] dataBytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				//处理返回数据
				resolveRecv(dataBytes);
				String data = Tools.Bytes2HexString(dataBytes, dataBytes.length);
				Log.e(TAG, data);
//            	Log.e(TAG, intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                Toast.makeText(getApplicationContext(), intent.getStringExtra(BluetoothLeService.EXTRA_DATA), 0).show();
			}
		}
	};



	//遍历所有的GATT服务
	private void loopcharactics(List<BluetoothGattService> gattServices){
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
				= new ArrayList<ArrayList<HashMap<String, String>>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(
					"NAME", SampleGattAttributes.lookup(uuid, "unknow service"));
			currentServiceData.put("UUID", uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
					new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas =
					new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				Log.e("uuid", uuid);
				currentCharaData.put(
						"NAME", SampleGattAttributes.lookup(uuid, "unknowcharacteristics"));
				currentCharaData.put("UUID", uuid);
				gattCharacteristicGroupData.add(currentCharaData);
				//可发送指令的服务
				if(uuid.equals(UUID_KEY_DATA)){
					mCharacteristc = gattCharacteristic ;
				}
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uhf_main);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView();
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		getActionBar().setTitle(mDeviceName);
		Util.initSoundPool(this);

		//绑定服务
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

		if (mBluetoothLeService != null) {
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
		setButtonClickable(false);
		Toast.makeText(getApplicationContext(), "正在获取蓝牙4.0服务", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		buttonReadTag = (Button) findViewById(R.id.button_start_read);
		buttonClear = (Button) findViewById(R.id.button_clear);
		listViewEPC = (ListView) findViewById(R.id.listView_data);
		buttonParaSetting  = (Button) findViewById(R.id.button_para_setting);
		buttonMoreOption = (Button) findViewById(R.id.button_more_option);
		editTagCount = (EditText) findViewById(R.id.editText_tag_count);
		buttonLockKill = (Button) findViewById(R.id.button_lock_kill);
		buttonOther = (Button) findViewById(R.id.button_others);

		buttonReadTag.setOnClickListener(this);
		buttonClear.setOnClickListener(this);
		buttonParaSetting.setOnClickListener(this);
		buttonMoreOption.setOnClickListener(this);
		buttonLockKill.setOnClickListener(this);
		buttonOther.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnected ) {
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		} else {
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}


	@Override
	protected void onResume() {
		super.onResume();


		/*****  注册广播接收服务端发送过来的数据********/
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		isRunning = true ;
		isSend = false ;
		sendCmdThread = new SendCmdThread();
		sendCmdThread.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		isRunning = false ;
		isSend = false ;
		buttonReadTag.setText(R.string.inventory);
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "On Destroy");
		//解除服务绑定
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_connect:
				mBluetoothLeService.connect(mDeviceAddress);
				return true;
			case R.id.menu_disconnect:
				mBluetoothLeService.disconnect();
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * 盘存数据线程
	 * @author mac
	 *
	 */
	private class SendCmdThread extends Thread{
		@Override
		public void run() {
			super.run();
			//盘存指令
			byte[] cmd = { HEAD, (byte) 0x00, (byte) 0x22, (byte) 0x00,
					(byte) 0x00, (byte) 0x22, END };
			while(isRunning){
				if(isSend){
					if(mCharacteristc != null){
						mCharacteristc.setValue(cmd);
						mBluetoothLeService.writeCharacteristic(mCharacteristc);

						mBluetoothLeService.setCharacteristicNotification(
								mCharacteristc, true);
					}
				}
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}


	private byte[] response = new byte[256];
	private byte[] temp = new byte[512];
	private int index = 0;  //temp有效数据指向
	private int count = 0;  //temp有效数据长度
	private void resolveRecv(byte[] buffer){
		if(buffer != null){
			int size = buffer.length;
			count += size;
			//超出temp长度清空
			if(count > 512){
				count = 0;
				index = 0;
				Arrays.fill(temp, (byte)0x00);
			}
			//先将接收到的数据拷到temp中
			System.arraycopy(buffer, 0, temp, index, size);
			index = index + size ;
			if(count > 7){
				if((temp[0] == HEAD)&& (temp[1] == (byte)0x02) && (temp[2] == (byte)0x22) && (temp[3] == (byte)0x00)){
					//正确数据位长度等于RSSI（1个字节）+PC（2个字节）+EPC
					int len = temp[4]&0xff;
					if(count < len + 7){//数据区尚未接收完整
						return;
					}
					if(temp[len + 6] != END){//数据区尚未接收完整
						return ;
					}
					//得到完整数据包
					byte[] packageBytes = new byte[len + 7];
					System.arraycopy(temp, 0, packageBytes, 0, len + 7);

					Log.e("PACKAGE--->", Tools.Bytes2HexString(packageBytes, len + 7));

					//读取出EPC信息
					InventoryInfo info = new InventoryInfo();
					//RSSI
					info.setRssi(temp[5]);
					//PC
					info.setPc(new byte[]{temp[6],temp[7]});
					//EPC
					byte[] epcBytes = new byte[len - 5];
					System.arraycopy(packageBytes, 8, epcBytes, 0, len - 5);
					info.setEpc(epcBytes);
					Util.play(1, 0);//播放提示音
					addToList(listEPC, info);
					//把后续的数据往前移动
					if(count - len - 7 > 0){
						byte[] test = new byte[count - len - 7];
						System.arraycopy(temp, len + 7, test, 0, test.length);
						Arrays.fill(temp, (byte)0x00);
						System.arraycopy(test, 0, temp, 0, test.length);
						count = test.length;
						index = count ;
					}else{
						count = 0;
						index = 0;
						Arrays.fill(temp, (byte)0x00);
					}

				}else{
					//包错误清空
					count = 0;
					index = 0;
					Arrays.fill(temp, (byte)0x00);
				}
			}
		}
	}


	List<Map<String, Object>> listMap ;

	// 将读取的EPC添加到LISTVIEW
	private void addToList(final List<EPC> list, final InventoryInfo info) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
		String epc = Tools.Bytes2HexString(info.getEpc(), info.getEpc().length);
		String pc = Tools.Bytes2HexString(info.getPc(), info.getPc().length);
		int rssi = info.getRssi();
		// 第一次读入数据
		if (list.isEmpty()) {
			EPC epcTag = new EPC();
			epcTag.setEpc(epc);
			epcTag.setCount(1);
			epcTag.setPc(pc);
			epcTag.setRssi(rssi);
			list.add(epcTag);
		} else {
			for (int i = 0; i < list.size(); i++) {
				EPC mEPC = list.get(i);
				// list中有此EPC
				if (epc.equals(mEPC.getEpc())) {
					mEPC.setCount(mEPC.getCount() + 1);
					mEPC.setRssi(rssi);
					mEPC.setPc(pc);
					list.set(i, mEPC);
					break;
				} else if (i == (list.size() - 1)) {
					// list中没有此epc
					EPC newEPC = new EPC();
					newEPC.setEpc(epc);
					newEPC.setCount(1);
					newEPC.setPc(pc);
					newEPC.setRssi(rssi);
					list.add(newEPC);
				}
			}
		}
		// 将数据添加到ListView
		listMap = new ArrayList<Map<String, Object>>();
		int idcount = 1;
//				Util.play(1, 0); //播放提示音
		for (EPC epcdata : list) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("EPC", epcdata.getEpc());
			map.put("PC", epcdata.getPc() +"");
			map.put("RSSI", epcdata.getRssi() + "Dbm");
			map.put("COUNT", epcdata.getCount());
			idcount++;
			listMap.add(map);
		}
		editTagCount.setText("" + listEPC.size());
		listViewEPC.setAdapter(new SimpleAdapter(UhfMainActivity.this,
				listMap, R.layout.list_epc_item, new String[] {
				"EPC", "PC","RSSI","COUNT" }, new int[] {
				R.id.textView_item_epc, R.id.textView_item_pc,
				R.id.textView_item_rssi,R.id.textView_item_count }));
//			}
//		});
	}


	private void setButtonClickable(boolean flag ){
		buttonLockKill.setClickable(flag);
		buttonMoreOption.setClickable(flag);
		buttonOther.setClickable(flag);
		buttonReadTag.setClickable(flag);
		buttonParaSetting.setClickable(flag);
		if(flag){
			buttonLockKill.setTextColor(getResources().getColor(R.color.black));
			buttonMoreOption.setTextColor(getResources().getColor(R.color.black));
			buttonOther.setTextColor(getResources().getColor(R.color.black));
			buttonReadTag.setTextColor(getResources().getColor(R.color.black));
			buttonParaSetting.setTextColor(getResources().getColor(R.color.black));
		}else{
			buttonLockKill.setTextColor(getResources().getColor(R.color.gray));
			buttonMoreOption.setTextColor(getResources().getColor(R.color.gray));
			buttonOther.setTextColor(getResources().getColor(R.color.gray));
			buttonReadTag.setTextColor(getResources().getColor(R.color.gray));
			buttonParaSetting.setTextColor(getResources().getColor(R.color.gray));
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.button_start_read://盘存标签
				if(!isSend){
					isSend = true ;
					buttonReadTag.setText(R.string.stop_read);
				}else{
					isSend = false ;
					buttonReadTag.setText(R.string.inventory);
				}
				break;
			case R.id.button_clear://清空
				editTagCount.setText("");
				listEPC.removeAll(listEPC);
				listViewEPC.setAdapter(null);
				break;
			case R.id.button_lock_kill://销毁锁定
				Intent toKill = new Intent(UhfMainActivity.this, KillLockActivity.class);
				startActivity(toKill);

				break;
			case R.id.button_more_option://读写操作
				Intent toWriteRead = new Intent(UhfMainActivity.this, UhfReadWriteActivity.class);
				startActivity(toWriteRead);

				break;
			case R.id.button_para_setting://UHF参数设置
				Intent toPara = new Intent(UhfMainActivity.this, UhfparaSettingActivity.class);
				startActivity(toPara);
				break;
			case R.id.button_others:
				Intent toOthers = new Intent(UhfMainActivity.this, OthersOption.class);
				startActivity(toOthers);

				break;
			default:
				break;
		}

	}

}
