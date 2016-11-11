/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.converter;

import com.eryansky.common.utils.io.ClobUtil;
import org.apache.struts2.util.StrutsTypeConverter;

import java.sql.Clob;
import java.util.Map;

/**
 * java.sql.Clob转换器.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-12-11 上午10:30:59
 */
public class ClobConverter extends StrutsTypeConverter {

	@SuppressWarnings("rawtypes")
    @Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0) {
			return null;
		}
		return ClobUtil.getClob(values[0]);
	}

	@SuppressWarnings("rawtypes")
    @Override
	public String convertToString(Map context, Object o) {
		if (o instanceof Clob) {
			return ClobUtil.getString((Clob) o);
		}
		return "";
	}

}
