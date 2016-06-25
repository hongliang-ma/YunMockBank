/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.service.facade;

import com.mock.common.service.facade.common.CacheName;

/**
 * 缓存管理者服务接口，由后台管理调用实现所有缓存的刷新
 * 
 * @author peng.lanqp
 * @version $Id: CacheManagerService.java, v 0.1 2011-1-17 下午02:01:20 peng.lanqp Exp $
 */
public interface CacheManagerService {

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