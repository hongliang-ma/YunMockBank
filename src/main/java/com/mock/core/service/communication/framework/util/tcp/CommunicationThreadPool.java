/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.service.communication.framework.util.config.ConfigUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 通信线程池。
 * 对不同的通讯组件建立各自独立的线程池，进行故障隔离。
 * 
 * @author zhao.xiong
 * @author 松雪
 * @author peng.lanqp
 * @version $Id: CommunicationThreadPool.java, v 0.2 2012-10-29 上午9:47:54 hao.zhang Exp $
 */
public final class CommunicationThreadPool {

    /** logger */
    private static final Logger                          logger            = LoggerFactory
                                                                               .getLogger(CommunicationThreadPool.class);

    /** 默认的corepoolsize初始值为6,代表66大顺,同时也是keepcache的值，这里性能并不是主要因素，主要以阀值为主 */
    private static final int                             DEFAULT_CORE_SIZE = 8;

    /** 线程池预警阙值：默认为80% */
    private static final int                             ALERT_PERCENT     = 80;

    /** Tcp客户端发送线程池， 组件隔离 key=communicationId**/
    private static final Map<String, ThreadPoolExecutor> tcpClientPool     = new ConcurrentHashMap<String, ThreadPoolExecutor>();

    /** Tcp服务器端接收线程池 组件隔离 key=communicationId **/
    private static final Map<String, ThreadPoolExecutor> tcpServerPool     = new ConcurrentHashMap<String, ThreadPoolExecutor>();

    /**
     * 获取隔离的线程池
     * @param config
     * @return
     */
    public static ThreadPoolExecutor getSeperatorPool(CommunicationConfig config) {
        if (config.getProtocol() != TransportProtocol.TCP
            && config.getProtocol() != TransportProtocol.SSL) {
            throw new AnymockException(CommunicationErrorCode.PROTOCOL_EXCEPTION,
                "[通信线程池]不支持的协议," + config.getProtocol());
        }
        if (config.isServer()) {
            return getPool(config, tcpServerPool);
        } else {
            return getPool(config, tcpClientPool);
        }
    }

    /**
     * 获取线程池
     * 
     * @param config
     * @param poolMap
     * @return
     */
    private static ThreadPoolExecutor getPool(CommunicationConfig config,
                                              Map<String, ThreadPoolExecutor> poolMap) {

        if (poolMap.get(config.getCommunicationId()) == null) {
            synchronized (poolMap) {
                if (poolMap.get(config.getCommunicationId()) == null) {
                    int poolsize = ConfigUtil.getThreadPoolThreshold(config, DEFAULT_CORE_SIZE);
                    poolMap.put(config.getCommunicationId(),
                        (ThreadPoolExecutor) Executors.newFixedThreadPool(poolsize));
                    LoggerUtil.info(logger, config, "申请到线程池,大小:" + poolsize);
                }
            }
        }
        return poolMap.get(config.getCommunicationId());

    }

    /**
     * 清理线程池
     * 
     * @param config
     */
    public static void cleanPool() {
        tcpClientPool.clear();
        tcpServerPool.clear();
    }

    /**
     * 根据通讯组件清理池
     * 
     * @param communicationId
     */
    public static void cleanByCommunicationId(String communicationId) {
        tcpClientPool.remove(communicationId);
        tcpServerPool.remove(communicationId);
    }

    /**
     * 向线程池加入一个新的任务,如果超过80%,开始预警打印日志
     *
     * @param task
     * @param config
     */
    public static void runTask(Runnable task, final CommunicationConfig config) {
        ThreadPoolExecutor exector = getSeperatorPool(config);
        if (exector.getActiveCount() >= (exector.getMaximumPoolSize() * ALERT_PERCENT / 100)) {
            LoggerUtil.warn(logger, config.getCommunicationId(), "[通信线程池]执行中线程数/cached线程数/最大线程数/:",
                exector.getActiveCount(), "/", exector.getPoolSize(), "/",
                exector.getMaximumPoolSize());
        } else {

            LoggerUtil.debug(logger, config.getCommunicationId(), "-",
                "[通信线程池]执行中线程数/cached线程数/最大线程数/:", exector.getActiveCount(), "/",
                exector.getPoolSize(), "/", exector.getMaximumPoolSize());
        }

        exector.execute(task);
    }
}
