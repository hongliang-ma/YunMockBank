/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.shared.initialization;

import com.alipay.sofa.common.xmap.annotation.XNode;
import com.alipay.sofa.common.xmap.annotation.XObject;
import com.alipay.sofa.common.xmap.spring.XNodeSpring;

/**
 * 可初始化的BEAN XML描述
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: InitializationDescriptor.java, v 0.1 2011-3-31 下午01:05:02 zhao.xiong Exp $
 */
@XObject("initializable")
public class InitializationDescriptor implements Comparable<InitializationDescriptor> {

    /** 可初始化的Spring bean id */
    @XNodeSpring("@bean")
    private Initializable bean;

    /** 
     * 初始化级别，越低越先初始化<br>
     * 约定最低级别是100，不同的初始化应用递增区间是100 <br>
     * 比如，初始化集群信息级别是100<br>
     * 初始化缓存是200<br>
     * 这样的递增区间是便于未来的扩展，比如可以中间插入150的级别，而不需要整体都调整级别
     **/
    @XNode("@level")
    private int           level;

    /** 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(InitializationDescriptor descriptor) {
        return this.getLevel() - descriptor.getLevel();
    }

    /**
     * Getter method for property <tt>bean</tt>.
     * 
     * @return property value of bean
     */
    public Initializable getBean() {
        return bean;
    }

    /**
     * Setter method for property <tt>bean</tt>.
     * 
     * @param bean value to be assigned to property bean
     */
    public void setBean(Initializable bean) {
        this.bean = bean;
    }

    /**
     * Getter method for property <tt>level</tt>.
     * 
     * @return property value of level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Setter method for property <tt>level</tt>.
     * 
     * @param level value to be assigned to property level
     */
    public void setLevel(int level) {
        this.level = level;
    }

}
