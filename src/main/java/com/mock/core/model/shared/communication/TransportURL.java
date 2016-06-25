/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.communication;

import java.net.InetSocketAddress;

import org.apache.commons.lang.math.NumberUtils;

import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;

/**
 *  <p>URL封装，对URL进行协议、地址、格式的进行合法性校验并进行格式转换</p>
 *  <p>example:</p>
 *  <p>TransportURL url = new TransportURL("tcp://localhost:8080/hello");</p>
 *  <p>transport=tcp, host=localhost, port=8080</p>
 *  <p>TransportURL url = new TransportURL("https://localhost:8446,443/hello");</p>
 *  <p>transport=http, host=localhost, port=443,url=https://localhost:8446/hello</p>
 * @author hongliang.ma
 * @version $Id: TransportURL.java, v 0.1 2010-12-14 上午11:11:45  hongliang.ma Exp $
 */
public class TransportURL {

    /** HTTPS协议 默认端口 */
    private static final int  HTTPS_DEFAULT_PORT = 443;

    /** HTTP协议 默认端口 */
    private static final int  HTTP_DEFAULT_PORT  = 80;

    /** URL格式 tcp://localhost:8080/hello */
    private String            url;

    /** 传输协议 */
    private TransportProtocol transport;

    /** TCP的地址 */
    private String            host;

    /** TCP端口 */
    private int               port;

    /**
     * @param url
     */
    public TransportURL(String url) {

        AssertUtil.isNotBlank(url, CommunicationErrorCode.ILLGE_PARAMS);

        //1. 解析传输协议
        parseTransportURL(url);

        //2. 重置检查Host信息
        reCheckHostInfo();
    }

    /**
     * 重置检查Host信息，处理默认端口
     */
    private void reCheckHostInfo() {
        switch (transport) {
            case FTP:
            case TCP:
            case SSL: {
                InetSocketAddress addr = new InetSocketAddress(this.host, this.port);
                AssertUtil.isNotNull(addr.getAddress(), CommunicationErrorCode.ILLGE_PARAMS);
                this.host = addr.getAddress().getHostAddress();
                this.port = addr.getPort();
                break;
            }
            case HTTP:
                this.port = this.port < 0 ? HTTP_DEFAULT_PORT : this.port;
                break;
            default: {
                this.port = this.port < 0 ? HTTPS_DEFAULT_PORT : this.port;
                break;
            }
        }
    }

    /**
     * 解析协议
     * 
     * @param uri
     */
    private void parseTransportURL(String uri) {
        try {
            String schema = getProtocol(uri);
            this.transport = TransportProtocol.getEnumByCode(schema);
            String[] protocolInfo = getProtocolInfo(uri);
            this.host = protocolInfo[0];
            if (protocolInfo.length == 3) {
                int idx = -1;
                //里面含有代理端口
                if ((idx = protocolInfo[1].indexOf(',')) > -1) {
                    this.url = schema + "://" + this.host + ":" + protocolInfo[1].substring(0, idx)
                               + protocolInfo[2];
                    this.port = NumberUtils.toInt(protocolInfo[1].substring(idx + 1), -1);
                } else {
                    this.url = uri;
                    this.port = NumberUtils.toInt(protocolInfo[1], -1);
                }
            }

        } catch (Exception e) {
            throw new AnymockException(CommunicationErrorCode.ILLGE_PARAMS, e);
        }

    }

    /**
     * 解析传输协议
     * 
     * @param inputUrl
     * @return
     */
    private String getProtocol(String inputUrl) {
        return inputUrl.substring(0, inputUrl.indexOf("://"));
    }

    /**
     * 解析传输协议
     * 
     * @param inputUrl
     * @return
     */
    private String[] getProtocolInfo(String inputUrl) {
        int idx1 = inputUrl.indexOf("://");
        String subUrl = inputUrl.substring(idx1 + 3);
        int idx2 = subUrl.indexOf('/');
        String queryPart = "";
        //如果不存在，以最后长度结尾
        if (idx2 < 0) {
            idx2 = subUrl.length();
        } else {
            queryPart = subUrl.substring(idx2);
        }
        String[] result = new String[3];
        String[] domainPart = subUrl.substring(0, idx2).split("\\:");
        result[0] = domainPart[0];
        result[1] = domainPart.length > 1 ? domainPart[1] : "";
        result[2] = queryPart;
        return result;
    }

    /**
     * Getter method for property <tt>url</tt>.
     * 
     * @return property value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Getter method for property <tt>transport</tt>.
     * 
     * @return property value of transport
     */
    public TransportProtocol getTransport() {
        return transport;
    }

    /**
     * Getter method for property <tt>host</tt>.
     * 
     * @return property value of host
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter method for property <tt>port</tt>.
     * 
     * @return property value of port
     */
    public int getPort() {
        return port;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder("TransportURL[");
        retValue.append("url=").append(this.url).append(',');
        retValue.append("transport=").append(this.transport).append(',');
        retValue.append("host=").append(this.host).append(',');
        retValue.append("port=").append(this.port);
        retValue.append(']');
        return retValue.toString();
    }

}