<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>用户登录</title>
    <%@ include file="/common/meta.jsp"%>
    <script type="text/javascript">
        var loginForm;
        var login_linkbutton;
        var $loginName,$password,$rememberPassword,$autoLogin;
        $(function(){
            $loginName = $("#loginName");
            $password = $("#password");
            $rememberPassword = $("#rememberPassword");
            $autoLogin = $("#autoLogin");
            loginForm = $('#loginForm').form();
            //refreshCheckCode();

            $rememberPassword.prop("checked", "${cookie.rememberPassword.value}" == "" ? false : true);
            $autoLogin.prop("checked", "${cookie.autoLogin.value}" == "" ? false : true);

            $loginName.val("${cookie.loginName.value}");
            if("${cookie.rememberPassword.value}" != ""){
                $password.val("${cookie.password.value}");
            }
            loginForm.form("validate");


            if("${cookie.autoLogin.value}" != ""){
                login();
            }else{
                $loginName.focus();
            }

            $rememberPassword.click(function(){
                var checked = $(this).prop('checked');
                if(checked){
                    $.cookie('password', $password.val(), {
                        expires : 7
                    });
                    $.cookie('rememberPassword', checked, {
                        expires : 7
                    });
                }else{
                    $.cookie('password', "", {
                        expires : 7
                    });
                    $.cookie('rememberPassword', "", {
                        expires : 7
                    });
                }
            });
            $autoLogin.click(function(){
                var checked = $(this).prop('checked');
                if(checked){
                    $.cookie('autoLogin', checked, {
                        expires : 7
                    });
                    $rememberPassword.prop('checked',checked);
                    $.cookie('rememberPassword', checked, {
                        expires : 7
                    });
                }else{
                    $.cookie('autoLogin', "", {
                        expires : 7
                    });
                }
            });
        });
        //刷新验证码
        function refreshCheckCode() {
            //加上随机时间 防止IE浏览器不请求数据
            var url = '${ctx}/servlet/ValidateCodeServlet?'+ new Date().getTime();
            $('#validateCode_img').attr('src',url);
        }
        // 登录
        function login() {
            $.cookie('loginName', $loginName.val(), {
                expires : 7
            });
            if($rememberPassword.prop("checked")){
                $.cookie('password', $password.val(), {
                    expires : 7
                });
            }
            if(loginForm.form('validate')){
                login_linkbutton = $('#login_linkbutton').linkbutton({
                    text:'正在登录...' ,
                    disabled:true
                });
                var cookieThemeType = "${cookie.themeType.value}"; //cookie初访的登录管理界面类型
                $.post('${ctx}/login!login.action?theme='+cookieThemeType,$.serializeObject(loginForm), function(data) {
                    if (data.code ==1){
                        window.location = data.obj;//操作结果提示
                    }else {
                        login_linkbutton.linkbutton({
                            text:'登录' ,
                            disabled:false
                        });
                        $('#validateCode').val('');
                        eu.showMsg(data.msg);
                        //refreshCheckCode();
                    }
                }, 'json');
            }
        }
    </script>
</head>
<body>
<form id="loginForm" method="post" novalidate>
    <div style="font-size: 14px;text-align: center;">用户登录</div>
    <table width="56%" align="center" border="0" cellpadding="5"
           cellspacing="0">
        <tr>
            <td width="10%" align="right">用户名：</td>
            <td width="20%" align="left">
                <input class="easyui-validatebox" placeholder="请输入登录名..." style="width: 200px" type="text"
                       id="loginName" name="loginName" required="true" value=""
                       data-options="validType:'minLength[1]',missingMessage:'请输入用户名!'"  />
            </td>
        </tr>
        <tr>
            <td align="right">密&nbsp;&nbsp;码：</td>
            <td align="left">
                <input type="password" id="password" name="password" placeholder="请输入密码..."  class="easyui-validatebox"
                       required="true" onkeydown="if(event.keyCode==13)login()" style="width: 200px" value=""
                       data-options="validType:'minLength[1]',missingMessage:'请输入密码!'" />
            </td>
        </tr>
        <%--
        <tr>
            <td align="right">验证码：</td>
            <td align="left">
                <input id="validateCode" name="validateCode" type="text" onkeydown="if(event.keyCode==13)login()" class="easyui-validatebox" style="width: 100px" required="true" data-options="tipPosition:'left',validType:'alphanum',missingMessage:'请输入验证码!'" />
                <img id="validateCode_img" align="middle" onclick="refreshCheckCode();" />
                <a href="javascript:void(0)" onclick="refreshCheckCode();" > 看不清,换一个</a>
            </td>
        </tr>
         --%>
        <tr>
            <td align="right">&nbsp;</td>
            <td align="left">
                <input id="rememberPassword" type="checkbox"/>
                <label for="rememberPassword">记住密码</label>
                <input id="autoLogin" type="checkbox"/>
                <label for="autoLogin">自动登录</label>
            </td>
        </tr>
        <tr>
            <td align="center" colspan="2">
                <a id="login_linkbutton" href="#" class="easyui-linkbutton" onclick="login()" >登录</a>
        </tr>

    </table>

</form>
</body>
</html>
