/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.mock.core.model.shared.message.enums;

/**
 * 消息模式，分成功能、性能和唯一用户
 * 
 * @author 马洪良
 * @version $Id: MessageRunMode.java, v 0.1 2013-1-24 下午3:06:40 马洪良 Exp $
 */
public enum MessageRunMode {
    /**该渠道作为普通模式 */
    NORMAL,

    /**该渠道作为唯一用户，不允许复制、拷贝 */
    ONLYONE,

    /** 该渠道作为性能测试模式 */
    PERFORM;

    /**
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }
}
