/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mock.core.model.shared.enums.TransportProtocol;

/**
 *  实例管理器工厂
 * 
 * @author hongliang.ma
 * @version $Id: InstanceManagerFactory.java, v 0.1 2012-6-25 下午3:14:17 hongliang.ma Exp $
 */
public class InstanceManagerFactory {

    private static final String                 CLIENT     = "Client";

    private static final String                 SERVER     = "Server";

    /** TransportProtocol +isServer,InstanceManager  **/
    private static Map<String, InstanceManager> managerMap = new ConcurrentHashMap<String, InstanceManager>();

    /**
     * 获取客户端实例管理
     * @param protocol
     * @return
     */
    public static ClientInstanceManager getClientInstanceManagerByProtocol(TransportProtocol protocol) {

        return (ClientInstanceManager) managerMap.get(protocol.name() + CLIENT);

    }

    /**
     * 获取服务实例管理
     * 
     * @param protocol
     * @return
     */
    public static ServerInstanceManager getServerInstanceManagerByProtocol(TransportProtocol protocol) {

        return (ServerInstanceManager) managerMap.get(protocol.name() + SERVER);

    }

    /**
     * 注册服务器管理
     * @param protocol
     * @param manager
     */
    public static void registerServerManager(TransportProtocol protocol,
                                             ServerInstanceManager manager) {
        managerMap.put(protocol.name() + SERVER, manager);
    }

    /**
     * 注册客户管理
     * @param protocol
     * @param manager
     */
    public static void registerClientManager(TransportProtocol protocol,
                                             ClientInstanceManager manager) {
        managerMap.put(protocol.name() + CLIENT, manager);
    }

    /**
     * Getter method for property <tt>managerMap</tt>.
     * 
     * @return property value of managerMap
     */
    public static Map<String, InstanceManager> getManagerMap() {
        return managerMap;
    }

}
