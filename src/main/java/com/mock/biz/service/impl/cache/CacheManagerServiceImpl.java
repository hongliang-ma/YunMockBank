/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.biz.service.impl.cache;

import com.mock.common.service.facade.CacheManagerService;
import com.mock.common.service.facade.common.CacheName;
import com.mock.core.service.shared.cache.CacheManagerRegistry;

/**
 * 缓存管理者服务实现
 * 
 * @author peng.lanqp
 * @version $Id: CacheManagerServiceImpl.java, v 0.1 2011-1-17 下午02:03:31 peng.lanqp Exp $
 */
public class CacheManagerServiceImpl implements CacheManagerService {

    private CacheManagerRegistry cacheManagerRegistry;

    /** 
     * @see com.alipay.supergw.service.facade.config.cache.CacheManagerService#refreshAllCache()
     */
    public void refreshAllCache() {
        cacheManagerRegistry.refreshAllCache();
    }

    /** 
     * @see com.alipay.supergw.service.facade.config.cache.CacheManagerService#dumpAllCache()
     */
    public void dumpAllCache() {
        cacheManagerRegistry.dumpAllCache();
    }

    /** 
     * @see com.alipay.supergw.service.facade.config.cache.CacheManagerService#refreshCache(com.alipay.supergw.service.facade.config.cache.CacheName)
     */
    public boolean refreshCache(CacheName cacheName) {
        return cacheManagerRegistry.refreshCache(cacheName);
    }

    /** 
     * @see com.alipay.supergw.service.facade.config.cache.CacheManagerService#dumpCache(com.alipay.supergw.service.facade.config.cache.CacheName)
     */
    public void dumpCache(CacheName cacheName) {
        cacheManagerRegistry.dumpCache(cacheName);
    }

    /**
     * Setter method for property <tt>cacheManagerRegistry</tt>.
     * 
     * @param cacheManagerRegistry value to be assigned to property cacheManagerRegistry
     */
    public void setCacheManagerRegistry(CacheManagerRegistry cacheManagerRegistry) {
        this.cacheManagerRegistry = cacheManagerRegistry;
    }

}
