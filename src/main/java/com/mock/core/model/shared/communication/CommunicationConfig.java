/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.communication;

import java.nio.charset.Charset;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.communication.config.PropertiesConfig;
import com.mock.core.model.shared.enums.PropertiesConfigEnum;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.core.model.shared.message.enums.MessageRunMode;

/**
 * 通讯配置模型
 * 
 * @author hongliang.ma
 * @version $Id: CommunicationConfig.java, v 0.1 2012-6-19 下午7:14:21 hongliang.ma Exp $
 */
public class CommunicationConfig {

    /** 是否服务端程序  默认：服务器程序 */
    private boolean                server            = true;

    /** 通讯协议类型 ,如TCP HTTP,WS等*/
    private TransportProtocol      protocol;

    private boolean                istransfer        = false;

    /** 处理模板的系统ID号 **/
    private String                 systemTemplateId;

    /** 通讯地址的ID号 **/
    private String                 communicationId;

    /**通讯地址  */
    private TransportURL           uri;

    /** 匹配方式说明*/
    private String                 description;

    /** 字符编码 默认：GBK */
    private Charset                charset           = Charset.forName("GBK");

    /** 发送消息类型 **/
    private MessageFormat          sendMessageFormat = MessageFormat.TEXT;

    /** 接收消息类型 **/
    private MessageFormat          recvMessageFormat = MessageFormat.TEXT;

    /** 运行模式*/
    private MessageRunMode         msgRunMode        = MessageRunMode.NORMAL;

    /** 只读ID，用来做性能和唯一用户的时候的关联用户配置ID */
    private String                 readUserConfigId;

    /** 连接超时 单位：ms 默认值20S */
    private final static int       connectTimeout    = 20 * 1000;

    /** 网络响应超时时间 单位：ms 默认值15S*/
    private final static int       readTimeout       = 15 * 1000;

    /** 发送缓冲区的大小  默认：8K*/
    private final static int       sendBufferSize    = 8 * 1024;

    /** 接收缓冲区的大小  默认：8K*/
    private final static int       receiveBufferSize = 8 * 1024;

    /** 通过属性配置 **/
    private final PropertiesConfig properties        = new PropertiesConfig();

    /** 链接最大并发数 100 */
    private int                    maxTotalConn      = 100;

    /** 是否对最大并发数进行限制 */
    private boolean                limitConnection   = false;

    /**
     * Getter method for property <tt>server</tt>.
     * 
     * @return property value of server
     */
    public boolean isServer() {
        return server;
    }

    /**
     * Getter method for property <tt>connecttimeout</tt>.
     * 
     * @return property value of connectTimeout
     */
    public int getConnecttimeout() {
        return connectTimeout;
    }

    /**
     * Setter method for property <tt>server</tt>.
     * 
     * @param server value to be assigned to property server
     */
    public void setServer(boolean server) {
        this.server = server;
    }

    /**
     * Getter method for property <tt>protocol</tt>.
     * 
     * @return property value of protocol
     */
    public TransportProtocol getProtocol() {
        return protocol;
    }

    /**
     * Setter method for property <tt>protocol</tt>.
     * 
     * @param protocol value to be assigned to property protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = TransportProtocol.getEnumByCode(protocol);
    }

    /**
     * Getter method for property <tt>charset</tt>.
     * 
     * @return property value of charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Setter method for property <tt>charset</tt>.
     * 
     * @param charset value to be assigned to property charset
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Getter method for property <tt>uri</tt>.
     * 
     * @return property value of uri
     */
    public TransportURL getUri() {
        return uri;
    }

    /**
     * Setter method for property <tt>uri</tt>.
     * 
     * @param uri value to be assigned to property uri
     */
    public void setUri(String uri) {
        this.uri = new TransportURL(uri);
    }

    /**
     * Getter method for property <tt>sendMessageFormat</tt>.
     * 
     * @return property value of sendMessageFormat
     */
    public MessageFormat getSendMessageFormat() {
        return sendMessageFormat;
    }

    /**
     * Setter method for property <tt>sendMessageFormat</tt>.
     * 
     * @param sendMessageFormat value to be assigned to property sendMessageFormat
     */
    public void setSendMessageFormat(MessageFormat sendMessageFormat) {
        this.sendMessageFormat = sendMessageFormat;
    }

    /**
     * Getter method for property <tt>recvMessageFormat</tt>.
     * 
     * @return property value of recvMessageFormat
     */
    public MessageFormat getRecvMessageFormat() {
        return recvMessageFormat;
    }

    /**
     * Setter method for property <tt>recvMessageFormat</tt>.
     * 
     * @param recvMessageFormat value to be assigned to property recvMessageFormat
     */
    public void setRecvMessageFormat(MessageFormat recvMessageFormat) {
        this.recvMessageFormat = recvMessageFormat;
    }

