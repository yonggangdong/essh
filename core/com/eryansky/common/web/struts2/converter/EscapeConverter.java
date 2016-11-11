/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

/**
 * 用于防止XSS攻击
 *
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-10-16 下午2:27
 *
 */
public class EscapeConverter extends StrutsTypeConverter {

    @Override
    public String convertToString(Map context, Object o) {
        if (o != null) {
            if (o instanceof String[]) {
                String[] str = (String[]) o;
                if (str != null && str[0] != null) {
                    return HtmlUtils.htmlEscape((String) ((String[]) o)[0]);
                }
            } else if (o instanceof String) {
                String str = (String) o;
                if (str != null) {
                    return HtmlUtils.htmlEscape(str);
                }
            }
        }
        return "";
    }

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        return values;
    }

}