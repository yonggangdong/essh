/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.service.base.ResourceManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 登录验证拦截器.
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@SuppressWarnings("serial")
public class AuthorityInterceptor extends MethodFilterInterceptor{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 返回无权限页面 对应403页面 由struts.xml配置
     */
    private static final String RESULT_NOAUTHORITY = "noauthority";

    @Autowired
    private ResourceManager resourceManager;

	@Override
	protected String doIntercept(ActionInvocation actioninvocation) throws Exception {
	    //登录用户
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String requestUrl = Struts2Utils.getRequest().getRequestURI();

		if(sessionInfo != null){
            //清空session中清空未被授权的访问地址
            Object unAuthorityUrl = Struts2Utils.getSession().getAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL);
            if(unAuthorityUrl != null){
                Struts2Utils.getSession().setAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL,null);
            }

            String url = StringUtils.replaceOnce(requestUrl,  Struts2Utils.getRequest().getContextPath(), "");
            //检查用户是否授权该URL
            boolean isAuthority = resourceManager.isAuthority(url,sessionInfo.getUserId());
            if(!isAuthority){
                logger.warn("用户{}未被授权URL:{}！", sessionInfo.getLoginName(), requestUrl);
                return RESULT_NOAUTHORITY;
            }

			return actioninvocation.invoke(); //递归调用拦截器
		}else{
            Struts2Utils.getSession().setAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL,requestUrl);
			return Action.LOGIN; //返回到登录页面
		}
		
	}

}
