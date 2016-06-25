/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.initialization;

/**
 * 可初始化的BEAN接口定义<br>
 * Supergw系统应用不采用Spring的init-method实现系统初始化<br>
 * 所有需要系统启动时自动初始化的BEAN都要实现这个接口<br>
 * 并严格控制初始化执行的逻辑，如果初始化抛出异常会导致系统无法启动
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: Initializable.java, v 0.1 2011-3-31 下午12:52:01 zhao.xiong Exp $
 */
public interface Initializable {

    /**
     * 初始化执行的逻辑
     */
    public void initialize();

}
