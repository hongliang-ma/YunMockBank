
package com.mock.core.model.transaction.template;

import java.nio.charset.Charset;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.message.enums.MessageFormat;

/**
 * 部分常用的通讯属性
 * @author hongliang.ma
 * @version $Id: CommunicationChange.java, v 0.1 2012-11-15 下午1:55:17 hongliang.ma Exp $
 */
public final class CommunicationPart {
    private boolean           serverType;
    private TransportProtocol protocol;
    private String            url;
    private String            description;
    private Charset           charset;
    private MessageFormat     sendMessageFormat;
    private MessageFormat     recvMessageFormat;
    private String            mapProperties;

    /**
     * Getter method for property <tt>serverType</tt>.
     * 
     * @return property value of serverType
     */
    public boolean isServerType() {
        return serverType;
    }

    /**
     * Setter method for property <tt>serverType</tt>.
     * 
     * @param serverType value to be assigned to property serverType
     */
    public void setServerType(boolean serverType) {
        this.serverType = serverType;
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
    public void setProtocol(TransportProtocol protocol) {
        this.protocol = protocol;
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
     * Setter method for property <tt>url</tt>.
     * 
     * @param url value to be assigned to property url
     */
    public void setUrl(String url) {
        this.url = url;
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
     * Getter method for property <tt>mapProperties</tt>.
     * 
     * @return property value of mapProperties
     */
    public String getMapProperties() {
        return mapProperties;
    }

    /**
     * Setter method for property <tt>mapProperties</tt>.
     * 
     * @param mapProperties value to be assigned to property mapProperties
     */
    public void setMapProperties(String mapProperties) {
        this.mapProperties = mapProperties;
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
