/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

import java.nio.charset.Charset;

/**
 * 字符串解码器
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: StringDecoder.java, v 0.1 2010-12-30 下午02:37:36 zhao.xiong Exp $
 */
public class StringDecoder implements Decoder {

    /** 字符集，默认GBK */
    private Charset charset = Charset.forName("GBK");

    /** 
     * @see com.mock.core.service.communication.util.tcp.codec.Decoder#decode(java.lang.Object)
     */
    public Object decode(Object message) {
        if (message instanceof byte[]) {
            byte[] arrs = (byte[]) message;
            return new String(arrs, charset);
        }
        return message;
    }

    /**
     * 设置字符集
     * 
     * @param charset
     */
    public void setCharSet(Charset charset) {
        this.charset = charset;
    }
}
