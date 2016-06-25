/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.http;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.stereotype.Component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.communication.TransportURL;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.ClientInstanceManager;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.model.CommunicationContext.ParamType;
import com.mock.core.service.communication.framework.protocol.ClientProtocolHandler;
import com.mock.core.service.communication.framework.protocol.ProtocolFactory;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.core.service.communication.framework.util.ssl.SSLProtocolHelper;
import com.mock.core.service.communication.framework.util.ssl.SSLSocketFactoryImpl;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * http客户端实例管理,此处没有写对应的实例，因为httpClient已经是相关的客户端
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: HttpClientInstanceManager.java, v 0.1 2011-9-7 下午05:05:55 zhao.xiong Exp $
 */
@Component("httpClientInstanceManager")
public class HttpClientInstanceManager implements ClientInstanceManager {

    /** logger */
    private static final Logger                       logger      = LoggerFactory
                                                                      .getLogger(HttpClientInstanceManager.class);

    /** 实例map key为outTransCodeId+communicationId **/
    private static Map<String, HttpClient>            instanceMap = new ConcurrentHashMap<String, HttpClient>();

    /** 协议处理类map key为outTransCodeId+communicationId  **/
    private static Map<String, ClientProtocolHandler> handlerMap  = new ConcurrentHashMap<String, ClientProtocolHandler>();

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */
    public void disposeAll() {
        instanceMap.clear();
        handlerMap.clear();
        //unregister所有协议
        unregisterProtocol();
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#startInstance(java.lang.String, java.lang.String)
     */
    public void startInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getClientConfig(communicationId);

        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_CLIENT_FOUND);
        getAvailableClient(config);
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeInstance(java.lang.String, java.lang.String)
     */
    public void disposeInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getClientConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_CLIENT_FOUND);
        instanceMap.remove(config.getKey());
    }

    /** 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {

        disposeAll();

        LoggerUtil.info(logger, "HTTP客户端缓存清理完毕");
    }

    /** 
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */
    public void initialize() {

        NetworkConfigCache.addListener(this);

        LoggerUtil.info(logger, this.getClass().getSimpleName(), "注册网络缓存监听");
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.ClientInstanceManager#sendByConfig(com.mock.core.model.shared.communication.CommunicationConfig,java.lang.String, java.lang.String, com.mock.core.model.communication.message.domain.MessageEnvelope)
     */
    public MessageSendResult sendByConfig(CommunicationConfig config, String communicationId,
                                          MessageEnvelope requestMsg) {

        boolean timeout = false;
        boolean hasResponse = false;
        MessageEnvelope responseMsg = null;

        try {
            HttpClient client = getAvailableClient(config);
            ClientProtocolHandler handler = handlerMap.get(config.getKey());
            HttpMethod method = createHttpMethod(config);

            CommunicationContext context = new CommunicationContext();
            context.addParam(ParamType.HTTP_CLIENT, client);
            context.addParam(ParamType.HTTP_METHOD, method);
            responseMsg = handler.handle(requestMsg, context);
            hasResponse = (responseMsg == null) ? false : true;

        } catch (IOException e) {

            ExceptionUtil.caught(e, "HTTP客户端发送超时:", config);

            timeout = true;

        } catch (Exception e) {

            ExceptionUtil.caught(e, "HTTP客户端发送异常:", config);

            throw new AnymockException(CommunicationErrorCode.IO_EXCEPTION, e);
        }

        return new MessageSendResult(responseMsg, timeout, hasResponse);
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        InstanceManagerFactory.registerClientManager(TransportProtocol.HTTP, this);
        InstanceManagerFactory.registerClientManager(TransportProtocol.HTTPS, this);
    }

    /**
     * 获取可用的Http客户端
     * 
     * @param config
     * @return
     */
    private HttpClient getAvailableClient(CommunicationConfig config) {
        try {
            String key = config.getKey();
            HttpClient client = instanceMap.get(key);

            if (client == null) {
                synchronized (instanceMap) {
                    if ((client = instanceMap.get(key)) == null) {

                        //注册https协议
                        registerSSLProtocol(config);

                        //创建客户端
                        client = contructClient(config);

                        //注册协议处理类
                        ClientProtocolHandler handler = ProtocolFactory.getClientProtocolHandler(
                            config.getProtocol()).newInstance();

                        handler.setConfig(config);
                        handlerMap.put(key, handler);
                        instanceMap.put(key, client);

                    }
                }
            }
            return client;
        } catch (Exception e) {

            ExceptionUtil.caught(e, "HTTP客户端注册异常", config);

            throw new AnymockException(CommunicationErrorCode.NO_CLIENT_FOUND, e);
        }
    }

    /**
     * 组装客户端
     * 
     * @param config
     * @return
     */
    private HttpClient contructClient(CommunicationConfig config) {
        HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();

        // 连接超时时间
        connectionManagerParams.setConnectionTimeout(config.getConnecttimeout());
        // 等待数据最大时间
        connectionManagerParams.setSoTimeout(config.getReadTimeout());

        int limiter = ConfigUtil.getSingleConnectionLimit(config);
        connectionManagerParams.setDefaultMaxConnectionsPerHost(limiter);
        connectionManagerParams.setMaxTotalConnections(limiter);

        httpConnectionManager.setParams(connectionManagerParams);
        HttpClient client = new HttpClient(httpConnectionManager);
        client.getParams().setConnectionManagerTimeout(config.getConnecttimeout());
        DefaultHttpMethodRetryHandler retryhandler = new DefaultHttpMethodRetryHandler(0, false);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryhandler);
        return client;
    }

    /**
     * 创建 http方法体
     * 
     * @param config
     * @return
     */
    private HttpMethod createHttpMethod(CommunicationConfig config) {
        HttpMethod result = null;
        String url = config.getUri().getUrl();
        String httpReqType = ConfigUtil.getHttpReqType(config);
        if (StringUtil.equalsIgnoreCase(httpReqType, "post")) {
            PostMethod method = new PostMethod(url);

            result = method;
        } else if (StringUtil.equalsIgnoreCase(httpReqType, "get")) {
            GetMethod method = new GetMethod(url);
            result = method;
        } else {
            LoggerUtil.warn(logger, "不支持的HTTP请求类型:", httpReqType);
        }
        return result;
    }

    /**
     * 注册SSL协议
     * 
     * @param config
     */
    private void registerSSLProtocol(CommunicationConfig config) {
        if (config.getProtocol() == TransportProtocol.HTTPS) {

            //已经注册过就不用再注册了,如果有自定义协议用 HTTPS_SCHEMA
            String scheme = ConfigUtil.getHttpsSchema(config);

            TransportURL url = config.getUri();
            SSLContext context = SSLProtocolHelper.create();
            SSLSocketFactory socketFactory = context.getSocketFactory();
            ProtocolSocketFactory factory = new SSLSocketFactoryImpl(socketFactory, config);

            // 注册的协议只能使用全小写
            Protocol myhttps = new Protocol(scheme, factory, url.getPort());
            Protocol.registerProtocol(scheme, myhttps);

            LoggerUtil.info(logger, "注册HTTPS协议,scheme=", scheme, ",url=", url.getUrl(), ",port=",
                url.getPort());
        }
    }

    /**
     * 只能hack了，没有访问权限
     */
    @SuppressWarnings("rawtypes")
    private void unregisterProtocol() {
        try {
            Field field = Protocol.class.getDeclaredField("PROTOCOLS");
            field.setAccessible(true);
            ((Map) field.get(Protocol.class)).clear();

        } catch (Exception e) {
            ExceptionUtil.caught(e, "销毁所有注册的HTTPS协议出现异常");
        }
    }

}
