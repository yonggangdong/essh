/**
 *  Copyright (c) 2012-2013 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.web.sys;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.common.web.struts2.StrutsAction;
import com.eryansky.common.web.struts2.utils.Struts2Utils;
import com.eryansky.entity.sys.Log;
import com.eryansky.entity.sys.state.LogType;
import com.eryansky.service.sys.LogManager;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date  2013-12-8 下午5:13
 */
public class LogAction
        extends StrutsAction<Log> {

    /**
     * 日志保留时间
     */
    private Integer keepTime;

    @Autowired
    private LogManager logManager;

    @Override
    public EntityManager<Log, Long> getEntityManager() {
        return logManager;
    }

    /**
     * 清空所有日志
     * @return
     * @throws Exception
     */
    public String removeAll() throws Exception {
        logManager.removeAll();
        Result result = Result.successResult();
        Struts2Utils.renderText(result);
        return null;
    }

    /**
     * 日志类型下拉列表.
     */
    public void logTypeCombobox() throws Exception {
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

            LogType[] lts = LogType.values();
            for(int i=0;i<lts.length;i++){
                Combobox combobox = new Combobox();
                combobox.setValue(lts[i].getValue().toString());
                combobox.setText(lts[i].getDescription());
                cList.add(combobox);
            }
            Struts2Utils.renderJson(cList);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 设置日志保留时间 页面
     * @return
     */
    public String time() throws Exception{
        Struts2Utils.getRequest().setAttribute("keepTime",AppConstants.getLogKeepTime());
        return "time";
    }
    /**
     * 更新日志保留时间
     * @throws Exception
     */
    public void updateKeepTime() throws Exception{
        Result reslut;
        if(keepTime != null){
            PropertiesLoader propertiesLoader = AppConstants.getConfig();
            AppConstants.getConfig().modifyProperties(AppConstants.CONFIG_FILE_PATH,AppConstants.CONFIG_LOGKEEPTIME,keepTime.toString());
            reslut = Result.successResult();
        } else{
            throw new ActionException("未设置参数[keepTime].");
//            reslut = new Result(Result.WARN,"未设置参数[keepTime].",null);
        }
        Struts2Utils.renderJson(reslut);
    }

    public void setKeepTime(Integer keepTime) {
        this.keepTime = keepTime;
    }
}
