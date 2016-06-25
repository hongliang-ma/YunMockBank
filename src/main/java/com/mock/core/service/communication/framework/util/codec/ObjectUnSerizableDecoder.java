/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

import java.io.IOException;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.common.util.lang.ByteUtil;

/**
 * 用于反序列化为内部对象，如果发送过来的可以的话
 * 
 * @author hongliang.ma
 * @version $Id: ObjectUnSerizableDecoder.java, v 0.1 2012-6-25 下午4:49:08 hongliang.ma Exp $
 */
public class ObjectUnSerizableDecoder implements Decoder {

    /** 
     * @see com.mock.core.service.communication.util.tcp.codec.Decoder#decode(java.lang.Object)
     */
    public Object decode(Object message) {
        if (message instanceof byte[]) {
            try {
                return ByteUtil.bytesToObject((byte[]) message);
            } catch (IOException e) {
                throw new AnymockException(CommunicationErrorCode.DECODE_IO_EXCEPTION, e);
            } catch (ClassNotFoundException e) {
                throw new AnymockException(CommunicationErrorCode.DECODE_CAST_EXCEPTION, e);
            }
        }
        return message;
    }

}
