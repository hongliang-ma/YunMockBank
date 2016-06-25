/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.biz.messages.routing;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.model.transaction.detail.TransferData;

/**
 *  路由引擎-响应生成接口，负责生成返回给通讯组件的MessageEnvelope
 * 
 * @author hongliang.ma
 * @version $Id: RoutingResponse.java, v 0.1 2012-7-2 下午4:02:03 hongliang.ma Exp $
 */
public interface RoutingResponse {

    /**
     * 生成返回给通讯组件的MessageEnvelope
     * 
     * @param msgResult 通讯返回信息
     * @param transferData  全局放置内容的
     * @param exception 路由中发生的异常
     * @return
     */
    public MessageEnvelope generate(MessageSendResult msgResult, TransferData transferData, AnymockException exception,boolean sendDirect);

}
