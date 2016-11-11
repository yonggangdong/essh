/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.base;

import com.eryansky.common.exception.ActionException;
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
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.base.Organ;
import com.eryansky.entity.base.User;
import com.eryansky.entity.base.state.OrganType;
import com.eryansky.service.base.OrganManager;
import com.eryansky.service.base.UserManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 机构Organ管理 Action层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-09-09 下午21:36:24
 */
@SuppressWarnings("serial")
public class OrganAction extends StrutsAction<Organ> {

    /**
     * 机构用户
     */
    private List<Long> userIds;
    /**
     * 管理员用户ID
     */
    private Long managerUserId;
    /**
     * 机构名称或编码
     */
    private String nameOrCode;
    /**
     * 上级机构类型
     */
    private Integer parentOrganType;
    @Autowired
    private OrganManager organManager;
    @Autowired
    private UserManager userManager;

    @Override
    public EntityManager<Organ, Long> getEntityManager() {
        return organManager;
    }

    public String treegrid() throws Exception {
        try {
            List<PropertyFilter> filters = Lists.newArrayList();
            // 自动构造属性过滤器
//            List<PropertyFilter> filters = HibernateWebUtils
//                    .buildPropertyFilters(Struts2Utils.getRequest());
            List<Organ> list = getEntityManager().find(filters,sort,order);
            Datagrid<Organ> dg = new Datagrid<Organ>(list.size(), list);
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

                if(StringUtils.isNotBlank(nameOrCode)){
                    Criterion nameCriterion = Restrictions.like("name", nameOrCode, MatchMode.ANYWHERE);
                    Criterion codeCriterion = Restrictions.like("code",nameOrCode, MatchMode.ANYWHERE);
                    Criterion criterion1 = Restrictions.or(nameCriterion,codeCriterion);
                    criterion = Restrictions.or(inCriterion,criterion1) ;
                }else{
                    criterion =  inCriterion;
                }
                //合并查询条件
                criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
            }else{
                if(StringUtils.isNotBlank(nameOrCode)){
                    Criterion nameCriterion = Restrictions.like("name", nameOrCode, MatchMode.ANYWHERE);
                    Criterion codeCriterion = Restrictions.like("code", nameOrCode, MatchMode.ANYWHERE);
                    criterion = Restrictions.or(nameCriterion,codeCriterion);
                    //合并查询条件
                    criterions = (Criterion[]) ArrayUtils.add(criterions, 0, criterion);
                }
            }

            //分页查询
            Page<Organ> p = new Page<Organ>(rows);//分页对象
            p = organManager.findByCriteria(p, criterions);
            Datagrid<Organ> dg = new Datagrid<Organ>(p.getTotalCount(), p.getResult());
            Struts2Utils.renderJson(dg);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    @Override
    public String input() throws Exception {
        if(parentOrganType == null && model.getParentOrgan() != null){
            parentOrganType = model.getParentOrgan().getType();
        }
        return super.input();
    }

    /**
     * 保存.
     */
    public String save() throws Exception {
        Result result = null;
        try {
            model.setParentOrgan(null);
            // 名称重复校验
            Organ organ = organManager.getByName(model.getName());
            if (organ != null && !organ.getId().equals(model.getId())) {
                result = new Result(Result.WARN, "名称为[" + model.getName()
                        + "]已存在,请修正!", "name");
                logger.debug(result.toString());
                Struts2Utils.renderText(result);
                return null;
            }

            // 设置上级节点
            if (model.get_parentId() != null) {
                Organ parentOrgan = organManager.loadById(model.get_parentId());
                if(parentOrgan == null){
                    logger.error("父级机构[{}]已被删除.",model.get_parentId());
                    throw new ActionException("父级机构已被删除.");
                }
                model.setParentOrgan(parentOrgan);
            }

            //自关联校验
            if (model.getId() != null) {
                if (model.getId().equals(model.get_parentId())) {
                    result = new Result(Result.ERROR, "[上级机构]不能与[机构名称]相同.",
                            null);
                    logger.debug(result.toString());
                    Struts2Utils.renderText(result);
                    return null;
                }
            }
            organManager.saveOrgan(model);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 设置机构用户 页面
     * @return
     * @throws Exception
     */
    public String user() throws Exception{
        try {
            super.prepareModel();
            List<User> organUsers = model.getUsers();
            Datagrid<User> dg = new Datagrid<User>(organUsers.size(), organUsers);
            String managerUserCombogridData = JsonMapper.nonDefaultMapper().toJson(dg);
            logger.debug(managerUserCombogridData);
            Struts2Utils.getRequest().setAttribute("managerUserCombogridData", managerUserCombogridData);
        } catch (Exception e) {
            throw e;
        }
        return "user";
    }

    /**
     * 二次绑定
     * @throws Exception
     */
    public void prepareUpdateOrganUser() throws Exception{
         super.prepareModel();
    }

    /**
     * 设置机构用户
     * @return
     * @throws Exception
     */
    public String updateOrganUser() throws Exception{
        Result result;
        try {
            //设置机构用户
            List<User> userList = Lists.newArrayList();
            if(!Collections3.isEmpty(userIds)) {
                for (Long userId : userIds) {
                    User user = userManager.loadById(userId);
                    userList.add(user);
                }
            }
            model.setUsers(userList);
            model.setManagerUserId(managerUserId);
//
//            //判断主管是否在这个组织机构用户中
//            List<User> orgUsers = Lists.newArrayList();
//            if(model.getManagerUserId() != null){
//                User managerUser = userManager.loadById(model.getManagerUserId()) ;
//                if(Collections3.isEmpty(model.getUsers())){
//                    orgUsers.add(managerUser);
//                    model.setUsers(orgUsers);
//                }else{
//                    orgUsers =  model.getUsers();
//                    boolean isContain = false;
//                    for(User user:model.getUsers()){
//                        if(user.getId().equals(model.getManagerUserId())){
//                            isContain = true;
//                        }
//                    }
//                    if(!isContain){
//                        orgUsers.add(managerUser);
//                        model.setUsers(orgUsers);
//                    }
//                }
//            }
            organManager.saveOrgan(model);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 机构树.
     */
    public void tree() throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
        try {
            List<TreeNode> titleList = Lists.newArrayList();
            // 添加 "---全部---"、"---请选择---"
            if (!StringUtils.isBlank(selectType)) {
                SelectType s = SelectType.getSelectTypeValue(selectType);
                if (s != null) {
                    TreeNode selectTreeNode = new TreeNode("",
                            s.getDescription());
                    titleList.add(selectTreeNode);
                }
            }
            treeNodes = organManager.getOrganTree(null,true);
            List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
            Struts2Utils.renderJson(unionList);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 机构类型下拉列表.
     */
    public void organTypeCombobox() throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        try {
            //为combobox添加  "---全部---"、"---请选择---"
            if(!StringUtils.isBlank(selectType)){
                SelectType s = SelectType.getSelectTypeValue(selectType);
                if(s!=null){
                    Combobox selectCombobox = new Combobox("", s.getDescription());
                    cList.add(selectCombobox);
                }
            }

            OrganType _enumParentType = OrganType.getOrganType(parentOrganType);
            if(_enumParentType != null){
                if(_enumParentType.equals(OrganType.organ)){
                    OrganType[] rss = OrganType.values();
                    for(int i=0;i<rss.length;i++){
                        Combobox combobox = new Combobox();
                        combobox.setValue(rss[i].getValue().toString());
                        combobox.setText(rss[i].getDescription());
                        cList.add(combobox);
                    }
                } else if(_enumParentType.equals(OrganType.department)){
                    Combobox departmentCombobox = new Combobox(OrganType.department.getValue().toString(),OrganType.department.getDescription().toString());
                    Combobox groupCombobox = new Combobox(OrganType.group.getValue().toString(),OrganType.group.getDescription().toString());
                    cList.add(departmentCombobox);
                    cList.add(groupCombobox);
                }else if(_enumParentType.equals(OrganType.group)){
                    Combobox groupCombobox = new Combobox(OrganType.group.getValue().toString(),OrganType.group.getDescription().toString());
                    cList.add(groupCombobox);
                }
            }else{
                Combobox groupCombobox = new Combobox(OrganType.organ.getValue().toString(),OrganType.organ.getDescription().toString());
                cList.add(groupCombobox);
            }
            Struts2Utils.renderJson(cList);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 父级机构下拉列表.
     */
    @SuppressWarnings("unchecked")
    public void parentOrgan() throws Exception {
        prepareModel();
        List<TreeNode> treeNodes = Lists.newArrayList();
        try {
            List<TreeNode> titleList = Lists.newArrayList();
            // 添加 "---全部---"、"---请选择---"
            if (!StringUtils.isBlank(selectType)) {
                SelectType s = SelectType.getSelectTypeValue(selectType);
                if (s != null) {
                    TreeNode selectTreeNode = new TreeNode("",
                            s.getDescription());
                    titleList.add(selectTreeNode);
                }
            }
            treeNodes = organManager.getOrganTree(model.getId(),true);
            List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
            Struts2Utils.renderJson(unionList);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 排序最大值.
     */
    public void maxSort() throws Exception {
        Result result;
        try {
            Integer maxSort = organManager.getMaxSort();
            result = new Result(Result.SUCCESS, null, maxSort);
            logger.debug(result.toString());
            Struts2Utils.renderJson(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public void setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
    }

    public void setNameOrCode(String nameOrCode) {
        this.nameOrCode = nameOrCode;
    }

    public Integer getParentOrganType() {
        return parentOrganType;
    }

    public void setParentOrganType(Integer parentOrganType) {
        this.parentOrganType = parentOrganType;
    }
}
