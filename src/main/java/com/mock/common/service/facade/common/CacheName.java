/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.common.service.facade.common;

/**
 * 缓存名称
 * 
 * @author peng.lanqp
 * @author lanxiao
 * @author 松雪
 * @version $Id: CacheName.java, v 0.1 2010-12-28 下午02:13:14 peng.lanqp Exp $
 */
public enum CacheName {
    COMMUNICATION("通讯配置的缓存"),

    SYS_TEMPLATER("公用模板的缓存"),

    USER_TEMPLATER("用户模板的缓存"),

    ANYMOCK_LABLE("标签的缓存");

    private final String description;

    /**
     * @param description
     */
    private CacheName(String description) {
        this.description = description;
    }

    /**
     * 根据枚举代码获取枚举信息
     * 
     * @param code
     * @return
     */
    public static CacheName get(String code) {
        for (CacheName cacheName : CacheName.values()) {
            if (cacheName.getCode().equalsIgnoreCase(code)) {
                return cacheName;
            }
        }

        return null;
    }

    /**
     * 获取枚举代码
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }

    /**
     * 获取枚举描述信息
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

}
