/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.instance.tcp;

import java.net.Socket;

import com.mock.core.model.shared.communication.CommunicationConfig;

/**
 * sock创建回调接口
 * @author zhao.xiong
 * @version $Id: SockCallBack.java, v 0.1 2011-9-19 下午12:41:15 zhao.xiong Exp $
 */
public interface SockCallBack {

    /**
     * 通过代理的回调创建sock
     * 
     * @param config
     * @return
     */
    Socket buildSock(CommunicationConfig config);
}
