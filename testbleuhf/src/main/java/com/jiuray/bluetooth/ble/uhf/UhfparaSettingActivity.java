package com.jiuray.bluetooth.ble.uhf;

import java.util.ArrayList;
import java.util.List;

import com.jiuray.bluetooth.ble.uhf.service.BluetoothLeService;
import com.jiuray.bluetooth.ble.uhf.command.UhfBLECommand;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UhfparaSettingActivity extends Activity {
	private Spinner outputSpinner;
	private Spinner workareaSpinner;
	private Button buttonSetOutput;
	private Button buttonSetWork;
	private TextView textTitle;
	private EditText editOutput;
	private Button buttonReadOutput;

	private String[] outputStrings = { "27dBm","26dBm", "25dBm", "24dBm", "23dBm",
			"22dBm", "21dBm", "20dBm", "19dBm", "18dBm", "17dBm", "16dBm" , "15dBm", "14dBm", "13dBm"};
	private String[] workAreaStrings;
	private List<String> listOutput = new ArrayList<String>();
	private List<String> listWorkarea = new ArrayList<String>();

	private int outputValue = 2700;
	private int area = 0;
	private UhfBLECommand uhf ;

	private BluetoothLeService bleService = UhfMainActivity.mBluetoothLeService;
	private BluetoothGattCharacteristic characteristic = UhfMainActivity.mCharacteristc;

	//消息处理器，用于接收UHF操作返回信息
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case UhfBLECommand.MSG_SET_OUTPUT://设置输出功率
					boolean setOutputFlag = msg.getData().getBoolean(UhfBLECommand.SET_OUTPUT);
					if(setOutputFlag){
						Util.play(1, 0);
						Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case UhfBLECommand.MSG_READ_OUTPUT:
					int value = msg.getData().getInt(UhfBLECommand.READ_OUTPUT);
					editOutput.setText(value + "dBm");
					Util.play(1, 0);
					break;
				case UhfBLECommand.MSG_SET_WORK_AREA:
					boolean setWorkAreaFlag = msg.getData().getBoolean(UhfBLECommand.SET_WORK_AREA);
					if(setWorkAreaFlag){
						Util.play(1, 0);
						Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_uhf_para_activity);
		initView();
		uhf = new UhfBLECommand(this, characteristic, bleService, mhandler);
		listener();
		Util.initSoundPool(this);
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

	private void initView() {
		outputSpinner = (Spinner) findViewById(R.id.spinner_setting_output);
		workareaSpinner = (Spinner) findViewById(R.id.spinner_set_work_area);
		buttonSetOutput = (Button) findViewById(R.id.button_set_output);
		buttonSetWork = (Button) findViewById(R.id.button_set_work_area);
		editOutput = (EditText) findViewById(R.id.editText_setting_output);
		buttonReadOutput = (Button) findViewById(R.id.button_read_output);

		workAreaStrings = getResources().getStringArray(R.array.work_area);
		for (String area : workAreaStrings) {
			listWorkarea.add(area);
		}
		for (String output : outputStrings) {
			listOutput.add(output);
		}
		outputSpinner.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, listOutput));
		workareaSpinner.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, listWorkarea));

	}

	// 监听
	private void listener() {
		// 功率设置
		buttonSetOutput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				uhf.setOutputPower(outputValue);

			}
		});
		// 工作地区设置
		buttonSetWork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				uhf.setWorArea(area);
			}
		});
		// 读取功率
		buttonReadOutput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uhf.getOutputPower();

			}
		});

		outputSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {
				outputValue = 2700 - position * 100;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		workareaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {
				String mArea = listWorkarea.get(position);
				if("中国1".equals(mArea)){
					area = 1;
				}else if("中国2".equals(mArea)){
					area = 4;
				}else if("美国".equals(mArea)){
					area = 2;
				}else if("欧洲".equals(mArea)){
					area = 3;
				}else if("韩国".equals(mArea)){
					area = 6;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
