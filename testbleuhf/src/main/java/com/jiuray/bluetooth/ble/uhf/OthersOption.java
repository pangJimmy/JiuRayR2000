package com.jiuray.bluetooth.ble.uhf;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiuray.bluetooth.ble.uhf.service.BluetoothLeService;

public class OthersOption extends Activity {

	private EditText editTips ;
	private Button buttonRfid ;
	private Button buttonBarcode ;
	private Button buttonZhijie ;

	byte[] cmdRFID = Tools.HexString2Bytes("AA5503FE");
	byte[] cmdBarcode = Tools.HexString2Bytes("AA5501FE");
	byte[] cmdZhijie = Tools.HexString2Bytes("AA5502FE");

	public static boolean barcodeFlag = false ;
	private String TAG = OthersOption.class.getName();

	/**
	 * 广播接收者，接收蓝牙连接状态
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				//连接成功
				Toast.makeText(getApplicationContext(), R.string.connected_success, Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				//断开连接
				Toast.makeText(getApplicationContext(), R.string.connected_fail, Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				//获取所有服务
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				//返回数据
				byte[] dataBytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				//处理返回数据
				String data = Tools.Bytes2HexString(dataBytes, dataBytes.length);
				//将数据显示
				if(barcodeFlag){
					editTips.append("[Recv HEX]: "+resolveData(data)+ "\n");
				}else{
					editTips.append("[Recv HEX]: "+data+ "\n");
				}
				Log.e(TAG, data);
			}
		}
	};

	//过滤
	private String resolveData(String data){
		char[] dataArray = data.toCharArray();
		String recv = "" + dataArray[0] + dataArray[1];
		for(int i = 2; i < dataArray.length ; i++){
			if(i%2 != 0){
				recv += dataArray[i];
			}
		}
		return recv;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other);

		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	private void initView(){
		editTips = (EditText) findViewById(R.id.editText_tips_others);
		buttonRfid = (Button) findViewById(R.id.button_rfid);
		buttonBarcode = (Button) findViewById(R.id.button_barcode);
		buttonZhijie = (Button) findViewById(R.id.button_other);
		buttonRfid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				barcodeFlag = false;
				BluetoothLeService service = UhfMainActivity.mBluetoothLeService;
				BluetoothGattCharacteristic characteristc = UhfMainActivity.mCharacteristc ;
				if(service != null && characteristc != null){
					characteristc.setValue(cmdRFID);
					service.writeCharacteristic(characteristc);
					service.setCharacteristicNotification(characteristc, true);
				}
			}
		});

		buttonBarcode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				barcodeFlag = true;
				BluetoothLeService service = UhfMainActivity.mBluetoothLeService;
				BluetoothGattCharacteristic characteristc = UhfMainActivity.mCharacteristc ;
				if(service != null && characteristc != null){
					characteristc.setValue(cmdBarcode);
					service.writeCharacteristic(characteristc);
					service.setCharacteristicNotification(characteristc, true);
				}
			}
		});

		buttonZhijie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				barcodeFlag = false;
				BluetoothLeService service = UhfMainActivity.mBluetoothLeService;
				BluetoothGattCharacteristic characteristc = UhfMainActivity.mCharacteristc ;
				if(service != null && characteristc != null){
					characteristc.setValue(cmdZhijie);
					service.writeCharacteristic(characteristc);
					service.setCharacteristicNotification(characteristc, true);
				}
			}
		});
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
