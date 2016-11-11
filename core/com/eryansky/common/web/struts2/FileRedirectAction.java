/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2;

import com.eryansky.common.web.struts2.utils.Struts2Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jsp页面重定向Action.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-23 下午10:47:59
 * 
 */
public class FileRedirectAction extends SimpleActionSupport {
	private static final long serialVersionUID = 7036941468397752690L;
	/**
	 * jsp文件夹
	 */
	private String prefix = "/WEB-INF/jsp/";
	/**
	 * 跳转页面
	 */
	private String toPage = "";

	public void redirect() throws Exception {
		HttpServletResponse response = Struts2Utils.getResponse();
		HttpServletRequest request = Struts2Utils.getRequest();
		String page = Struts2Utils.getParameter("toPage");
		if ((page == null) || ("".equals(page))) {
			logger.warn("重定向页面为空!");
			response.sendError(404);
		} else {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("重定向到页面:" + this.prefix + page);
			}
			request.getRequestDispatcher(this.prefix + page).forward(request,
					response);
		}
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

}