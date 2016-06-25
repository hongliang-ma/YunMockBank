/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.codec;

import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.mock.core.model.shared.communication.CommunicationConfig;

/***
 * 通用mina解码器工厂
 * 
 * @author zhao.xiong
 * @version $Id: DefaultProtocolCodecFactory.java, v 0.1 2012-8-17 下午07:27:09 zhao.xiong Exp $
 */
public class DefaultProtocolCodecFactory extends DemuxingProtocolCodecFactory {

    private final DefaultDecoderAdapter decoder;
    private final DefaultEncoderAdapter encoder;

    public DefaultProtocolCodecFactory(final CommunicationConfig config) {
        encoder = new DefaultEncoderAdapter();
        decoder = new DefaultDecoderAdapter(config);
    }

    /** 
     * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder()
     */
    @Override
    public ProtocolEncoder getEncoder() {
        return encoder;
    }

    /** 
     * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder()
     */
    @Override
    public ProtocolDecoder getDecoder() {
        return decoder;
    }

}
