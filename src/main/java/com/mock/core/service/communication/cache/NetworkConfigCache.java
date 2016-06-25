/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.cache;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.ConnectTypeEnum;
import com.mock.core.model.shared.enums.PropertiesConfigEnum;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.common.util.CacheMessageUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 通讯网络配置缓存
 * 
 * @author hongliang.ma
 * @version $Id: NetworkConfigCache.java, v 0.1 2012-6-20 下午1:14:33 hongliang.ma Exp $
 */
public final class NetworkConfigCache {

    /** logger **/
    private static final Logger                     logger        = LoggerFactory
                                                                      .getLogger(NetworkConfigCache.class);

    /** 所有的组件配置的缓存 key为uriID+s 或者 key为uriID+C */
    private static Map<String, CommunicationConfig> cache         = new ConcurrentHashMap<String, CommunicationConfig>();

    /** 所有的组件配置的缓存 key为id */
    private static Map<String, CommunicationConfig> norlmalcache  = new ConcurrentHashMap<String, CommunicationConfig>();

    /** 服务器地址缓存,key为url地址,为接收器定位对应的NetworkConfig     **/
    private static Map<String, CommunicationConfig> receiverCache = new ConcurrentHashMap<String, CommunicationConfig>();

    /** 缓存监听器 */
    private static List<Observer>                   listeners     = Collections
                                                                      .synchronizedList(new ArrayList<Observer>());
    /** 读写锁 */
    private static final ReadWriteLock              lock          = new ReentrantReadWriteLock();

    /**
     * 禁用构造函数
     */
    private NetworkConfigCache() {
        // 禁用构造函数
    }

    /**
     *  加载缓存
     * 
     * @param listCommunicationConfig
     */
    public static void putAll(List<CommunicationConfig> listCommunicationConfig) {

        Lock writelock = lock.writeLock();
        writelock.lock();
        try {
            // 清空缓存
            cache.clear();
            norlmalcache.clear();
            receiverCache.clear();

            for (CommunicationConfig communicationConfig : listCommunicationConfig) {
                if (null != communicationConfig && null != communicationConfig.getCommunicationId()) {
                    norlmalcache.put(communicationConfig.getCommunicationId(), communicationConfig);
                    addCache(communicationConfig);
                }
            }

        } finally {
            writelock.unlock();
        }

    }

    /**
     *  添加一项
     * 
     * @param listCommunicationConfig
     */
    public static void putOne(CommunicationConfig communicationConfig) {
        // 清空缓存
        norlmalcache.put(communicationConfig.getCommunicationId(), communicationConfig);
        addCache(communicationConfig);
    }

    public static void deleteOne(String urlId) {
        CommunicationConfig communicationConfig = getCommunicationConfigById(urlId);
        if (null != communicationConfig && communicationConfig.isServer()) {
            receiverCache.remove(communicationConfig.getUri().getUrl());
            cache.remove(urlId + "S");
            getServerManager(communicationConfig.getProtocol()).disposeInstance(urlId);
        } else {
            cache.remove(urlId + "C");
        }

        norlmalcache.remove(urlId);

    }

    /**
     * 获取http的实例管理管理器,兼容HTTPS
     * @return
     */
    private static ServerInstanceManager getServerManager(TransportProtocol protocal) {
        return InstanceManagerFactory.getServerInstanceManagerByProtocol(protocal);
    }

    /**
     *  获取客户端的缓存
     * 
     * @return
     */
    public static CommunicationConfig getClientConfig(String communicationId) {
        String key = getCacheKey(communicationId, ConnectTypeEnum.CLIENT);
        return cache.get(key);
    }

    /**
     * 更新某个通讯缓存
     * 
     * @param communicationConfig
     */
    public static void modifyCache(CommunicationConfig communicationConfig) {
        norlmalcache.put(communicationConfig.getCommunicationId(), communicationConfig);
        addCache(communicationConfig);
    }

    /**
     * 获取对应的服务通讯组件 
     * 
     * @param communicationId
     * @return
     */
    public static CommunicationConfig getServerConfig(String communicationId) {
        LoggerUtil.debug(logger, "读取服务端缓存,communicationId=", communicationId);

        String key = getCacheKey(communicationId, ConnectTypeEnum.SERVER);
        return cache.get(key);
    }

    /**
     * 根据接收器的地址找到对应缓存
     * 
     * @param uri
     * @return
     */
    public static CommunicationConfig findByReceiver(String uri) {
        return receiverCache.get(uri);
    }

