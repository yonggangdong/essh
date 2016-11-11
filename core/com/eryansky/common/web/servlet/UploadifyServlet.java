/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.servlet;

import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.web.utils.ServletUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
 
/**
 * Uploadify文件上传.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-5-16 下午2:45:55 
 * @version 1.0
 */
public class UploadifyServlet extends HttpServlet {
	 
	private static final long serialVersionUID = 1L;

	/**
	 * 实现多文件的同时上传
	 */ 
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String savePath = request.getSession().getServletContext().getRealPath("/") + SysConstants.getUploadDirectory() + "/";// 文件保存目录路径
		String saveUrl = "/" + SysConstants.getUploadDirectory() + "/";// 文件保存目录URL

		String contentDisposition = request.getHeader("Content-Disposition");// 如果是HTML5上传文件，那么这里有相应头的

		if (contentDisposition != null) {// HTML5拖拽上传文件
			Long fileSize = Long.valueOf(request.getHeader("Content-Length"));// 上传的文件大小
			String fileName = contentDisposition.substring(contentDisposition.lastIndexOf("filename=\""));// 文件名称
			fileName = fileName.substring(fileName.indexOf("\"") + 1);
			fileName = fileName.substring(0, fileName.indexOf("\""));

			ServletInputStream inputStream;
			try {
				inputStream = request.getInputStream();
			} catch (IOException e) {
				uploadError("上传文件出错！",response);
				return;
			}

			if (inputStream == null) {
				uploadError("您没有上传任何文件！",response);
				return;
			}

			if (fileSize > SysConstants.getUploadFileMaxSize()) {
				uploadError("上传文件超出限制大小！", fileName,response);
				return;
			}

			// 检查文件扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if (!Arrays.<String> asList(SysConstants.getUploadFileExts().split(",")).contains(fileExt)) {
				uploadError("上传文件扩展名是不允许的扩展名。\n只允许" + SysConstants.getUploadFileExts() + "格式！",response);
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
				uploadError("上传文件出错！",response);
				return;
			} catch (IOException e) {
				uploadError("上传文件出错！",response);
				return;
			}

			uploadSuccess(request.getContextPath() + saveUrl + newFileName, fileName, 0,response);// 文件上传成功

			return;
		}

		MultiPartRequestWrapper multiPartRequest = (MultiPartRequestWrapper) request;// 由于struts2上传文件时自动使用了request封装
		File[] files = multiPartRequest.getFiles(SysConstants.getUploadFieldName());// 上传的文件集合
		String[] fileNames = multiPartRequest.getFileNames(SysConstants.getUploadFieldName());// 上传文件名称集合

		if (files == null || files.length < 1) {
			uploadError("您没有上传任何文件！",response);
			return;
		}

		for (int i = 0; i < files.length; i++) {// 循环所有文件
			File file = files[i];// 上传的文件(临时文件)
			String fileName = fileNames[i];// 上传文件名

			if (file.length() > SysConstants.getUploadFileMaxSize()) {
				uploadError("上传文件超出限制大小！", fileName,response);
				return;
			}

			// 检查文件扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if (!Arrays.<String> asList(SysConstants.getUploadFileExts().split(",")).contains(fileExt)) {
				uploadError("上传文件扩展名是不允许的扩展名。\n只允许" + SysConstants.getUploadFileExts() + "格式！",response);
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
				uploadError("上传文件失败！", fileName,response);
				return;
			}

			uploadSuccess(request.getContextPath() + saveUrl + newFileName, fileName, i,response);// 文件上传成功

		}
	}
 
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	private void uploadError(String err, String msg,HttpServletResponse response) throws IOException {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("err", err);
		m.put("msg", msg);
		ServletUtils.renderText(m,response);
	}

	private void uploadError(String err,HttpServletResponse response) throws IOException {
		uploadError(err, "",response);
	}

	private void uploadSuccess(String newFileName, String fileName, int id,HttpServletResponse response) throws IOException {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("err", "");
		Map<String, Object> nm = new HashMap<String, Object>();
		nm.put("url", newFileName);
		nm.put("localfile", fileName);
		nm.put("id", id);
		m.put("msg", nm);
		ServletUtils.renderText(m,response);
	}
}