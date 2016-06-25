/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */

package com.mock.core.service.transaction.component.message;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 自定义组装长度组件
 * @author jun.qi
 */
public final class BuildLengthAdaptor extends ComponetHandler {

    /**
     * 组装报文长度
     */
    private String buildMessageLength(String template, int length, String type) throws Exception {
        String lengthOfTemplateStr = Integer.valueOf(template.length()).toString();
        String fixStr = StringUtil.alignRight(lengthOfTemplateStr, length, type);
        return fixStr + template;
    }

    @Override
    public void process(TransferData data, TransferData localTransferData) throws AnymockException {
        ///报文长度位
        String length = (String) localTransferData.getObject("BuildLengthAdaptor" + "Msglength");
        //报文长度填充符
        String type = (String) localTransferData.getObject("BuildLengthAdaptor" + "MsgType");
        String template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            String rs = buildMessageLength(template, Integer.parseInt(length), type);
            LoggerUtil.info(logger, "组装报文长度后的消息为 ", rs);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, rs);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "组装报文长度失败，length为", length, "MsgType=", type);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData)
     */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}