/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.interceptor;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Properties;

/**
 * 表单输入特殊字符Html转译、敏感词汇转译 .
 * <br>如果请求参数中包含replace并且不为空则根据过滤规则过滤
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-2-23 上午11:24:35
 */
@SuppressWarnings("serial")
public class InputReplaceInterceptor extends AbstractInterceptor{

	protected Logger logger = LoggerFactory.getLogger(InputReplaceInterceptor.class);
	
	private static final String FILTER_REPLACE = "replace";

	private Properties pp = null;
	
	public InputReplaceInterceptor(){
		try {
			pp = SysConstants.getSensitive().getProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 如果请求参数中包含replace并且不为空则根据过滤规则过滤
	 */
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		String replace = Struts2Utils.getParameter(FILTER_REPLACE);
		logger.debug("{}:{}",FILTER_REPLACE,replace);
		//如果请求参数中包含FILTER_REPLACE并且不为空则根据过滤规则过滤
		if(!StringUtils.isBlank(replace)){
			HttpServletRequest rq = Struts2Utils.getRequest();
			// 过滤客户端提交表单中特殊字符
			Iterator<?> its = rq.getParameterMap().values().iterator();
			while (its.hasNext()) {
				String[] params = (String[]) its.next();
				for (int i = 0; i < params.length; i++) {
					//特殊字符Html转译
//					params[i] = params[i].replaceAll(params[i], EncodeUtils.htmlEscape(params[i]));
					//自定义转换规则
					for (int j = 0; j < params.length; j++) {
						for (Object oj : pp.keySet()) {
							String key = (String) oj;
							params[i] = params[i].replace(key, pp.getProperty(key));
						}
					}
				}
			}
		}
		return actionInvocation.invoke();
	}
	

}
