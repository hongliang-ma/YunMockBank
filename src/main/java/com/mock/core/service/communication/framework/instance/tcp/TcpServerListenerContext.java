/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.model.CommunicationContext.ParamType;
import com.mock.core.service.communication.framework.protocol.tcp.MinaServerHandler;

/**
 * tcp服务应用监听器上下文,所有tcp数据都从这里面接收,然后 分发给实例管理器
 * 
 * @author zhao.xiong
 * @version $Id: TcpServerListenerContext.java, v 0.1 2011-9-15 上午09:57:32 zhao.xiong Exp $
 */
public class TcpServerListenerContext {

    /** 监听map,key为uri **/
    private static Map<String, IoHandler> listenerMap = new ConcurrentHashMap<String, IoHandler>();

    /**
     * 添加监听器
     * @param configList
     */
    public static void addListener(CommunicationConfig configList) {

        final CommunicationConfig serverConfig = configList;
        final String uri = NetworkConfigCache.getTcpKey(serverConfig);

        if (listenerMap.containsKey(uri)) {
            return;
        }

        IoHandler serverHandler = new MinaServerHandler(configList) {

            /**
             * @see com.alipay.supergw.core.service.communication.framework.protocol.tcp.MinaServerHandler#handleRecvMessage(org.apache.mina.common.IoSession, java.lang.Object)
             */
            @Override
            protected void handleRecvMessage(IoSession session, final Object recvMsg) {
                ServerInstanceManager manager = InstanceManagerFactory
                    .getServerInstanceManagerByProtocol(serverConfig.getProtocol());

                CommunicationContext context = new CommunicationContext();
                context.addParam(ParamType.MINA_SESSION, session);
                context.addParam(ParamType.MESSAGE_BYTE, recvMsg);

                manager.addrRoute(uri, context);
            }
        };
        listenerMap.put(uri, serverHandler);
    }

    /**
     * 获取注册的IO监听器
     * 
     * @param uri
     * @return
     */
    public static IoHandler getListener(String uri) {
        return listenerMap.get(uri);
    }

    /**
     * 移除所有监听器
     * @param uri
     */
    public static void removeListener(String uri) {
        listenerMap.remove(uri);
    }

    /**
     * 清空所有监听器
     */
    public static void clear() {
        listenerMap.clear();
    }

}
