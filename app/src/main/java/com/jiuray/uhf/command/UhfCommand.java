package com.jiuray.uhf.command;

/**
 *
 * 超高频指令集
 */

public class UhfCommand {

    /**
     * 指令数据包头，每包数据均以0xAA开始。
     */
    public static final byte CMD_HEAD = (byte) 0xAA ;


    /**
     * 返回数据包头，每包数据均以0xFA开始。
     */
    public static final byte RESP_HEAD = (byte) 0xFA ;

    /**
     * 读写器地址。供RS-485接口串联时使用。一般地址从0～254(0xFE)，
     * 255（0xFF）为公用地址。读写器接收自身地址和公用地址的命令。
     */
    public static final byte ADDR = (byte) 0xFF ;

    //--------------6C标签指令-------------------------//
    /**
     *复位读写器
     */
    public static final byte CMD_RESET = (byte) 0x0E ;
    /**
     *设置串口通讯波特率   0x01
     */
    public static final byte CMD_SET_UART_BAUDRATE = (byte) 0x01 ;
    /**
     *读取读写器固件版本    0x02
     */
    public static final byte CMD_GET_FIRMWARE_VERSION = (byte) 0x02 ;
    /**
     *设置读写器地址        0x07
     */
    public static final byte CMD_SET_READER_ADDRESS = (byte) 0x07 ;
    /**
     *设置读写器工作天线    0x0A
     */
    public static final byte CMD_SET_WORK_ANTENNA = (byte) 0x0A ;
    /**
     *查询当前天线工作天线   0xA1
     */
    public static final byte CMD_GET_WORK_ANTENNA = (byte) 0xA1 ;
    /**
     *设置读写器射频输出功率  0x04
     */
    public static final byte CMD_SET_OUTPUT_POWER = (byte) 0x04 ;
    /**
     *查询读写器当前输出功率   0x4A
     */
    public static final byte CMD_GET_OUTPUT_POWER = (byte) 0x4A ;
    /**
     *设置读写器工作频率范围   0x05
     */
    public static final byte CMD_SET_FREQUENCY_REGION = (byte) 0x05 ;
    /**
     *查询读写器工作频率范围   0x5A
     */
    public static final byte CMD_GET_FREQUENCY_REGION = (byte) 0x5A ;
    /**
     *设置蜂鸣器状态          0x18
     */
    public static final byte CMD_SET_BEEPER_MODE = (byte) 0x18 ;
    /**
     *查询当前设备的工作温度   0x19
     */
    public static final byte CMD_GET_READER_TEMPERATURE = (byte) 0x19 ;
    /**
     *设置DRM状态           0x2C
     */
    public static final byte CMD_SET_DRM_MODE = (byte) 0x2C ;
    /**
     *获取DRM状态          0x2D
     */
    public static final byte DMD_GET_DRM_MODE = (byte) 0x2D ;
    /**
     *读取GPIO电平           0x20
     */
    public static final byte CMD_READ_GPIO_VALUE = (byte) 0x20 ;
    /**
     *设置GPIO电平            0x21
     */
    public static final byte CMD_WRITE_GPIO_VALUE = (byte) 0x21 ;
    /**
     *设置天线连接检测器状态   0x22
     */
    public static final byte CMD_SET_ANT_CONNECTION_DETECTOR = (byte) 0x22 ;
    /**
     *读取天线连接检测器状态   0x23
     */
    public static final byte CMD_GET_ANT_CONNECTION_DETECTOR = (byte) 0x23 ;
    /**
     *设置读写器临时射频输出功率 0x24
     */
    public static final byte CMD_SET_TEMPORARY_OUTPUT_POWER = (byte) 0x24 ;
    /**
     *设置读写器识别码     0x25
     */
    public static final byte CMD_SET_READER_IDENTIFIER = (byte) 0x25 ;
    /**
     *读取读写器识别码     0x26
     */
    public static final byte CMD_GET_READER_IDENTIFIER = (byte) 0x26 ;
    /**
     *设置射频链路的通讯速率  0x27
     */
    public static final byte CMD_SET_RF_LINK_PROFILE = (byte) 0x27 ;
    /**
     *读取射频链路的通讯速率   0x28
     */
    public static final byte CMD_GET_RF_LINK_PROFILE = (byte) 0x28 ;
    /**
     *测量天线端口的回波损耗  0x29
     */
    public static final byte CMD_GET_RF_PORT_RETURN_LOSS = (byte) 0x29 ;
    /**
     *盘存标签     0xEE
     */
    public static final byte CMD_INVENTORY = (byte) 0xEE ;
    /**
     *读标签    0xEC
     */
    public static final byte CMD_READ = (byte) 0xEC ;
    /**
     *写标签     0xEB
     */
    public static final byte CMD_WRITE = (byte) 0xEB ;
    /**
     *锁定标签    0xE6
     */
    public static final byte CMD_LOCK = (byte) 0xE6 ;
    /**
     *灭活标签    0xE8
     */
    public static final byte CMD_KILL = (byte) 0xE8 ;
    /**
     *匹配ACCESS操作的EPC号  0xE5
     */
    public static final byte CMD_SET_ACCESS_EPC_MATCH = (byte) 0xE5 ;
    /**
     *查询匹配的EPC状态   0xE7
     */
    public static final byte CMD_GET_ACCESS_EPC_MATCH = (byte) 0x0E ;
    /**
     *盘存标签(实时上传标签数据)  0xEF
     */
    public static final byte CMD_REAL_TIME_INVENTORY = (byte) 0xEF ;
    /**
     *快速轮询多个天线盘存标签    0xF4
     */
    public static final byte CMD_FAST_SWITCH_ANT_INVENTORY = (byte) 0xF4 ;
    /**
     *自定义session和target盘存  0xE1
     */
    public static final byte CMD_CUSTOMIZED_SESSION_TARGET_INVENTORY = (byte) 0xE1 ;
    /**
     *设置Monza标签快速读TID   0xE0
     (设置不被保存至内部FLASH)
     */
    public static final byte CMD_SET_IMPINJ_FAST_TID = (byte) 0xE0 ;
    /**
     *设置Monza标签快速读TID   0xE2
     (设置被保存至内部FLASH)
     */
    public static final byte CMD_SET_AND_SAVE_IMPINJ_FAST_TID = (byte) 0xE2 ;
    /**
     *查询当前的快速TID设置     0xE3
     */
    public static final byte CMD_GET_IMPINJ_FAST_TID = (byte) 0xE3 ;


}
