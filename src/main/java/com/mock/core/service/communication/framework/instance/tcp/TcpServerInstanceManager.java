/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IoSession;
import org.springframework.stereotype.Component;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.model.CommunicationContext.ParamType;
import com.mock.core.service.communication.framework.protocol.ServerProtocolHandler;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.core.service.shared.communication.MessageReceiver;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * TCP服务实例管理器
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: TcpServerInstanceManager.java, v 0.1 2011-9-5 下午03:51:50 zhao.xiong Exp $
 */
@Component("tcpServerInstanceManager")
public class TcpServerInstanceManager implements ServerInstanceManager {

    /** logger */
    private static final Logger           logger      = LoggerFactory
                                                          .getLogger(TcpServerInstanceManager.class);

    /** 实例map key为 url path **/
    private static Map<String, TcpServer> instanceMap = new ConcurrentHashMap<String, TcpServer>();

    /** 报文接收器**/
    private MessageReceiver               messageReceiver;

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */

    public void disposeAll() {

        //停止已经启动的服务器
        for (TcpServer server : instanceMap.values()) {
            server.dispose();
        }

        instanceMap.clear();
        TcpServerListenerContext.clear();
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#startInstance(java.lang.String, java.lang.String)
     */

    public void startInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getServerConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_SERVER_FOUND);

        String key = NetworkConfigCache.getTcpKey(config);
        TcpServer server = instanceMap.get(key);
        if (server == null) {
            CommunicationConfig configList = NetworkConfigCache.findByReceiver(key);

            innerStartInstance(key, configList);
        }
    }

    private void innerStartInstance(String key, CommunicationConfig configList) {
        TcpServer server;
        try {
            server = new TcpServer(configList, messageReceiver);
            TcpServerListenerContext.addListener(configList);
            instanceMap.put(key, server);

            server.startup();
        } catch (Exception e) {

            ExceptionUtil.caught(e, "TCP服务器启动异常", configList);

            throw new AnymockException(CommunicationErrorCode.IO_EXCEPTION, e);
        }
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeInstance(java.lang.String, java.lang.String)
     */

    public void disposeInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getServerConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_SERVER_FOUND);
        String key = NetworkConfigCache.getTcpKey(config);
        TcpServer server = instanceMap.get(key);
        AssertUtil.isNotNull(server, CommunicationErrorCode.NO_SERVER_FOUND);
        server.dispose();
        instanceMap.remove(key);
        TcpServerListenerContext.removeListener(key);
    }

    /** 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */

    public void update(Observable o, Object arg) {

        LoggerUtil.info(logger, "开始更新TCP服务端实例管理器。");

        refreshAll();

        LoggerUtil.info(logger, "TCP服务端实例管理器更新完毕。");
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#startAll()
     */

    public void startAll() {
        for (Entry<String, CommunicationConfig> entry : NetworkConfigCache.getAllServerConfig()
            .entrySet()) {

            CommunicationConfig configs = entry.getValue();
            AssertUtil.isNotNull(configs, CommunicationErrorCode.EMPTY_CONFIG);
            if (configs.getProtocol() == TransportProtocol.TCP
                || configs.getProtocol() == TransportProtocol.SSL) {

                String uri = NetworkConfigCache.getTcpKey(configs);

                innerStartInstance(uri, configs);
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
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */

    public void initialize() {

        startAll();

        LoggerUtil.info(logger, "TCP服务端实例管理器加载完毕。");
    }

    /**
     * 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#setMessageReceiver(com.mock.core.service.shared.communication.MessageReceiver)
     */

    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#addrRoute(java.lang.String, java.lang.Object[])
     */

    public Object addrRoute(String uri, CommunicationContext context) {
        TcpServer server = null;
        if ((server = instanceMap.get(uri)) == null) {
            throw new AnymockException(CommunicationErrorCode.PROTOCOL_EXCEPTION, "非法uri请求:" + uri);
        }

        ServerProtocolHandler handler = server.getHandler();
        IoSession session = context.getIoSession();
        Object recvMsg = context.getObject(ParamType.MESSAGE_BYTE);
        MessageDescription requestEnvelope = null;
        Object responseMsg = null;

        try {

            //1.处理消息
            responseMsg = handler.handle(recvMsg, context);
            requestEnvelope = handler.getRequestMessage();

            //2.判断是否需要响应消息
            if (ConfigUtil.needsResponse(handler.getConfigList()) && isNotEmptyMessage(responseMsg)) {
                byte[] out = null;
                if (responseMsg instanceof String) {
                    out = ((String) responseMsg).getBytes();
                } else if (responseMsg instanceof byte[]) {
                    out = (byte[]) responseMsg;
                }
                session.write(out);
            }
        } catch (AnymockException e) {

            LoggerUtil.warn(logger, "流程处理信息异常:", requestEnvelope, e);

        } catch (Exception e) {

            ExceptionUtil.caught(e, "流程处理信息异常:", requestEnvelope);

        }
        return null;
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */

    public void afterPropertiesSet() {
        NetworkConfigCache.addListener(this);
        InstanceManagerFactory.registerServerManager(TransportProtocol.TCP, this);
        InstanceManagerFactory.registerServerManager(TransportProtocol.SSL, this);
    }

    /**
     * 如果是文本类型，检查是否是空报文，对于空报文不需要响应了
     * 
     * @param responseMsg
     * @return
     */
    private boolean isNotEmptyMessage(Object responseMsg) {
        if (responseMsg instanceof String) {
            return StringUtil.isNotEmpty((String) responseMsg);
        }
        return true;
    }

}
