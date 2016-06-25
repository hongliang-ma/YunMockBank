/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.log;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.ByteUtil;

/**
 * 通讯日志打印帮助类，记录通讯的原文日志到anymock-sendrecevie.log
 * 
 * @author hongliang.ma
 * @version $Id: MessageLogUtil.java, v 0.1 2012-6-25 下午4:42:28 hongliang.ma Exp $
 */
public class MessageLogUtil {

    private static final Logger logger = LoggerFactory.getLogger("anymock-SENDRECEVIE");

    /**
     * 打印日志原生信息
     * 
     * @param msg
     */
    public static void printLog(String msg) {
        LoggerUtil.info(logger, msg);
    }

    /**
     * 打印日志
     * 
     * @param message
     * @param config
     * @param isResponse true表示接受报文，false表示发送报文
     * @param description
     */
    public static void printLog(Object message, CommunicationConfig config, boolean isResponse,
                                String description) {

        AssertUtil.isNotNull(config, CommunicationErrorCode.EMPTY_CONFIG);

        TransportProtocol protocol = config.getProtocol();
        String proxyBean = (protocol == TransportProtocol.PROXY ? "-" + config.getProxyBean() : "");

        StringBuilder builder = config.isServer() ? new StringBuilder("[" + protocol + proxyBean
                                                                      + "] 服务端")
            : new StringBuilder("[" + config.getProtocol() + proxyBean + "] 客户端");

        builder.append(
            (config.isServer() ? (isResponse ? "响应" : "接收") : (isResponse ? "接收" : "发送"))).append(
            "报文");
        builder.append(StringUtil.defaultIfBlank(description)).append(LoggerUtil.COMMA);
        builder.append("comunicationId=").append(config.getCommunicationId())
            .append(LoggerUtil.COMMA);
        builder.append("sysTemplateId=").append(config.getSysTemplateId());
        builder.append(LoggerUtil.ENTERSTR);
        Object stringMessage = getLogContent(message, config, isResponse);
        builder.append(stringMessage);

        LoggerUtil.info(logger, builder.toString());
    }

    /**
     * 生成日志内容
     * @param message
     * @param config
     * @param isResponse  true表示接受报文，false表示发送报文
     * @return
     */
    private static Object getLogContent(Object message, CommunicationConfig config,
                                        boolean isResponse) {
        Object stringMessage = null;

        if (config.getProtocol() == TransportProtocol.TCP
            || config.getProtocol() == TransportProtocol.SSL) {

            MessageFormat format = isResponse ? config.getRecvMessageFormat() : config
                .getSendMessageFormat();

            if (MessageFormat.TEXT != format) {
                stringMessage = ByteUtil.formatByte((byte[]) message);
            } else {
                stringMessage = new String(((byte[]) message), config.getCharset());
            }
        } else {
            stringMessage = message;
        }

        return stringMessage;
    }

    /**
     * 打印日志
     * 
     * @param message
     * @param config
     * @param isResponse
     * @param description
     */
    public static void printLog(MessageEnvelope message, CommunicationConfig config,
                                boolean isResponse, String description) {
        printLog(message.getContent(), config, isResponse, description);
    }

    /**
     * 打印日志
     * 
     * @param message
     * @param config
     * @param isResponse
     */
    public static void printLog(MessageEnvelope message, CommunicationConfig config,
                                boolean isResponse) {
        printLog(message, config, isResponse, null);
    }

}
