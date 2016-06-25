/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.transaction.detail.TransferData;

/**
 * 处理类，为所有工具的抽象接口
 * 
 * @author hongliang.ma
 * @version $Id: ComponetHandler.java, v 0.1 2012-6-27 上午11:05:03 hongliang.ma Exp $
 */
public abstract class ComponetHandler {

    /** logger */
    protected static final Logger logger = LoggerFactory.getLogger(ComponetHandler.class);

    /**
     * 基本处理工具，处理从通讯传过来的数据
     * 
     * @param data   传入和处理的实际数据
     * @param localTransferData  局部数据,系统初始化的时候就完成了
     * @throws Exception
     */
    protected abstract void process(TransferData data, final TransferData localTransferData)
                                                                                            throws AnymockException;

    /**
     * 再次处理工具，通常用于一个处理器的内部还有需要内部处理
     * 例如template多个模板
     * 或者模板内部含有时间、随机值等等
     * 
     * @param data 传入和处理的实际数据
     * @param localTransferData  局部数据,系统初始化的时候就完成了
     * @throws Exception
     */
    protected abstract void processInner(TransferData data, final TransferData localTransferData)
                                                                                                 throws AnymockException;

}
