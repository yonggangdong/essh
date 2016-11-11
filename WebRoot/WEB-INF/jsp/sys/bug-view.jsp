<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div>
	<div align="center">
	    <h3 style="color:${color} ">${title}</h3>
	</div>
	<div align="right">
	   [ ${createUser} 发布于<fmt:formatDate value="${createTime}" type="both" /> ]
	</div>
	<hr>
	<div>${contentView}</div>
</div>