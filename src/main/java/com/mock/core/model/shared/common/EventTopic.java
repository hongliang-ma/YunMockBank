/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.common;

/**
 * 超级网关本地事件主题枚举，目前主要用于摘要日志
 *
 * @author 松雪
 * @version $Id: EventTopic.java, v 0.2 2011-11-29 下午2:46:47 hao.zhang Exp $
 */
public enum EventTopic {

    /** 业务摘要日志事件 */
    BIZ_DIGEST;

    /** 避免碰巧跟SOFA框架的事件代码冲突，统一加上前缀"SUPERGW" */
    private static final String PREFIX = "ANYMOCK";

    /**
     * 
     * @return
     */
    public String getCode() {
        return Constant.getKey('_', PREFIX, this.name());
    }
}
