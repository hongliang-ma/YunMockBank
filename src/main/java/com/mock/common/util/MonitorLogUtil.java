/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;

/**
 * 监控日志工具类
 * 
 * @author peng.lanqp
 * @author zhao.xiong
 * @author 松雪
 * @version $Id: MonitorLogUtil.java, v 0.2 2012-4-26 下午3:31:47 hao.zhang Exp $
 */
public final class MonitorLogUtil {

    /** 系统启动初始化日志记录 */
    private static final Logger SYSINIT   = LoggerFactory.getLogger("anymock-SYSINIT");

    /** 日志开始符号 */
    public static final String  FMT_BEGIN = "[(";

    /** 格式化结束符号 */
    public static final String  FMT_END   = ")]";

    /** 毫秒 */
    public static final String  MS        = "ms";

    /** 逗号 */
    public static final char    COMMA     = ',';

    /** 点号 */
    public static final char    DOT       = '.';

    /** null数据替换符 */
    public static final String  NULL      = "-";

    /**
     * 禁用构造函数
     */
    private MonitorLogUtil() {
        // 禁用构造函数
    }

    /**
     * 记录监控日志
     * 
     * @param monitorLogType 日志监控类型
     * @param monitorLog 日志监控信息
     */
    public static void monitor(MonitorLogType monitorLogType, MonitorLog monitorLog) {
        switch (monitorLogType) {
            case DAL:
                DALLogUtil.info(monitorLog);
                break;

            case SAL:
                SALLogUtil.info(monitorLog);
                break;

            default:
                break;
        }
    }

    /**
     * 系统初始化的日志记录，supergw-sysinit.log
     * 
     * @param obj
     */
    public static void init(Object... obj) {
        LoggerUtil.info(SYSINIT, obj);
    }

    /**
     * 数据服务的日志工具封装
     * 
     * @author peng.lanqp
     * @version $Id: DALLogUtil.java, v 0.1 2011-8-26 下午9:39:02 peng.lanqp Exp $
     */
    private final static class DALLogUtil {

        /** 数据服务的日志记录 */
        private static final Logger DAL = LoggerFactory.getLogger("supergw-DAL-MONITOR");

        /**
         * 记录数据服务层日志
         * 
         * @param monitorLog
         */
        public static void info(MonitorLog monitorLog) {
            LoggerUtil.info(DAL, formatDAL(monitorLog));
        }
    }

    /**
     * 集成服务的日志工具封装
     * 
     * @author peng.lanqp
     * @version $Id: SALLogUtil.java, v 0.1 2011-8-26 下午9:39:15 peng.lanqp Exp $
     */
    private final static class SALLogUtil {

        /** 集成服务的日志记录 */
        private static final Logger SAL = LoggerFactory.getLogger("supergw-SAL-MONITOR");

        /**
         * 记录网关服务日志
         * 
         * @param monitorLog
         */
        public static void info(MonitorLog monitorLog) {
            LoggerUtil.info(SAL, formatSAL(monitorLog));
        }
    }

    /**
     * 对dal-digest日志进行格式化: [(dbName,interface.method,Y,elapseTime ms)]
     * 
     * @param monitorLog 
     * @return 格式化串
     */
    private static String formatDAL(MonitorLog monitorLog) {
        StringBuilder builder = new StringBuilder();

        builder.append(FMT_BEGIN).append(monitorLog.getDatabaseName()).append(COMMA);
        builder.append(monitorLog.getInterceptorClass()).append(DOT);
        builder.append(monitorLog.getInterceptorMethod()).append(COMMA);
        builder.append(getInvokeResult(monitorLog)).append(COMMA);
        builder.append(monitorLog.getStopWatch().getSplitTime()).append(MS).append(FMT_END);

        return builder.toString();
    }

    /**
     * 对sal-digest日志进行格式化[(systemName,interface.method,Y,elapseTime ms)]
     * 
     * @param monitorLog
     * @return 格式化串
     */
    private static String formatSAL(MonitorLog monitorLog) {
        StringBuilder builder = new StringBuilder();

        builder.append(FMT_BEGIN).append(monitorLog.getSystemName()).append(COMMA);
        builder.append(monitorLog.getInterceptorClass()).append(DOT);
        builder.append(monitorLog.getInterceptorMethod()).append(COMMA);
        builder.append(getInvokeResult(monitorLog)).append(COMMA);
        builder.append(monitorLog.getStopWatch().getSplitTime()).append(MS).append(FMT_END);

        return builder.toString();
    }

    /**
     * 获取调用结果，日志中记录，成功:Y，失败:N
     * 
     * @param monitorLog
     * @return
     */
    private static char getInvokeResult(MonitorLog monitorLog) {
        return monitorLog.isInvokeSuccess() ? 'Y' : 'N';
    }

}
