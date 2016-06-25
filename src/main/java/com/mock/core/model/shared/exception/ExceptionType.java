/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.exception;

/**
 * ANYMOCK31002
 * 
 * @author hongliang.ma
 * @version $Id: ExceptionType.java, v 0.1 2012-6-20 上午11:11:10 hongliang.ma Exp $
 */
public enum ExceptionType {

    /** 系统异常 */
    SYSTEM_ERROR,

    /** 通讯 */
    COMMUNICATION,

    /**  分发阶段异常 */
    DISPATCH_ERROR,

    /** 报文处理异常 */
    TRANSACTION;

    /**
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }

}
