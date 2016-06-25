/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.http;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.protocol.ProtocolFactory;
import com.mock.core.service.communication.framework.protocol.ServerProtocolHandler;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.core.service.communication.framework.util.log.MessageLogUtil;
import com.mock.core.service.shared.communication.MessageReceiver;
import com.mock.util.ExceptionUtil;
import com.mock.util.LoggerUtil;
import com.mock.util.lang.StringUtil;

/**
 * http服务端实例管理器 ,http服务本身是servlet,这里面的实例主要为协议处理类
 * 
 * @author zhao.xiong
 * @author 松雪
 * @version $Id: HttpServerInstanceManager.java, v 0.2 2012-5-3 上午10:08:31 hao.zhang Exp $
 */
@Component("httpServerInstanceManager")
public class HttpServerInstanceManager implements ServerInstanceManager {
    /** logger */
    private static final Logger                       logger     = LoggerFactory
                                                                     .getLogger(HttpServerInstanceManager.class);

    /** 协议处理类map key为 uri path **/
    private static Map<String, ServerProtocolHandler> handlerMap = new ConcurrentHashMap<String, ServerProtocolHandler>();

    /** 报文接收器**/
    private MessageReceiver                           messageReceiver;

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */
    public void disposeAll() {
        handlerMap.clear();
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#startInstance(java.lang.String, java.lang.String)
     */
    public void startInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getServerConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_SERVER_FOUND);
        String key = ConfigUtil.getHttpKey(config.getUri().getUrl());
        ServerProtocolHandler serverHandler = handlerMap.get(key);
        if (serverHandler == null) {
            synchronized (handlerMap) {
                if (handlerMap.get(key) == null) {
                    try {
                        serverHandler = ProtocolFactory.getServerProtocolHandler(
                            config.getProtocol()).newInstance();
                        serverHandler.setMessageReceiver(messageReceiver);
                        serverHandler.setConfigList(config);
                        handlerMap.put(key, serverHandler);

                    } catch (Exception e) {

                        ExceptionUtil.caught(e, "HTTP服务器启动异常", config);

                        throw new AnymockException(CommunicationErrorCode.PROTOCOL_EXCEPTION, e);
                    }
                }
            }
        } else {
            serverHandler.setConfigList(config);
        }
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeInstance(java.lang.String, java.lang.String)
     */
    public void disposeInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getServerConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_SERVER_FOUND);
        String key = ConfigUtil.getHttpKey(config.getUri().getUrl());
        handlerMap.remove(key);
    }

    /** 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {

        LoggerUtil.info(logger, "开始更新HTTP服务端实例管理器。");

        refreshAll();

        LoggerUtil.info(logger, "HTTP服务端实例管理器更新完毕。");
    }

    /** 
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */
    public void initialize() {

        NetworkConfigCache.addListener(this);

        startAll();

        LoggerUtil.info(logger, "HTTP服务端实例管理器加载完毕。");
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#startAll()
     */
    public void startAll() {
        for (Entry<String, CommunicationConfig> entry : NetworkConfigCache.getAllServerConfig()
            .entrySet()) {
            CommunicationConfig config = entry.getValue();
            AssertUtil.isNotNull(config, CommunicationErrorCode.EMPTY_CONFIG);
            if (config.getProtocol() == TransportProtocol.HTTP
                || config.getProtocol() == TransportProtocol.HTTPS) {
                try {
                    ServerProtocolHandler serverHandler = ProtocolFactory.getServerProtocolHandler(
                        config.getProtocol()).newInstance();
                    serverHandler.setConfigList(config);
                    serverHandler.setMessageReceiver(messageReceiver);
                    handlerMap.put(ConfigUtil.getHttpKey(config.getUri().getUrl()), serverHandler);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "HTTP服务器启动异常", config);
                }
            }
        }
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#refreshAll()
     */
    public void refreshAll() {
        disposeAll();
        startAll();
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#setMessageReceiver(com.mock.core.service.shared.communication.MessageReceiver)
     */
    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#addrRoute(java.lang.String)
     */
    public Object addrRoute(String uri, CommunicationContext context) {
        ServerProtocolHandler handler = null;
        HttpServletResponse response = context.getHttpServletResponse();

        if ((handler = handlerMap.get(uri)) == null) {
            throw new AnymockException(CommunicationErrorCode.ILLGE_PARAMS, "非法url请求 [" + uri + "]");
        }
        CommunicationConfig config = handler.getConfigList();
        MessageDescription requestEnvelope = null;
        MessageEnvelope responseEnvelope = null;
        Object responseMsg = null;
        try {
            //1.处理消息
            responseMsg = handler.handle(null, context);
            requestEnvelope = handler.getRequestMessage();
            responseEnvelope = handler.getResponseMessage();
            //2.处理重定向
            boolean isRedirect = handleRedirect(config, responseEnvelope, response);

            if (isRedirect) {
                return null;
            }
        } catch (AnymockException e) {
            responseMsg = "WRAN:ERROR HAPPEND!";
            responseEnvelope = new MessageEnvelope(config.getSendMessageFormat(), responseMsg);
            LoggerUtil.warn(logger, "HTTP流程处理信息异常:", requestEnvelope, e);

        } catch (Exception e) {
            responseMsg = "WRAN:ERROR HAPPEND!";
            responseEnvelope = new MessageEnvelope(config.getSendMessageFormat(), responseMsg);
            ExceptionUtil.caught(e, "HTTP流程处理信息异常:", requestEnvelope);

        } finally {

            try {
                //3.响应消息
                response(response, config, responseMsg, responseEnvelope);
            } catch (IOException e) {
                ExceptionUtil.caught(e, "消息响应异常", responseEnvelope);
            }
        }

        return null;
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        InstanceManagerFactory.registerServerManager(TransportProtocol.HTTP, this);
        InstanceManagerFactory.registerServerManager(TransportProtocol.HTTPS, this);
    }

    /**
     * 处理重定向流程
     * @param config
     * @param responseMessageEnvelope
     * @param httpResponse
     * @return
     */
    private boolean handleRedirect(CommunicationConfig config,
                                   MessageEnvelope responseMessageEnvelope,
                                   HttpServletResponse httpResponse) {

        // 处理类重定向请求,必须要是文本格式，url是在响应脚本里面配置的,注意 主的组装脚本和附加的组装脚本的使用
        if (ConfigUtil.isRedirect(config)) {

            String url = responseMessageEnvelope.getContent().toString();
            if (StringUtil.isNotEmpty(url)) {

                LoggerUtil.info(logger, "生成页面重定向url=[", url, "]");

                try {
                    httpResponse.sendRedirect(url);
                    return true;
                } catch (IOException e) {
                    ExceptionUtil.caught(e, "页面重定向异常,url=", url);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 
     * 响应日志
     * @param response
     * @param config
     * @param responseMsg
     * @param responseEnvelope
     * @throws IOException
     */
    private void response(HttpServletResponse response, CommunicationConfig config,
                          Object responseMsg, MessageEnvelope responseEnvelope) throws IOException {
        if (responseMsg == null) {
            return;
        }
        if (!ConfigUtil.needsResponse(config)) {
            return;
        }

        MessageFormat sendFormat = config.getSendMessageFormat();
        if (sendFormat == MessageFormat.BYTE || sendFormat == MessageFormat.MAP) {
            response.getOutputStream().write((byte[]) responseMsg);
        } else {
            // 返回文本时，根据sgw_communication_map.charset设置字符集
            response.setCharacterEncoding(config.getCharset().name());
            response.getWriter().write((String) responseMsg);
        }

        MessageLogUtil.printLog(responseEnvelope, config, true);
    }
}
