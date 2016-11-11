/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2.interceptor;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.eryansky.common.utils.SysConstants;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Result;
import com.eryansky.common.utils.Exceptions;
import com.eryansky.common.utils.SysUtils;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.common.web.utils.WebUtils;

/**
 * 自定义struts2 Action层异常拦截器.
 * <br>如果是Ajax请求发生的异常(包含Action层抛出的业务异常)则以Ajax方式返回，否则返回500错误页面.
 * <br>1、Hibernate Validator校验运行时异常.
 * <br>2、外键关联引用处理.
 * <br>3、Hibernate乐观锁 并发异常处理.
 * <br>4、参数类异常 Spring Assert、Apache Common Validate抛出该异常.
 * <br>5、NullPointerException空指针异常.
 * <br>6、ServiceException业务异常.
 * <br>7、SystemException系统异常
 * <br>8、其它异常,无法处理的异常返回异常相信信息.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-29 上午9:05:20
 */
@SuppressWarnings("serial")
public class ExceptionInterceptor extends AbstractInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Result result;
		try{
		    //递归调用拦截器
		    return invocation.invoke();
        }catch(Exception e){
        	//开发模式下打印堆栈信息
        	if(SysConstants.isdevMode()){
        		e.printStackTrace();
        	}else{
        		logger.error(e.getMessage());
        	}
        	//非Ajax请求 将跳转到500错误页面
//            if(!WebUtils.isAjaxRequest(Struts2Utils.getRequest())){
//            	throw e;
//            }
            //Ajax方式返回错误信息
            String emsg = e.getMessage();
    		StringBuilder sb = new StringBuilder();
    		final String MSG_DETAIL = " 详细信息:";
    		boolean isWarn = false;//是否是警告级别的异常
    		Object obj = null;//其它信息
    		sb.append("发生异常:");
    		//Hibernate Validator Bo注解校验异常处理
    		if(Exceptions.isCausedBy(e, ConstraintViolationException.class)){
    			isWarn = true;
    			ConstraintViolationException ce = (ConstraintViolationException) e;
    			Set<ConstraintViolation<?>> set =  ce.getConstraintViolations();
    			Iterator<?> iterator = set.iterator();
    			int i = -1;
    			while(iterator.hasNext()){
    				ConstraintViolation<?> c = (ConstraintViolation<?>) iterator.next();
    				sb.append(c.getMessage());
    				i++;
    				if(i==0){
    					obj = c.getPropertyPath().toString();
    				}
    				if (i < set.size() - 1) {
    					sb.append(",");
    				}else{
    					sb.append(".");
    				}
    			}
    			if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(e.getMessage());//将":"替换为","
    			}
    		}
    		//Hibernate 外键关联引用操作异常.
    		else if(Exceptions.isCausedBy(e, org.hibernate.exception.ConstraintViolationException.class)){
    			isWarn = true;
                sb.append("存在数据被引用,无法执行操作！");
                if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
    			}
            }
    		//Hibernate乐观锁 并发异常处理
    		else if(Exceptions.isCausedBy(e, StaleObjectStateException.class)){
    			isWarn = true;
                sb.append("当前记录已被其它用户修改或删除！");
                if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
    			}
            }
    		else if(Exceptions.isCausedBy(e, ObjectNotFoundException.class)){
    			sb.append("当前记录不存在或已被其它用户删除！");
                if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
    			}
    		}
    		//参数类异常 Spring Assert、Apache Common Validate抛出该异常
    		else if(Exceptions.isCausedBy(e, IllegalArgumentException.class)){
    			isWarn = true;
                sb.append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
            }
    		//空指针异常
    		else if(Exceptions.isCausedBy(e, NullPointerException.class)){
                sb.append("程序没写好,发生空指针异常！");
                if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
    			}
            }
    		
    		//业务异常
    		else if(Exceptions.isCausedBy(e, ServiceException.class)){
                ServiceException serviceException = (ServiceException) e;
                result = new Result(serviceException.getCode(), serviceException.getMessage(), serviceException.getObj());
                Struts2Utils.renderText(result);//以Text方式返回json异常字符串 兼容IE
        		return null;
            }
    		
    		//系统异常
    		else if(Exceptions.isCausedBy(e, SystemException.class)){
                sb.append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
            }
    		
    		//其它异常
    		else{
    			if(SysConstants.isdevMode()){
    				sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
    			}else{
    				sb.append("未知异常！");
    			}
    		}
    		if(isWarn){
    			result = new Result(Result.WARN,sb.toString(),obj);
    			logger.warn(result.toString());
    		}else{
    			result = new Result(Result.ERROR,sb.toString(),obj);
    			logger.error(result.toString());
    		}
        	Struts2Utils.renderText(result);//以Text方式返回json异常字符串 兼容IE
    		return null;
        }
	}
}