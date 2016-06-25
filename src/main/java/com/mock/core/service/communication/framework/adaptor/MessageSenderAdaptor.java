/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.adaptor;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.ClientInstanceManager;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.shared.communication.MessageSender;
import com.mock.core.service.shared.communication.ProxyProtocolSender;
import com.mock.common.util.LoggerUtil;

/**
 * 消息发送实现适配器，这里会找到对应的协议实例管理器，然后进行处理
 * 
 * 
 * @author hongliang.ma
 * @version $Id: MessageSenderAdaptor.java, v 0.1 2012-6-25 下午3:07:40 hongliang.ma Exp $
 */
public class MessageSenderAdaptor implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderAdaptor.class);

    /** 代理发送器,实现于通讯plugin **/
    private ProxyProtocolSender proxyProtocolSender;

    /** 
     * @see com.mock.core.service.shared.communication.MessageSender#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.mock.core.model.communication.message.domain.MessageEnvelope)
     */
    public MessageSendResult send(String communicationId, MessageEnvelope request) {
        AssertUtil.isNotNull(request, CommunicationErrorCode.REQUEST_IS_NULL);

        // 通过ID去缓存中取通讯配置
        CommunicationConfig config = NetworkConfigCache.getClientConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.EMPTY_CONFIG);

        LoggerUtil.debug(logger, "通讯配置:config=", config);

        //如果是使用代理发送，直接使用代理
        if (config.getProtocol() == TransportProtocol.PROXY) {
            return proxyProtocolSender.send(config, communicationId, request);
        }

        ClientInstanceManager clientInstanceManager = InstanceManagerFactory
            .getClientInstanceManagerByProtocol(config.getProtocol());

        //此处的channelSystemId不是通讯的，而是目标的渠道
        return clientInstanceManager.sendByConfig(config, communicationId, request);
    }

    /**
     * Setter method for property <tt>proxyProtocolSender</tt>.
     * 
     * @param proxyProtocolSender value to be assigned to property proxyProtocolSender
     */
    public void setProxyProtocolSender(ProxyProtocolSender proxyProtocolSender) {
        this.proxyProtocolSender = proxyProtocolSender;
    }

}
