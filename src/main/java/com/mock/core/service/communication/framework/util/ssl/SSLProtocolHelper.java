/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.common.util.ExceptionUtil;

/**
 * SSL安全协议助手，提供创建SSLContext的方法
 * 
 * @author peng.lanqp
 * @author 松雪
 * @version $Id: SSLProtocolHelper.java, v 0.4 2012-7-31 下午3:41:37 hao.zhang Exp $
 */
public class SSLProtocolHelper {

    /**
     * 根据通讯配置，获取相关的证书内容和证书密码，创建SSLContext
     * 
     * @param config
     * @return
     */
    public static SSLContext create() {

        try {
            return SSLContextFactory.getInstance().createTrustSSLContext();
        } catch (GeneralSecurityException e) {

            ExceptionUtil.caught(e, "初始化证书容器发生异常");
            throw new AnymockException(CommunicationErrorCode.KEY_SECURITY_EXCEPTION, e);

        } catch (IOException e) {
            ExceptionUtil.caught(e, "初始化证书IO异常");
            throw new AnymockException(CommunicationErrorCode.KEY_SECURITY_EXCEPTION, e);
        }
    }

}
