/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 通用编码适配器
 * 
 * @author zhao.xiong
 * @version $Id: DefaultEncoderAdapter.java, v 0.1 2010-12-27 下午02:03:31 zhao.xiong Exp $
 */
public class DefaultEncoderAdapter extends ProtocolEncoderAdapter {

    /** 
     * @see org.apache.mina.filter.codec.demux.MessageEncoder#encode(org.apache.mina.core.session.IoSession, java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
        ByteBuffer buffer = ByteBuffer.allocate(1024, false).setAutoExpand(true);
        buffer.put((byte[]) message);
        buffer.flip();
        out.write(buffer);
    }

}
