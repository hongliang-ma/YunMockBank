/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.thread;

import java.util.HashMap;
import java.util.Map;

/**
 *  控制器流程上下文，用于通讯在控制器的上下文参数传递
 * 
 * @author hongliang.ma
 * @version $Id: ControllerContext.java, v 0.1 2012-7-3 下午12:42:56 hongliang.ma Exp $
 */
public final class ControllerContext {

    /** 控制器上下文本地线程变量**/
    private static ThreadLocal<Map<String, String>> context       = new ThreadLocal<Map<String, String>>();

    /** 是否为长连接，参数值为boolean类型 */
    public static final String                      KEEP_ALIVE    = "isKeepAlive";

    /** 超时时间，参数值为整形 */
    public static final String                      TIMEOUT       = "timeout";

    /** 通讯分流标识,决定通讯的分流流向，决定着PropertiesConfigEnum 中的DISPATCH_ADDR 对应ip **/
    public static final String                      DISPATCH_FLAG = "anymockDispatchFlag";

    /**
     * 禁用构造函数
     */
    private ControllerContext() {
        // 禁用构造函数
    }

    /**
     * 获取控制器本地线程某个属性
     * 
     * @param name
     * @return
     */
    public static String getAttributeByName(String name) {
        if (context.get() == null) {
            return null;
        }

        return context.get().get(name);
    }

    /**
     * 设置控制器本地线程属性
     * 
     * @param name
     * @param o
     * @return
     */
    public static Object setAttribute(String name, String o) {
        init();
        return context.get().put(name, o);
    }

    /**
     * 清空上下文信息
     */
    public static void clear() {
        context.remove();
    }

    /**
     * 初始化
     */
    private static void init() {
        if (context.get() == null) {
            context.set(new HashMap<String, String>());
        }
    }

}
