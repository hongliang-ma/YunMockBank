/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.enums;

/**
 * 通讯协议类型枚举
 * 
 * @author hongliang.ma
 * @version $Id: TransportProtocol.java, v 0.1 2012-6-28 上午9:48:35 hongliang.ma Exp $
 */
public enum TransportProtocol {

    /** socket连接 */
    TCP,

    /** UDP连接 */
    UDP,

    /** socket TLS 连接,兼容ssl3.0 */
    SSL,

    /** http */
    HTTP,

    /** https */
    HTTPS,

    /** 使用PROXY自己定义的协议 */
    PROXY,

    /** FTP */
    FTP,

    /** WebService */
    SOAP,

    /** TBnotify */
    TBNOTIFY;

    /**
     * 根据枚举代码，获取枚举
     * 
     * @param code
     * @return
     */
    public static TransportProtocol getEnumByCode(String code) {
        for (TransportProtocol protocol : TransportProtocol.values()) {
            if (protocol.getCode().equalsIgnoreCase(code)) {
                return protocol;
            }
        }

        return HTTPS;
    }

    /**
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }

}
