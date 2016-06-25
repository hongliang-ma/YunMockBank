/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.common.util.lang.StringUtil;

import com.sun.net.ssl.internal.ssl.Provider;

/**
 * 创建SSLContext工厂类，使用bcm提供的证书来初始化SSLContext
 * 当没有证书时，会建立永久信任连接
 * 
 * @author zhao.chenz
 * @author zhao.xiong
 * @author peng.lanqp
 * @author 松雪
 * @version $Id: SSLContextFactory.java, v 0.2 2012-4-11 下午5:23:36 hao.zhang Exp $
 */
public final class SSLContextFactory {

    /** 协议类型 */
    private static final String      PROTOCOL  = "TLS";

    /** 证书工厂算法  */
    private static final String      ALGORITHM = "SunX509";

    /** ssl上下文工厂类实例 */
    private static SSLContextFactory instance  = null;

    /**
     * 禁用构造函数
     */
    private SSLContextFactory() {
        // 禁用构造函数
    }

    /**
     * SSLContext单例工厂类实现方式
     * 
     * @return
     * @throws GeneralSecurityException
     */
    public static SSLContextFactory getInstance() throws GeneralSecurityException {
        synchronized (SSLContextFactory.class) {
            if (instance == null) {
                instance = new SSLContextFactory();
            }
            return instance;
        }
    }

    /**
     * 创建SSL上下文
     * 
     * @param certStream 证书内容字节流
     * @param certPassword 证书密码
     * @param privateKeyPassword 私钥密码
     * 
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public SSLContext createSSLContext(InputStream certStream, String certPassword,
                                       String privateKeyPassword) throws GeneralSecurityException,
                                                                 IOException {
        if (certStream == null) {
            throw new AnymockException(CommunicationErrorCode.KEY_SECURITY_EXCEPTION,
                "通过BCM系统获取证书信息不完整");
        }

        // 注意： 此处如果你的Eclipse报编译错误：
        // The constructor Provider() is not accessible due to restriction on required library
        // 说明你用的是使用jdk来编译的，换成用jre来编译就可以解决编译问题啦
        Provider tProvider = new Provider();
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL, tProvider);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(ALGORITHM, tProvider);
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(ALGORITHM, tProvider);

        try {
            // 使用输入流和证书密码来加载证书，证书的字节流和密码应来自bcm
            char[] certPasswordChars = getCharArray(certPassword);
            keyStore.load(certStream, certPasswordChars);

            // 使用KeyStore对象实例化TrustManagerFactory
            trustFactory.init(keyStore);

            if (null == privateKeyPassword) {
                // 如果未设置私钥密码，使用证书密码初始化KeyManagerFactory
                keyFactory.init(keyStore, certPasswordChars);
            } else {
                // 设置了私钥密码，使用私钥密码初始化KeyManagerFactory
                keyFactory.init(keyStore, getCharArray(privateKeyPassword));
            }

            // 初始化SSL上下文
            sslContext.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), null);

        } finally {
            // 关闭证书字节流
            IOUtils.closeQuietly(certStream);
        }

        return sslContext;
    }

    /**
     * 单向连接，当没有证书时，可以建立这种永远信任的连接
     * 
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public SSLContext createTrustSSLContext() throws GeneralSecurityException, IOException {
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        TrustManager[] tm = { getForeverTrusterManager() };
        sslContext.init(null, tm, new SecureRandom());
        return sslContext;
    }

    /**
     * 将string格式的密码转换为char数组格式
     * 
     * @param password
     * @return
     */
    private static char[] getCharArray(String password) {
        if (StringUtil.isNotEmpty(password)) {
            return password.toCharArray();
        }
        return null;
    }

    /**
     * 获取永远信任的证书管理器
     * 
     * @return
     */
    private TrustManager getForeverTrusterManager() {
        return new X509TrustManager() {

            /** 
             * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
             */
            public void checkClientTrusted(X509Certificate[] input, String authType)
                                                                                    throws CertificateException {
                //建立这种永远信任的连接，不需要校验
            }

            /** 
             * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
             */
            public void checkServerTrusted(X509Certificate[] input, String arg1)
                                                                                throws CertificateException {
                //建立这种永远信任的连接，不需要校验
            }

            /** 
             * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
             */
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

        };
    }
}
