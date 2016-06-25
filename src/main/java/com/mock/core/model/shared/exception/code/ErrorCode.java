/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.code;

import com.mock.core.model.shared.exception.ExceptionType;

/**
 * 异常代码接口
 * 
 * @author hongliang.ma
 * @version $Id: ErrorCode.java, v 0.1 2012-6-20 上午10:56:09 hongliang.ma Exp $
 */
public interface ErrorCode {

    /**
     * 获取异常类型
     * 
     * @return 异常类型枚举
     */
    public ExceptionType getType();

    /**
     * 获取网关错误代码
     * 
     * @return 
     */
    public String getCode();

    /**
     * 获取网关错误描述
     * 
     * @return 
     */
    public String getDescription();
}
