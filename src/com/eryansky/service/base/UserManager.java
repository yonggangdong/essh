/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.service.base;

import java.util.ArrayList;
import java.util.List;

import com.eryansky.core.security.SessionInfo;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.entity.base.Organ;
import com.eryansky.utils.CacheConstants;
import com.eryansky.core.security.SecurityUtils;
import com.google.common.collect.Lists;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.entity.base.User;
import com.eryansky.common.orm.entity.StatusState;

/**
 * 用户管理User Service层实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-20 上午11:22:13 
 *
 */
@Service
public class UserManager extends EntityManager<User, Long> {
	
	private HibernateDao<User, Long> userDao;

    @Autowired
    private OrganManager organManager;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
	}
	
	@Override
	protected HibernateDao<User, Long> getEntityDao() {
		return userDao;
	}

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void saveOrUpdate(User entity) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        userDao.saveOrUpdate(entity);
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void merge(User entity) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        userDao.merge(entity);
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    @Override
    public void saveEntity(User entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        super.saveEntity(entity);
    }

    /**
	 * 自定义删除方法.
	 */
    @CacheEvict(value = {
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
	public void deleteByIds(List<Long> ids) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
		if(!Collections3.isEmpty(ids)){
			for(Long id :ids){
				User superUser = this.getSuperUser();
				if (id.equals(superUser.getId())) {
					throw new SystemException("不允许删除超级用户!");
				}
				User user = userDao.get(id);
				if(user != null){
					//清空关联关系
                    user.setDefaultOrgan(null);
                    user.setOrgans(null);
					user.setRoles(null);
                    user.setResources(null);
					//逻辑删除
					//手工方式(此处不使用 由注解方式实现逻辑删除)
//					user.setStatus(StatusState.delete.getValue());
					//注解方式 由注解设置用户状态
					userDao.delete(user);
				}
			}
		}else{
			logger.warn("参数[ids]为空.");
		}
	}
	
	/**
	 * 得到当前登录用户.
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public User getCurrentUser() throws DaoException,SystemException,ServiceException{
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        User user = getEntityDao().load(sessionInfo.getUserId());
        return user;
    }
	
	/**
	 * 得到超级用户.
	 * 
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public User getSuperUser() throws DaoException,SystemException,ServiceException {
        User superUser = userDao.load(1l);//超级用户ID为1
        if(superUser == null){
            throw new SystemException("系统未设置超级用户.");
        }
        return superUser;
	}
	/**
	 * 根据登录名、密码查找用户.
	 * <br/>排除已删除的用户
	 * @param loginName
	 *            登录名
	 * @param password
	 *            密码
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public User getUserByLP(String loginName, String password)
			throws DaoException,SystemException,ServiceException {
		Assert.notNull(loginName, "参数[loginName]为空!");
		Assert.notNull(password, "参数[password]为空!");
		List<User> list = userDao.createQuery(
					"from User u where u.loginName = ? and u.password = ? and u.status <> ?",
					new Object[] { loginName, password,StatusState.delete.getValue() }).list();
		return list.isEmpty() ? null:list.get(0);
	}

	
	/**
	 * 根据父ID得到角色.
	 * 
	 * @param roleids
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<User> getRoleIds(String roleids) throws DaoException,SystemException,ServiceException {
		StringBuffer sb = new StringBuffer();
		sb.append("from User u where u.roleids ");
		List<User> list = new ArrayList<User>();
		if (roleids == null) {
			sb.append(" is null order by u.id asc");
			list = userDao.createQuery(sb.toString()).list();
		} else {
			sb.append(" = ? order by u.id asc");
			list = userDao.createQuery(sb.toString(),
					new Object[] { roleids }).list();
		}
		return list;
	}

	/**
	 * 根据登录名查找.
	 * <br>注：排除已删除的对象
	 * @param loginName 登录名
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public User getUserByLoginName(String loginName)
			throws DaoException,SystemException,ServiceException {
		Assert.notNull(loginName, "参数[loginName]为空!");
		Assert.notNull(loginName, "参数[status]为空!");
		List<User> list = userDao.createQuery(
					"from User u where u.loginName = ? and u.status <> ?",
					new Object[] { loginName, StatusState.delete.getValue() }).list();
		return list.isEmpty() ? null:list.get(0);
	}

    /**
     * 根据组织机构Id以及登录名或姓名分页查询
     * @param organId 组织机构ID
     * @param loginNameOrName 姓名或手机号码
     * @param page 第几页
     * @param rows 页大小
     * @param sort 排序字段
     * @param order 排序方式 增序:'asc',降序:'desc'
     * @return
     */
    public Page<User> getUsersByQuery(Long organId, String loginNameOrName, int page, int rows, String sort, String order) {
        //条件都为空的时候能够查询出所有数据
        if(organId==null && StringUtils.isBlank(loginNameOrName)){
            return super.find(page,rows,null,null,new ArrayList<PropertyFilter>());
        }
        List<Object> params = Lists.newArrayList();
        StringBuilder hql = new StringBuilder();
        hql.append("from User u where 1=1 ");
        if(organId != null){
            Organ organ = organManager.loadById(organId);
            hql.append("and ? in elements(u.organs) ");
            params.add(organ);
        }
        if(StringUtils.isNotBlank(loginNameOrName)){
            hql.append("and (u.loginName like ? or u.name like ?) ");
            params.add("%"+loginNameOrName+"%");
            params.add("%"+loginNameOrName+"%");
        }
        //设置分页
        Page<User> p = new Page<User>(rows);
        p.setPageNo(page);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            p.setOrder(order);
            p.setOrderBy(sort);
        } else {
            p.setOrder(Page.ASC);
            p.setOrderBy("id");
        }
        logger.debug(hql.toString());
        Page<User> userPage = userDao.findPage(p,hql.toString(),params.toArray());
        return userPage;
    }

    /**
     * 根据组织机构Id以及登录名或姓名分页查询
     * @param inUserIds 用户id集合
     * @param loginNameOrName 姓名或手机号码
     * @param page 第几页
     * @param rows 页大小
     * @param sort 排序字段
     * @param order 排序方式 增序:'asc',降序:'desc'
     * @return
     */
    public Page<User> getUsersByQuery(List<Long> inUserIds, String loginNameOrName, int page, int rows, String sort, String order) {
        List<Object> params = Lists.newArrayList();
        StringBuilder hql = new StringBuilder();
        hql.append("from User u where 1=1 ");
        if(!Collections3.isEmpty(inUserIds)){
            hql.append("and (u.id in (:ids) ");
            if(StringUtils.isNotBlank(loginNameOrName)){
                hql.append("or u.loginName like ? or u.name like ? ) ");
                params.add("%"+loginNameOrName+"%");
                params.add("%"+loginNameOrName+"%");
            }
        }else{
            if(StringUtils.isNotBlank(loginNameOrName)){
                hql.append("and (u.loginName like ? or u.name like ?) ");
                params.add("%"+loginNameOrName+"%");
                params.add("%"+loginNameOrName+"%");
            }
        }

        hql.append("and u.status = ? ");
        params.add(StatusState.normal.getValue());
        //设置分页
        Page<User> p = new Page<User>(rows);
        p.setPageNo(page);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            p.setOrder(order);
            p.setOrderBy(sort);
        } else {
            p.setOrder(Page.ASC);
            p.setOrderBy("id");
        }
        Query q = userDao.createQuery(hql.toString(), params.toArray());
        if(!Collections3.isEmpty(inUserIds)){
            q.setParameterList("ids",inUserIds);
        }

        if (p.isAutoCount()) {
            long totalCount = getEntityDao().countHqlResult(hql.toString(), params.toArray());
            p.setTotalCount(totalCount);
        }

        getEntityDao().setPageParameterToQuery(q, p);

        List result = q.list();
        p.setResult(result);
        return p;
    }

}
