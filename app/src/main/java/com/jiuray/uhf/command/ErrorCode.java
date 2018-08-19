package com.jiuray.uhf.command;

/**
 * Created by jj on 2018/8/20.
 * 错误码
 */

public class ErrorCode {

    /**
     *0x10	command_success	命令成功完成
     */
    public static final byte command_success = 0x10 ;
    /**
     *0x11	command_fail	命令执行失败
     */
    public static final byte command_fail = 0x11 ;
    /**
     *0x20	mcu_reset_error	CPU复位错误
     */
    public static final byte mcu_reset_error = 0x20 ;
    /**
     *0x21	cw_on_error	打开CW错误
     */
    public static final byte cw_on_error = 0x21 ;
    /**
     *0x22	antenna_missing_error	天线未连接
     */
    public static final byte antenna_missing_error = 0x22 ;
    /**
     *0x23	write_flash_error	写Flash错误
     */
    public static final byte write_flash_error = 0x23 ;
    /**
     *0x24	read_flash_error	读Flash错误
     */
    public static final byte read_flash_error = 0x24 ;
    /**
     *0x25	set_output_power_error	设置发射功率错误
     */
    public static final byte set_output_power_error = 0x25 ;
    /**
     *0x31	tag_inventory_error	盘存标签错误
     */
    public static final byte tag_inventory_error = 0x31 ;
    /**
     *0x32	tag_read_error	读标签错误
     */
    public static final byte tag_read_error = 0x32 ;
    /**
     *0x33	tag_write_error	写标签错误
     */
    public static final byte tag_write_error = 0x33 ;
    /**
     *0x34	tag_lock_error	锁定标签错误
     */
    public static final byte tag_lock_error = 0x34 ;
    /**
     *0x35	tag_kill_error	灭活标签错误
     */
    public static final byte tag_kill_error = 0x35 ;
    /**
     *0x36	no_tag_error	无可操作标签错误
     */
    public static final byte no_tag_error = 0x36 ;
    /**
     *0x37	inventory_ok_but_access_fail	成功盘存但访问失败
     */
    public static final byte inventory_ok_but_access_fail = 0x37 ;
    /**
     *0x38	buffer_is_empty_error	缓存为空
     */
    public static final byte buffer_is_empty_error = 0x38 ;
    /**
     *0x40	access_or_password_error	访问标签错误或访问密码错误
     */
    public static final byte access_or_password_error = 0x40 ;
    /**
     *0x41	parameter_invalid	无效的参数
     */
    public static final byte parameter_invalid = 0x41 ;
    /**
     *0x42	parameter_invalid_wordCnt_too_long	wordCnt参数超过规定长度
     */
    public static final byte parameter_invalid_wordCnt_too_long = 0x42 ;
    /**
     *0x43	parameter_invalid_membank_out_of_range	MemBank参数超出范围
     */
    public static final byte parameter_invalid_membank_out_of_range = 0x43 ;
    /**
     *0x44	parameter_invalid_lock_region_out_of_range	Lock数据区参数超出范围
     */
    public static final byte parameter_invalid_lock_region_out_of_range = 0x44 ;
    /**
     *0x45	parameter_invalid_lock_action_out_of_range	LockType参数超出范围
     */
    public static final byte parameter_invalid_lock_action_out_of_range = 0x45 ;
    /**
     *0x46	parameter_reader_address_invalid	读写器地址无效
     */
    public static final byte parameter_reader_address_invalid = 0x46 ;
    /**
     *0x47	parameter_invalid_antenna_id_out_of_range	Antenna_id 超出范围
     */
    public static final byte parameter_invalid_antenna_id_out_of_range = 0x47 ;
    /**
     *0x48	parameter_invalid_output_power_out_of_range	输出功率参数超出范围
     */
    public static final byte parameter_invalid_output_power_out_of_range = 0x48 ;
    /**
     *0x49	parameter_invalid_frequency_region_out_of_range	射频规范区域参数超出范围
     */
    public static final byte parameter_invalid_frequency_region_out_of_range = 0x49 ;
    /**
     *0x4A	parameter_invalid_baudrate_out_of_range	波特率参数超出范围
     */
    public static final byte parameter_invalid_baudrate_out_of_range = 0x4A ;
    /**
     *0x4B	parameter_beeper_mode_out_of_range	蜂鸣器设置参数超出范围
     */
    public static final byte parameter_beeper_mode_out_of_range = 0x4B ;
    /**
     *0x4C	parameter_epc_match_len_too_long	EPC匹配长度越界
     */
    public static final byte parameter_epc_match_len_too_long = 0x4C ;
    /**
     *0x4D	parameter_epc_match_len_error	EPC匹配长度错误
     */
    public static final byte parameter_epc_match_len_error = 0x4D ;
    /**
     *0x4E	parameter_invalid_epc_match_mode	EPC匹配参数超出范围
     */
    public static final byte parameter_invalid_epc_match_mode = 0x4E ;
    /**
     *0x4F	parameter_invalid_frequency_range	频率范围设置参数错误
     */
    public static final byte parameter_invalid_frequency_range = 0x4F ;
    /**
     *0x50	fail_to_get_RN16_from_tag	无法接收标签的RN16
     */
    public static final byte fail_to_get_RN16_from_tag = 0x50 ;
    /**
     *0x51	parameter_invalid_drm_mode	DRM设置参数错误
     */
    public static final byte parameter_invalid_drm_mode = 0x51 ;
    /**
     *0x52	pll_lock_fail	PLL不能锁定
     */
    public static final byte pll_lock_fail = 0x52 ;
    /**
     *0x53	rf_chip_fail_to_response 	射频芯片无响应
     */
    public static final byte rf_chip_fail_to_response = 0x53 ;
    /**
     *0x54	fail_to_achieve_desired_output_power	输出达不到指定的输出功率
     */
    public static final byte fail_to_achieve_desired_output_power = 0x54 ;
    /**
     *0x55	copyright_authentication_fail	版权认证未通过
     */
    public static final byte copyright_authentication_fail = 0x55 ;
    /**
     *0x56	spectrum_regulation_error	频谱规范设置错误
     */
    public static final byte spectrum_regulation_error = 0x56 ;
    /**
     *0x57	output_power_too_low	输出功率过低
     */
    public static final byte output_power_too_low = 0x57 ;
    /**
     *0xEE	fail_to_get_rf_port_return_loss	测量回波损耗失败
     */
    public static final byte fail_to_get_rf_port_return_loss = (byte)0xEE ;



}
