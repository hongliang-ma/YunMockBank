/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.communication;

import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;

/**
 * 通讯报文发送接口，其实现类要根据外部交易对应的协议及报文格式，来发送报文
 * 
 * @author hongliang.ma
 * @version $Id: MessageSender.java, v 0.1 2012-6-25 下午3:12:57 hongliang.ma Exp $
 */
public interface MessageSender {

    /**
     * 发送报文
     * @param communicationId
     * @param request
     * @return
     */
    public MessageSendResult send(String communicationId, MessageEnvelope request);
}
