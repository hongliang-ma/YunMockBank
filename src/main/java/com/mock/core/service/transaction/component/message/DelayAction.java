/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;

/**
 * 普通延时
 * @author jun.qi
 * @version $Id: DelayAction.java, v 0.1 2012-7-3 下午01:08:26 jun.qi Exp $
 */
public final class DelayAction extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        /**延迟时间,单位（毫秒）*/
        String delay = (String) localTransferData.getObject("DelayAction" + "delay");
        try {
            Thread.sleep(Integer.parseInt(delay));
        } catch (NumberFormatException e) {
            ExceptionUtil.caught(e, "延迟时间异常! ");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "延迟时间异常! ");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}