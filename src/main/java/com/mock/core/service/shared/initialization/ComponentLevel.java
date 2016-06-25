/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.initialization;

/**
 * 通过Sofa框架的扩展点实现的组件级别，控制deployCompletion的调用执行顺序<br>
 * 数字越小，执行顺序越靠前
 * 
 * @author peng.lanqp
 * @version $Id: ComponentLevel.java, v 0.1 2011-3-31 下午07:21:39 peng.lanqp Exp $
 */
public final class ComponentLevel {

    /** 初始化管理扩展点，最后执行 */
    public static final int INITIALIZATION = 200;

    /** 缓存管理扩展点 */
    public static final int CACHE          = 10;

    /** 发布者插件扩展点 */
    public static final int PUBLISHER      = 20;

    /** 订阅者插件扩展点 */
    public static final int SUBSCRIBER     = 30;

    /**
     * 禁用构造函数
     */
    private ComponentLevel() {
        // 禁用构造函数
    }

}
