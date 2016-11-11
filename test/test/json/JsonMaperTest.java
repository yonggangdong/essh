/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.json;

import com.eryansky.common.utils.mapper.JsonMapper;

import java.util.Date;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-04-29 19:37
 */
public class JsonMaperTest {
    public static void main(String[] args) {
        Javabean javabean = new Javabean("name",100,new Date());
        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
        //传统模式 转换所有属性  需要在bean上加上注解 @JsonFilter(" ")
        System.out.println(jsonMapper.toJson(javabean));
        //排除属性
        System.out.println(jsonMapper.toJsonWithExcludeProperties(javabean,new String[]{"name"}));
        //转换指定属性
        System.out.println(jsonMapper.toJson(javabean,new String[]{"name","birthday"}));
    }
}
