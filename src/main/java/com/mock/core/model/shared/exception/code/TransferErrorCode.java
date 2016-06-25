/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.code;

import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.exception.ExceptionType;

/**
 * 转发器阶段的异常
 * 
 * @author hongliang.ma
 * @version $Id: TransferErrorCode.java, v 0.1 2012-6-14 下午4:24:03 hongliang.ma Exp $
 */
public enum TransferErrorCode implements ErrorCode {

    /** 空报文 */
    EMPTY_MESSAGE("空报文", "ANYMOCK12001"),

    /** 加载转发器的处理模板异常 */
    SCRIPT_LOAD_ERROR("加载转发器的处理模板异常", "ANYMOCK12004"),

    /** 转发器报文处理异常 */
    TRANS_EXCUSE_ERROR("转发器报文处理异常", "ANYMOCK12005"),

    /**找不到相应的用户配置 */
    NO_USERTEMPLATE("找不到相应的用户配置", "ANYMOCK12006"),

    /** 找不到需要发送的客服端 */
    NO_CLIENT("找不到需要发送的客服端", "ANYMOCK12003"),

    /** xml格式错误 */
    ILLEGAL_XML("xml格式错误", "ANYMOCK12007"),

    /** json格式错误 */
    ILLEGAL_JSON("json格式错误", "ANYMOCK12008"),

    /** 非法参数 */
    ILLEGAL_PARAMETER("非法参数", "ANYMOCK12009"),

    /** 找不到相应的公共配置 */
    SYSTEMPLATE_NOT_FIND("找不到相应的公共配置", "ANYMOCK12011"),

    /** 8583报文数据流格式错误 */
    ILLEGAL_8583_BYTE("8583报文数据流格式错误", "ANYMOCK12012"),

    /** 工具类加载异常 */
    LOAD_TOOLS_ERROR("工具类加载异常", "ANYMOCK12013"),

    /** 工具类加载异常 */
    TOOLS_EXCUES_ERROR("工具方法处理异常", "ANYMOCK12014");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    /**
     * @param description
     */
    private TransferErrorCode(String description, String shortCode) {
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
        return ExceptionType.DISPATCH_ERROR;
    }

}
