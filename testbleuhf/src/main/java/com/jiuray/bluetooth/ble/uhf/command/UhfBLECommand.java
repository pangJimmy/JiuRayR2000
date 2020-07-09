package com.jiuray.bluetooth.ble.uhf.command;

import java.util.Arrays;
import java.util.List;

import com.jiuray.bluetooth.ble.uhf.service.BluetoothLeService;
import com.jiuray.bluetooth.ble.uhf.R;
import com.jiuray.bluetooth.ble.uhf.Tools;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UhfBLECommand  {


	private Context context ;
	private BluetoothGattCharacteristic character ;
	private BluetoothLeService bleService ;



	private final byte HEAD = (byte) 0xAA;  //包头
	private final byte END = (byte) 0x8E;	//包尾

	public static final int RESEVER_MENBANK = 0;  //RESEVER区
	public static final int EPC_MEMBANK = 1;   //EPC区
	public static final int TID_MEMBANK = 2;	//TID区
	public static final int USER_MENBANK = 3;	//USER区


	public static final int LOCK_TYPE_OPEN = 0; // 开放
	public static final int LOCK_TYPE_PERMA_OPEN = 1; // 永久开放
	public static final int LOCK_TYPE_LOCK = 2; // 锁定
	public static final int LOCK_TYPE_PERMA_LOCK = 3; // 永久锁定

	public static final int LOCK_MEM_KILL = 1; // 销毁密码
	public static final int LOCK_MEM_ACCESS = 2; // 访问密码
	public static final int LOCK_MEM_EPC = 3; // EPC
	public static final int LOCK_MEM_TID = 4; // TID
	public static final int LOCK_MEM_USER = 5; // USER


	private Handler handler ;//消息处理器

	private int messegeWhat = 0;
	public static final int MSG_INVENTORY = 1001;   //盘存
	public static final String INVENTORY = "inventory";
	public static final int MSG_READ_6C = 1002;   //读数据
	public static final String READ_6C = "read";
	public static final int MSG_WRITE_6C = 1003;   //写数据
	public static final String WRITE_6C = "write";
	public static final int MSG_SET_OUTPUT = 1004;   //设置输出功率
	public static final String SET_OUTPUT = "setOutput";
	public static final int MSG_READ_OUTPUT = 1005;   //读取数据功率
	public static final String READ_OUTPUT = "readOutput";
	public static final int MSG_SET_WORK_AREA = 1006;   //设置工作区域
	public static final String SET_WORK_AREA = "setWorkArea";
	public static final int MSG_KILL = 1007;   //销毁
	public static final String KILL = "kill";
	public static final int MSG_LOCK = 1008;   //锁定
	public static final String LOCK = "lock";


	private String TAG = UhfBLECommand.class.getName();
	/**
	 *
	 * @param context
	 * @param character
	 * @param service  蓝牙服务
	 */
	public UhfBLECommand(Context context, BluetoothGattCharacteristic character,
						 BluetoothLeService service, Handler handler){
		this.context = context ;
		this.character = character;
		this.bleService = service ;
		this.handler = handler ;
	}


	public void registerReceiver(){
		context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	public void unregisterReceiver(){
		context.unregisterReceiver(mGattUpdateReceiver);
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
	 * 广播接收者，接收蓝牙连接状态,和接收蓝牙返回的数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				//连接成功
				Toast.makeText(context, R.string.connected_success, Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				//断开连接
				Toast.makeText(context, R.string.connected_fail, Toast.LENGTH_SHORT).show();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				//获取所有服务
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				//返回数据
				byte[] dataBytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				getResp(dataBytes);
				//处理返回数据
				String data = Tools.Bytes2HexString(dataBytes, dataBytes.length);
				Log.e(TAG, data);
			}
		}
	};


	private boolean resp_ok = false ;  //数据包是否接收完整
	private byte[] response = null;  //返回数据包
	private byte[] temp = new byte[512];  //中间层
	private int index = 0;  //temp有效数据指向
	private int count = 0;  //temp有效数据长度
	//获取完整的数据包
	private void getResp(byte[] buffer){
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
				//判断包头
				if(temp[0] == HEAD){
					//获取包长度
					int len = temp[4]&0xff;
					if(temp[len + 6] != END){//数据区尚未接收完整
						return ;
					}
					//得到完整数据包
					byte[] packageBytes = new byte[len + 7];
					System.arraycopy(temp, 0, packageBytes, 0, len + 7);
					response = packageBytes;
//					Message msg = new Message();
//					msg.what = MSG_INVENTORY;
//					Bundle bundle = new Bundle();
//					bundle.putByteArray("inventory", packageBytes);
//					msg.setData(bundle);
//					handler.sendMessage(msg);
					Log.e("PACKAGE--->", Tools.Bytes2HexString(packageBytes, len + 7));
					//解析数据包中的数据
					byte[] packagerData = handlerResponse(packageBytes);
					if(packagerData != null){
						Log.e("packagerData--->", Tools.Bytes2HexString(packagerData, packagerData.length));
						//回传数据
						sendRecv(packagerData);
					}
					resp_ok = true;
					//包清空
					count = 0;
					index = 0;
					Arrays.fill(temp, (byte)0x00);
				}else{
					//包清空
					count = 0;
					index = 0;
					Arrays.fill(temp, (byte)0x00);
				}
			}
		}
	}

	private void sendRecv(byte[] data){
		Message msg = new Message();
		msg.what = messegeWhat;
		Bundle bundle = new Bundle();
		switch (messegeWhat) {
			case MSG_INVENTORY:  //盘存得到的EPC
				if(data.length > 3){//有数据返回
					//数据第一位是指令代码，第2位RSSI，第3-4位是PC，第5位到最后是EPC
					byte[] epc = new byte[data.length - 6];
					System.arraycopy(data, 4, epc, 0, data.length - 6);
					bundle.putByteArray(INVENTORY, epc);
					msg.setData(bundle);
				}
				handler.sendMessage(msg);
				break;
			case MSG_READ_6C://读数据
				if(data != null && data.length > 3){
					if (data[0] == (byte) 0x39) {
						int lengData = data.length - data[1] - 2;
						byte[] readData = new byte[lengData];
						System.arraycopy(data, data[1] + 2, readData, 0, lengData);
						bundle.putByteArray(READ_6C, readData);

						Log.e("readFrom6c", Tools.Bytes2HexString(readData,
								readData.length));
					}

					msg.setData(bundle);
				}
				handler.sendMessage(msg);
				break;
			case MSG_WRITE_6C://写数据//返回 490E3400AABBCC44556677889900112200

				if(data != null && data.length > 3){
					Log.e("WRITE---->", Tools.Bytes2HexString(data,
							data.length));
					bundle.putBoolean(WRITE_6C, true);
				}else{
					bundle.putBoolean(WRITE_6C, false);
				}
				msg.setData(bundle);
				handler.sendMessage(msg);
				break;
			case MSG_SET_OUTPUT://设置输出功率
				if(data != null){
					if(data[0] == (byte)0xB6 && data[1] == 0x00){
						Log.e("setoutput---->", Tools.Bytes2HexString(data,
								data.length));
						bundle.putBoolean(SET_OUTPUT, true);
						msg.setData(bundle);
					}
					handler.sendMessage(msg);
				}
				break;
			case MSG_READ_OUTPUT://获取输出功率
				if(data != null){
					int value = ((data[1] & 0xff) * 256 + (data[2] & 0xff)) / 100;
					bundle.putInt(READ_OUTPUT, value);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
				break;
			case MSG_SET_WORK_AREA://设置工作区域
				if(data != null){
					if(data[0] == 0x07 && data[1] == 0x00){
						bundle.putBoolean(SET_WORK_AREA, true);
						msg.setData(bundle);
						Log.e("setWORKAREA---->", Tools.Bytes2HexString(data,
								data.length));
					}
					handler.sendMessage(msg);
				}
				break;
			case MSG_LOCK://锁定
				if(data != null){
					if(data[0] == (byte)0x82){//锁定成功
						bundle.putBoolean(LOCK, true);
						msg.setData(bundle);
					}
					Log.e("LOCK--->", Tools.Bytes2HexString(data,
							data.length));
					handler.sendMessage(msg);
				}
				break;
			case MSG_KILL://销毁
				if(data != null){
					if(data[0] == (byte)0x65){//销毁成功
						bundle.putBoolean(LOCK, true);
						msg.setData(bundle);
					}
					handler.sendMessage(msg);
					Log.e("kill--->", Tools.Bytes2HexString(data,
							data.length));
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 发送指令
	 * @param cmd
	 */
	private void sendCmd(byte[] cmd){
		if(character != null && bleService != null){
			character.setValue(cmd);
			bleService.writeCharacteristic(character);
			bleService.setCharacteristicNotification(character, true);
		}
	}

	/**
	 * 处理响应帧
	 *
	 * @param response
	 * @return
	 */
	private byte[] handlerResponse(byte[] response) {
		byte[] data = null;
		byte crc = 0x00;
		int responseLength = response.length;
		if (response[0] != HEAD) {
			Log.e("handlerResponse", "head error");
			return data;
		}
		if (response[responseLength - 1] != END) {
			Log.e("handlerResponse", "end error");
			return data;
		}
		if (responseLength < 7)
			return data;
		// 转成无符号int
		int lengthHigh = response[3] & 0xff;
		int lengthLow = response[4] & 0xff;
		int dataLength = lengthHigh * 256 + lengthLow;
		// 计算CRC
		crc = checkSum(response);
		if (crc != response[responseLength - 2]) {
//			Log.e("handlerResponse", "crc error");
			return data;
		}
		if (dataLength != 0 && responseLength == dataLength + 7) {
//			Log.e("handlerResponse", "response right");
			data = new byte[dataLength + 1];
			data[0] = response[2];
			System.arraycopy(response, 5, data, 1, dataLength);
		}
		return data;
	}


	public boolean setBaudrate() {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] getFirmware() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 设置输出功率
	 * @param value
	 */
	public void  setOutputPower(int value) {
		messegeWhat = MSG_SET_OUTPUT;
		byte[] cmd = {HEAD ,(byte)0x00 ,(byte)0xB6 ,(byte)0x00
				,(byte)0x02 ,(byte)0x0A ,(byte)0x28
				,(byte)0xEA ,END};
		cmd[5] = (byte)((0xff00 & value)>>8);
		cmd[6] = (byte)(0xff & value);
		cmd[cmd.length - 2] = checkSum(cmd);
		sendCmd(cmd);
	}

	/**
	 * 获取输出功率
	 */
	public void getOutputPower(){
		messegeWhat = MSG_READ_OUTPUT;
		byte[] cmd = { HEAD, (byte) 0x00, (byte) 0xB7, (byte) 0x00,
				(byte) 0x00, (byte) 0xB7, END };
		sendCmd(cmd);
	}

	/**
	 * 设置工作区域
	 * @param workArea
	 */
	public void setWorArea(int workArea){
		messegeWhat = MSG_SET_WORK_AREA;
		byte[] cmd = { HEAD, (byte) 0x00, (byte) 0x07, (byte) 0x00,
				(byte) 0x01, (byte) 0x01, (byte) 0x09, END };
		cmd[5] = (byte) workArea;
		cmd[6] = checkSum(cmd);
		sendCmd(cmd);
	}

	/**
	 * 取消选择
	 */
	private void unSelectEPC(){
		byte[] cmd = { HEAD, (byte) 0x00, (byte) 0x12, (byte) 0x00,
				(byte) 0x01, (byte) 0x01, (byte) 0x14, END };
		sendCmd(cmd);
	}

	/**
	 * 盘存
	 */
	public void inventoryRealTime() {
		byte[] cmd = Tools.HexString2Bytes("AA00220000228E");
		messegeWhat = MSG_INVENTORY ;
		sendCmd(cmd);

	}

	public void selectEPC(byte[] epc) {
		// TODO Auto-generated method stub

	}

	/**
	 * 读数据
	 * @param memBank
	 * @param startAddr
	 * @param length
	 * @param accessPassword
	 */
	public void  readFrom6C(int memBank, int startAddr, int length,
							byte[] accessPassword) {
		messegeWhat = MSG_READ_6C;
		byte[] cmd = { HEAD, (byte) 0x00, (byte) 0x39, (byte) 0x00,
				(byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x08, (byte) 0x4D, END };
		if (accessPassword == null || accessPassword.length != 4) {
			return ;
		}
		System.arraycopy(accessPassword, 0, cmd, 5, 4);
		cmd[9] = (byte) memBank;
		if (startAddr <= 255) {
			cmd[10] = 0x00;
			cmd[11] = (byte) startAddr;
		} else {
			int addrH = startAddr / 256;
			int addrL = startAddr % 256;
			cmd[10] = (byte) addrH;
			cmd[11] = (byte) addrL;
		}
		if (length <= 255) {
			cmd[12] = 0x00;
			cmd[13] = (byte) length;
		} else {
			int lengH = length / 256;
			int lengL = length % 256;
			cmd[12] = (byte) lengH;
			cmd[13] = (byte) lengL;
		}
		cmd[14] = checkSum(cmd);
		sendCmd(cmd);
	}

	/**
	 * 写数据
	 * @param password  密码
	 * @param memBank  存储区
	 * @param startAddr 起始地址，如果是写EPC区的话从2开始写
	 * @param dataLen 数据长度
	 * @param data  数据
	 */
	public void writeTo6C(byte[] password, int memBank, int startAddr,
						  int dataLen, byte[] data) {
		messegeWhat = MSG_WRITE_6C;
		int cmdLen = 16 + data.length;
		int parameterLen = 9 + data.length;
		byte[] cmd = new byte[cmdLen];
		cmd[0] = HEAD;
		cmd[1] = 0x00;
		cmd[2] = 0x49;
		if (parameterLen < 256) {
			cmd[3] = 0x00;
			cmd[4] = (byte) parameterLen;
		} else {
			int paraH = parameterLen / 256;
			int paraL = parameterLen % 256;
			cmd[3] = (byte) paraH;
			cmd[4] = (byte) paraL;
		}
		System.arraycopy(password, 0, cmd, 5, 4);
		cmd[9] = (byte) memBank;
		if (startAddr < 256) {
			cmd[10] = 0x00;
			cmd[11] = (byte) startAddr;
		} else {
			int startH = startAddr / 256;
			int startL = startAddr % 256;
			cmd[10] = (byte) startH;
			cmd[11] = (byte) startL;
		}
		if (dataLen < 256) {
			cmd[12] = 0x00;
			cmd[13] = (byte) dataLen;
		} else {
			int dataLenH = dataLen / 256;
			int dataLenL = dataLen % 256;
			cmd[12] = (byte) dataLenH;
			cmd[13] = (byte) dataLenL;
		}
		System.arraycopy(data, 0, cmd, 14, data.length);
		cmd[cmdLen - 2] = checkSum(cmd);
		cmd[cmdLen - 1] = END;
		// Log.e("write data", Tools.Bytes2HexString(cmd, cmdLen));
		sendCmd(cmd);
//		return false;
	}

	/**
	 * 设置灵敏度
	 */
	public void setSensitivity(int value) {
		byte[] cmd = { HEAD, (byte) 0x00, (byte) 0xF0, (byte) 0x00,
				(byte) 0x04, (byte) 0x02, (byte) 0x06, (byte) 0x00,
				(byte) 0xA0, (byte) 0x9C, END };
		cmd[5] = (byte) value;
		cmd[cmd.length - 2] = checkSum(cmd);
		//发送指令
		response = null;
		if(!resp_ok){

		}


	}

	/**
	 * 锁定标签
	 * @param password
	 * @param memBank
	 * @param lockType
	 */
	public void lock6C(byte[] password, int memBank, int lockType) {
		// TODO Auto-generated method stub
		messegeWhat = MSG_LOCK;
		byte[] cmd = { HEAD, 0x00, (byte) 0x82, 0x00, 0x07, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, END };
		int lockPay = 0;
		byte[] lockPara = new byte[3];
		// 开放
		if (lockType == LOCK_TYPE_OPEN) {
			// System.out.println("开放");
			lockPay = (1 << (20 - 2 * memBank + 1));
		}
		// 永久开
		if (lockType == LOCK_TYPE_PERMA_OPEN) {
			// System.out.println("永久开");
			lockPay = (1 << (20 - 2 * memBank + 1)) + (1 << (20 - 2 * memBank))
					+ (1 << (2 * (5 - memBank)));
		}
		// 锁定
		if (lockType == LOCK_TYPE_LOCK) {
			// System.out.println("锁定");
			lockPay = (1 << (20 - 2 * memBank + 1))
					+ (2 << (2 * (5 - memBank)));
		}
		// 永久锁定
		if (lockType == LOCK_TYPE_PERMA_LOCK) {
			// System.out.println("永久锁定");
			lockPay = (1 << (20 - 2 * memBank + 1)) + (1 << (20 - 2 * memBank))
					+ (3 << (2 * (5 - memBank)));
		}
		lockPara = Tools.intToByte(lockPay);
		// 密码
		System.arraycopy(password, 0, cmd, 5, password.length);
		// 锁定参数
		System.arraycopy(lockPara, 0, cmd, 9, lockPara.length);
		cmd[cmd.length - 2] = checkSum(cmd);

		sendCmd(cmd);
	}


	public void  killTag(byte[] password) {
		messegeWhat = MSG_KILL;
		byte[] cmd = { HEAD, 0x00, (byte) 0x65, (byte) 0x00, (byte) 0x04,
				(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF,
				(byte) 0x67, END };
		System.arraycopy(cmd, 4, password, 0, password.length);
		cmd[cmd.length - 2] = checkSum(cmd);
		sendCmd(cmd);
	}


	public void close() {
		// TODO Auto-generated method stub

	}


	public byte checkSum(byte[] data) {
		byte crc = 0x00;
		// 从指令类型累加到参数最后一位
		for (int i = 1; i < data.length - 2; i++) {
			crc += data[i];
		}
		return crc;
	}


	public int setFrequency(int startFrequency, int freqSpace, int freqQuality) {
		// TODO Auto-generated method stub
		return 0;
	}

}
