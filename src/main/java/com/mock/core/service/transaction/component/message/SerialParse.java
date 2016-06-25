
package com.mock.core.service.transaction.component.message;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.SerialParseTypeEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeSerialContent;
import com.mock.common.util.LoggerUtil;

/**
 * 定长解析
 * @author jun.qi
 * @version $Id: SerialParse.java, v 0.1 2012-6-28 下午08:52:16 jun.qi Exp $
 */
public final class SerialParse extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        if (StringUtil.equalsIgnoreCase((String) data.getProperties().get("AnymockIsPERFORM"),
            "TRUE")) {
            String defaultReturn = (String) localTransferData.getProperties().get(
                "SerialParsedefaultReturn");
            String ruleValue = null;
            if (StringUtil.isNotEmpty(defaultReturn)
                && !StringUtil.equalsIgnoreCase(defaultReturn, "null")) {
                ruleValue = (String) localTransferData.getObject("SerialParse" + defaultReturn);
            }

            String msage = StringUtil.defaultIfEmpty(ruleValue, "没有指定默认返回值!");
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, msage);
        } else {
            String msgType = (String) localTransferData
                .getObject("SerialParse" + DataMapDict.STATE);

            String ruleValue = null;
            /**解析定长类报文内容 */
            switch (Enum.valueOf(SerialParseTypeEnum.class, StringUtil.toUpperCase(msgType))) {
                case STRING:
                    ruleValue = getRuleValue(data, localTransferData);
                    String requestXML = (String) data.getProperties().get(DataMapDict.MSGBODY);
                    String msage = DecodeSerialContent.decodeSerial(requestXML, ruleValue);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, msage);
                    break;
                case BYTE:
                    byte[] reqXMLbyte = (byte[]) data.getProperties().get(DataMapDict.MSGBODY);
                    ruleValue = getRuleValue(data, localTransferData);
                    int idx = ruleValue.indexOf("{all}");
                    byte[] mesage = new byte[ruleValue.length() - 5 + reqXMLbyte.length];
                    System.arraycopy(ruleValue.getBytes(), 0, mesage, 0, idx);
                    System.arraycopy(reqXMLbyte, 0, mesage, idx, reqXMLbyte.length);
                    System.arraycopy(ruleValue.getBytes(), idx + 5, mesage,
                        idx + reqXMLbyte.length, ruleValue.length() - idx - 5);
                    data.getProperties()
                        .put(DataMapDict.SERVER_FORWARD_CONTENT, new String(mesage));
                    break;
                default:
                    LoggerUtil.warn(logger, "定长解析, msgType=", msgType);
                    throw new AnymockException(TransactionErrorCode.ILLEGAL_PARAMETER);
            }
        }

        ComponetFactory.componetSubHandler("ReturnMsghand", data, localTransferData);
    }

    /**
     * 
     * @param data
     * @param localTransferData
     * @return
     */
    private String getRuleValue(TransferData data, TransferData localTransferData) {
        String ruleValue = null;
        String coderule = (String) data.getProperties().get(DataMapDict.CODERULE);
        String defaultReturn = (String) localTransferData.getProperties().get(
            "SerialParsedefaultReturn");
        if (StringUtil.isNotEmpty(defaultReturn)
            && !StringUtil.equalsIgnoreCase(defaultReturn, "null")) {
            ruleValue = (String) localTransferData.getObject("SerialParse" + defaultReturn);
        }
        if (StringUtil.isNotEmpty(coderule) && StringUtil.isEmpty(ruleValue)) {
            ruleValue = (String) localTransferData.getObject("SerialParse" + coderule);
        }

        return ruleValue;
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}