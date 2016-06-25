/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

/**
 * 字节数组解码器
 * 
 * @author zhao.xiong
 * @version $Id: ByteDecoder.java, v 0.1 2010-12-30 下午02:34:53 zhao.xiong Exp $
 */
public class ByteDecoder implements Decoder {

    /** 
     * @see com.mock.core.service.communication.util.tcp.codec.Decoder#decode(java.lang.Object)
     */
    public Object decode(Object message) {

        //message里面的内容是byte[]类型
        return message;
    }

}
