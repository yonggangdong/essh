﻿<%@ page import="com.eryansky.common.utils.browser.BrowserType" %>
<%@ page import="com.eryansky.common.utils.browser.BrowserUtils" %>
<%
    String ctx = request.getContextPath();
    BrowserType browserType = BrowserUtils.getBrowserType(request);
//    String browser =  BrowserUtils.checkBrowse(request);
//    boolean isIE = BrowserUtils.isIE(request);
//    if (browserType != null
//            && browserType.equals(BrowserType.IE11)) {  //降级到IE10
//        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=10;chrome=1\" /> ");
//    } else {
//        out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EDGE;chrome=1\" /> ");
//    }
%>
<c:set var="ev" value="1.3.6" />
<meta http-equiv="X-UA-Compatible" content="IE=EDGE;chrome=1" />
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<meta http-equiv="Cache-Control" content="no-store"/>
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Expires" content="0"/>
<meta name="author" content="尔演&Eryan" />
<link rel="shortcut icon" href="${ctx}/img/favicon.ico" />
<script type="text/javascript" charset="utf-8">
    var ctx = "${ctx}";
</script>
<link rel="stylesheet" type="text/css" href="${ctx}/css/default.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/form_style.css" />
<%-- 引入jQuery --%>
<%
    if (browserType != null
            && (browserType.equals(BrowserType.IE11) || browserType.equals(BrowserType.IE10) || browserType.equals(BrowserType.IE9)
            || browserType.equals(BrowserType.Chrome) || browserType.equals(BrowserType.Firefox)
            || browserType.equals(BrowserType.Safari))) {
        out.println("<script type='text/javascript' src='" + ctx + "/js/jquery/jquery-2.1.0.min.js' charset='utf-8'></script>");
    } else {
        out.println("<script type='text/javascript' src='" + ctx + "/js/jquery/jquery-1.10.2.min.js' charset='utf-8'></script>");
        out.println("<script type='text/javascript' src='" + ctx + "/js/jquery/jquery-migrate-1.2.1.min.js' charset='utf-8'></script>");
    }
%>
<%-- jQuery Cookie插件 --%>
<script type="text/javascript" src="${ctx}/js/jquery/jquery.cookie.js" charset="utf-8"></script>

<link id="easyuiTheme" rel="stylesheet" type="text/css" href="${ctx}/js/jquery/easyui-${ev}/themes/<c:out value="${cookie.easyuiThemeName.value}" default="bootstrap"/>/easyui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/jquery/easyui-${ev}/themes/default/my97.css" />

<link rel="stylesheet" type="text/css" href="${ctx}/js/jquery/easyui-${ev}/themes/icon.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/jquery/easyui-${ev}/portal/portal.css">
<script type="text/javascript" src="${ctx}/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
<script type="text/javascript" src="${ctx}/js/jquery/easyui-${ev}/jquery.easyui.mine.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${ctx}/js/jquery/easyui-${ev}/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript" src="${ctx}/js/jquery/easyui-${ev}/jquery.easyui.my97.js" charset="utf-8"></script>

<script type="text/javascript" src="${ctx}/js/jquery/easyui-${ev}/portal/jquery.portal.js" charset="utf-8"></script>

<%-- jQuery方法扩展 --%>
<script type="text/javascript" src="${ctx}/js/jquery/jquery-extend.js" charset="utf-8"></script>
<%-- easyui扩展 --%>
<script type="text/javascript" src="${ctx}/js/easyui-extend.js" charset="utf-8"></script>
<!-- 屏蔽键盘等事件 -->
<%-- 
<script type="text/javascript" src="${ctx}/js/prohibit.js" charset="utf-8"></script> 
--%>
<%-- easyui自定义表单校验扩展 --%>
<script type="text/javascript" src="${ctx}/js/validatebox-extend.js" charset="utf-8"></script>
<%-- easyui后台异步校验 --%>
<script type="text/javascript" src="${ctx}/js/validatebox-ajax.js" charset="utf-8"></script>
