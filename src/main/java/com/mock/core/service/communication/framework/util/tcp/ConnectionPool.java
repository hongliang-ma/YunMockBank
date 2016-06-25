/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.service.communication.framework.instance.tcp.SockCallBack;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 *  长连接的连接池,里面会管理一些连接,如有实时性必要需要分布式管理长连接
 * 达到最大数目开始进行复用，这里暂时不做闲置
 * 对于长连接communicationId唯一性检查
 * 
 * @author hongliang.ma
 * @version $Id: ConnectionPool.java, v 0.1 2012-6-25 下午3:16:26 hongliang.ma Exp $
 */
public class ConnectionPool {

    /** logger */
    private static final Logger                         logger = LoggerFactory
                                                                   .getLogger(ConnectionPool.class);

    /** 连接池，key为通讯组件outTransCodeId+communicationId, 连接里面的内容为**/
    private static final Map<String, ConnectionManager> pool   = new ConcurrentHashMap<String, ConnectionManager>();

    /**
     *  通过通讯获取可用连接<br> 
     *  采用回调模式
     * @param config
     * @param callBack
     * @return
     * @throws IOException
     */
    public static SockConnection getAvaiableConnectionByConfig(final CommunicationConfig config,
                                                               final SockCallBack callBack)
                                                                                           throws IOException {
        if (!pool.containsKey(config.getCommunicationId())) {
            initByKey(config.getCommunicationId());
        }
        ConnectionManager connectionManager = pool.get(config.getCommunicationId());
        SockConnection connection = getConnection(connectionManager, config, callBack);
        return resetConnection(connection, callBack, config);
    }

    /**
     * 重置连接,如果已经断掉，重连
     * @param connection
     * @param callBack
     * @param config
     * @return
     * @throws IOException 
     */
    private static SockConnection resetConnection(SockConnection connection, SockCallBack callBack,
                                                  CommunicationConfig config) throws IOException {
        if (connection.getSock() == null || connection.getSock().isClosed()) {
            LoggerUtil.info(logger, connection, "已经断掉,准备重新建立连接");
            connection.setSock(callBack.buildSock(config));
            connection.clearLoadValue();
        }
        return connection;
    }

    /**
     * 通过通讯移除连接,在长连接发生异常的时候移除
     * 
     * @param config
     * @param connection
     */
    public static void removeConnectionByConfig(final CommunicationConfig config,
                                                SockConnection connection) {
        if (!pool.containsKey(config.getCommunicationId())) {
            return;
        }
        pool.get(config.getCommunicationId()).removeConnection(connection);
    }

    /**
     * 移除所有连接
     * @param config
     */
    public static void removeAllByConfig(final CommunicationConfig config) {
        if (!pool.containsKey(config.getCommunicationId())) {
            return;
        }
        LoggerUtil.info(logger, config, "移除所有连接");
        pool.get(config.getCommunicationId()).removeAllConnection();
    }

    /**
     * 初始化池以及锁
     * 
     * @param communicationId
     */
    private static void initByKey(final String key) {
        if (!pool.containsKey(key)) {
            synchronized (pool) {
                if (!pool.containsKey(key)) {
                    ConnectionManager manager = new ConnectionManager();
                    pool.put(key, manager);
                }
            }
        }
    }

    /**
     *  新建一条连接,超过最大数量就阻塞最小load量使用连接
     *
     * @param manager
     * @param config
     * @param callBack
     * @return
     */
    private static SockConnection getConnection(ConnectionManager manager,
                                                final CommunicationConfig config,
                                                final SockCallBack callBack) {

        synchronized (manager) {
            try {
                int curConnection = manager.getConnectionSize();

                LoggerUtil.debug(logger, config, "当前连接数", curConnection);

                //达到峰值，开始轮询最小load使用
                if (curConnection > 0
                    && curConnection == ConfigUtil.getSingleConnectionLimit(config)) {
                    SockConnection conn = manager.searchConnection();
                    if (conn != null) {
                        LoggerUtil.debug(logger, config, "查到可复用连接", conn);
                        return conn;
                    }
                }

                SockConnection sockConn = createSock(callBack, config);
                LoggerUtil.debug(logger, config, "新增连接", sockConn);
                manager.addConnection(sockConn);

                return sockConn;

            } catch (Exception e) {
                ExceptionUtil.caught(e, "连接池连接异常", config);
                throw new AnymockException(CommunicationErrorCode.SOCKET_CONNECTION_FAIL, e);

            }
        }
    }

    /**
     * 创建套接字
     * 
     * @param callBack
     * @param config
     * @return
     * @throws IOException
     */
    private static SockConnection createSock(SockCallBack callBack, final CommunicationConfig config)
                                                                                                     throws IOException {
        Socket sock = callBack.buildSock(config);
        return new SockConnection(sock);
    }

}
