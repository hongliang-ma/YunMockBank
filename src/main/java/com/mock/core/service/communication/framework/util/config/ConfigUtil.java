/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.PropertiesConfigEnum;
import com.mock.common.util.ExceptionUtil;

/**
 * 配置帮助类
 * 
 * @author hongliang.ma
 * @version $Id: ConfigUtil.java, v 0.1 2012-6-25 下午3:17:46 hongliang.ma Exp $
 */
public final class ConfigUtil {

    /**
     * 禁用构造函数
     */
    private ConfigUtil() {
        // 禁用构造函数
    }

    //
    //    /**
    //     * 是否包括数据头
    //     * 
    //     * @param config
    //     * @return
    //     */
    //    public static boolean hasHeaderType(CommunicationConfig config) {
    //        return StringUtil.isNotBlank(config.getProperties(PropertiesConfigEnum.HEAD_TYPE));
    //    }
    //
    //    /**
    //     * 获取TCP头的数据信息
    //     * 
    //     * @param config
    //     * @return
    //     */
    //    public static TcpHeader getTcpHeader(CommunicationConfig config) {
    //        String headerType = config.getProperties(PropertiesConfigEnum.HEAD_TYPE);
    //        String offset = config.getProperties(PropertiesConfigEnum.HEAD_OFFSET);
    //        String width = config.getProperties(PropertiesConfigEnum.HEAD_WIDTH);
    //        TcpHeader header = new TcpHeader(TcpHeadType.valueOf(headerType), offset, width);
    //        header.setSubstr(config.getProperties(PropertiesConfigEnum.HEAD_SUBSTR));
    //        return header;
    //    }

    /**
     * 是否需要响应信息，默认需要
     * 
     * @param config
     * @return
     */
    public static boolean needsResponse(CommunicationConfig config) {
        String properties = config.getProperties(PropertiesConfigEnum.NEEDS_RESPONSE);
        return StringUtil.isEmpty(properties) ? true : BooleanUtils.toBoolean(properties);

    }

    /**
     * 获取接收编码类型
     * 
     * @param config
     * @return
     */
    public static String getRecvCharset(CommunicationConfig config) {
        String recvCharset = config.getProperties(PropertiesConfigEnum.RECV_CHARSET);
        return StringUtil.isBlank(recvCharset) ? config.getCharset().name() : recvCharset;
    }

    /**
     * 获取单点连接池的并发限制数
     * @param config
     * @return
     */
    public static int getSingleConnectionLimit(CommunicationConfig config) {
        return NumberUtils.toInt(config.getProperties(PropertiesConfigEnum.SINGLE_POOL_LIMIT), 20);
    }

    /**
     * 是否异步接收
     * 
     * @param config
     * @return
     */
    public static boolean isAsynReceiver(CommunicationConfig config) {
        return BooleanUtils.toBoolean(config.getProperties(PropertiesConfigEnum.IS_ASYN_RECEIVER));
    }

    /**
     * 是否长连接
     * 
     * @param config
     * @return
     */
    public static boolean isKeepAlive(CommunicationConfig config) {
        return BooleanUtils.toBoolean(config.getProperties(PropertiesConfigEnum.IS_KEEPALIVE));
    }

    /**
     * HTTP是否重定向
     * 
     * @param config
     * @return
     */
    public static boolean isRedirect(CommunicationConfig config) {
        return BooleanUtils.toBoolean(config.getProperties(PropertiesConfigEnum.IS_REDIRECT));
    }

    /**
     * 获取HTTP请求类型
     * 
     * @param config
     * @return
     */
    public static String getHttpReqType(CommunicationConfig config) {
        String requestType = config.getProperties(PropertiesConfigEnum.HTTP_REQ_TYPE);
        return StringUtil.isBlank(requestType) ? "get" : requestType;
    }

    /**
     * TCP通信组件发送线程池大小
     * @param config
     * @param defaultSize
     * @return
     */
    public static int getThreadPoolThreshold(CommunicationConfig config, int defaultSize) {
        String threadPool = config.getProperties(PropertiesConfigEnum.THREAD_POOL_THRESHOLD);
        return NumberUtils.toInt(threadPool, defaultSize);
    }

    /**
     * HTTPS自定义协议类型，默认为渠道系统编号
     * 协议类型在初始化的时候必须是小写的，但是数据库的通讯地址可以配置为大写的
     * 比如：ICBCSZ02://127.0.0.1:443/servlet
     * 
     * @param config
     * @return
     */
    public static String getHttpsSchema(CommunicationConfig config) {
        String customScheme = config.getProperties(PropertiesConfigEnum.HTTPS_SCHEMA);
        String sysTemplateId = StringUtil.toLowerCase(config.getSysTemplateId());
        return StringUtil.defaultIfBlank(StringUtil.toLowerCase(customScheme), sysTemplateId);
    }

    /**
     * 获取URL路径，非法路径会返回null，调用方判断
     * 
     * @param url
     * @return
     */
    public static String getHttpKey(String url) {

        try {
            URL uri = new URL(url);
            return uri.getPath();
        } catch (MalformedURLException e) {
            ExceptionUtil.caught(e, "HTTP通讯协议URL不合法,url=", url);
        }

        return null;
    }

}
