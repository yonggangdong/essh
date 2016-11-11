/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eryansky.utils.SelectType;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 继承ActionSupport.
 * <br>包含分页属性当前页码page、每页记录数rows、排序字段sort、排序方式order 主键id、下拉选项类型selectType
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-23 下午7:52:44
 * 
 */
@SuppressWarnings("serial")
public class SimpleActionSupport extends ActionSupport {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * /重定向，@Result type属性对应的值
	 */
	public static final String REDIRECT = "redirect";
	/**
	 * Action之间重定向，@Result type属性对应的值
	 */
	public static final String REDIRECT_ACTION = "redirectAction";

	/**
	 * 实体类的主键ID
	 */
	protected Long id;
	/**
	 * 分页 当前页码(默认值:1)
	 */
	protected int page = 1;
	/**
	 * 分页 每页记录数 (默认值:10)
	 */
	protected int rows = 10;
	/**
	 * 排序字段
	 */
	protected String sort;
	/**
	 * 排序方式 增序:'asc',降序:'desc'
	 */
	protected String order;
	/**
	 * 下拉框类型： 搜索:all(---全部---) 选择:select(---请选择---)
	 * 
	 * @see SelectType
	 */
	protected String selectType;

	/**
	 * 获取页面传递的id值
	 * 
	 * @param id
	 *            主键ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 主键ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 分页 当前页码
	 */
	public int getPage() {
		return page;
	}

	/**
	 * 设置 分页 当前页码
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * 分页 每页记录数
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * 设置分页 每页记录数
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * 排序字段
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * 设置 排序字段
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * 排序方式 增序:'asc',降序:'desc'
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 设置 排序方式 增序:'asc',降序:'desc'
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * 下拉框类型： 搜索:all(---全部---) 选择:select(---请选择---)
	 */
	public String getSelectType() {
		return selectType;
	}

	/**
	 * 设置下拉框类型： 搜索:all(---全部---) 选择:select(---请选择---)
	 * 
	 * @see SelectType
	 */
	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}
}
