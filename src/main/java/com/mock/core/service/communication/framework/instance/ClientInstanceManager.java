/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;

/**
 * 客户端实例管理器
 * 
 * @author hongliang.ma
 * @version $Id: ClientInstanceManager.java, v 0.1 2012-6-25 下午3:13:56 hongliang.ma Exp $
 */
public interface ClientInstanceManager extends InstanceManager {

    /**
     *  通过指定的communication组件进行发送
     * 
     * @param config
     * @param communicationId 目标ID
     * @param requestMsg
     * @return
     */
    MessageSendResult sendByConfig(CommunicationConfig config, String communicationId,
                                   MessageEnvelope requestMsg);

}
