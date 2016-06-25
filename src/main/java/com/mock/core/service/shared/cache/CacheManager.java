/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.cache;

import com.mock.common.service.facade.common.CacheName;

/**
 * 缓存管理必须实现的接口,提供刷新机制
 * 
 * @author peng.lanqp
 * @version $Id: CacheManager.java, v 0.1 2010-12-28 下午02:49:16 peng.lanqp Exp $
 */
public interface CacheManager {

    /**
     * 初始化缓存（不管初始化是否出现异常，都不能影响系统启动）<br>
     * 这是系统启动初始化时使用的接口
     * 
     * @return 初始化是否出现异常
     */
    public boolean initCache();

    /**
     * 刷新缓存信息（不管刷新是否出现异常，都不能影响系统启动）<br>
     * 这是人工触发刷新缓存时使用的接口。
     * 缓存刷新过程中，应使用锁保证缓存数据读写的强一致性，防止在缓存刷新造成交易异常。
     * 
     * @return 刷新是否出现异常
     */
    public boolean refreshCache();

    /**
     * 获取缓存的名称
     * 
     * @return
     */
    public CacheName getCacheName();

    /**
     * 打印缓存信息
     */
    public void dump();
}
