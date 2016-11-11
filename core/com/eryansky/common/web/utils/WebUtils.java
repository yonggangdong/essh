/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

/**
 * Http与Servlet工具类.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class WebUtils {

	public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

	/**
	 * 设置客户端缓存过期时间 Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response,
			long expiresSeconds) {
		// Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis()
				+ expiresSeconds * 1000);
		// Http 1.1 header
		response.setHeader("Cache-Control", "max-age=" + expiresSeconds);
	}

	/**
	 * 设置客户端无缓存Header.
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 0);
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache");
	}

	/**
	 * 设置LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response,
			long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

	/**
	 * 设置Etag Header.
	 */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}

	/**
	 * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
	 * <p/>
	 * 如果无修改, checkIfModify返回false ,设置304 not modify status.
	 * 
	 * @param lastModified
	 *            内容的最后修改时间.
	 */
	public static boolean checkIfModifiedSince(HttpServletRequest request,
			HttpServletResponse response, long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

	/**
	 * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
	 * <p/>
	 * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
	 * 
	 * @param etag
	 *            内容的ETag.
	 */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request,
			HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(
						headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查浏览器客户端是否支持gzip编码.
	 */
	public static boolean checkAccetptGzip(HttpServletRequest request) {
		// Http1.1 header
		String acceptEncoding = request.getHeader("Accept-Encoding");

		if (StringUtils.contains(acceptEncoding, "gzip")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置Gzip Header并返回GZIPOutputStream.
	 */
	public static OutputStream buildGzipOutputStream(
			HttpServletResponse response) throws IOException {
		response.setHeader("Content-Encoding", "gzip");
		response.setHeader("Vary", "Accept-Encoding");
		return new GZIPOutputStream(response.getOutputStream());
	}

	/**
	 * 设置让浏览器弹出下载对话框的Header.
	 * 
	 * @param fileName
	 *            下载后的文件名.
	 */
	public static void setDownloadableHeader(HttpServletResponse response,
			String fileName) {
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");
	}

	/**
	 * 取得带相同前缀的Request Parameters.
	 * <p/>
	 * 返回的结果Parameter名已去除前缀.
	 */
	public static Map<String, Object> getParametersStartingWith(
			HttpServletRequest request, String prefix) {
		return org.springframework.web.util.WebUtils.getParametersStartingWith(
				request, prefix);
	}

	/**
	 * 判断请求是否是Ajax请求.
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		if (header != null && "XMLHttpRequest".equals(header)) {
			return true;
		} else {
			return false;
		}
	}
}
