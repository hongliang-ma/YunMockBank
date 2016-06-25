/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.biz.messages.routing;

import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;

/**
 *  路由引擎接口
 * 
 * @author hongliang.ma
 * @version $Id: RoutingEngine.java, v 0.1 2012-6-28 下午4:27:04 hongliang.ma Exp $
 */
public interface RoutingEngine {

    /**
     * 接收消息，处理后返回
     * 
     * @param description 传入的消息信封
     * @param sendDirect  发送报文还是处理报文
     * @return
     */
    public MessageEnvelope route(MessageDescription description, boolean sendDirect);

    /**
     * 简化版的路由，用来直接返回报文
     * 
     * @param transferId
     * @param MsgSend
     * @return 返回报文
     */
    public String sendDirectRoute(String transferId, String MsgSend);

}
