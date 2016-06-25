/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.cache.descriptor;

import com.mock.core.service.shared.cache.CacheManager;
import com.alipay.sofa.common.xmap.annotation.XObject;
import com.alipay.sofa.common.xmap.spring.XNodeSpring;

/**
 * 缓存管理描述符，用于通过sofa框架提供的扩展机制来注册缓存管理器。
 * 
 * @author peng.lanqp
 * @version $Id: CacheDescriptor.java, v 0.1 2011-1-17 下午01:53:04 peng.lanqp Exp $
 */
@XObject("cache")
public class CacheDescriptor {

    @XNodeSpring("@manager")
    private CacheManager manager;

    /**
     * Getter method for property <tt>manager</tt>.
     * 
     * @return property value of manager
     */
    public CacheManager getManager() {
        return manager;
    }

    /**
     * Setter method for property <tt>manager</tt>.
     * 
     * @param manager value to be assigned to property manager
     */
    public void setManager(CacheManager manager) {
        this.manager = manager;
    }

}
