/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.core.aop.SecurityLogAspect;
import com.eryansky.entity.base.Role;
import com.eryansky.entity.base.User;
import com.eryansky.service.base.ResourceManager;
import com.eryansky.service.base.UserManager;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 系统使用的特殊工具类 简化代码编写.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-18 上午8:25:36
 */
public class SecurityUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(SecurityUtils.class);

    /**
     * 是否授权某个资源
     * @param resourceCode 资源编码
     * @return
     */
    public static boolean isPermitted(String resourceCode) {
        boolean flag = false;
        try {
            ResourceManager resourceManager = SpringContextHolder.getBean(ResourceManager.class);
            UserManager userManager = SpringContextHolder.getBean(UserManager.class);
            User superUser = userManager.getSuperUser();
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (sessionInfo != null && superUser != null
                    && sessionInfo.getUserId() == superUser.getId()) {// 超级用户
                flag = true;
            } else {
                flag = resourceManager.isUserPermittedResourceCode(sessionInfo.getUserId(), resourceCode);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 是否授权某个角色
     * @param roleCode 角色编码
     * @return
     */
    public static boolean isPermittedRole(String roleCode) {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return  isPermittedRole(sessionInfo.getUserId(),roleCode);
    }

    /**
     * 判断某个用户是否授权某个角色
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return
     */
    public static boolean isPermittedRole(Long userId,String roleCode) {
        boolean flag = false;
        try {
            if(userId == null){
                SessionInfo sessionInfo = getCurrentSessionInfo();
                if(sessionInfo != null){
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户["+userId+"]不存在.");
            }

            UserManager userManager = SpringContextHolder.getBean(UserManager.class);
            User superUser = userManager.getSuperUser();
            if (userId != null && superUser != null
                    && userId == superUser.getId()) {// 超级用户
                flag = true;
            } else {
                User user = userManager.loadById(userId);
                List<Role> userRoles = user.getRoles();
                for(Role role:userRoles){
                    if(roleCode.equalsIgnoreCase(role.getCode())){
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * User转SessionInfo.
     * @param user
     * @return
     */
    public static SessionInfo userToSessionInfo(User user){
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(user.getId());
        sessionInfo.setLoginName(user.getLoginName());
        sessionInfo.setRoleIds(user.getRoleIds());
        sessionInfo.setRoleNames(user.getRoleNames());
        sessionInfo.setLoginOrganName(user.getDefaultOrganName());
        sessionInfo.setOrganNames(user.getOrganNames());
        sessionInfo.setIp(Struts2Utils.getIp());
        return sessionInfo;
    }

    /**
     * 将用户放入session中.
     * @param user
     */
    public static void putUserToSession(User user){
        String sessionId = Struts2Utils.getSession().getId();
        logger.debug("putUserToSession:{}",sessionId);
        SessionInfo sessionInfo =  userToSessionInfo(user);
        sessionInfo.setId(sessionId);
        Struts2Utils.getSession().setAttribute(SecurityConstants.SESSION_SESSIONINFO, sessionInfo);
        SecurityConstants.sessionInfoMap.put(sessionId,sessionInfo);
    }

    /**
     * 获取当前用户session信息.
     */
    public static SessionInfo getCurrentSessionInfo(){
        return (SessionInfo)Struts2Utils.getSessionAttribute(SecurityConstants.SESSION_SESSIONINFO);
    }

    /**
     * 将用户信息从sessionInf中移除
     * @param sessionId  session ID
     * @param saveLog 是否 保存切面日志
     */
    public static void removeUserFromSession(String sessionId,boolean saveLog){
        if(StringUtils.isNotBlank(sessionId)){
            Set<String> keySet = SecurityConstants.sessionInfoMap.keySet();
            for(String key:keySet){
                if(key.equals(sessionId)){
                    logger.debug("removeUserFromSession:{}",sessionId);
                    if(saveLog){
                        SecurityLogAspect securityLogAspect = SpringContextHolder.getBean(SecurityConstants.SERVICE_SECURITY_LOGINASPECT);
                        SessionInfo sessionInfo = SecurityConstants.sessionInfoMap.get(key);
                        securityLogAspect.saveLog(sessionInfo,null,SecurityType.logout_abnormal);
                    }
                    SecurityConstants.sessionInfoMap.remove(key);
                }
            }
        }
    }

    public static Datagrid<SessionInfo> getSessionUser(){
        List<SessionInfo> list = Lists.newArrayList();
        Set<String> keySet = SecurityConstants.sessionInfoMap.keySet();
        for(String key:keySet){
            SessionInfo sessionInfo = SecurityConstants.sessionInfoMap.get(key);
            list.add(sessionInfo);
        }
        //排序
        Collections.sort(list, new Comparator<SessionInfo>() {
            @Override
            public int compare(SessionInfo o1, SessionInfo o2) {
                return o2.getLoginTime().compareTo(o1.getLoginTime());
            }
        });

        Datagrid<SessionInfo> dg = new Datagrid<SessionInfo>(SecurityConstants.sessionInfoMap.size(), list);
        return dg;
    }
}