    /** 
     * 获取缓存信息
     */
    public static String getCacheInfo() {
        StringBuilder builder = new StringBuilder();

        builder.append("所有的通讯组件缓存:");
        builder.append(CacheMessageUtil.mapStringAndObject(cache));
        builder.append(LoggerUtil.ENTERSTR).append(LoggerUtil.ENTERSTR);
        builder.append("接收器地址寻址缓存:");
        builder.append(CacheMessageUtil.mapStringAndObject(receiverCache));

        return builder.toString();
    }

    /**
     * 添加缓存监听器
     * 
     * @param obj
     */
    public static void addListener(Observer obj) {
        listeners.add(obj);
    }

    /**
     * 通知已经注册的所有缓存监听器
     */
    public static void notifyCacheListener() {
        for (Observer obj : listeners) {
            obj.update(null, null);
        }
    }

    /**
     * 获取所有的通讯缓存
     * 
     * @return
     */
    public static Map<String, CommunicationConfig> getAll() {
        return cache;
    }

    /**
     * 根据通讯ID获取相应的通讯配置
     * 
     * @param communicationID
     * @return
     */
    public static CommunicationConfig getCommunicationConfigById(String communicationID) {
        return norlmalcache.get(communicationID);
    }

    public static Boolean isTransfer(String communicationID) {
        return norlmalcache.get(communicationID).isIstransfer();
    }

    /**
     * 获取所有的通讯配置
     * 
     * @return
     */
    public static Map<String, CommunicationConfig> getAllNorlmal() {
        return norlmalcache;
    }

    /**
     * 获取所有的服务端的通讯缓存
     * @return
     */
    public static Map<String, CommunicationConfig> getAllServerConfig() {
        return receiverCache;
    }

    /**
     * 通讯配置插入缓存
     * 
     * @param config
     */
    private static void addCache(CommunicationConfig config) {
        String cacheKey = getCacheKey(config);
        cache.put(cacheKey, config);

        if (config.isServer()) {
            addReceiverCache(config);
        }
    }

    /**
     * 作为服务端，把地址缓存起来，以便接收器的地址寻址
     * 
     * @param config
     */
    private static void addReceiverCache(CommunicationConfig config) {
        String address = null;
        switch (config.getProtocol()) {
            case HTTPS:
            case HTTP: {
                address = getHttpKey(config.getUri().getUrl());
                break;
            }

            case TCP:
            case SSL: {
                address = getTcpKey(config);
                break;
            }

            case TBNOTIFY: {
                address = getTBNotifyKey(config);
                break;
            }

            case SOAP: {
                address = config.getKey();
                break;
            }

            default: {

                LoggerUtil.warn(logger, "通讯协议不支持地址路由,Protocol=", config.getProtocol());
                break;
            }
        }

        // URL地址非法，或者不支持地址路由，不需要加载缓存        
        if (StringUtil.isNotBlank(address)) {
            addServerConfigs(address, config);
        }
    }

    /**
     * 添加作为服务端通讯组件的配置缓存
     * 
     * @param address
     * @param config
     */
    private static void addServerConfigs(String address, CommunicationConfig config) {
        receiverCache.put(address, config);
    }

    /**
     * 组装缓存的KEY
     * 
     * @param config
     * @return
     */
    public static String getCacheKey(CommunicationConfig config) {
        return getCacheKey(config.getCommunicationId(), (config.isServer() ? ConnectTypeEnum.SERVER
            : ConnectTypeEnum.CLIENT));
    }

    /**
     * 组装缓存的KEY
     * 
     * @param config
     * @return
     */
    public static String getTcpKey(CommunicationConfig config) {
        return Constant.getKey(':', config.getHost(), String.valueOf(config.getPort()));
    }

    /**
     * 组装TBNotify的KEY
     * 
     * @param config
     * @return
     */
    public static String getTBNotifyKey(CommunicationConfig config) {
        String topic = config.getProperties(PropertiesConfigEnum.TB_TOPIC);
        String eventCode = config.getProperties(PropertiesConfigEnum.TB_EVENT_CODE);
        if (StringUtil.isBlank(topic) || StringUtil.isBlank(eventCode)) {
            LoggerUtil.warn(logger, "TPNotify协议必须配置topic和eventCode");
            return null;
        }

        return Constant.getKey(topic, eventCode);
    }

    /**
     * 组装缓存的KEY
     * 
     * @param communicationId
     * @param connType
     * @return
     */
    private static String getCacheKey(String communicationId, ConnectTypeEnum connType) {
        return Constant.getKey(communicationId, connType.getCode());
    }

    /**
     * 获取URL路径，非法路径会返回null，调用方判断
     * 
     * @param url
     * @return
     */
    public static String getHttpKey(String url) {

        try {
            URL uri = new URL(url);
            return uri.getPath();
        } catch (MalformedURLException e) {
            ExceptionUtil.caught(e, "HTTP通讯协议URL不合法,url=", url);
        }

        return null;
    }

}