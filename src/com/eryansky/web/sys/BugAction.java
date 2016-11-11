/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.sys;

import com.eryansky.common.excel.ExcelUtil;
import com.eryansky.common.excel.ExportExcel;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.utils.io.ClobUtil;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.common.web.utils.ServletUtils;
import com.eryansky.entity.sys.Bug;
import com.eryansky.entity.sys.Dictionary;
import com.eryansky.service.sys.BugManager;
import com.eryansky.service.sys.DictionaryManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletInputStream;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * bug管理Action层.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-27 下午8:02:39
 * 
 */
@SuppressWarnings("serial")
public class BugAction extends StrutsAction<Bug> {

	public final static String SSSION_SEARCH = "BUG_SEARCH";
	
	@Autowired
	private BugManager bugManager;
	@Autowired
	private DictionaryManager dictionaryManager;

	@Override
	public EntityManager<Bug, Long> getEntityManager() {
		return bugManager;
	}

	@Override
	public String save() throws Exception {
		Result result;
		try {
			// 名称重复校验
			Bug bug = bugManager.findUniqueBy("title", model.getTitle());
            if (bug != null && !bug.getId().equals(model.getId())) {
            	result = new Result(Result.WARN,"标题为["+model.getTitle()+"]已存在,请修正!", "title");
                logger.debug(result.toString());
                Struts2Utils.renderText(result);
                return null;
            }

            bugManager.saveEntity(model);
            result = Result.successResult();
            logger.debug(result.toString());
			Struts2Utils.renderText(result);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	@Override
	public String datagrid() throws Exception {
		try {
			// 自动构造属性过滤器
			List<PropertyFilter> filters = HibernateWebUtils
					.buildPropertyFilters(Struts2Utils.getRequest());
			Page<Bug> p = getEntityManager().find(page, rows, sort, order,
					filters);
			
			//转换设置bug类型名称
			if(p.getResult() != null){
				for(Bug bug:p.getResult()){
					Dictionary dictionary = dictionaryManager.getByCode(bug.getType());
					if(dictionary!=null){
						bug.setTypeName(dictionary.getName());
					}else{
						logger.warn("[{}]未设置类型.",bug.getTitle());
					}
				}
			}
			Datagrid<Bug> dg = new Datagrid<Bug>(p.getTotalCount(), p.getResult());
			Struts2Utils.renderJson(dg);
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * Excel导入
	 */
	@SuppressWarnings("unchecked")
	public void importExcel() throws Exception {
		Result result = null;
        
        MultiPartRequestWrapper multiPartRequest = (MultiPartRequestWrapper) Struts2Utils.getRequest();// 由于struts2上传文件时自动使用了request封装
		File[] files = multiPartRequest.getFiles("filedata");// 上传的文件集合
		
        List<Bug> bugs = Lists.newArrayList();
        List<Bug> bugs_new = Lists.newArrayList();
		try {
			if(files != null && files.length >0){
				InputStream inputStream= new FileInputStream(files[0]);
				bugs = (List<Bug>) ExcelUtil.importExcelByIs(inputStream, Bug.class);
			    if(bugs != null && bugs.size() >0){
			    	for (Bug bug : bugs) {
						//重复数据校验
						Bug checkBug = bugManager.findUniqueBy("title",bug.getTitle());
						if(checkBug == null){
							bug.setVersion(0);
                            bug.setContent(ClobUtil.getClob(bug.getContentView()));
                            Dictionary dictionary = dictionaryManager.findUniqueBy("name", bug.getTypeName());
							if(dictionary !=null){
								bug.setType(dictionary.getCode());
							}else{
								logger.warn("无法识别[{}].",bug.getTypeName());
							}
							bugs_new.add(bug);
						}else{
							logger.warn("[{}]已存在.",bug.getTitle());
						}
					}
			    }
				bugManager.saveOrUpdate(bugs_new);
				result = new Result(Result.SUCCESS,"已导入"+bugs_new.size()+"条数据.",null);
			}else{
				result = new Result(Result.WARN,"未上传任何文件.",null);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			result = new Result(Result.ERROR,"文件导入失败",null);           
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(Result.ERROR,"文件格式不正确，导入失败",null);
		}finally{
			Struts2Utils.renderText(result);
		}
	}
    /**
     * Excel导出
     */
	@SuppressWarnings("unchecked")
	public void exportExcel() throws Exception {
		// 生成提示信息，
		final String fileName = "内容信息.xls";
		OutputStream outStream = null;
		try {
			//设置文件类型
			Struts2Utils.getResponse().setContentType(ServletUtils.EXCEL_TYPE);
			//设置下载弹出对话框
			ServletUtils.setFileDownloadHeader(Struts2Utils.getRequest(),Struts2Utils.getResponse(), fileName);
			//从session中获取查询参数
			List<PropertyFilter> sessionFilters = (List<PropertyFilter>) Struts2Utils.getSessionAttribute(SSSION_SEARCH);
			List<Bug> bugs = Lists.newArrayList();
			if(sessionFilters != null){
				bugs = bugManager.find(sessionFilters,"orderNo",Page.ASC);
			}else{
				bugs = bugManager.getAll("id",Page.ASC);
			}
			//设置bug类型（此处由于Bug未直接关联Dictionary所以被动设置类型名称）
			for(Bug bug:bugs){
				Dictionary dictionary = dictionaryManager.getByCode(bug.getType());
				String dicStringName = "";
				if(dictionary != null){
					dicStringName = dictionary.getName();
				}
				bug.setTypeName(dicStringName);
			}
			HSSFWorkbook workbook = new ExportExcel<Bug>().exportExcel("导出信息",
					Bug.class, bugs);
			outStream = Struts2Utils.getResponse().getOutputStream();
			workbook.write(outStream);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.flush();
				outStream.close();
			} catch (IOException e) {

			}
		}
	}
	
	
	
	/**
	 * 文件上传
	 */
	public void upload() {
		String savePath = ServletActionContext.getServletContext().getRealPath("/") + SysConstants.getUploadDirectory() + "/";// 文件保存目录路径
		String saveUrl = "/" + SysConstants.getUploadDirectory() + "/";// 文件保存目录URL

		String contentDisposition = ServletActionContext.getRequest().getHeader("Content-Disposition");// 如果是HTML5上传文件，那么这里有相应头的

		if (contentDisposition != null) {// HTML5拖拽上传文件
			Long fileSize = Long.valueOf(ServletActionContext.getRequest().getHeader("Content-Length"));// 上传的文件大小
			String fileName = contentDisposition.substring(contentDisposition.lastIndexOf("filename=\""));// 文件名称
			fileName = fileName.substring(fileName.indexOf("\"") + 1);
			fileName = fileName.substring(0, fileName.indexOf("\""));

			ServletInputStream inputStream;
			try {
				inputStream = ServletActionContext.getRequest().getInputStream();
			} catch (IOException e) {
				uploadError("上传文件出错！");
				return;
			}

			if (inputStream == null) {
				uploadError("您没有上传任何文件！");
				return;
			}

			if (fileSize > SysConstants.getUploadFileMaxSize()) {
				uploadError("上传文件超出限制大小！", fileName);
				return;
			}

			// 检查文件扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if (!Arrays.<String> asList(SysConstants.getUploadFileExts().split(",")).contains(fileExt)) {
				uploadError("上传文件扩展名是不允许的扩展名。\n只允许" + SysConstants.getUploadFileExts() + "格式！");
				return;
			}

			savePath += fileExt + "/";
			saveUrl += fileExt + "/";

			SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
			SimpleDateFormat monthDf = new SimpleDateFormat("MM");
			SimpleDateFormat dateDf = new SimpleDateFormat("dd");
			Date date = new Date();
			String ymd = yearDf.format(date) + "/" + monthDf.format(date) + "/" + dateDf.format(date) + "/";
			savePath += ymd;
			saveUrl += ymd;

			File uploadDir = new File(savePath);// 创建要上传文件到指定的目录
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;// 新的文件名称
			File uploadedFile = new File(savePath, newFileName);

			try {
				FileCopyUtils.copy(inputStream, new FileOutputStream(uploadedFile));
			} catch (FileNotFoundException e) {
				uploadError("上传文件出错！");
				return;
			} catch (IOException e) {
				uploadError("上传文件出错！");
				return;
			}

			uploadSuccess(ServletActionContext.getRequest().getContextPath() + saveUrl + newFileName, fileName, 0);// 文件上传成功

			return;
		}

		MultiPartRequestWrapper multiPartRequest = (MultiPartRequestWrapper) ServletActionContext.getRequest();// 由于struts2上传文件时自动使用了request封装
		File[] files = multiPartRequest.getFiles(SysConstants.getUploadFieldName());// 上传的文件集合
		String[] fileNames = multiPartRequest.getFileNames(SysConstants.getUploadFieldName());// 上传文件名称集合

		if (files == null || files.length < 1) {
			uploadError("您没有上传任何文件！");
			return;
		}

		for (int i = 0; i < files.length; i++) {// 循环所有文件
			File file = files[i];// 上传的文件(临时文件)
			String fileName = fileNames[i];// 上传文件名

			if (file.length() > SysConstants.getUploadFileMaxSize()) {
				uploadError("上传文件超出限制大小！", fileName);
				return;
			}

			// 检查文件扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if (!Arrays.<String> asList(SysConstants.getUploadFileExts().split(",")).contains(fileExt)) {
				uploadError("上传文件扩展名是不允许的扩展名。\n只允许" + SysConstants.getUploadFileExts() + "格式！");
				return;
			}

			savePath += fileExt + "/";
			saveUrl += fileExt + "/";

			SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
			SimpleDateFormat monthDf = new SimpleDateFormat("MM");
			SimpleDateFormat dateDf = new SimpleDateFormat("dd");
			Date date = new Date();
			String ymd = yearDf.format(date) + "/" + monthDf.format(date) + "/" + dateDf.format(date) + "/";
			savePath += ymd;
			saveUrl += ymd;

			File uploadDir = new File(savePath);// 创建要上传文件到指定的目录
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;// 新的文件名称
			File uploadedFile = new File(savePath, newFileName);

			try {
				FileCopyUtils.copy(file, uploadedFile);// 利用spring的文件工具上传
			} catch (Exception e) {
				uploadError("上传文件失败！", fileName);
				return;
			}

			uploadSuccess(ServletActionContext.getRequest().getContextPath() + saveUrl + newFileName, fileName, i);// 文件上传成功

		}

	}

	private void uploadError(String err, String msg) {
		Map<String, Object> m = Maps.newHashMap();
		m.put("err", err);
		m.put("msg", msg);
		Struts2Utils.renderText(m);
	}

	private void uploadError(String err) {
		uploadError(err, "");
	}

	private void uploadSuccess(String newFileName, String fileName, int id) {
		Map<String, Object> m = Maps.newHashMap();
		m.put("err", "");
		Map<String, Object> nm = Maps.newHashMap();
		nm.put("url", newFileName);
		nm.put("localfile", fileName);
		nm.put("id", id);
		m.put("msg", nm);
		Struts2Utils.renderText(m);
	}
}
