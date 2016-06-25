/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance;

import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.shared.communication.MessageReceiver;

/**
 * 服务实例管理器
 * 
 * @author hongliang.ma
 * @version $Id: ServerInstanceManager.java, v 0.1 2012-6-25 下午3:14:46 hongliang.ma Exp $
 */
public interface ServerInstanceManager extends InstanceManager {

    /**
     * 启动所有实例
     */
    void startAll();

    /**
     * 刷新所有实例
     */
    void refreshAll();

    /**
     * 地址路由，通过此uri找到对应的通讯组件做处理，主要针对应用层，TCP之类的不做处理
     * @param uri
     * @param context
     */
    Object addrRoute(String uri, CommunicationContext context);

    /**
     * 设置路由接收
     */
    void setMessageReceiver(MessageReceiver messageReceiver);

}
