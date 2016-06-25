/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.code;

import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.exception.ExceptionType;

/**
 * 用户配置交易处理错误代码
 * 
 * @author hongliang.ma
 * @version $Id: TransactionErrorCode.java, v 0.1 2012-6-14 下午4:23:55 hongliang.ma Exp $
 */
public enum TransactionErrorCode implements ErrorCode {

    /** 非法参数 */
    ILLEGAL_PARAMETER("非法参数", "ANYMOCK31002"),

    /** 无法找到对应的处理组件 */
    CANNOT_FIND_COMPONENT("无法找到对应的处理组件", "ANYMOCK31003"),

    /** 组件处理异常 */
    COMPONENT_HANDLE_ERROR("组件处理异常", "ANYMOCK31004"),

    /**无法找到返回值 */
    NOT_MATCH_CODERULE("无法找到返回值", "ANYMOCK31005"),

    /** 组件发生未知异常 */
    COMPONENT_UNKWON_ERROR("组件发生未知异常", "ANYMOCK31006"),

    /** 传送给下一个组件时发生错误 */
    TRANS_NEXT_ERROR("传送给下一个组件时发生错误", "ANYMOCK31007"),

    /**无法找到返回值 */
    ASSERT_FAILED("执行assert方法失败", "ANYMOCK31009"),

    /** 延时组件发生异常 */
    TIME_ERROR("延时组件发生异常", "ANYMOCK31008");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    /**
     * @param description
     */
    private TransactionErrorCode(String description, String shortCode) {
        this.description = description;
        this.shortCode = shortCode;
    }

    /** 
     * @see com.mock.core.model.shared.exception.code.ErrorCode#getDescription()
     */
    public String getDescription() {
        return Constant.getKey(this.name(), this.description);
    }

    /** 
     * @see com.mock.core.model.shared.exception.code.ErrorCode#getCode()
     */
    public String getCode() {
        return this.shortCode;
    }

    /** 
     * @see com.mock.core.model.shared.exception.code.ErrorCode#getType()
     */
    public ExceptionType getType() {
        return ExceptionType.TRANSACTION;
    }

}
