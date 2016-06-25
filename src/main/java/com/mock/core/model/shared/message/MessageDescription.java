/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 请求信息描述类，包含了一次交易请求所包含的全部数据<br>
 * 任何内部系统或外部系统的请求，会首先转换为此类对象，再交给路由引擎的路由适配器处理
 * 
 * @author hongliang.ma
 * @version $Id: MessageDescription.java, v 0.1 2012-8-14 下午3:21:36 hongliang.ma Exp $
 */
public class MessageDescription {

    /** 系统模板 */
    private String                sysInnerid;

    /**
     * 转发的匹配ID
     */
    private String                transferId;

    /**
     * 异步时的匹配值
     */
    private String                transCodeRule;

    /** 通讯组件id */
    private final String          conmunicaionId;

    /** 报文数据  */
    private final MessageEnvelope messageEnvelope;

    /**
     * 构造函数
     * @param messageEnvelope
     */
    public MessageDescription(String conmunicaionId, MessageEnvelope messageEnvelope) {
        this.conmunicaionId = conmunicaionId;
        this.messageEnvelope = messageEnvelope;
    }

    /**
     * Getter method for property <tt>conmunicaionId</tt>.
     * 
     * @return property value of conmunicaionId
     */
    public String getConmunicaionId() {
        return conmunicaionId;
    }

    /**
     * Getter method for property <tt>messageEnvelope</tt>.
     * 
     * @return property value of messageEnvelope
     */
    public MessageEnvelope getMessageEnvelope() {
        return messageEnvelope;
    }

    /**
     * Getter method for property <tt>sysInnerid</tt>.
     * 
     * @return property value of sysInnerid
     */
    public String getSysInnerid() {
        return sysInnerid;
    }

    /**
     * Setter method for property <tt>sysInnerid</tt>.
     * 
     * @param sysInnerid value to be assigned to property sysInnerid
     */
    public void setSysInnerid(String sysInnerid) {
        this.sysInnerid = sysInnerid;
    }

    /**
     * Getter method for property <tt>transferId</tt>.
     * 
     * @return property value of transferId
     */
    public String getTransferId() {
        return transferId;
    }

    /**
     * Setter method for property <tt>transferId</tt>.
     * 
     * @param transferId value to be assigned to property transferId
     */
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    /**
     * Getter method for property <tt>transCodeRule</tt>.
     * 
     * @return property value of transCodeRule
     */
    public String getTransCodeRule() {
        return transCodeRule;
    }

    /**
     * Setter method for property <tt>transCodeRule</tt>.
     * 
     * @param transCodeRule value to be assigned to property transCodeRule
     */
    public void setTransCodeRule(String transCodeRule) {
        this.transCodeRule = transCodeRule;
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
