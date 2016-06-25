/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.code;

import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.exception.ExceptionType;

/**
 * 系统异常错误代码
 * 
 * @author hongliang.ma
 * @version $Id: SystemErrorCode.java, v 0.1 2012-6-14 下午4:24:23 hongliang.ma Exp $
 */
public enum SystemErrorCode implements ErrorCode {

    /** 非法参数 */
    ILLEGAL_PARAMETER("非法参数", "ANYMOCK01001"),

    /** 系统异常 */
    SYSTEM_ERROR("系统异常", "ANYMOCK01002"),

    /** 系统异常 */
    SYSTEM_HAND_ERROR("内部处理异常", "ANYMOCK01003"),

    /** 初始化工具失败 */
    TOOL_INIT_ERROR("初始化工具失败", "ANYMOCK01004"),

    /** 工具启动时抛出异常 */
    TOOL_START_ERROR("工具启动时抛出异常", "ANYMOCK01005"),

    /** 数据库访问异常 */
    DB_ACCESS_ERROR("数据库访问异常", "ANYMOCK01006");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    /**
     * @param description
     */
    private SystemErrorCode(String description, String shortCode) {
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
        return ExceptionType.SYSTEM_ERROR;
    }

}
