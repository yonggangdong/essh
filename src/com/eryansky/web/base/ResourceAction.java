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
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.base.Resource;
import com.eryansky.entity.base.state.ResourceType;
import com.eryansky.service.base.ResourceManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 资源权限Resource管理 Action层.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
public class ResourceAction extends StrutsAction<Resource> {

    /**
     * 上级资源类型
     */
    private Integer parentType;

	@Autowired
	private ResourceManager resourceManager;

	@Override
	public EntityManager<Resource, Long> getEntityManager() {
		return resourceManager;
	}

    @Override
    public String input() throws Exception {
        if(parentType == null && model.getParentResource() != null){
            parentType = model.getParentResource().getType();
        }
        return super.input();
    }

    /**
     * 删除.
     */
    @Override
    public String delete() throws Exception {
        Result result;
        try {
            List<Long> ids = Lists.newArrayList();
            ids.add(super.id);
            resourceManager.deleteByIds(ids);
            result = Result.successResult();
            logger.debug(result.toString());
            Struts2Utils.renderJson(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    public String treegrid() throws Exception {
        try {
            List<PropertyFilter> filters = Lists.newArrayList();
            // 自动构造属性过滤器
//            List<PropertyFilter> filters = HibernateWebUtils
//                    .buildPropertyFilters(Struts2Utils.getRequest());
            List<Resource> list = getEntityManager().find(filters,sort,order);
            Datagrid<Resource> dg = new Datagrid<Resource>(list.size(), list);
            Struts2Utils.renderJson(dg);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    /**
	 * 保存.
	 */
	public String save() throws Exception {
		Result result = null;
		try {
            model.setParentResource(null);
			// 名称重复校验
//			Resource resource = resourceManager.getByName(model.getName());
//			if (resource != null && !resource.getId().equals(model.getId())) {
//				result = new Result(Result.WARN, "名称为[" + model.getName()
//						+ "]已存在,请修正!", "name");
//				logger.debug(result.toString());
//				Struts2Utils.renderText(result);
//				return null;
//			}

			// 设置上级节点
			if (model.get_parentId() != null) {
				Resource parentResource = resourceManager.loadById(model.get_parentId());
				if(parentResource == null){
					logger.error("父级资源[{}]已被删除.",model.get_parentId());
					throw new ActionException("父级资源已被删除.");
				}
				model.setParentResource(parentResource);
			}
			
			if (model.getId() != null) {
				if (model.getId().equals(model.get_parentId())) {
					result = new Result(Result.ERROR, "[上级资源]不能与[资源名称]相同.",
							null);
					logger.debug(result.toString());
					Struts2Utils.renderText(result);
					return null;
				}
			}
			resourceManager.saveResource(model);
			result = Result.successResult();
			logger.debug(result.toString());
			Struts2Utils.renderText(result);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * 资源树.
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
			treeNodes = resourceManager.getResourceTree(null,true);
            List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
            Struts2Utils.renderJson(unionList);
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * 资源类型下拉列表.
     */
    public void resourceTypeCombobox() throws Exception {
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

            ResourceType parentResourceType = ResourceType.getResourceType(parentType);
            if(parentResourceType !=null){
                 if(parentResourceType.equals(ResourceType.menu)){
                     ResourceType[] rss = ResourceType.values();
                     for(int i=0;i<rss.length;i++){
                         Combobox combobox = new Combobox();
                         combobox.setValue(rss[i].getValue().toString());
                         combobox.setText(rss[i].getDescription());
                         cList.add(combobox);
                     }
                 }else if(parentResourceType.equals(ResourceType.function)){
                     Combobox combobox = new Combobox();
                     combobox.setValue(ResourceType.function.getValue().toString());
                     combobox.setText(ResourceType.function.getDescription());
                     cList.add(combobox);
                 }
            }else{
                ResourceType[] rss = ResourceType.values();
                for(int i=0;i<rss.length;i++){
                    Combobox combobox = new Combobox();
                    combobox.setValue(rss[i].getValue().toString());
                    combobox.setText(rss[i].getDescription());
                    cList.add(combobox);
                }
            }


            Struts2Utils.renderJson(cList);
        } catch (Exception e) {
            throw e;
        }
    }

	/**
	 * 父级资源下拉列表.
	 */
	@SuppressWarnings("unchecked")
	public void parentResource() throws Exception {
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
            treeNodes = resourceManager.getResourceTree(model.getId(),true);
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
			Integer maxSort = resourceManager.getMaxSort();
			result = new Result(Result.SUCCESS, null, maxSort);
			logger.debug(result.toString());
			Struts2Utils.renderJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

    public Integer getParentType() {
        return parentType;
    }

    public void setParentType(Integer parentType) {
        this.parentType = parentType;
    }
}
