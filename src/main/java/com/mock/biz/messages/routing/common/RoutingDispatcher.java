/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.biz.messages.routing.common;

import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.model.transaction.detail.TransferData;

/**
 * 路由分发接口，负责路由类型（同步/异步）进行路由分发
 * 
 * @author hongliang.ma
 * @version $Id: RoutingDispatcher.java, v 0.1 2012-7-2 下午2:11:05 hongliang.ma Exp $
 */
public interface RoutingDispatcher {

    /**
     * 根据路由类型（同步/异步）进行路由分发 
     * @param TransferData 交易对象
     * @param asyncReceiver 是否为异步发送,更多出于是否起新线程发送
     * @return 返回结果 
     */
    public MessageSendResult dispatch(TransferData myTransferData, boolean asyncReceiver);
}
