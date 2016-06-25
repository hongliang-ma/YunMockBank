/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.message.enums;

/**
 * 报文数据表现格式，可以根据这个格式完成相应的报文读取
 * 
 * @author hongliang.ma
 * @version $Id: MessageFormat.java, v 0.1 2012-6-20 上午10:54:29 hongliang.ma Exp $
 */
public enum MessageFormat {

    /** 文本 */
    TEXT,

    /** 字节 */
    BYTE,

    /** MAP结构 */
    MAP;

    /**
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }

}
