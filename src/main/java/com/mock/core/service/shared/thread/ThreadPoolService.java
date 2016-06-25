/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.common.util.LoggerUtil;

/**
 * 供线程池服务类，统一提供anymock内部所有的线程池操作。<br>
 * 线程池内的线程会复用，池为固定大小，先入先得，如果无可用线程，会进行排队等待直到有可用线程。
 * 
 * @author 松雪
 * @version $Id: ThreadPoolService.java, v 0.1 2011-3-28 上午09:06:45 hao.zhang Exp $
 */
public final class ThreadPoolService {

    /** logger */
    private static final Logger             logger                       = LoggerFactory
                                                                             .getLogger(ThreadPoolService.class);

    /** 用于发送异步交易的线程池大小 */
    public static final int                 THREAD_ASYN_TRANSACTION_SEND = 20;

    /** 异步交易发送线程池 */
    private static final ThreadPoolExecutor asyncTranSendPool            = (ThreadPoolExecutor) Executors
                                                                             .newFixedThreadPool(THREAD_ASYN_TRANSACTION_SEND);

    /** 异步刷新缓存线程池 */
    private static final ThreadPoolExecutor asyn_refresh                 = (ThreadPoolExecutor) Executors
                                                                             .newFixedThreadPool(THREAD_ASYN_TRANSACTION_SEND);

    /** 异步更新使用次数 */
    private static final ThreadPoolExecutor asyn_upcount                 = (ThreadPoolExecutor) Executors
                                                                             .newFixedThreadPool(THREAD_ASYN_TRANSACTION_SEND);

    /**
     * 线程池类型
     * 
     * @author 松雪
     * @version $Id: PoolType.java, v 0.1 2011-6-21 上午10:48:48 hao.zhang Exp $
     */
    public static enum PoolType {

        /** 异步交易发送线程池 */
        ASYN_TRANSACTION,
        /**
         * 异步刷新缓存
         */
        ASYN_REFRESH,

        /**
         * 异步更新使用次数
         */
        ASYN_UPCOUNT,

        /**
         * 异步检查配置
         */
        ASYN_CHECK

    }

    /**
     * 禁用构造函数
     */
    private ThreadPoolService() {
        // 禁用构造函数
    }

    /**
     * 向线程池加入一个新的任务
     * 
     * @param type
     * @param task
     */
    public static void addTask(PoolType type, Runnable task) {
        ThreadPoolExecutor pool = getPool(type);

        LoggerUtil.debug(logger, "[线程池]活动线程数/最大线程数:", pool.getPoolSize(), "/",
            pool.getMaximumPoolSize());

        pool.execute(task);
    }

    /**
     * 获取线程池当前活动线程数
     * @param type
     * @return
     */
    public static int getActiveThreadCount(PoolType type) {
        return getPool(type).getPoolSize();
    }

    /**
     * 获取指定类型的线程池
     * @param type
     * @return
     */
    public static ThreadPoolExecutor getPool(PoolType type) {
        ThreadPoolExecutor pool = null;

        switch (type) {
            case ASYN_TRANSACTION: {
                pool = asyncTranSendPool;
                break;
            }
            case ASYN_REFRESH: {
                pool = asyn_refresh;
                break;
            }
            case ASYN_UPCOUNT: {
                pool = asyn_upcount;
            }
                break;
            default: {
                throw new IllegalArgumentException("非法的线程池类型");
            }
        }

        return pool;
    }
}
