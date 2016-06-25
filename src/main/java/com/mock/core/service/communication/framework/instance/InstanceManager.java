/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance;

import java.util.Observer;

import org.springframework.beans.factory.InitializingBean;

import com.mock.core.service.shared.initialization.Initializable;

/**
 * 实例管理器
 * 
 * @author hongliang.ma
 * @version $Id: InstanceManager.java, v 0.1 2012-6-25 下午3:14:08 hongliang.ma Exp $
 */
public interface InstanceManager extends Observer, Initializable, InitializingBean {

    /**
     * 关闭所有实例
     */
    void disposeAll();

    /**
     * 根communicationId来加载实例
     * 
     * @param communicationId
     * 
     */
    void startInstance(String communicationId);

    /**
     * 根据communicationId销毁实例
     * 
     * @param communicationId
     */
    void disposeInstance(String communicationId);

}
