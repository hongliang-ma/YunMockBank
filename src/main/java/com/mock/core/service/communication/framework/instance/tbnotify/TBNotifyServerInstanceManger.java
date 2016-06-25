/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tbnotify;

import java.util.Observable;

import org.springframework.stereotype.Component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.protocol.tbnotify.TBNotifyServerHandler;
import com.mock.core.service.shared.communication.MessageReceiver;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.alipay.common.event.UniformEvent;

/**
 * TBNotify 服务端实例管理器（通讯框架），提供给通讯接入模块调用，然后调用MessageReceiver接口
 * 调用内部服务的入口。
 * 
 * @author wenjie.su
 * @author peng.lanqp
 * @version $Id: TBNotifyServerInstanceManger.java, v 0.1 2011-9-22 下午8:47:43 wenjie.su Exp $
 */
@Component("tBNotifyServerInstanceManger")
public class TBNotifyServerInstanceManger implements ServerInstanceManager {

    /** logger */
    private static final Logger                logger  = LoggerFactory
                                                           .getLogger(TBNotifyServerInstanceManger.class);

    /** TBNotify服务端协议处理类，由于其调用的特殊性，采用单例处理 */
    private static final TBNotifyServerHandler handler = new TBNotifyServerHandler();

    /** 报文接收器 */
    private MessageReceiver                    messageReceiver;

    /**
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#addrRoute(java.lang.String,java.lang.Object[])
     */
    public Object addrRoute(String uri, CommunicationContext context) {

        UniformEvent message = context.getUniformEvent();

        String topic = message.getTopic();
        String eventCode = message.getEventCode();

        try {
            handler.handle(message, context);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "处理消息失败，topic=", topic, ",eventCode=", eventCode);
        }

        return null;
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#refreshAll()
     */

    public void refreshAll() {
        disposeAll();
        startAll();
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#setMessageReceiver(com.mock.core.service.shared.communication.MessageReceiver)
     */
    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.ServerInstanceManager#startAll()
     */
    public void startAll() {

        //TBNotify协议处理类采用单例实现
        synchronized (handler) {
            handler.setMessageReceiver(messageReceiver);
        }
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */
    public void disposeAll() {
        //do nothing
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeInstance(java.lang.String,
     *      java.lang.String)
     */
    public void disposeInstance(String communicationId) {
        //do nothing
    }

    /**
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#startInstance(java.lang.String,
     *      java.lang.String)
     */
    public void startInstance(String communicationId) {
        //do nothing
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {

        LoggerUtil.info(logger, "开始更新TBNotify服务端实例管理器。");

        refreshAll();

        LoggerUtil.info(logger, "TBNotify服务端实例管理器更新完毕。");
    }

    /**
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */
    public void initialize() {

        NetworkConfigCache.addListener(this);

        startAll();

        LoggerUtil.info(logger, "TBNotify服务端实例管理器加载完毕。");
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        InstanceManagerFactory.registerServerManager(TransportProtocol.TBNOTIFY, this);
    }
}
