/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.eryansky.common.model.Result;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.service.CommonManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 提供公共方法的Action.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-2-25 下午1:59:38
 */
public class CommonAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 实体类名称 例如: "Resource"
	 */
	private String entityName;
	/**
	 * 主键ID
	 */
	private Long rowId;
	/**
	 * 属性名称
	 */
	private String fieldName;
	/**
	 * 属性值
	 */
	private String fieldValue;

	@Autowired
	private CommonManager commonManager;

	/**
	 * 字段校验
	 */
	public void fieldCheck() {
		Long entityId = commonManager.getIdByProperty(entityName, fieldName,
				fieldValue);
		boolean isCheck = true;// 是否通过检查
		if (entityId != null) {
			if (rowId != null) {
				if (!rowId.equals(entityId)) {
					isCheck = false;
				}
			} else {
				isCheck = false;
			}

		}
		Struts2Utils.renderJson(new Result(Result.SUCCESS, null, isCheck));
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public String getPropertyName() {
		return fieldName;
	}

	public void setPropertyName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
