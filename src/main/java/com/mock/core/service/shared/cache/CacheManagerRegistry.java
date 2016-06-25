/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.cache;

import com.mock.common.service.facade.common.CacheName;

/**
 * 缓存管理器集中注册接口，可以实现对分散在各处的缓存管理器统一维护
 * 
 * @author peng.lanqp
 * @version $Id: CacheManagerRegistry.java, v 0.1 2011-3-30 上午08:19:33 peng.lanqp Exp $
 */
public interface CacheManagerRegistry {

    /**
     * 刷新全部缓存
     */
    public void refreshAllCache();

    /**
     * 打印全部缓存
     */
    public void dumpAllCache();

    /**
     * 刷新特定的缓存
     * 
     * @param cacheName
     * @return 刷新是否有异常
     */
    public boolean refreshCache(CacheName cacheName);

    /**
     * 打印特定的缓存
     * 
     * @param cacheName
     */
    public void dumpCache(CacheName cacheName);
}
