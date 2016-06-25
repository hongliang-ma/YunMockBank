
package com.mock.core.service.shared.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.common.util.CacheMessageUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: SystemTemplateCache.java, v 0.1 2012-6-21 下午4:41:08 hongliang.ma Exp $
 */
public final class SystemTemplateCache {
    /** 读写锁 */
    private static final ReadWriteLock         lock                = new ReentrantReadWriteLock();

    /**  系统模板的缓存， key 为urlID ，value为模板*/
    private static Map<String, SystemTemplate> systemTemplateCache = new ConcurrentHashMap<String, SystemTemplate>();

    private SystemTemplateCache() {
        //不要用这个构造函数
    }

    /**
     *  刷新组件相关的缓存
     * 
     * @param listSystemTemplate
     */
    public static void putAll(List<SystemTemplate> listSystemTemplate) {
        Lock writelock = lock.writeLock();
        writelock.lock();

        try {
            systemTemplateCache.clear();
            for (SystemTemplate systemTemplate : listSystemTemplate) {
                if (null == systemTemplate) {
                    continue;
                }
                systemTemplateCache.put(systemTemplate.getUrlId(), systemTemplate);
            }

        } finally {
            writelock.unlock();
        }
    }

    public static void modifyCache(SystemTemplate systemTemplate) {
        systemTemplateCache.put(systemTemplate.getUrlId(), systemTemplate);
    }

    public static void deleteOne(String sysID) {
        systemTemplateCache.remove(getSystemTemplateById(sysID).getUrlId());
    }

    /**
     * 根据urlID获取到系统模板
     * 
     * @param innerId
     * @return
     */
    public static SystemTemplate getSystemTemplate(String urlId) {
        return systemTemplateCache.get(urlId);
    }

    /**
     * 根据系统模板ID取系统模板
     * 
     * @param strSysTemplateID
     * @return
     */
    public static SystemTemplate getSystemTemplateById(String strSysTemplateID) {
        for (SystemTemplate systemTemplate : systemTemplateCache.values()) {
            if (StringUtil.equals(strSysTemplateID, systemTemplate.getSysId())) {
                return systemTemplate;
            }
        }
        return null;
    }

    /**
     * 打印缓存内容
     * 
     * @return
     */
    public static String getCacheInfo() {
        return CacheMessageUtil.mapStringAndObject(systemTemplateCache).toString();
    }
}