package com.jiuray.uhf.command;

import android.util.Log;

/**
 * Created by jj on 2018/8/19.
 * 生成UHF指令，并解析指令
 */

public class UhfCommandHelper {

    private final String TAG = "UhfCommandHelper";

//////////////////系统指令//////////////////////////////////////////////
    public byte[] reset(){
        byte[] cmd = genCmd(UhfCommand.CMD_RESET, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

    public byte[] getFirwaremVersion(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_FIRMWARE_VERSION, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

    public byte[] getOutPower(){
        byte[] cmd = genCmd(UhfCommand.CMD_GET_OUTPUT_POWER, null) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

////////////////////6C指令/////////////////////////////////
    public byte[] inventory(int repeat) {
        byte [] data = {(byte)repeat};
        byte[] cmd = genCmd(UhfCommand.CMD_INVENTORY, data) ;
        if (cmd != null) {
            Log.e(TAG, Tools.Bytes2HexString(cmd, cmd.length)) ;
        }
        return cmd ;
    }

    /**
     * Head  	Len 	Address	  Cmd 	 Data	 Check
     * 1Byte   1 Byte	 1 Byte	1 Byte	N Bytes	 1 Byte
     * 生成指令
     * @param cmdCode
     * @param data
     * @return
     */
    private byte[] genCmd(byte cmdCode, byte[] data){
        byte[] cmd  = null ;
        if (data != null) {
            cmd = new byte[5+ data.length] ;
            System.arraycopy(data, 0, cmd, 4, data.length);
        }else{
            cmd = new byte[5] ;
        }
        cmd[0] = UhfCommand.CMD_HEAD ;
        cmd[1] = (byte)(cmd.length - 2) ;
        cmd[2] = UhfCommand.ADDR ;
        cmd[3] = cmdCode ;
        cmd[cmd.length - 1] = checkSum(cmd, 0, cmd.length - 1);
         return cmd ;
    }

    /**
     * 计算校验位
     * @param btAryBuffer
     * @param nStartPos
     * @param nLen
     * @return
     */
    private  byte checkSum(byte[] btAryBuffer, int nStartPos, int nLen) {
        byte btSum = 0x00;

        for (int nloop = nStartPos; nloop < nStartPos + nLen; nloop++ ) {
            btSum += btAryBuffer[nloop];
        }

        return (byte)(((~btSum) + 1) & 0xFF);
    }
}
