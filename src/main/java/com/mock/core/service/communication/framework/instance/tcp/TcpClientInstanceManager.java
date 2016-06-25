/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.ClientInstanceManager;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.common.util.LoggerUtil;

/**
 * tcp客户端实例管理器
 * @author zhao.xiong
 * @version $Id: TcpClientInstanceManager.java, v 0.1 2011-9-5 下午02:14:15 zhao.xiong Exp $
 */
@Component("tcpClientInstanceManager")
public class TcpClientInstanceManager implements ClientInstanceManager {

    /** logger */
    private static final Logger           logger      = LoggerFactory
                                                          .getLogger(TcpClientInstanceManager.class);

    /** 实例map key为 outTransCodeId+communicationId **/
    private static Map<String, TcpClient> instanceMap = new ConcurrentHashMap<String, TcpClient>();

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */

    public void disposeAll() {
        for (TcpClient client : instanceMap.values()) {
            client.dispose();
        }
        instanceMap.clear();
    }

    /** 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */

    public void update(Observable o, Object arg) {

        disposeAll();

        LoggerUtil.info(logger, "TcpClient缓存清理完毕");
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
        instanceMap.get(config.getKey()).dispose();
        instanceMap.remove(config.getKey());
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.ClientInstanceManager#sendByConfig(com.mock.core.model.shared.communication.CommunicationConfig,java.lang.String, java.lang.String, com.mock.core.model.communication.message.domain.MessageEnvelope)
     */

    public MessageSendResult sendByConfig(CommunicationConfig config, String communicationId,
                                          MessageEnvelope requestMsg) {
        return getAvailableClient(config).send(requestMsg);
    }

    /** 
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */

    public void initialize() {

        NetworkConfigCache.addListener(this);

        LoggerUtil.info(logger, this.getClass().getSimpleName(), "注册网络缓存监听");
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */

    public void afterPropertiesSet() {
        InstanceManagerFactory.registerClientManager(TransportProtocol.TCP, this);
        InstanceManagerFactory.registerClientManager(TransportProtocol.SSL, this);
    }

    /**
     * 获取TCP的客户端
     * 
     * @param config
     * @return
     */
    private TcpClient getAvailableClient(final CommunicationConfig config) {
        String key = config.getKey();
        TcpClient client = instanceMap.get(key);
        if (client == null) {
            synchronized (instanceMap) {
                if ((client = instanceMap.get(key)) == null) {

                    client = new TcpClient(config);

                    instanceMap.put(key, client);
                }
            }
        }
        return client;
    }

}
