/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.cache;

import java.util.List;

import com.mock.common.service.facade.common.CacheName;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.service.shared.cache.impl.AbstractCacheManager;
import com.mock.core.service.shared.repository.CommunicationRepository;

/**
 * 通讯网络配置缓存管理者
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: NetworkConfigCacheManager.java, v 0.1 2011-1-17 上午09:34:16 zhao.xiong Exp $
 */

public class NetworkConfigCacheManager extends AbstractCacheManager {
    /**通讯组件领域仓库**/
    private CommunicationRepository communicationRepository;

    /** 
     * @see com.mock.core.service.shared.cache.impl.AbstractCacheManager#afterRefresh()
     */
    @Override
    protected void afterRefresh() {

        //通知监听缓存刷新的监听器
        NetworkConfigCache.notifyCacheListener();

    }

    /** 
     * @see com.mock.core.service.shared.cache.CacheManager#getCacheName()
     */
    public CacheName getCacheName() {
        return CacheName.COMMUNICATION;
    }

    /** 
     * @see com.mock.core.service.shared.cache.impl.AbstractCacheManager#getCacheInfo()
     */
    @Override
    protected String getCacheInfo() {
        return NetworkConfigCache.getCacheInfo();
    }

    /** 
     * @see com.mock.core.service.shared.cache.impl.AbstractCacheManager#loadingCache()
     */
    @Override
    protected void loadingCache() {

        List<CommunicationConfig> listCommunicationConfig = communicationRepository.loadAll();

        NetworkConfigCache.putAll(listCommunicationConfig);
    }

    /**
     * Setter method for property <tt>communicationRepository</tt>.
     * 
     * @param communicationRepository value to be assigned to property communicationRepository
     */
    public void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

}
