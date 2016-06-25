/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.code;

import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.exception.ExceptionType;

/**
 * 通讯错误代码定义
 * 
 * @author hongliang.ma
 * @version $Id: CommunicationErrorCode.java, v 0.1 2012-6-14 下午4:24:12 hongliang.ma Exp $
 */
public enum CommunicationErrorCode implements ErrorCode {

    /** SOCKET连接失败 */
    SOCKET_CONNECTION_FAIL("SOCKET连接失败", "ANYMOCK21001"),

    /** HTTP连接失败即404错误 */
    HTTP_CONNECTION_FAIL("HTTP连接失败", "ANYMOCK21002"),

    /** 协议异常 */
    PROTOCOL_EXCEPTION("协议异常", "ANYMOCK21003"),

    /** 传输异常 */
    IO_EXCEPTION("IO传输异常", "ANYMOCK21004"),

    /** 初始化证书容器发生异常 */
    KEY_SECURITY_EXCEPTION("初始化证书容器发生异常", "ANYMOCK21005"),

    /** 请求参数为空 */
    REQUEST_IS_NULL("请求参数为空", "ANYMOCK21006"),

    /** 报文编码格式不支持 */
    ENCODE_UNSUPPORTED("报文编码格式不支持", "ANYMOCK21007"),

    /** READ TIME OUT异常 */
    READ_TIME_OUT("读取超时", "ANYMOCK21008"),

    /** 无法找到对应的编码格式 */
    NOT_FORMAT_CONFIG("无法找到报文编码格式枚举", "ANYMOCK21009"),

    /** 无法找到对应的发送客户端 */
    NO_CLIENT_FOUND("无法找到对应的发送客户端", "ANYMOCK21010"),

    /** 无法找到对应的服务端 */
    NO_SERVER_FOUND("无法找到对应的发送服务端", "ANYMOCK21011"),

    /** HTTP通信异常，非200状态码 */
    HTTP_STATUS_ERROR("HTTP通信异常，非200状态码", "ANYMOCK21012,非200状态码"),

    /** 调用其它系统异常 */
    INVOKE_OTHER_SYSTEM_ERROR("调用其它系统异常", "ANYMOCK21013"),

    /** 读取取网络缓存超时 */
    READ_COMMUNICATION_CACHE_TIMEOUT("读取取网络缓存超时", "ANYMOCK21018"),

    /** 字符串解码IO异常 */
    DECODE_IO_EXCEPTION("字符串解码IO异常", "ANYMOCK21019"),

    /** 字符串解码转化异常 */
    DECODE_TRANS_EXCEPTION("字符串解码转化异常", "ANYMOCK21020"),

    /** 不符合类型的格式 */
    DECODE_CAST_EXCEPTION("不符合类型的格式", "ANYMOCK21021"),

    /** 通讯配置不能为空 */
    EMPTY_CONFIG("通讯配置不能为空", "ANYMOCK21022"),

    /** 非法参数 */
    ILLGE_PARAMS("非法参数", "ANYMOCK21023"),

    /** 线程副本为空 */
    THREAD_LOCAL_IS_EMPTY("线程副本为空", "ANYMOCK21024"),

    /** 系统异常 */
    SYSTEM_ERROR("系统异常", "ANYMOCK21025");

    /** 异常描述 */
    private final String description;

    /** 异常编码 */
    private final String shortCode;

    /**
     * @param description
     */
    private CommunicationErrorCode(String description, String shortCode) {
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
        return ExceptionType.COMMUNICATION;
    }

}
