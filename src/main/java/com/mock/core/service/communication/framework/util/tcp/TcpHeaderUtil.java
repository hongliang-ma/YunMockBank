/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.service.communication.framework.model.TcpHeader;
import com.mock.common.util.lang.ByteConstant;
import com.mock.common.util.lang.ISOUtil;

/**
 *  用于采集字节流总共的长度
 * 
 * @author hongliang.ma
 * @version $Id: TcpHeaderUtil.java, v 0.1 2012-6-25 下午3:15:47 hongliang.ma Exp $
 */
public final class TcpHeaderUtil {

    /**
     * 禁用构造函数
     */
    private TcpHeaderUtil() {
        // 禁用构造函数
    }

    /**
     * 从输入流中读取长度的byte数组
     * 
     * @param headType
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] getLengthByte(TcpHeader header, InputStream inputStream)
                                                                                 throws IOException {

        byte[] length = new byte[header.getHeaderLength()];
        inputStream.read(length);
        return length;
    }

    /**
     * 对报文长度字节数组进行解码
     * 
     * @param header
     * @param byteLength
     * @return
     * @throws IOException 
     */
    public static int decodeLength(TcpHeader header, byte[] byteLength) {
        int headerLength = header.getExHeaderLength();
        switch (header.getHeadType()) {
            case BCD: {
                return Integer.parseInt(ISOUtil.bcd2number(byteLength, 0, headerLength, true));
            }

            case ASCII: {
                String str = new String(byteLength);
                return Integer.parseInt(StringUtil.trim(str));
            }

            case HEX_BCD: {
                String length = ISOUtil.bcd2string(byteLength, 0, headerLength, true);
                return Integer.parseInt(length, ByteConstant.HEX);
            }

            default: {
                break;
            }
        }

        return 0;
    }

    /**
     * 创建发送报文的报文长度
     * 
     * @param header
     * @param message
     * @return
     */
    public static byte[] createHeaderLength(TcpHeader header, byte[] message) {
        int headerLength = header.getExHeaderLength();
        switch (header.getHeadType()) {
            case BCD: {
                String length = ISOUtil.zeropad(message.length, headerLength);
                return ISOUtil.number2bcd(length, true);
            }

            case ASCII: {
                String length = ISOUtil.zeropad(Integer.toString(message.length), headerLength);
                return length.getBytes();
            }

            case HEX_BCD: {
                String hexLength = ISOUtil.zeropad(Integer.toHexString(message.length),
                    headerLength);
                return ISOUtil.string2bcd(hexLength, true);
            }

            default: {
                break;
            }
        }

        return null;
    }

    /**
     * 填充报文流
     * 
     * @param message
     * @param byteStream
     * @param config
     * @throws IOException
     */
    public static void fillMessageStream(Object message, ByteArrayOutputStream byteStream,
                                         CommunicationConfig config, TcpHeader tcpHeader)
                                                                                         throws IOException {
        byte[] content = (byte[]) message;
        if (tcpHeader != null) {
            //如果报头包含子域，不会智能把报头发送，需要开发人员自己填充
            if (tcpHeader.hasSub()) {
                byteStream.write(content);
                return;
            }
            byte[] header = TcpHeaderUtil.createHeaderLength(tcpHeader, content);
            if (header != null) {
                byteStream.write(header);
            }
        }
        byteStream.write(content);
    }
}
