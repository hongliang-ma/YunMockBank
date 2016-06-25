
package com.mock.core.service.transaction.component.message;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.StringSubAdaptorEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 截取指定字符串
 * @author jun.qi
 * @version $Id: StringSubAdaptor.java, v 0.1 2012-6-30 下午03:00:30 jun.qi Exp $
 */
public final class StringSubAdaptor extends ComponetHandler {

    /**
     * 截取指定字符串
     * @param codeRule
     * @param message
     * @param type
     * @param req
     * @return
     * @throws Exception
     */
    private String getStringSubMessage(String codeRule, String message, String type, String req) {
        //去除固定部分的消息
        try {
            int state = Integer.parseInt(type);
            String tempMessage = null;
            //指定字符串截取的类型，分为: 1.左边   2.右边。3.指定字符串
            switch (state) {
                case 1:
                    tempMessage = StringUtil.substringBefore(message, codeRule);
                    break;
                case 2:
                    tempMessage = StringUtil.substringAfter(message, codeRule);
                    break;
                case 3:
                    tempMessage = StringUtil.substringBefore(req, message);
                    break;
                default:
                    LoggerUtil.warn(logger, "type类型错误, type=", type);
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }

            return tempMessage;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "getStringSubMessage失败，codeRule为", codeRule, "message=",
                message, "type=", type, "req=", req);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String req = (String) data.getProperties().get(DataMapDict.MSGBODY);
        String message = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        String codeRule = (String) localTransferData.getObject("StringSubAdaptorcodeRule");
        String type = (String) localTransferData.getObject("StringSubAdaptorstate");
        String isRule = (String) localTransferData.getObject("StringSubAdaptorisRule");
        String msage = null;
        if (StringUtil.isBlank(isRule)) {
            isRule = "false";
        }

        msage = getStringSubMessage(codeRule, message, type, req);
        switch (Enum.valueOf(StringSubAdaptorEnum.class, isRule)) {
            case TRUE:
                LoggerUtil.info(logger, "解析后的协议码RuleCode为[", msage, "]");
                data.getProperties().put(DataMapDict.CODERULE, msage);
                data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, message);
                break;

            case FALSE:
                LoggerUtil.info(logger, "指定字符串截取后的消息为 ", msage);
                data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, msage);
                break;

            default:
                LoggerUtil.warn(logger, "msage类型错误, msage=", msage);
                throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}