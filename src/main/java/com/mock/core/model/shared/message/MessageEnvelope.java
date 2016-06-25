/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.message.enums.MessageFormat;

/**
 * 报文信息载体，可以简单理解为“信封”<br>
 * 组装之后的请求报文和等待解析的响应报文，可以形象理解为保存在信封中，邮寄来邮寄去，<br>
 * 
 * 于是，通讯组件的数据请求和响应都是基于这个信息载体
 * 
 * @author hongliang.ma
 * @version $Id: MessageEnvelope.java, v 0.1 2012-6-20 上午10:43:51 hongliang.ma Exp $
 */
public final class MessageEnvelope implements Serializable {

    /** serialVersionUID */
    private static final long   serialVersionUID = 5294001038813463863L;

    /** 报文正文格式 */
    private MessageFormat       messageFormat;

    /** 报文正文信息 */
    private Object              content;

    /** 报文日志描述，可以屏蔽敏感信息 */
    private String              logContent;

    /** 报文附加信息，例如HTTP报文头的附加信息 */
    private Map<String, String> extraContent;

    /** 网关返回码 */
    private String              code;

    /** 网关返回码描述  */
    private String              description;

    /**
     * 构造函数
     * @param code
     * @param description
     */
    public MessageEnvelope(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 构造函数
     * @param messageFormat
     * @param content
     */
    public MessageEnvelope(MessageFormat messageFormat, Object content) {
        this.messageFormat = messageFormat;
        this.content = content;
        this.extraContent = new HashMap<String, String>();
    }

    /**
     * 构造函数
     * @param format
     * @param content
     * @param extraContent
     */
    public MessageEnvelope(MessageFormat format, Object content, Map<String, String> extraContent) {
        this.messageFormat = format;
        this.content = content;
        if (extraContent != null) {
            this.extraContent = extraContent;
        } else {
            this.extraContent = new HashMap<String, String>();
        }
    }

    /**
     * 错误码为空的时候，说明报文渲染成功了，可以获取content
     * 
     * @return
     */
    public boolean isSuccess() {
        return StringUtil.isBlank(code);
    }

    /**
     * 检查信息是否为空
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean isEmpty() {
        if (this.content == null) {
            return true;
        }

        boolean empty = true;
        switch (messageFormat) {
            case TEXT: {
                empty = ((String) this.content).isEmpty();
                break;
            }
            case MAP: {
                empty = ((Map) this.content).isEmpty();
                break;
            }
            case BYTE: {
                empty = ((byte[]) this.content).length == 0;
                break;
            }
            default: {
                return empty;
            }
        }

        return empty;
    }

    /**
     * Getter method for property <tt>messageFormat</tt>.
     * 
     * @return property value of messageFormat
     */
    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    /**
     * Getter method for property <tt>content</tt>.
     * 
     * @return property value of content
     */
    public Object getContent() {
        return isSuccess() ? content : code;
    }

    /**
     * Setter method for property <tt>content</tt>.
     * 
     * @param content value to be assigned to property content
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * Getter method for property <tt>extraContent</tt>.
     * 
     * @return property value of extraContent
     */
    public Map<String, String> getExtraContent() {
        return extraContent;
    }

    /** 
     * Setter method for property <tt>extraContent</tt>.
     * 
     * @param extraContent value to be assigned to property extraContent
     */
    public void setExtraContent(Map<String, String> extraContent) {
        this.extraContent = extraContent;
    }



    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public String getCode() {
        return code;
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
     * Setter method for property <tt>code</tt>.
     * 
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
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
     * Getter method for property <tt>logContent</tt>.
     * 
     * @return property value of logContent
     */
    public String getLogContent() {
        return logContent;
    }

    /**
     * Setter method for property <tt>logContent</tt>.
     * 
     * @param logContent value to be assigned to property logContent
     */
    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder(" MessageEnvelope[");
        if (!isSuccess()) {
            retValue.append("code=").append(code).append(',');
            retValue.append("description=").append(description);
            retValue.append(']');
            return retValue.toString();
        }

        retValue.append("messageFormat=").append(this.messageFormat).append(',');
        retValue.append("logContent=").append(this.logContent);

        retValue.append(",extraContent=").append(this.extraContent);
        retValue.append(']');
        return retValue.toString();
    }
}
