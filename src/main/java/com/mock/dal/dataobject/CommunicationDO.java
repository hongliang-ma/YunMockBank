/*
 * Alipay.com Inc.
 * Copyright (c) 2004 - 2010 All Rights Reserved.
 * Powered By [rapid-generator]
 */

package com.mock.dal.dataobject;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * CommunicationDO
 * database table: anymock_communication
 * database table comments: Communication
 * This file is generated by <tt>dalgen</tt>, a DAL (Data Access Layer)
 * 
 * PLEASE DO NOT MODIFY THIS FILE MANUALLY, or else your modification may
 * be OVERWRITTEN by someone else. To modify the file, you should go to 
 * directory <tt>(project-home)/dalgen</tt>
 * @author badqiu(zhongxuan)
 * 
 */

public class CommunicationDO implements java.io.Serializable {
    private static final long serialVersionUID = -5216457518046898601L;

    /**
     * 通讯组件ID 		db_column: ID 
     */
    private String            id;
    /**
     * 连接类型（SERVER:服务端;CLIENT:客户端） 		db_column: CONNECT_TYPE 
     */
    private String            connectType;
    /**
     * 该地址是否作为转发地址，默认false 		db_column: ISTRANSFER 
     */
    private String            istransfer;
    /**
     * 协议类型（HTTP/HTTPS/TCP） 		db_column: PROTOCOL_TYPE 
     */
    private String            protocolType;
    /**
     * 字符集（例如:UTF-8） 		db_column: CHARSET 
     */
    private String            charset;
    /**
     * 地址描述（协议为http时为主地址,例如:http://zhongxinbank-line/ebank;协议为TCP时为tcp://地址:端口号） 		db_column: URI 
     */
    private String            uri;
    /**
     * 接收数据格式（TEXT:文本;BYTE:字节流;MAP:Map形式） 		db_column: RECV_DATA_TYPE 
     */
    private String            recvDataType;
    /**
     * 发送数据格式（TEXT:文本;BYTE:字节流;MAP:Map形式） 		db_column: SEND_DATA_TYPE 
     */
    private String            sendDataType;
    /**
     * 通讯属性 		db_column: PROPERTIES 
     */
    private String            properties;
    /**
     * 业务描述 		db_column: DESCRIPTION 
     */
    private String            description;
    /**
     * 最大并发数，用来限流。默认不限流 		db_column: MAX_COUNTER 
     */
    private int               maxCounter;
    /**
     * 创建时间 		db_column: GM_CREATED 
     */
    private Date              gmCreated;
    /**
     * 修改时间 		db_column: GM_MODIFIED 
     */
    private Date              gmModified;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public String getConnectType() {
        return this.connectType;
    }

    public void setIstransfer(String istransfer) {
        this.istransfer = istransfer;
    }

    public String getIstransfer() {
        return this.istransfer;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getProtocolType() {
        return this.protocolType;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public void setRecvDataType(String recvDataType) {
        this.recvDataType = recvDataType;
    }

    public String getRecvDataType() {
        return this.recvDataType;
    }

    public void setSendDataType(String sendDataType) {
        this.sendDataType = sendDataType;
    }

    public String getSendDataType() {
        return this.sendDataType;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return this.properties;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setMaxCounter(int maxCounter) {
        this.maxCounter = maxCounter;
    }

    public int getMaxCounter() {
        return this.maxCounter;
    }

    public void setGmCreated(Date gmCreated) {
        this.gmCreated = gmCreated;
    }

    public Date getGmCreated() {
        return this.gmCreated;
    }

    public void setGmModified(Date gmModified) {
        this.gmModified = gmModified;
    }

    public Date getGmModified() {
        return this.gmModified;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("Id", getId())
            .append("ConnectType", getConnectType()).append("Istransfer", getIstransfer())
            .append("ProtocolType", getProtocolType()).append("Charset", getCharset())
            .append("Uri", getUri()).append("RecvDataType", getRecvDataType())
            .append("SendDataType", getSendDataType()).append("Properties", getProperties())
            .append("Description", getDescription()).append("MaxCounter", getMaxCounter())
            .append("GmCreated", getGmCreated()).append("GmModified", getGmModified()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof CommunicationDO == false)
            return false;
        CommunicationDO other = (CommunicationDO) obj;
        return new EqualsBuilder().append(getId(), other.getId()).isEquals();
    }
}
