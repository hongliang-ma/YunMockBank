/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.SSLFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.protocol.ProtocolFactory;
import com.mock.core.service.communication.framework.protocol.ServerProtocolHandler;
import com.mock.core.service.communication.framework.util.codec.DefaultProtocolCodecFactory;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.core.service.communication.framework.util.ssl.SSLProtocolHelper;
import com.mock.core.service.communication.framework.util.tcp.CommunicationThreadPool;
import com.mock.core.service.shared.communication.MessageReceiver;

/**
 * TCP服务端
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: TcpServer.java, v 0.1 2011-9-2 下午04:53:47 zhao.xiong Exp $
 */
public class TcpServer {

    /** 服务端处理**/
    private ServerProtocolHandler     handler;

    /** Mina SocketAcceptor **/
    private SocketAcceptor            acceptor;

    /** 绑定到当前端口的的通讯配置 */
    private final CommunicationConfig configs;

    /**
     * @param configs
     * @param messageReceiver
     */
    public TcpServer(CommunicationConfig configs, MessageReceiver messageReceiver) {
        this.configs = configs;
        AssertUtil.isNotNull(configs, CommunicationErrorCode.EMPTY_CONFIG);

        try {
            TransportProtocol protocol = configs.getProtocol();
            handler = ProtocolFactory.getServerProtocolHandler(protocol).newInstance();
        } catch (Exception e) {
            throw new AnymockException(CommunicationErrorCode.PROTOCOL_EXCEPTION, e);
        }

        handler.setConfigList(configs);
        handler.setMessageReceiver(messageReceiver);
    }

    /**
     * 停止监听服务
     */
    public void dispose() {
        if (acceptor != null) {
            acceptor.unbindAll();
        }
    }

    /**
     *  启动服务端监听
     * 
     * @param listener
     * @throws IOException
     */
    public void startup() throws IOException {

        CommunicationConfig networkConfig = configs;

        /* 设置内存获取方式 */
        ByteBuffer.allocate(1024, false).setAutoExpand(true);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        acceptor = new SocketAcceptor(availableProcessors + 1,
            CommunicationThreadPool.getSeperatorPool(networkConfig));

        // SSL连接属性设置,责任链模式，必须在解码器链前面
        initSSLConfig(networkConfig);

        // 初始化Socket接收连接
        initSocketAcceptorConfig(networkConfig);

        acceptor.bind(new InetSocketAddress(networkConfig.getPort()),
            TcpServerListenerContext.getListener(NetworkConfigCache.getTcpKey(networkConfig)));

    }

    /**
     * 初始化Socket接收连接
     * @param networkConfig
     */
    private void initSocketAcceptorConfig(CommunicationConfig networkConfig) {
        SocketAcceptorConfig config = acceptor.getDefaultConfig();
        config.setReuseAddress(true);
        config.setThreadModel(ThreadModel.MANUAL);

        // 设置缓冲区大小        
        config.getSessionConfig().setReceiveBufferSize(networkConfig.getReceiveBufferSize());
        config.getSessionConfig().setSendBufferSize(networkConfig.getSendBufferSize());

        // 设置连接类型
        config.getSessionConfig().setKeepAlive(ConfigUtil.isKeepAlive(networkConfig));

        ProtocolCodecFilter filter = new ProtocolCodecFilter(new DefaultProtocolCodecFactory(
            networkConfig));
        acceptor.getFilterChain().addLast("codec", filter);
        acceptor.getFilterChain().addLast("threadPool",
            new ExecutorFilter(CommunicationThreadPool.getSeperatorPool(networkConfig)));
    }

    /**
     * SSL连接属性设置
     * 
     * @param config
     */
    private void initSSLConfig(CommunicationConfig config) {
        if (config.getProtocol() == TransportProtocol.SSL) {
            SSLContext sslContext = SSLProtocolHelper.create();
            SSLFilter connectorTLSFilter = new SSLFilter(sslContext);

            //只做单向认证
            connectorTLSFilter.setNeedClientAuth(false);

            // 设置加密过滤器
            acceptor.getFilterChain().addLast("SSL", connectorTLSFilter);
        }
    }

    /**
     * 获取连接数
     * 
     * @return
     */
    public int getConnectionCount() {
        CommunicationConfig networkConfig = configs;

        return acceptor.getManagedSessions(
            new InetSocketAddress(networkConfig != null ? networkConfig.getPort() : 0)).size();
    }

    /**
     * Getter method for property <tt>handler</tt>.
     * 
     * @return property value of handler
     */
    public ServerProtocolHandler getHandler() {
        return handler;
    }

    /**
     * Getter method for property <tt>configs</tt>.
     * 
     * @return property value of configs
     */
    public final CommunicationConfig getConfigs() {
        return configs;
    }

}
