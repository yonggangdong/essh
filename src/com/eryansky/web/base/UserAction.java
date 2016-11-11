/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.base;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.base.Organ;
import com.eryansky.entity.base.Resource;
import com.eryansky.entity.base.Role;
import com.eryansky.entity.base.User;
import com.eryansky.entity.base.state.SexType;
import com.eryansky.service.base.OrganManager;
import com.eryansky.service.base.ResourceManager;
import com.eryansky.service.base.RoleManager;
import com.eryansky.service.base.UserManager;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.SystemException;
import java.util.List;

/**
 * 用户User管理 Action层.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-21 上午12:20:13 
 *
 */
@SuppressWarnings("serial")
public class UserAction extends StrutsAction<User> {

	private String upateOperate;// 修改密码操作码 1:顶部 0:列表
	private String newPassword;// 新密码
    /**
     * 查询条件 组织机构ID
     */
    private Long organId;
    /**
     * 组织机构ID集合
     */
    private List<Long> organIds = Lists.newArrayList();
    /**
     * 查询条件 登录名或姓名
     */
    private String loginNameOrName;
    /**
     * 默认组织机构ID
     */
    private Long defaultOrganId;

	@Autowired
	private UserManager userManager;
    @Autowired
    private OrganManager organManager;
    @Autowired
	private RoleManager roleManager;
    @Autowired
    private ResourceManager resourceManager;
    //用户关连角色ID集合
	private List<Long> roleIds = Lists.newArrayList();

    //用户关连资源ID集合
    private List<Long> resourceIds = Lists.newArrayList();

	@Override
	public EntityManager<User, Long> getEntityManager() {
		return userManager;
	}

