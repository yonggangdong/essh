/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.Map;

public class UrlParamsInterceptor extends AbstractInterceptor {

	/**
	 * Url参数绑定到ognl中
	 */
	private static final long serialVersionUID = -1718725814661509918L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionMapping mapping = ServletActionContext.getActionMapping();
		Map map = mapping.getParams();
		if (map != null && map.size() > 0) {
			ValueStack stack = invocation.getInvocationContext()
					.getValueStack();
			for (Object o : map.keySet()) {
				stack.setValue(o.toString(), map.get(o));
			}
			invocation.getInvocationContext().setValueStack(stack);
		}
		return invocation.invoke();
	}
}
