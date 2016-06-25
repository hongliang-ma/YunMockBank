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
 * 定长截取组件
 * @author jun.qi
 */
public final class LengthCodeRule extends ComponetHandler {

    private String getLengthMessage(String codeRule, String message) throws Exception {
        //去除固定部分的消息
        String tempMessage = StringUtil.substring(message, Integer.parseInt(codeRule));

        return tempMessage;
    }

    @Override
    public void process(TransferData data, TransferData localTransferData) throws AnymockException {
        String codeRule = (String) localTransferData.getObject("LengthCodeRule"
                                                               + DataMapDict.CODERULE);
        String msage = null;
        String message = (String) data.getProperties().get(DataMapDict.MSGBODY);
        try {
            msage = getLengthMessage(codeRule, message);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "定长截取失败，协议吗为", codeRule);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
        LoggerUtil.info(logger, "定长截取后的消息为 ", msage);
        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, msage);
        data.getProperties().put(DataMapDict.MSGBODY, msage);
    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData)
     */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}