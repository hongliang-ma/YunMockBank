/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.model;

import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.mina.common.IoSession;

import com.mock.core.service.communication.framework.instance.tcp.SockCallBack;
import com.alipay.common.event.UniformEvent;

/**
 *  通讯参数上下文，用于规范通讯组件间的参数传递
 * 
 * @author hongliang.ma
 * @version $Id: CommunicationContext.java, v 0.1 2012-6-25 下午5:13:54 hongliang.ma Exp $
 */
public class CommunicationContext {

    /** 存储参数的map */
    private final Map<ParamType, Object> paramMap = new EnumMap<ParamType, Object>(ParamType.class);

    /**
     * 通讯参数类型枚举
     *
     * @author 松雪
     * @version $Id: CommunicationContext.java, v 0.1 2011-9-26 下午4:24:21 hao.zhang Exp $
     */
    public static enum ParamType {
        HTTP_SERVLET_REQUEST,

        HTTP_SERVLET_RESPONSE,

        SHORT_TCP_CONNECTION,

        MINA_SESSION,

        MESSAGE_TEXT,

        SYSTEM_TEMPLATE_ID,

        COMMUNICATIONID,

        MESSAGE_BYTE,

        UNIFORM_EVENT,

        HTTP_METHOD,

        HTTP_CLIENT,

        TCP_CLIENT;
    }

    /**
     * 按类型添加参数
     * @param type
     * @param value
     */
    public void addParam(ParamType type, Object value) {
        this.paramMap.put(type, value);
    }

    /**
     * 
     * @return
     */
    public HttpClient getHttpClient() {
        return (HttpClient) paramMap.get(ParamType.HTTP_CLIENT);
    }

    /**
     * 
     * @return
     */
    public HttpMethod getHttpMethod() {
        return (HttpMethod) paramMap.get(ParamType.HTTP_METHOD);
    }

    /**
     * 
     * @param type
     * @return
     */
    public Object getObject(ParamType type) {
        return paramMap.get(type);
    }

    /**
     * 
     * @param type
     * @return
     */
    public String getString(ParamType type) {
        return (String) paramMap.get(type);
    }

    /**
     * 
     * @param type
     * @return
     */
    public boolean getBoolean(ParamType type) {
        Object value = paramMap.get(type);
        if (value != null) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) paramMap.get(ParamType.HTTP_SERVLET_RESPONSE);
    }

    /**
     * 
     * @return
     */
    public HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) paramMap.get(ParamType.HTTP_SERVLET_REQUEST);
    }

    /**
     * 
     * @return
     */
    public UniformEvent getUniformEvent() {
        return (UniformEvent) paramMap.get(ParamType.UNIFORM_EVENT);
    }

    /**
     * 
     * @return
     */
    public IoSession getIoSession() {
        return (IoSession) paramMap.get(ParamType.MINA_SESSION);
    }

    /**
     * 
     * @return
     */
    public Socket getSocket() {
        return (Socket) paramMap.get(ParamType.SHORT_TCP_CONNECTION);
    }

    /**
     * 
     * @return
     */
    public SockCallBack getTcpClient() {
        return (SockCallBack) paramMap.get(ParamType.TCP_CLIENT);
    }
}