    /**
     * Getter method for property <tt>readTimeout</tt>.
     * 
     * @return property value of readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Getter method for property <tt>sendBufferSize</tt>.
     * 
     * @return property value of sendBufferSize
     */
    public int getSendBufferSize() {
        return sendBufferSize;
    }

    /**
     * Getter method for property <tt>receiveBufferSize</tt>.
     * 
     * @return property value of receiveBufferSize
     */
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    /**
     * Getter method for property <tt>properties</tt>.
     * 
     * @return property value of properties
     */
    public String getProperties(PropertiesConfigEnum configEnum) {
        return properties.getPropertyByName(configEnum);
    }

    /**
     * Setter method for property <tt>properties</tt>.
     * 
     * @param properties value to be assigned to property properties
     */
    public void setProperties(String properties) {
        this.properties.setProperties(properties);
    }

    /**
     * Getter method for property <tt>maxTotalConn</tt>.
     * 
     * @return property value of maxTotalConn
     */
    public int getMaxTotalConn() {
        return maxTotalConn;
    }

    /**
     * Setter method for property <tt>maxTotalConn</tt>.
     * 
     * @param maxTotalConn value to be assigned to property maxTotalConn
     */
    public void setMaxTotalConn(int maxTotalConn) {
        this.maxTotalConn = maxTotalConn;
        if (maxTotalConn >= 0) {
            limitConnection = true;
        }
    }

    /**
     * Getter method for property <tt>limitConnection</tt>.
     * 
     * @return property value of limitConnection
     */
    public boolean isLimitConnection() {
        return limitConnection;
    }

    public String getHost() {
        return uri.getHost();
    }

    public int getPort() {
        return uri.getPort();
    }

    /**
     * 判断是否支持双向SSL协议双向认证:<br>
     * 1. 配置为HTTPS协议或者SSL协议<br>
     * 2. 通讯配置设置了BCM_CODE，能够获取相关的证书信息
     * 
     * @return
     */
    public boolean isTwoWaySSL() {
        return (protocol == TransportProtocol.HTTPS || protocol == TransportProtocol.SSL);
    }

    /**
     * 获取代理bean的名称
     * @return
     */
    public String getProxyBean() {
        return StringUtil.defaultIfBlank(getProperties(PropertiesConfigEnum.PROXY_PROTOCOL_BEAN));
    }

    /**
     * 获取通讯组件唯一标识
     * 
     * @return
     */
    public String getKey() {
        return Constant.getKey(uri.getUrl());
    }

    /**
     * Getter method for property <tt>templateInnerID</tt>.
     * 
     * @return property value of templateInnerID
     */
    public String getSysTemplateId() {
        return systemTemplateId;
    }

    /**
     * Setter method for property <tt>templateInnerID</tt>.
     * 
     * @param systemTemplateId value to be assigned to property templateInnerID
     */
    public void setSysTemplateId(String systemTemplateId) {
        this.systemTemplateId = systemTemplateId;
    }

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for property <tt>description</tt>.
     * 
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter method for property <tt>communicationId</tt>.
     * 
     * @return property value of communicationId
     */
    public String getCommunicationId() {
        return communicationId;
    }

    /**
     * Setter method for property <tt>communicationId</tt>.
     * 
     * @param communicationId value to be assigned to property communicationId
     */
    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    /**
     * Getter method for property <tt>istransfer</tt>.
     * 
     * @return property value of istransfer
     */
    public boolean isIstransfer() {
        return istransfer;
    }

    /**
     * Setter method for property <tt>istransfer</tt>.
     * 
     * @param istransfer value to be assigned to property istransfer
     */
    public void setIstransfer(boolean istransfer) {
        this.istransfer = istransfer;
    }

    /**
     * Getter method for property <tt>msgRunMode</tt>.
     * 
     * @return property value of msgRunMode
     */
    public final MessageRunMode getMsgRunMode() {
        return msgRunMode;
    }

    /**
     * Setter method for property <tt>msgRunMode</tt>.
     * 
     * @param msgRunMode value to be assigned to property msgRunMode
     */
    public final void setMsgRunMode(MessageRunMode msgRunMode) {
        this.msgRunMode = msgRunMode;
    }

    /**
     * Getter method for property <tt>readUserConfigId</tt>.
     * 
     * @return property value of readUserConfigId
     */
    public final String getReadUserConfigId() {
        return readUserConfigId;
    }

    /**
     * Setter method for property <tt>readUserConfigId</tt>.
     * 
     * @param readUserConfigId value to be assigned to property readUserConfigId
     */
    public final void setReadUserConfigId(String readUserConfigId) {
        this.readUserConfigId = readUserConfigId;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}