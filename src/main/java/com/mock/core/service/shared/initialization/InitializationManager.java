/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.initialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.lang.diagnostic.Profiler;
import com.mock.common.util.ExceptionUtil;
import com.alipay.sofa.service.api.component.ComponentInstance;
import com.alipay.sofa.service.api.component.DefaultComponent;

/**
 * 可初始化的BEAN管理器，根据bean的级别顺序完成初始化逻辑
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: InitializationManager.java, v 0.1 2011-3-31 下午12:57:35 zhao.xiong Exp $
 */
public class InitializationManager extends DefaultComponent {

    /** 初始化管理组件名称*/
    private static final String                   POINT_NAME = "initialization";

    /** 可初始化BEAN列表 */
    private static List<InitializationDescriptor> beans      = new ArrayList<InitializationDescriptor>();

    /** 
     * @see com.alipay.sofa.service.api.component.DefaultComponent#registerContribution(java.lang.Object, java.lang.String, com.alipay.sofa.service.api.component.ComponentInstance)
     */
    @Override
    public void registerContribution(Object contribution, String extensionPoint,
                                     ComponentInstance contributor) {
        if (contribution != null && StringUtil.equals(extensionPoint, POINT_NAME)) {
            beans.add((InitializationDescriptor) contribution);
        }
    }

    /** 
     * @see com.alipay.sofa.service.api.component.DefaultComponent#deployCompletion()
     */
    @Override
    public void deployCompletion() {

        // 计时器
        Profiler.reset();

        try {
            Profiler.start("初始化组件详细信息");
            init();
        } finally {
            Profiler.release();
        }
    }

    /**
     * 初始化组件完成各个系统应用的初始化
     */
    private void init() {

        // 根据启动的级别进行排序 
        Collections.sort(beans);

        for (InitializationDescriptor descriptor : beans) {

            // 计时器
            StopWatch oneWatch = new StopWatch();
            oneWatch.start();

            try {
                descriptor.getBean().initialize();
            } catch (Throwable e) {
                // 某个bean初始化异常时，打印异常信息后继续初始化其他bean
                ExceptionUtil.caught(e, "初始化异常");
            }

            oneWatch.split();

        }
    }

    /** 
     * @see com.alipay.sofa.service.api.component.DefaultComponent#getStartLevel()
     */
    @Override
    public int getStartLevel() {

        // 需要保证在业务扩展点中是最后执行的
        return ComponentLevel.INITIALIZATION;
    }

}
