/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import com.eryansky.common.utils.io.PropertiesLoader;


/**
 * 项目中用到的静态变量.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-20 上午11:40:56
 */
public class SysConstants {
    /**
     * session 验证码key
     */
    public static final String SESSION_VALIDATE_CODE = "validateCode";
    
    private static PropertiesLoader appconfig = null;
    private static PropertiesLoader sensitive = null;
    private static PropertiesLoader sqlfilter = null;
    
    /**
     * 配置文件(appconfig.properties)
     */
    public static PropertiesLoader getAppConfig() {
    	if(appconfig == null){
    		appconfig = new PropertiesLoader("appconfig.properties");
    	}
        return appconfig;
    }

    /**
     * 修改配置文件
     * @param key
     * @param value
     */
    public static void modifyAppConfig(String key,String value) {
        String filePath = "appconfig.properties";
        if(appconfig == null){
            appconfig = new PropertiesLoader(filePath);
        }
        appconfig.modifyProperties(filePath,key,value);
    }
    
    /**
     * 配置文件(sensitive.properties)
     */
    public static PropertiesLoader getSensitive() {
    	if(sensitive == null){
    		sensitive = new PropertiesLoader("sensitive.properties");
    	}
        return sensitive;
    }
    
    /**
     * SQL参数过滤配置文件(sqlfilter.properties)
     */
    public static PropertiesLoader getSqlfilter() {
    	if(sqlfilter == null){
    		sqlfilter = new PropertiesLoader("sqlfilter.properties");
    	}
        return sqlfilter;
    }
    
    /**
     * jdbc url连接参数(默认:"").
     */
    public static String getJdbcUrl(){
    	return SysConstants.getAppConfig().getProperty("jdbc.url","");
    }
    /**
     * 获取是否是开发模式(默认:false).
     */
    public static boolean isdevMode(){
    	return getAppConfig().getBoolean("devMode",false);
    }
    
    
    
	/**
	 * 获得上传表单域的名称
	 * 
	 * @return
	 */
	public static final String getUploadFieldName() {
		return getAppConfig().getProperty("uploadFieldName", "filedata");
	}

	/**
	 * 获得上传文件的最大大小限制
	 * 
	 * @return
	 */
	public static final long getUploadFileMaxSize() {
		 String uploadFileMaxSize = getAppConfig().getProperty("uploadFileMaxSize", "20971520");
		 return Long.valueOf(uploadFileMaxSize);
	}

	/**
	 * 获得允许上传文件的扩展名
	 * 
	 * @return
	 */
	public static final String getUploadFileExts() {
		return getAppConfig().getProperty("uploadFileExts","txt,rar,zip,doc,docx,xls,xlsx,jpg,jpeg,gif,png,swf,wmv,avi,wma,mp3,mid");
	}

	/**
	 * 获得上传文件要放到那个目录
	 * 
	 * @return
	 */
	public static final String getUploadDirectory() {
		return getAppConfig().getProperty("uploadDirectory", "attached");
	}
    
}
