/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.communication;

import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;

/**
 * 消息接收接口，供通讯组件调用，将收到的消息组装成MessageDescription对象后传递给路由引擎<br>
 * 路由引擎处理交易流程完成后，返回给通讯组件MessageEnvelope对象，由通讯组件返回响应报文
 * 
 * @author hongliang.ma
 * @version $Id: MessageReceiver.java, v 0.1 2012-6-25 下午3:13:08 hongliang.ma Exp $
 */
public interface MessageReceiver {

    /**
     * 接收消息，处理后返回
     * 
     * @param description 消息内容
     * @sendDirect  发送请求还是处理报文请求，true为发送请求
     * @return
     */
    public MessageEnvelope receive(MessageDescription description, boolean sendDirect);
}
