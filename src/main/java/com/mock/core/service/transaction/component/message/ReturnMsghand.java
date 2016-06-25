
package com.mock.core.service.transaction.component.message;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 对返回报文进行进一步处理，通常是函数处理
 * @author hongliang.ma
 * @version $Id: ReturnMsghand.java, v 0.1 2012-11-14 下午5:19:06 hongliang.ma Exp $
 */
public final class ReturnMsghand extends ComponetHandler {

    /** 
     * @see com.mock.transaction.component.ComponetHandler#process(com.mock.model.detail.TransferData, com.mock.model.detail.TransferData)
     */
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData, com.mock.model.detail.TransferData)
     */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
        String template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        if (StringUtil.contains(template, "[repeat(")) {
            LoggerUtil.info(logger, "内容替换工具为RepeatCode");
            ComponetFactory.componetSubHandler("RepeatCode", data, localTransferData);
            template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        }
        
        while (StringUtil.contains(template, "${")) {
            if (StringUtil.contains(template, "repeat(")) {
                LoggerUtil.info(logger, "内容替换工具为RepeatCode");
                ComponetFactory.componetSubHandler("RepeatCode", data, localTransferData);
            } else if (StringUtil.contains(template, "random(")) {
                LoggerUtil.info(logger, "内容替换工具为RandomCode");
                ComponetFactory.componetSubHandler("RandomCode", data, localTransferData);
            } else if (StringUtil.contains(template, "date(")) {
                LoggerUtil.info(logger, "内容替换工具为DateCode");
                ComponetFactory.componetSubHandler("DateCode", data, localTransferData);
            } else if (StringUtil.contains(template, "${/")) {
                LoggerUtil.info(logger, "内容替换工具为XmlParaReplace");
                ComponetFactory.componetSubHandler("XmlParaReplace", data, localTransferData);
            } else if (StringUtil.contains(template, "${[")) {
                LoggerUtil.info(logger, "内容替换工具为XmlMaxReplace");
                ComponetFactory.componetSubHandler("XmlMaxReplace", data, localTransferData);
            } else if (StringUtil.contains(template, "encoder:base64")) {
                ComponetFactory.componetSubHandler("Encoder", data, localTransferData);
            } else {
                break;
            }
            template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        }

        
    }
}
