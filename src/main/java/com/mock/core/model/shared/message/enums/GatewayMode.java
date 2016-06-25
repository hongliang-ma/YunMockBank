/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.message.enums;

/**
 * 报文处理方式
 * 
 * @author hongliang.ma
 * @version $Id: GatewayMode.java, v 0.1 2012-6-20 上午10:50:23 hongliang.ma Exp $
 */
public enum GatewayMode {

    /**anymock作为客户端，处理服务端返回的报文 */
    CLIENT,

    /** anymock作为服务端，处理客户端请求的报文 */
    SERVER;
}
