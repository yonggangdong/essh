/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web;

import com.eryansky.common.model.Menu;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.web.struts2.SimpleActionSupport;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.entity.base.Resource;
import com.eryansky.entity.base.User;
import com.eryansky.entity.base.state.ResourceType;
import com.eryansky.service.base.ResourceManager;
import com.eryansky.service.base.UserManager;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * 系统登录.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-21 上午12:18:54
 *
 */
@SuppressWarnings("serial")
public class LoginAction
        extends SimpleActionSupport {

    /**
     * 登录用户名
     */
    private String loginName;
    /**
     * 登录密码
     */
    private String password; 
    /**
     * 验证码
     */
    private String validateCode;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 主题
     */
    private String theme;

    /**
     * 操作员管理Service
     */
    @Autowired
    private UserManager userManager;
    @Autowired
    private ResourceManager resourceManager;
    
    public String welcome() throws Exception{
        return list();
    }

    public String list() throws Exception{
    	return SUCCESS;
    }

    /**
     * 用户登录验证
     * @throws Exception 
     */
    public String login() throws Exception {
    	Result result = null;
        try {
            //如果校验不通过将会抛出异常 异常会被异常拦截器ExceptionInterceptor拦截并返回客户端 客户端解析返回的数据即可做相应的提示
        	Validate.notBlank(loginName, "用户名不能为空!");
        	Validate.notBlank(password, "密码不能为空!");
            //验证码校验
            if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)) {
                msg = "用户名或密码不能为空!";
                result = new Result(Result.ERROR, msg, null);
                Struts2Utils.renderText(result);
                return null;
            }
            /*
            if (StringUtils.isEmpty(validateCode)) {
                msg = "验证码不能为空!";
                result = new Result(Result.RESULT_ERROR, msg, null);
                Struts2Utils.renderText(result);
                return null;
            }else{
            	if(!ValidateCodeServlet.validate(Struts2Utils.getRequest(), validateCode)){
            		 msg = "验证码不正确或验证码已过期!";
                     result = new Result(Result.RESULT_ERROR, msg, null);
                     Struts2Utils.renderText(result);
                     return null;
            	}
            }
            */
            // 获取用户信息
            User user = userManager.getUserByLP(loginName, Encrypt.e(password));
            if (user == null) {
                msg = "用户名或密码不正确!";
            }else if (user.getStatus().intValue() == StatusState.lock.getValue()) {
                msg = "该用户已被锁定，暂不允许登陆!";
            }
            if(msg != null){
            	result = new Result(Result.ERROR, msg, null);
                Struts2Utils.renderText(result);
                return null;
            }

            //将用户信息放入session中
            SecurityUtils.putUserToSession(user);
            logger.info("用户{}登录系统,IP:{}.", user.getLoginName(),Struts2Utils.getIp());

            //设置调整URL 如果session中包含未被授权的URL 则跳转到该页面
            String resultUrl = Struts2Utils.getRequest().getContextPath()+"/login!index.action?theme="+theme;
            Object unAuthorityUrl = Struts2Utils.getSession().getAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL);
            if(unAuthorityUrl != null){
                resultUrl = unAuthorityUrl.toString();
                //清空未被授权的URL
                Struts2Utils.getSession().setAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL,null);
            }
            //返回
        	result = new Result(Result.SUCCESS, "用户验证通过!",resultUrl);
            Struts2Utils.renderText(result);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * 当前在线用户
     * @throws Exception
     */
    public void onlineDatagrid() throws Exception{
    	try {
			Struts2Utils.renderJson(SecurityUtils.getSessionUser());
		} catch (Exception e) {
			throw e;
		}
    }

    /**
     * 导航菜单.
     */
    public void navTree() throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
        try {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            if (sessionInfo != null) {
                treeNodes = resourceManager.getNavMenuTreeByUserId(sessionInfo.getUserId());
            }
            Struts2Utils.renderJson(treeNodes);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 桌面版 开始菜单
     */
    public void startMenu() throws Exception {
        List<Menu> menus = Lists.newArrayList();
        try {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            if (sessionInfo != null) {
                List<Resource> rootResources = Lists.newArrayList();
                User superUser = userManager.getSuperUser();
                if (sessionInfo != null && superUser != null
                        && sessionInfo.getUserId() == superUser.getId()) {// 超级用户
                    rootResources = resourceManager.getByParentId(null, StatusState.normal.getValue());
                } else if (sessionInfo != null) {
                    rootResources = resourceManager.getResourcesByUserId(sessionInfo.getUserId(), null);
                    //去除非菜单资源
                    Iterator<Resource> iterator = rootResources.iterator();
                    while (iterator.hasNext()){
                        if(!ResourceType.menu.getValue().equals(iterator.next().getType())) {
                            iterator.remove();
                        }
                    }
                }
                for(Resource parentResource:rootResources){
                    Menu menu = this.resourceToMenu(parentResource, true);
                    if(menu!=null){
                        menus.add(menu);
                    }
                }
            }
            Struts2Utils.renderJson(menus);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 桌面版 桌面应用程序列表
     */
    public void apps() throws Exception {
        List<Menu> menus = Lists.newArrayList();
        try {
            String head = this.getHeadFromUrl(Struts2Utils.getRequest().getRequestURL().toString());
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            if (sessionInfo != null) {
                List<Resource> resources = Lists.newArrayList();
                User superUser = userManager.getSuperUser();
                if (sessionInfo != null && superUser != null
                        && sessionInfo.getUserId() == superUser.getId()) {// 超级用户
                    resources = resourceManager.getAll("orderNo", Page.ASC);
                } else if (sessionInfo != null) {
                    resources = resourceManager.getResourcesByUserId(sessionInfo.getUserId());
                }
                for(Resource resource:resources){
                    if(resource!=null && StringUtils.isNotBlank(resource.getUrl())){
                        if(ResourceType.menu.getValue().equals(resource.getType())) {
                            Menu menu = new Menu();
                            menu.setId(resource.getId().toString());
                            menu.setText(resource.getName());
                            menu.setHref(head+Struts2Utils.getRequest().getContextPath() + resource.getUrl());
                            menu.setIconCls(resource.getIconCls());
                            menus.add(menu);
                        }
                    }

                }
            }
            Struts2Utils.renderJson(menus);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 资源转M
     * @param resource 资源
     * @param isCascade 是否级联
     * @return
     */
    private Menu resourceToMenu(Resource resource,boolean isCascade) {
        Assert.notNull(resource, "参数resource不能为空");
        String head = this.getHeadFromUrl(Struts2Utils.getRequest().getRequestURL().toString());
        if(ResourceType.menu.getValue().equals(resource.getType())){
            Menu menu = new Menu();
            menu.setId(resource.getId().toString());
            menu.setText(resource.getName());
            menu.setHref(head+Struts2Utils.getRequest().getContextPath() + resource.getUrl());
            if(isCascade){
                List<Menu> childrenMenus = Lists.newArrayList();
                for(Resource subResource:resource.getSubResources()){
                    if(ResourceType.menu.getValue().equals(subResource.getType())){
                        childrenMenus.add(resourceToMenu(subResource,true));
                    }
                }
                menu.setChildren(childrenMenus);
            }
            return menu;
        }
        return null;
    }

    /**
     * 根据URL地址获取请求地址前面部分信息
     * @param url
     * @return
     */
    private String getHeadFromUrl(String url){
        int firSplit=url.indexOf("//");
        String proto=url.substring(0, firSplit+2);
        int webSplit=url.indexOf("/", firSplit+2);
        int portIndex=url.indexOf(":",firSplit);
        String webUrl=url.substring(firSplit+2, webSplit);
        String port= "";
        if(portIndex >= 0){
            webUrl=webUrl.substring(0, webUrl.indexOf(":"));
            port=url.substring(portIndex+1, webSplit);
        }else{
            port = "80";
        }
        return proto + webUrl+":"+ port;
    }
    
    /**
     * 注销登录
     */
    public String logout() {
        try {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            if (sessionInfo != null) {
                // 退出时清空session中的内容
                String sessionId = Struts2Utils.getSession().getId();
                //由监听器更新在线用户列表
                SecurityUtils.removeUserFromSession(sessionId, false);
                logger.info("用户{}退出系统.", sessionInfo.getLoginName());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return LOGIN;
    }

    /**
     * 后台管理主界面
     * @return
     * @throws Exception
     */
    public String index() throws Exception {
        //根据客户端指定的参数跳转至 不同的主题 如果未指定 默认:index
        if(StringUtils.isNotBlank(theme) && (theme.equals("app") || theme.equals("index"))){
            return theme;
        }else{
            return "index";
        }
//        return "app";
    }

    /**
     * 异步方式返回session信息
     */
    public void sessionInfo(){
        Result result = Result.successResult();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        result.setObj(sessionInfo);
        if(logger.isDebugEnabled()){
            logger.debug(result.toString());
        }
        Struts2Utils.renderJson(result);
    }
    
    /**
     * 防止重复提示错误信息
     */
    public void prepare() {
        clearErrorsAndMessages();
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getMsg() {
        return msg;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
