/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 消息中的时间函数解析组件
 * @author jun.qi
 */
public final class DateCode extends ComponetHandler {

    /**
     * 解析消息中的时间
     * @param template
     * @return 解析后的消息
     */
    private String decodeDate(String template) {
        StringBuilder sbfSplite = new StringBuilder(template);
        StringBuilder sbfSpliteAfter = new StringBuilder();
        String strStart = "${date(";
        String strEnd = ")}";
        String format = null;
        while (StringUtil.contains(sbfSplite.toString(), strStart)) {
            sbfSpliteAfter.setLength(0);
            sbfSpliteAfter.append(StringUtil.substringBefore(sbfSplite.toString(), strStart));
            format = StringUtil.substringBetween(sbfSplite.toString(), strStart, strEnd);
            sbfSpliteAfter.append(new SimpleDateFormat(format).format(new Date()));
            sbfSpliteAfter.append(StringUtil.substringAfter(sbfSplite.toString(), format + strEnd));
            sbfSplite.setLength(0);
            sbfSplite.append(sbfSpliteAfter);
        }

        return sbfSplite.toString();
    }

    public boolean ValidDateStr(String rStr, String rDateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
        formatter.setLenient(false);
        try {
            formatter.format(formatter.parse(rStr));
        } catch (Exception e) {
            LoggerUtil.warn(logger, "日期格式非法!");
            return false;
        }
        return true;
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
        String template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            String str = decodeDate(template);
            data.setObject(DataMapDict.SERVER_FORWARD_CONTENT, str);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "时间解析异常 " + template);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

}