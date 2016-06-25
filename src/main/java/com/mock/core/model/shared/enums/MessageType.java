/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.enums;

/**
 * 报文类型，用于标识报文是在哪个交互过程中产生的
 * 
 * @author hongliang.ma
 * @version $Id: MessageType.java, v 0.1 2012-6-20 上午10:36:07 hongliang.ma Exp $
 */
public enum MessageType {

    /** 客户端请求报文 */
    CALL_MESSAGE,

    /**  客户端响应报文*/
    CALL_RESPONSE,

    /** 服务器端请求报文 */
    SEND_MESSAGE,

    /** 服务器端响应报文*/
    SEND_RESPONSE;
}
