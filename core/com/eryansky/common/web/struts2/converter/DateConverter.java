/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 日期转换器.
 * <br>struts2默认自带日期转换器，但是如果前台传递的日期字符串是"null"的话，会出现错误，使用该日期转换器来替代默认的
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-12-11 上午10:40:19
 */
public class DateConverter extends StrutsTypeConverter {

    private static final String FROMDATE_YM = "yyyy-MM";
    private static final String FROMDATE = "yyyy-MM-dd";
    private static final String FROMDATETIME_ = "yyyy-MM-dd HH:mm";
    private static final String FROMDATETIME = "yyyy-MM-dd HH:mm:ss";

    @SuppressWarnings("rawtypes")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values == null || values.length == 0) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(FROMDATETIME);
        Date date = null;
        String dateString = values[0];
        if (dateString != null && !dateString.trim().equals("") && !dateString.trim().equals("null")) {
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                date = null;
            }
            if (date == null) {
                sdf = new SimpleDateFormat(FROMDATETIME_);
                try {
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    date = null;
                }
            }
            if (date == null) {
                sdf = new SimpleDateFormat(FROMDATE);
                try {
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    date = null;
                }
            }
            if (date == null) {
                sdf = new SimpleDateFormat(FROMDATE_YM);
                try {
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    date = null;
                }
            }
        }
        return date;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String convertToString(Map context, Object o) {
        if (o instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(FROMDATETIME);
            return sdf.format(o);
        }
        return "";
    }

}
