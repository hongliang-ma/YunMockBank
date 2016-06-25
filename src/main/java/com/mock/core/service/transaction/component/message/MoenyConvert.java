/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.HttpReqTypeEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.ParseUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.iwallet.biz.common.util.money.Money;

/**
 * 金额转化器工具
 * @author jun.qi
 */
public final class MoenyConvert extends ComponetHandler {

    public String toMoneyConvert(String ruleValue) throws Exception {
        String strStart = ParseUtil.take("start", "convert", "money");
        String strEnd = ParseUtil.take("end", "convert", "money");
        int idxStart = ruleValue.indexOf(strStart);
        int idxEnd = ruleValue.indexOf(strEnd, idxStart);
        String strName = null;
        String paramName = null;
        String paramVal = null;
        int iStartLength = strStart.length();
        int iEndLength = strEnd.length();
        while (idxStart > -1 && idxEnd > -1) {
            strName = ruleValue.substring(idxStart + iStartLength, idxEnd);
            paramName = ruleValue.substring(idxStart, idxEnd + iEndLength);
            paramVal = toYuan(strName);
            LoggerUtil.info(logger, "[交易金额转换]", "值：", paramVal);
            ruleValue = StringUtil.replace(ruleValue, paramName, paramVal);
            idxStart = ruleValue.indexOf(strStart, idxStart);
            idxEnd = ruleValue.indexOf(strEnd, idxEnd);
        }
        return ruleValue;
    }

    public String toYuan(String cent) {
        Money money = new Money();
        money.setCent(Long.valueOf(cent));
        return money.toString();
    }

    @Override
    public void process(TransferData data, TransferData localTransferData) throws AnymockException {

    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData)
     */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {

        String reqType = (String) localTransferData.getObject(DataMapDict.REQ_TYPE);
        try {
            switch (Enum.valueOf(HttpReqTypeEnum.class, reqType)) {
                case PARAMETER: {
                    @SuppressWarnings("unchecked")
                    Map<String, String> pars = (Map<String, String>) data
                        .getObject(DataMapDict.SERVER_FORWARD_CONTENT);

                    for (Entry<String, String> pa : pars.entrySet()) {
                        String neValue = toMoneyConvert(pa.getValue());
                        pars.put(pa.getKey(), neValue);
                    }
                    data.setObject(DataMapDict.SERVER_FORWARD_CONTENT, pars);
                    LoggerUtil.info(logger, "交易金额转换为:", pars);
                }
                    break;
                case CONTENT: {
                    String content = (String) data.getObject(DataMapDict.SERVER_FORWARD_CONTENT);
                    String ruleValue = toMoneyConvert(content);
                    data.setObject(DataMapDict.SERVER_FORWARD_CONTENT, ruleValue);
                    LoggerUtil.info(logger, "交易金额转换为:", ruleValue);
                }
                    break;
                default: {
                    LoggerUtil.warn(logger, "金额转转化器异常，，reqType为", reqType);
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "toMoneyConvert出现异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }
}