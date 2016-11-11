/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.struts2;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.reflection.MyBeanUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import java.util.List;

/**
 * Struts2中典型CRUD Action的抽象基类.
 * 
 * 主要定义了对Preparable,ModelDriven接口的使用,以及CRUD函数和返回值的命名.
 * 
 * <br>注:子类可覆盖相应方法.
 * <br>2013-05-11:修正因二次绑定导致乐观锁失效bug.
 * 
 * @param <T>
 *            CRUDAction所管理的对象类型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@SuppressWarnings("serial")
public abstract class StrutsAction<T> extends SimpleActionSupport implements
		ModelDriven<T>, Preparable {

	/**
	 * 进行增删改操作后,以redirect方式重新打开action默认页的result名.
	 */
	public static final String RELOAD = "reload";
	/**
	 * 查看方法的返回字符串
	 */
	public static final String VIEW = "view";

	/**
	 * T类型对象
	 */
	protected T model;
	
	/**
	 * ID集合
	 */
	protected List<Long> ids;

	/**
	 * EntityManager.
	 */
	public abstract EntityManager<T, Long> getEntityManager();

	/**
	 * Action函数, 默认的action函数, 默认调用list()函数.
	 */
	@Override
	public String execute() throws Exception {
		return list();
	}

	// -- CRUD Action函数 --//
	/**
	 * Action函数, 显示Entity列表. 建议return SUCCESS.
	 */
	public String list() throws Exception {
		return SUCCESS;
	}

	/**
	 * Action函数,显示新增或修改Entity界面. 建议return INPUT.
	 */
	public String input() throws Exception {
		return INPUT;
	}

	/**
	 * Action函数,显示. 建议return VIEW.
	 */
	public String view() throws Exception {
		return VIEW;
	}

	/**
	 * Action函数,新增或修改Entity. 
	 */
	public String save() throws Exception {
		try {
			getEntityManager().saveEntity(model);
			Struts2Utils.renderText(Result.successResult());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * Action函数,删除Entity. 
	 */
	public String delete() throws Exception {
		try {
			getEntityManager().deleteById(id);
			Struts2Utils.renderText(Result.successResult());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * Action函数,批量删除Entity.
	 */
	public String remove() throws Exception {
		try {
			getEntityManager().deleteByIds(ids);
			Struts2Utils.renderText(Result.successResult());
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * 数据列表. 子类可覆盖.
	 * @return
	 * @throws Exception
	 */
	public String datagrid() throws Exception {
		try {
			// 自动构造属性过滤器
			List<PropertyFilter> filters = HibernateWebUtils
					.buildPropertyFilters(Struts2Utils.getRequest());
			Page<T> p = getEntityManager().find(page, rows, sort, order,
					filters);
			Datagrid<T> dg = new Datagrid<T>(p.getTotalCount(), p.getResult());
			Struts2Utils.renderJson(dg);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	// -- Preparable函数 --//
	/**
	 * 实现空的prepare()函数,屏蔽所有Action函数公共的二次绑定.
	 */
	public void prepare() throws Exception {
	}

	/**
	 * 在input()前执行二次绑定.
	 */
	public void prepareInput() throws Exception {
		prepareModel();
	}

	/**
	 * 在view()前执行二次绑定.
	 */
	public void prepareView() throws Exception {
		prepareModel();
	}

	/**
	 * 在save()前执行二次绑定.
	 */
	public void prepareSave() throws Exception {
		prepareModel();
	}

	/**
	 * 等同于prepare()的内部函数,供prepardMethodName()函数调用.
	 */
	@SuppressWarnings("unchecked")
	protected void prepareModel() throws Exception {
		if (id != null) {
//			model = getEntityManager().getById(id);

			//修正因使用以上代码(根据ID查找对象)导致乐观锁失效bug
			T entity = getEntityManager().getById(id);
			//对象拷贝
            if(entity != null){
                model = (T) MyBeanUtils.cloneBean(entity);
            }else{
                throw new ActionException("ID为["+id+"]的记录不存在或已被其它用户删除！");
            }
		} else {
			model = (T) ReflectionUtils.getClassGenricType(getClass())
					.newInstance();
		}
	}

    /**
     * @return     模型对象
     */
	public T getModel() {
		return model;
	}

	/**
	 * 设置 ID集合
	 */
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	/**
	 *  ID集合
	 */
	public List<Long> getIds() {
		return ids;
	}

}