    /**
     * 自定义查询
     * @return
     * @throws Exception
     */
    public String userDatagrid() throws Exception {
        try {
            Page<User> p = userManager.getUsersByQuery(organId, loginNameOrName, page, rows, sort, order);
            Datagrid<User> dg = new Datagrid<User>(p.getTotalCount(), p.getResult());
            Struts2Utils.renderJson(dg);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 用户combogrid所有
     * @return
     * @throws Exception
     */
    public String combogridAll() throws Exception {
        try {
            List<PropertyFilter> filters = Lists.newArrayList();
            filters.add(new PropertyFilter("EQI_status",StatusState.normal.getValue().toString()));
            List<User> users = userManager.find(filters,"id","asc");
            Datagrid<User> dg = new Datagrid<User>(users.size(), users);
            Struts2Utils.renderJson(dg);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * combogrid
     * @return
     * @throws Exception
     */
    public String combogrid() throws Exception {
        try {
            Criterion statusCriterion = Restrictions.eq("status", StatusState.normal.getValue());
            Criterion[] criterions = new Criterion[0];
            criterions = (Criterion[]) ArrayUtils.add(criterions, 0, statusCriterion);
            Criterion criterion = null;
            if(!Collections3.isEmpty(ids)){
                //in条件
                Criterion inCriterion= Restrictions.in("id", ids);

                if(StringUtils.isNotBlank(loginNameOrName)){
                    Criterion loginNameCriterion = Restrictions.like("loginName", loginNameOrName, MatchMode.ANYWHERE);
                    Criterion nameCriterion = Restrictions.like("name",loginNameOrName, MatchMode.ANYWHERE);
                    Criterion criterion1 = Restrictions.or(loginNameCriterion,nameCriterion);
                    criterion = Restrictions.or(inCriterion,criterion1) ;
                }else{
                    criterion =  inCriterion;
                }
                //合并查询条件
                criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
            }else{
                if(StringUtils.isNotBlank(loginNameOrName)){
                    Criterion loginNameCriterion = Restrictions.like("loginName", loginNameOrName, MatchMode.ANYWHERE);
                    Criterion nameCriterion = Restrictions.like("name",loginNameOrName, MatchMode.ANYWHERE);
                    criterion = Restrictions.or(loginNameCriterion,nameCriterion);
                    //合并查询条件
                    criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
                }
            }

            //分页查询
            Page<User> p = new Page<User>(rows);//分页对象
            p = userManager.findByCriteria(p, criterions);
            Datagrid<User> dg = new Datagrid<User>(p.getTotalCount(), p.getResult());
            Struts2Utils.renderJson(dg);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 删除.
     */
    @Override
    public String remove() throws Exception {
        Result result;
        try {
            userManager.deleteByIds(ids);
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
		Result result = null;
		try {
			 // 名称重复校验
			User user = userManager.getUserByLoginName(model.getLoginName());
            if (user != null && !user.getId().equals(model.getId())) {
            	result = new Result(Result.WARN,"登录名为["+model.getLoginName()+"]已存在,请修正!", "loginName");
                logger.debug(result.toString());
                Struts2Utils.renderText(result);
                return null;
            }

            if (model.getId() == null) {// 新增
            	model.setPassword(Encrypt.e(model.getPassword()));
            } else {// 修改
				User superUser = userManager.getSuperUser();
				User sessionUser = userManager.getCurrentUser();
				if (!sessionUser.getId().equals(superUser.getId())) {
					throw new SystemException("超级用户信息仅允许自己修改!");
				}
            }
            userManager.saveEntity(model);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

    /**
     * 修改用户密码页面.
     */
    public String password() throws Exception {
        return "password";

    }
    //调用updateUserPassword()方法之前执行
    public void prepareUpdateUserPassword() throws Exception {
        model = new User();
        model.setId(super.id);
    }

    /**
     * 修改用户密码.
     * <br>参数upateOperate 需要密码"1" 不需要密码"0".
     * @see UserAction.prepareUpdateUserPassword()
     */
    public String updateUserPassword() throws Exception {
        Result result;
        try {
            if (!StringUtils.isEmpty(upateOperate)) {
                User user = userManager.loadById(model.getId());
                if (user != null) {
                    boolean isCheck = true;
                    //需要输入原始密码
                    if (AppConstants.USER_UPDATE_PASSWORD_YES.equals(upateOperate)) {
                        String originalPassword = user.getPassword(); //数据库存储的原始密码
                        String pagePassword = model.getPassword(); //页面输入的原始密码（未加密）
                        if (!originalPassword.equals(Encrypt.e(pagePassword))) {
                            isCheck = false;
                        }
                    }
                    //不需要输入原始密码
                    if (AppConstants.USER_UPDATE_PASSWORD_NO.equals(upateOperate)) {
                        isCheck = true;
                    }
                    if (isCheck) {
                        user.setPassword(Encrypt.e(newPassword));
                        userManager.saveEntity(user);
                        result = Result.successResult();
                    } else {
                        result = new Result(Result.WARN, "原始密码输入错误.","password");
                    }
                } else {
                    result = new Result(Result.ERROR,"修改的用户不存在或已被删除.", null);
                }
            }else{
                result = Result.errorResult();
                logger.warn("请求参数错误,未设置参数[upateOperate].");
            }
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }



    /**
     * 修改用户角色页面.
     */
    public String role() throws Exception {
        return "role";
    }
    //调用updateUserRole()方法之前执行
    public void prepareUpdateUserRole() throws Exception {
        super.prepareModel();
    }
    /**
     * 修改用户角色.
     */
    public void updateUserRole() throws Exception {
        Result result = null;
        try {
            List<Role> rs = Lists.newArrayList();
            for (Long id : roleIds) {
                Role role = roleManager.loadById(id);
                rs.add(role);
            }
            model.setRoles(rs);
            userManager.saveEntity(model);
            result = Result.successResult();
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 设置组织机构页面.
     */
    public String organ() throws Exception {
        super.prepareModel();
        //设置默认组织机构初始值
        List<Combobox> defaultOrganCombobox = Lists.newArrayList();
        if(model.getId() != null){
            List<Organ> organs = model.getOrgans();
            Combobox combobox;
            if(!Collections3.isEmpty(organs)){
                for(Organ organ:organs){
                    combobox = new Combobox(organ.getId().toString(),organ.getName());
                    defaultOrganCombobox.add(combobox);
                }
            }
        }
        String defaultOrganComboboxData = JsonMapper.nonDefaultMapper().toJson(defaultOrganCombobox);
        logger.debug(defaultOrganComboboxData);
        Struts2Utils.getRequest().setAttribute("defaultOrganComboboxData",defaultOrganComboboxData);
        return "organ";
    }
    //调用updateUserOrgan()方法之前执行
    public void prepareUpdateUserOrgan() throws Exception {
        super.prepareModel();
    }
    /**
     * 设置用户组织机构.
     */
    public void updateUserOrgan() throws Exception {
        Result result = null;
        try {
            //绑定组织机构
            model.setOrgans(null);
            List<Organ> organs = Lists.newArrayList();
            for(Long organId:organIds){
                Organ organ = organManager.loadById(organId);
                organs.add(organ);
            }
            model.setOrgans(organs);

            //绑定默认组织机构
            model.setDefaultOrgan(null);
            Organ defaultOrgan = null;
            if(defaultOrganId !=null){
                defaultOrgan = organManager.loadById(model.getDefaultOrganId());
            }
            model.setDefaultOrgan(defaultOrgan);

            userManager.saveEntity(model);
            result = Result.successResult();
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 修改用户资源页面.
     */
    public String resource() throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
        try {
            treeNodes = resourceManager.getResourceTree(null,true);
            String resourceComboboxData =  JsonMapper.nonDefaultMapper().toJson(treeNodes);
            logger.debug(resourceComboboxData);
            Struts2Utils.getRequest().setAttribute("resourceComboboxData", resourceComboboxData);
        } catch (Exception e) {
            throw e;
        }
        return "resource";
    }
    //updateUserResource()方法之前执行
    public void prepareUpdateUserResource() throws Exception {
        super.prepareModel();
    }
    /**
     * 修改用户资源.
     */
    public void updateUserResource() throws Exception {
        Result result = null;
        try {
            List<Resource> rs = Lists.newArrayList();
            for (Long id : resourceIds) {
                Resource resource = resourceManager.loadById(id);
                rs.add(resource);
            }
            model.setResources(rs);
            userManager.saveEntity(model);
            result = Result.successResult();
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 性别下拉框
     * @throws Exception
     */
    public void sexTypeCombobox() throws Exception{
        try {
            List<Combobox> cList = Lists.newArrayList();

            //为combobox添加  "---全部---"、"---请选择---"
            if(!StringUtils.isBlank(selectType)){
                SelectType s = SelectType.getSelectTypeValue(selectType);
                if(s!=null){
                    Combobox selectCombobox = new Combobox("", s.getDescription());
                    cList.add(selectCombobox);
                }
            }
            SexType[] _enums = SexType.values();
            for(int i=0;i<_enums.length;i++){
                Combobox combobox = new Combobox(_enums[i].getValue().toString(), _enums[i].getDescription());
                cList.add(combobox);
            }
            Struts2Utils.renderJson(cList);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setUpateOperate(String upateOperate) {
		this.upateOperate = upateOperate;
	}
	
	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public void setLoginNameOrName(String loginNameOrName) {
        this.loginNameOrName = loginNameOrName;
    }

    public void setDefaultOrganId(Long defaultOrganId) {
        this.defaultOrganId = defaultOrganId;
    }

    public void setOrganIds(List<Long> organIds) {
        this.organIds = organIds;
    }
}
