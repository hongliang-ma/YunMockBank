/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tbnotify;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.ClientInstanceManager;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.model.CommunicationContext;
import com.mock.core.service.communication.framework.model.CommunicationContext.ParamType;
import com.mock.core.service.communication.framework.protocol.ClientProtocolHandler;
import com.mock.core.service.communication.framework.protocol.ProtocolFactory;
import com.mock.core.service.communication.framework.protocol.tbnotify.TBNotifyClientHandler;
import com.mock.core.service.shared.event.PublisherEventManager;
import com.mock.util.ExceptionUtil;
import com.mock.util.LoggerUtil;

/**
 * TBNotify 客户端实例管理器（通讯框架），提供给MessageSender调用
 * 
 * @author wenjie.su
 * @author peng.lanqp
 * @version $Id: TBNotifyClientInstanceManger.java, v 0.1 2011-9-14 下午02:31:27 wenjie.su Exp $
 */
@Component("tBNotifyClientInstanceManger")
public class TBNotifyClientInstanceManger implements ClientInstanceManager {

    /** logger */
    private static final Logger                       logger     = LoggerFactory
                                                                     .getLogger(TBNotifyClientInstanceManger.class);

    /** 协议处理类map key为outTransCodeId+communicationId  **/
    private static Map<String, TBNotifyClientHandler> handlerMap = new ConcurrentHashMap<String, TBNotifyClientHandler>();

    /** 对象锁 */
    private static final Object                       lock       = new Object();

    /** TBNotify 消息发送 */
    private PublisherEventManager                     publisherEventManager;

    /**
     * @see com.mock.core.service.communication.framework.instance.ClientInstanceManager#sendByConfig(com.mock.core.model.shared.communication.CommunicationConfig,java.lang.String, java.lang.String, com.mock.core.model.communication.message.domain.MessageEnvelope)
     */

    public MessageSendResult sendByConfig(CommunicationConfig config, String communicationId,
                                          MessageEnvelope request) {

        boolean timeout = false;
        MessageEnvelope responseMsg = null;

        try {
            ClientProtocolHandler handler = getAvailableProtocolHandler(config);
            CommunicationContext context = new CommunicationContext();
            context.addParam(ParamType.COMMUNICATIONID, communicationId);
            context.addParam(ParamType.SYSTEM_TEMPLATE_ID, config.getSysTemplateId());

            responseMsg = handler.handle(request, context);
        } catch (AnymockException e) {

            LoggerUtil.info(logger, "通过TBNotify方式调用内部系统出现异常:", request, e);

            throw e;

        } catch (Exception e) {

            timeout = true;

            ExceptionUtil.caught(e, "通过TBNotify方式调用内部系统出现异常:", request);

            throw new AnymockException(CommunicationErrorCode.INVOKE_OTHER_SYSTEM_ERROR, e);
        }

        return new MessageSendResult(responseMsg, timeout, true);
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeAll()
     */
    public void disposeAll() {
        handlerMap.clear();
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#disposeInstance(java.lang.String, java.lang.String)
     */
    public void disposeInstance(String communicationId) {
        CommunicationConfig config = NetworkConfigCache.getClientConfig(communicationId);
        AssertUtil.isNotNull(config, CommunicationErrorCode.NO_CLIENT_FOUND);
        handlerMap.remove(config.getKey());
    }

    /** 
     * @see com.mock.core.service.communication.framework.instance.InstanceManager#startInstance(java.lang.String, java.lang.String)
     */
    public void startInstance(String communicationId) {
        //do nothing，会在sendByConfig中做。
    }

    /** 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
        disposeAll();
        LoggerUtil.info(logger, "TBNotify缓存清理完毕");
    }

    /** 
     * @see com.mock.core.service.shared.initialization.Initializable#initialize()
     */
    public void initialize() {
        LoggerUtil.info(logger, this.getClass().getSimpleName(), "注册网络缓存监听");
        NetworkConfigCache.addListener(this);
    }

    /**
     * 依据通讯配置，获取客户端协议处理类，key采用outTransCodeId+communicationId
     * 
     * @param config
     * @return
     */
    private TBNotifyClientHandler getAvailableProtocolHandler(CommunicationConfig config) {
        try {
            String key = config.getKey();
            TBNotifyClientHandler rv = handlerMap.get(key);
            if (rv == null) {
                synchronized (lock) {
                    if (handlerMap.get(key) == null) {
                        //注册协议处理类
                        rv = (TBNotifyClientHandler) ProtocolFactory.getClientProtocolHandler(
                            config.getProtocol()).newInstance();
                        rv.setPublisherEventManager(publisherEventManager);
                        rv.setConfig(config);
                        handlerMap.put(key, rv);
                    }
                }
            }
            return rv;
        } catch (Exception e) {
            ExceptionUtil.caught(e, config, "注册客户端失败");
            throw new AnymockException(CommunicationErrorCode.NO_CLIENT_FOUND, e);
        }
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        InstanceManagerFactory.registerClientManager(TransportProtocol.TBNOTIFY, this);
    }

    /**
     * Setter method for property <tt>publisherEventManager</tt>.
     * 
     * @param publisherEventManager value to be assigned to property publisherEventManager
     */
    public void setPublisherEventManager(PublisherEventManager publisherEventManager) {
        this.publisherEventManager = publisherEventManager;
    }

}
