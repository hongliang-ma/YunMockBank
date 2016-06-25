/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.model.CommunicationContext.ParamType;
import com.mock.core.service.communication.framework.protocol.ClientProtocolHandler;
import com.mock.core.service.communication.framework.protocol.ProtocolFactory;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.core.service.communication.framework.util.ssl.SSLProtocolHelper;
import com.mock.core.service.communication.framework.util.tcp.ConnectionPool;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * TCP客户端，用于发送与接收
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: TcpClient.java, v 0.1 2011-9-2 下午02:42:24 zhao.xiong Exp $
 */
public class TcpClient implements SockCallBack {

    /** logger */
    protected static final Logger     logger = LoggerFactory.getLogger(TcpClient.class);

    /** 客户端协议 **/
    private ClientProtocolHandler     handler;

    /** 通讯配置**/
    private final CommunicationConfig config;

    /** cached Context,即通信证书和密码 **/
    private SSLContext                sslContext;

    /**
     * @param config
     */
    public TcpClient(CommunicationConfig config) {

        this.config = config;

        //协议实例初始化
        try {
            handler = ProtocolFactory.getClientProtocolHandler(config.getProtocol()).newInstance();
        } catch (Exception e) {
            throw new AnymockException(CommunicationErrorCode.PROTOCOL_EXCEPTION, e);
        }

        handler.setConfig(config);
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.tcp.SockCallBack#buildSock(com.mock.core.model.shared.communication.CommunicationConfig)
     */
    public Socket buildSock(CommunicationConfig inputConfig) {
        try {
            Socket socket = registerProtocol();
            socket.setKeepAlive(ConfigUtil.isKeepAlive(inputConfig));
            socket.setSoTimeout(inputConfig.getConnecttimeout());
            socket.setReceiveBufferSize(inputConfig.getReceiveBufferSize());
            socket.setSendBufferSize(inputConfig.getSendBufferSize());
            return socket;
        } catch (Exception e) {
            LoggerUtil.warn(logger, "无法连接远程服务器:", inputConfig);
            throw new AnymockException(CommunicationErrorCode.SOCKET_CONNECTION_FAIL, e);
        }
    }

    /**
     * 注册协议
     * 
     * @return
     * @throws IOException
     */
    private Socket registerProtocol() throws IOException {
        Socket sock;
        InetSocketAddress addr = getAddr(config);
        LoggerUtil.debug(logger, "获取地址==>" + addr);
        AssertUtil.isTrue(null != addr, CommunicationErrorCode.EMPTY_CONFIG);

        if (config.getProtocol() != TransportProtocol.SSL) {
            sock = new Socket();
            sock.connect(addr, config.getConnecttimeout());
            return sock;
        }
        if (sslContext == null) {
            sslContext = SSLProtocolHelper.create();
        }
        sock = sslContext.getSocketFactory().createSocket();
        sock.connect(addr, config.getConnecttimeout());
        return sock;
    }

    /**
     * 获取TCP通讯地址
     * 
     * @param tcpConfig
     * @return
     */
    protected InetSocketAddress getAddr(CommunicationConfig tcpConfig) {
        return new InetSocketAddress(tcpConfig.getHost(), tcpConfig.getPort());
    }

    /**
     * 协议选择器
     * 
     * @return
     * @throws IOException 
     */
    private CommunicationContext getCommunicationContext() throws IOException {
        CommunicationContext context = new CommunicationContext();

        if (ConfigUtil.isKeepAlive(config)) {
            context.addParam(ParamType.TCP_CLIENT, this);
        } else {
            context.addParam(ParamType.SHORT_TCP_CONNECTION, buildSock(config));
        }

        return context;
    }

    /**
     * 客户端发送封装
     * 
     * @param request
     * @return
     */
    public MessageSendResult send(MessageEnvelope request) {

        boolean timeout = false;
        boolean hasResponse = false;
        MessageEnvelope responseMsg = null;

        try {
            responseMsg = handler.handle(request, getCommunicationContext());
            hasResponse = (responseMsg == null) ? false : true;
        } catch (SocketTimeoutException e) {
            ExceptionUtil.caught(e, "TCP客户端发送超时", config);

            timeout = true;
        } catch (TimeoutException e) {

            ExceptionUtil.caught(e, "TCP客户端发送超时", config);

            timeout = true;

        } catch (Exception e) {

            ExceptionUtil.caught(e, "TCP客户端发送异常", config);

            throw new AnymockException(CommunicationErrorCode.IO_EXCEPTION, e);

        }

        return new MessageSendResult(responseMsg, timeout, hasResponse);
    }

    /**
     * dispose所有连接
     */
    public void dispose() {
        if (ConfigUtil.isKeepAlive(config)) {
            ConnectionPool.removeAllByConfig(config);
        }
    }

    /**
     * Getter method for property <tt>config</tt>.
     * 
     * @return property value of config
     */
    public CommunicationConfig getConfig() {
        return config;
    }

}
