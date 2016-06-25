/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.communication;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;

/**
 * 协议代理发送
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: ProxyProtocolSender.java, v 0.1 2011-10-21 上午10:35:31 zhao.xiong Exp $
 */
public interface ProxyProtocolSender {

    /**
     * 代理发送
     * 
     * @param config
     * @param request
     * @return
     */
    MessageSendResult send(final CommunicationConfig config, final String communicationId,
                           final MessageEnvelope request);

}
