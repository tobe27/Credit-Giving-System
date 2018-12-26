package com.example.common.util;
import java.util.Properties;
import java.util.ResourceBundle;
/**
 * 读取配置文件工具类
 *  @author Created by W.S.T on 2018-10-25
 * */
public class PropertiesUtil {
	private static String outnet="";//从配置文件中读取是否启用外网  0 未启用  1启用
	private PropertiesUtil() {}
	/**
    * 调用此方法获取配置文件中的外网是否启用
    * @param  
    * @return
    */
	public static String GetOutnet() {
		if("".equals(outnet)) {
			setOutnet(getPropertyForString("dataSource.outnet"));	
		}
		return outnet;
	}
	

	public static void setOutnet(String outnet) {
		PropertiesUtil.outnet = outnet;
		
	}
	
	/**
     * 调用此方法获取配置文件的参数值
     * @param propertyName 参数名
     * @return
     */
	public static String getPropertyForString(String propertyName){
		
		ResourceBundle resourceBundle=ResourceBundle.getBundle("config");
		return resourceBundle.getString(propertyName);
	}
	

}
