/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import com.eryansky.common.orm.entity.BaseEntity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 用户抽象类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-24 下午3:08:54 
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class User extends BaseEntity implements Serializable {

	public abstract String getPassword();

	public abstract String getLoginName();

	public boolean isLocked() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}