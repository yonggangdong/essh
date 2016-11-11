/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.webservice.server.impl;


import javax.jws.WebService;

import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.eryansky.entity.base.User;
import com.eryansky.service.base.UserManager;
import com.eryansky.webservice.server.UserWebService;
import com.eryansky.webservice.server.WsConstants;
import com.eryansky.webservice.server.result.GetUserResult;
import com.eryansky.webservice.server.result.WSResult;

/**
 * UserWebService服务端实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-11 下午9:29:12 
 *
 */
//serviceName与portName属性指明WSDL中的名称, endpointInterface属性指向Interface定义类.
@WebService(serviceName = "UserService", portName = "UserServicePort", endpointInterface = "com.eryansky.webservice.server.UserWebService", targetNamespace = WsConstants.NS)
public class UserWebServiceImpl implements UserWebService {

	private static Logger logger = LoggerFactory.getLogger(UserWebServiceImpl.class);

	@Autowired
	private UserManager userManager;

	/**
	 * @see UserWebService#getUser()
	 */
	public GetUserResult getUser(String loginName) {
		//校验请求参数
		try {
			Assert.notNull(loginName, "loginName参数为空");
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return WSResult.buildResult(GetUserResult.class, WSResult.PARAMETER_ERROR, e.getMessage());
		}

		//获取用户
		try {

			User entity = userManager.findUniqueBy("loginName", loginName);

			GetUserResult result = new GetUserResult();
			result.setUser(entity);

			return result;
		} catch (ObjectNotFoundException e) {
			String message = "用户不存在(loginName:" + loginName + ")";
			logger.error(message, e);
			return WSResult.buildResult(GetUserResult.class, WSResult.PARAMETER_ERROR, message);
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			return WSResult.buildDefaultErrorResult(GetUserResult.class);
		}
	}
}
