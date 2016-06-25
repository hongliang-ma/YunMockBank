/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.biz.messages.routing.response;

import java.util.Map;

import com.mock.biz.messages.routing.RoutingResponse;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.MessageType;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.code.ErrorCode;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.common.util.lang.StringUtil;

/**
 * 路由引擎-响应生成器实现类
 * 
 * @author hongliang.ma
 * @version $Id: RoutingResponseImpl.java, v 0.1 2012-7-3 下午1:58:27 hongliang.ma Exp $
 */
public class RoutingResponseImpl implements RoutingResponse {

    /** 
     * @see com.alipay.supergw.biz.transaction.routing.common.RoutingResponse#generate(com.alipay.supergw.core.transaction.domain.Transaction, boolean, com.alipay.supergw.core.domain.shared.exception.SupergwException)
     */
    public MessageEnvelope generate(final MessageSendResult msgResult, TransferData transferData,
                                    AnymockException exception, boolean sendDirect) {

        // 路由处理异常，并且不是重复交易，不再组装响应报文
        if (exception != null) {
            return new MessageEnvelope(exception.getErrorCode().getCode(), exception.getMessage());
        }

        if (sendDirect) {
            if (null == msgResult) {
                ErrorCode errorCode = CommunicationErrorCode.HTTP_STATUS_ERROR;
                return new MessageEnvelope(errorCode.getCode(), errorCode.getDescription());
            }
            // 同步交易调用超时
            if (isTimeout(msgResult)) {
                ErrorCode errorCode = CommunicationErrorCode.READ_TIME_OUT;
                return new MessageEnvelope(errorCode.getCode(), errorCode.getDescription());
            }
            return msgResult.getMessageEnvelope();
        }

        // TransactionThreadLocal.put(transaction); 使用线程来做
        return getMessageEnvelope(transferData, MessageType.SEND_RESPONSE);
    }

    /**
     * 
     * @param transferData
     * @param sendResponse
     * @return
     */
    private MessageEnvelope getMessageEnvelope(TransferData transferData, MessageType sendResponse) {

        /** 报文正文格式 */
        String sendFormat = (String) transferData.getObject(DataMapDict.SENDFORMAT);
        MessageFormat messageFormat;
        if (StringUtil.isNotBlank(sendFormat)) {
            messageFormat = Enum.valueOf(MessageFormat.class, sendFormat);
        } else {
            messageFormat = MessageFormat.TEXT;
        }

        /** 报文正文信息 */
        Object content = transferData.getObject(DataMapDict.SERVER_FORWARD_CONTENT);
		if (content == null) {
			throw new AnymockException(SystemErrorCode.SYSTEM_ERROR,
					"没有查询到返回报文！");
		}
        /** 报文附加信息，例如HTTP报文头的附加信息 */
        @SuppressWarnings("unchecked")
        Map<String, String> extraContent = (Map<String, String>) transferData
            .getObject(DataMapDict.SENDEXTRACONTENT);

        MessageEnvelope myMessageEnvelope = new MessageEnvelope(messageFormat, content,
            extraContent);
        return myMessageEnvelope;
    }

    /**
     * 根据交易报文判断是否是交易超时（在没有其他交易错误码的情况下使用）
     * 
     * @param transaction
     * @param messageType
     * @return
     */
    private boolean isTimeout(final MessageSendResult transaction) {
        return transaction.isTimeout() ? true : false;
    }

}
