/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.enums;

/**
 * 通讯组件的服务方式：服务端或客户端
 * 
 * @author hongliang.ma
 * @version $Id: ConnectTypeEnum.java, v 0.1 2012-6-28 上午9:48:58 hongliang.ma Exp $
 */
public enum ConnectTypeEnum {

    /** 服务端 */
    SERVER("S"),

    /** 客户端 */
    CLIENT("C");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private ConnectTypeEnum(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     */
    public String getCode() {
        return code;
    }

}
