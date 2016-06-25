/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.common.util;


/**
 * 捕捉到异常的时候，我们通常会使用<code>logger.error("xxxx",e)</code>方式打印日常堆栈日志<br>
 * 但是这种方式会造成错误日志打印两遍，精益求精，日志也追求极致，SUPERGW的错误日志全部使用本工具类输出
 * 
 * @author peng.lanqp
 * @version $Id: ExceptionUtil.java, v 0.1 2010-11-20 下午12:23:22 peng.lanqp Exp $
 */
public final class ExceptionUtil {

    /**
     * 禁用构造函数
     */
    private ExceptionUtil() {
        // 禁用构造函数
    }

    /**
     * 捕捉错误日志并输出到日志文件：common-error.log
     * 
     * @param e 异常堆栈
     * @param message 错误日志上下文信息描述，尽量带上业务特征
     */
    public static void caught(Throwable e, Object... message) {
        //logger.error(LoggerUtil.getLogString(message), e);
    }

}
