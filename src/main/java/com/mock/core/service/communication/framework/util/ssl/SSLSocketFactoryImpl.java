/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;

/**
 * SSL套接字工厂，用于在现在socket上加入代理socket连接
 * 
 * @author zhao.chenz
 * @version $Id: SSLSocketFactoryImpl.java, v 0.1 2010-12-6 下午08:45:18 zhao.chenz Exp $
 */
public class SSLSocketFactoryImpl implements SecureProtocolSocketFactory {

    /** SSLSocketFactory */
    private final SSLSocketFactory    socketFactory;

    /** 交易通讯配置 */
    private final CommunicationConfig config;

    /**
     * @param socketFactory
     */
    public SSLSocketFactoryImpl(SSLSocketFactory socketFactory, CommunicationConfig config) {
        this.socketFactory = socketFactory;
        this.config = config;
    }

    /** 
     * @see org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
                                                                                             throws IOException {
        return createSocket(new InetSocketAddress(host, port), clientHost, clientPort);

    }

    /** 
     * @see org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket(String host, int port) throws IOException {
        return createSocket(new InetSocketAddress(host, port), null, 0);
    }

    /** 
     * @see org.apache.commons.httpclient.protocol.ProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int, org.apache.commons.httpclient.params.HttpConnectionParams)
     */
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
                               HttpConnectionParams params) throws IOException {
        return createSocket(new InetSocketAddress(host, port), localAddress, localPort);
    }

    /** 
     * @see org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                                                                                       throws IOException,
                                                                                       UnknownHostException {
        //此处不需要，没有建立连接过程
        return socketFactory.createSocket(socket, host, port, autoClose);
    }

    /**
     * 创建Socket连接
     * 
     * @param host
     * @param cHost
     * @param cPort
     * @return
     * @throws IOException
     */
    private Socket createSocket(InetSocketAddress host, InetAddress cHost, int cPort)
                                                                                     throws IOException {
        Socket sock = socketFactory.createSocket();
        sock.setKeepAlive(ConfigUtil.isKeepAlive(config));
        sock.setSoTimeout(config.getReadTimeout());
        sock.setSendBufferSize(config.getSendBufferSize());
        sock.setReceiveBufferSize(config.getReceiveBufferSize());
        if (cHost != null && cPort != 0) {
            sock.bind(new InetSocketAddress(cHost, cPort));
        }
        sock.connect(host, 20 * 1000);
        return sock;
    }

}
