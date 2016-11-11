/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.sys;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.sys.Dictionary;
import com.eryansky.service.sys.DictionaryManager;
import com.eryansky.service.sys.DictionaryTypeManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 数据字典Dictionary管理 Action层.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
public class DictionaryAction extends StrutsAction<Dictionary> {

	@Autowired
	private DictionaryManager dictionaryManager;
	@Autowired
	private DictionaryTypeManager dictionaryTypeManager;
	/**
	 * 数据字典类型.
	 */
	private String dictionaryTypeCode;
	/**
	 * 父级数据字典编号.
	 */
	private String parentDictionaryCode;

	@Override
	public EntityManager<Dictionary, Long> getEntityManager() {
		return dictionaryManager;
	}

	/**
	 * 保存
	 */
	public String save() throws Exception {
		Result result = null;
		try {
			// 名称是否重复校验
			Dictionary dictionaryType = dictionaryManager.findUniqueBy(
					"name", model.getName());
			if (dictionaryType != null
					&& !dictionaryType.getId().equals(model.getId())) {
				result = new Result(Result.WARN, "名称为[" + model.getName()
						+ "]已存在,请修正!", "name");
				logger.debug(result.toString());
				Struts2Utils.renderText(result);
				return null;
			}

			// 编码是否重复校验
			Dictionary dictionaryType2 = dictionaryManager.getByCode(model
					.getCode());
			if (dictionaryType2 != null
					&& !dictionaryType2.getId().equals(model.getId())) {
				result = new Result(Result.WARN, "编码为[" + model.getCode()
						+ "]已存在,请修正!", "code");
				logger.debug(result.toString());
				Struts2Utils.renderText(result);
				return null;
			}

			// 设置上级节点
			if (!StringUtils.isEmpty(parentDictionaryCode)) {
				Dictionary parentDictionary = dictionaryManager.getByCode(parentDictionaryCode);
				if(parentDictionary == null){
					logger.error("上级数据字典[{}]已被删除.",parentDictionaryCode);
					throw new ActionException("上级数据字典已被删除.");
				}
				model.setParentDictionary(parentDictionary);
			}else {
				model.setParentDictionary(null);
			}

			// 设置字典类型
			if (!StringUtils.isEmpty(model.getDictionaryTypeCode())) {
				model.setDictionaryType(dictionaryTypeManager.getByCode(model
						.getDictionaryTypeCode()));
			}else{
				logger.error("字典类型为空.");
				throw new ActionException("字典类型为空.");
			}

			dictionaryManager.saveEntity(model);
			result = Result.successResult();
			logger.debug(result.toString());
			Struts2Utils.renderText(result);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 在combotree()前执行二次绑定.
	 * @throws Exception
	 */
	public void prepareCombotree() throws Exception {
		model = new Dictionary();
	}

	/**
	 * combotree下拉列表数据.
	 */
	@SuppressWarnings("unchecked")
	public void combotree() throws Exception {
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
			List<TreeNode> treeNodes = dictionaryManager
					.getByDictionaryTypeCode(model,
							model.getDictionaryTypeCode(), model.getId(), true);

			List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
			Struts2Utils.renderJson(unionList);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * combobox下拉列表框数据.
	 */
	@SuppressWarnings("unchecked")
	public void combobox() throws Exception {
		try {
			List<Combobox> titleList = Lists.newArrayList();
			// 为combobox添加 "---全部---"、"---请选择---"
			if (!StringUtils.isBlank(selectType)) {
				SelectType s = SelectType.getSelectTypeValue(selectType);
				if (s != null) {
					Combobox selectCombobox = new Combobox("",
							s.getDescription());
					titleList.add(selectCombobox);
				}
			}

			List<Combobox> cList = dictionaryManager
					.getByDictionaryTypeCode(dictionaryTypeCode);
			List<Combobox> unionList = ListUtils.union(titleList, cList);
			Struts2Utils.renderJson(unionList);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 排序最大值.
	 */
	public void maxSort() throws Exception {
		try {
			Integer maxSort = dictionaryManager.getMaxSort();
			Result result = new Result(Result.SUCCESS, null, maxSort);
			Struts2Utils.renderJson(result);
		} catch (Exception e) {
			throw e;
		}
	}

	public void setDictionaryTypeCode(String dictionaryTypeCode) {
		this.dictionaryTypeCode = dictionaryTypeCode;
	}

	public void setParentDictionaryCode(String parentDictionaryCode) {
		this.parentDictionaryCode = parentDictionaryCode;
	}
	
	
}
