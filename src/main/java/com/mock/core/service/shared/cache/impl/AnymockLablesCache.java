
package com.mock.core.service.shared.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.mock.core.model.transaction.template.AnymockLable;
import com.mock.common.util.CacheMessageUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: AnymockLablesCache.java, v 0.1 2012-6-12 下午5:01:52 hongliang.ma Exp $
 */
public final class AnymockLablesCache {
    /** 读写锁 */
    private static final ReadWriteLock       lock             = new ReentrantReadWriteLock();

    //标签缓存，以通讯url为key
    private static Map<String, AnymockLable> aymockLableCache = new ConcurrentHashMap<String, AnymockLable>();

    private AnymockLablesCache() {
        //不要用这个构造函数
    }

    /**
     * 刷新组件相关的缓存
     * 
     * @param users
     */
    public static void putAll(List<AnymockLable> listTeams) {
        Lock writelock = lock.writeLock();
        writelock.lock();

        try {
            aymockLableCache.clear();

            for (AnymockLable team : listTeams) {
                if (team != null && null != team.getCommunicationId()) {
                    aymockLableCache.put(team.getCommunicationId(), team);
                }
            }

        } finally {
            writelock.unlock();
        }
    }

    /**
     * 新增一个缓存
     * 
     * @param anymockLable
     */
    public static void putNew(AnymockLable anymockLable) {
        String communicationId = anymockLable.getCommunicationId();
        if (!aymockLableCache.containsKey(communicationId)) {
            aymockLableCache.put(communicationId, anymockLable);
        }
    }

    /**
     * 从缓存中删除掉一个
     * 
     * @param communicationId
     */
    public static void deleteOne(String communicationId) {
        if (aymockLableCache.containsKey(communicationId)) {
            aymockLableCache.remove(aymockLableCache.get(communicationId));
        }
    }

    /**
     * 根据组名获取到标签列表
     * 
     * @param innerId
     * @return
     */
    public static String getLabes(String communicationId) {
        if (null != aymockLableCache.get(communicationId)) {
            return aymockLableCache.get(communicationId).getLabList();
        }
        return null;
    }

    /**
     * 根据组名获取到缓存
     * 
     * @param innerId
     * @return
     */
    public static AnymockLable getLabesbyId(String communicationId) {
        if (null != aymockLableCache.get(communicationId)) {
            return aymockLableCache.get(communicationId);
        }
        return null;
    }

    /**
     * 打印缓存内容
     * 
     * @return
     */
    public static String getCacheInfo() {
        return CacheMessageUtil.mapStringAndObject(aymockLableCache).toString();
    }

}
