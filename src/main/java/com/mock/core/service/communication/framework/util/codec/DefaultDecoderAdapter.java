/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.service.communication.framework.model.TcpHeader;
import com.mock.core.service.communication.framework.util.tcp.TcpHeaderUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * MINA解码适配器的实现
 * 
 * @author zhao.xiong
 * @version $Id: DefaultDecoderAdapter.java, v 0.1 2012-8-17 下午07:23:56 zhao.xiong Exp $
 */
public class DefaultDecoderAdapter extends CumulativeProtocolDecoder {

    private static final Logger       logger = LoggerFactory.getLogger(DefaultDecoderAdapter.class);

    private TcpHeader                 tcpHeader;

    private final CommunicationConfig config;

    public DefaultDecoderAdapter(final CommunicationConfig config) {
        this.config = config;
        //        if (ConfigUtil.hasHeaderType(config)) {
        //            tcpHeader = ConfigUtil.getTcpHeader(config);
        //        }
    }

    /** 
     * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache.mina.common.IoSession, org.apache.mina.common.ByteBuffer, org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    @Override
    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) {
        if (in.remaining() > 0) {
            if (tcpHeader != null) {
                return handleMsgWithHeader(in, out);
            } else {
                return handleMsgWithOutHeader(in, out);
            }
        }
        return false;
    }

    /**
     * 处理无报头的
     * 
     * @param in
     * @param out
     * @return
     */
    private boolean handleMsgWithOutHeader(ByteBuffer in, ProtocolDecoderOutput out) {
        //对于tcp类的无报头的，一律统一处理，来多少处理多少,一次处理干净
        InputStream is = in.asInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[in.limit()];
        int offset = -1;
        try {
            while ((offset = is.read(buf)) != -1) {
                bos.write(buf, 0, offset);
            }
            out.write(bos.toByteArray());
            return true;
        } catch (IOException e) {
            ExceptionUtil.caught(e, "解码器在读取应答报文时发生I/O异常");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bos);
        }
        return false;
    }

    /**
     * 处理有报头的
     * 
     * @param in
     * @param out
     * @return
     */
    private boolean handleMsgWithHeader(ByteBuffer in, ProtocolDecoderOutput out) {
        int lenStrLenghth = tcpHeader.getHeaderLength();
        byte[] lenBytes = new byte[lenStrLenghth];
        //mark用于reset
        in.mark();
        in.get(lenBytes, 0, lenStrLenghth);
        int messageLength = TcpHeaderUtil.decodeLength(tcpHeader, lenBytes);
        //延迟预测：接收报文没有达到标示长度
        if (in.remaining() < messageLength) {

            LoggerUtil.warn(logger, config, "未接收完毕:buff remaing[", in.remaining(), "],报头预计大小:[",
                messageLength, "]");
            in.reset();
            return false;
        } else {
            byte[] content = new byte[messageLength];
            in.get(content, 0, messageLength);
            out.write(content);
            //粘包的话，重新返回true,继续搞之
            if (in.remaining() > 0) {
                return true;
            }
        }
        return false;
    }

}
