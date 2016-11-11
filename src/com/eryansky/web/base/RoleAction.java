/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.base;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.base.Resource;
import com.eryansky.entity.base.Role;
import com.eryansky.entity.base.User;
import com.eryansky.service.base.ResourceManager;
import com.eryansky.service.base.RoleManager;
import com.eryansky.service.base.UserManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 角色Role管理 Action层.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
public class RoleAction extends StrutsAction<Role> {

    /**
     * 资源ID集合
     */
    private List<Long> resourceIds = Lists.newArrayList();
    /**
     * 用户ID集合
     */
    private List<Long> userIds = Lists.newArrayList();

	@Autowired
	private RoleManager roleManager;
	@Autowired
	private ResourceManager resourceManager;
    @Autowired
    private UserManager userManager;

    @Override
	public EntityManager<Role, Long> getEntityManager() {
		return roleManager;
	}
	
    /**
     * 删除.
     */
	@Override
	public String remove() throws Exception {
		Result result;
		try {
			roleManager.deleteByIds(ids);
			result = Result.successResult();
			logger.debug(result.toString());
			Struts2Utils.renderJson(result);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
     * 保存.
     */
	@Override
	public String save() throws Exception {
		Result result;
		try {
			// 名称重复校验
			Role role = roleManager.findUniqueBy("name", model.getName());
            if (role != null && !role.getId().equals(model.getId())) {
            	result = new Result(Result.WARN,"名称为["+model.getName()+"]已存在,请修正!", "name");
                logger.debug(result.toString());
                Struts2Utils.renderText(result);
                return null;
            }

            // 编码重复校验
            if(StringUtils.isNotBlank(model.getCode())){
                Role checkRole = roleManager.findUniqueBy("code", model.getCode());
                if (checkRole != null && !checkRole.getId().equals(model.getId())) {
                    result = new Result(Result.WARN,"编码为["+model.getCode()+"]已存在,请修正!", "code");
                    logger.debug(result.toString());
                    Struts2Utils.renderText(result);
                    return null;
                }
            }

			roleManager.saveEntity(model);
            result = Result.successResult();
            logger.debug(result.toString());
			Struts2Utils.renderText(result);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

    /**
     * 设置资源 页面
     * @return
     * @throws Exception
     */
    public String resource() throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
        try {
            treeNodes = resourceManager.getResourceTree(null,true);
            String resourceComboboxData = JsonMapper.nonDefaultMapper().toJson(treeNodes);
            logger.debug(resourceComboboxData);
            Struts2Utils.getRequest().setAttribute("resourceComboboxData", resourceComboboxData);
        } catch (Exception e) {
            throw e;
        }
        return "resource";
    }
    //updateRoleResource()方法之前执行
    public void prepareUpdateRoleResource() throws Exception {
        super.prepareModel();
    }


    /**
     * 设置角色资源
     * @return
     * @throws Exception
     */
    public String updateRoleResource() throws Exception {
        Result result;
        try {
            //设置用户角色信息
            List<Resource> resourceList = Lists.newArrayList();
            for (Long resourceId : resourceIds) {
                Resource resource = resourceManager.loadById(resourceId);
                resourceList.add(resource);
            }
            model.setResources(resourceList);

            roleManager.saveEntity(model);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return "resource";
    }

    /**
     * 设置角色用户 页面
     * @return
     * @throws Exception
     */
    public String user() throws Exception{
        return "user";
    }

    /**
     * 二次绑定
     * @throws Exception
     */
    public void prepareUpdateRoleUser() throws Exception{
        super.prepareModel();
    }
    /**
     * 设置机构用户
     * @return
     * @throws Exception
     */
    public String updateRoleUser() throws Exception{
        Result result;
        try {
            //设置用户角色信息
            List<User> userList = Lists.newArrayList();
            for (Long userId : userIds) {
                User user = userManager.loadById(userId);
                userList.add(user);
            }
            model.setUsers(userList);

            roleManager.saveEntity(model);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


	/**
	 * 角色下拉框列表.
	 */
	public void combobox() throws Exception {
		try {
			List<Role> list = roleManager.getAll();
            List<Combobox> cList = Lists.newArrayList();
            
            //为combobox添加  "---全部---"、"---请选择---"
            if(!StringUtils.isBlank(selectType)){
            	SelectType s = SelectType.getSelectTypeValue(selectType);
            	if(s!=null){
            		Combobox selectCombobox = new Combobox("", s.getDescription());
            		cList.add(selectCombobox);
            	}
            }
            for(Role r:list){
                Combobox combobox = new Combobox(r.getId()+"", r.getName());
                cList.add(combobox);
            }
            Struts2Utils.renderJson(cList);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
