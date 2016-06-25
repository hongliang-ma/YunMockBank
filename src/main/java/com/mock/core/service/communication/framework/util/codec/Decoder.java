/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

/**
 * 解码器接口
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: Decoder.java, v 0.1 2010-12-30 下午02:36:52 zhao.xiong Exp $
 */
public interface Decoder {

    /**
     * 根据NetworkConfig的数据格式，来反编成相关的数据返回，如果不符合类型，直接返回字节数组
     * 
     * @param message 参数本应是字节数组，但是由于历史原因，还是用Object来代替了。
     * @return
     */
    Object decode(Object message);
}
