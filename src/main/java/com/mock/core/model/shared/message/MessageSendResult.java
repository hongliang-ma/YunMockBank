/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.message;

/**
 * 消息发送响应结果
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: MessageSendResult.java, v 0.1 2011-9-1 上午10:17:58 zhao.xiong Exp $
 */
public class MessageSendResult {

    /** 消息响应内容 **/
    private final MessageEnvelope messageEnvelope;

    /** 是否超时**/
    private final boolean         timeout;

    /** 是否有响应**/
    private final boolean         response;

    /**
     * @param messageEnvelope
     * @param timeout
     * @param response
     */
    public MessageSendResult(MessageEnvelope messageEnvelope, boolean timeout, boolean response) {
        this.messageEnvelope = messageEnvelope;
        this.timeout = timeout;
        this.response = response;
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
     * Getter method for property <tt>timeout</tt>.
     * 
     * @return property value of timeout
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * Getter method for property <tt>response</tt>.
     * 
     * @return property value of response
     */
    public boolean isResponse() {
        return response;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder("MessageSendResult[");
        retValue.append("timeout=").append(this.timeout).append(',');
        retValue.append("response=").append(this.response);
        retValue.append(']');

        return retValue.toString();
    }

}
