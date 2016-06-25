
package com.mock.core.service.transaction.component.message;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.XMLParseTypeEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * XML解析
 * @author jun.qi
 * @version $Id: XMLParse.java, v 0.1 2012-6-28 下午04:26:27 jun.qi Exp $
 */
public class XMLParse extends ComponetHandler {

    /**
     * 解析定长与XML报文类型其它内容
     * @param message
     * @param template
     * @return
     */
    private String getSerialXmlContent(String message, String template) {
        try {
            int idxLeftAngularBracket = StringUtil.indexOf(message, '<');
            /**去除固定长度部分的子串 */
            template = StringUtil.substring(message, idxLeftAngularBracket);
            /**利用decodeXmlContent解析去除固定长度后的消息 */

            /**
             * 如果返回信息字符串的长度位数小于固定长度，则用0左填充
             * 这里假定接受消息的固定长度等于发送消息的固定长度
             */
            String lengthOfTemplateStr = Integer.valueOf(template.length()).toString();
            String fixStr = StringUtil.alignRight(lengthOfTemplateStr, idxLeftAngularBracket, '0');
            return fixStr + template;
        } catch (Exception e) {
            ExceptionUtil
                .caught(e, "getSerialXmlContent失败，message为", message, "template", template);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String msgType = (String) localTransferData.getObject("XMLParse" + DataMapDict.STATE);

        String ruleValue = null;
        String coderule = (String) data.getProperties().get(DataMapDict.CODERULE);
        String defaultReturn = (String) localTransferData.getProperties().get(
            "XMLParsedefaultReturn");
        if (StringUtil.isNotEmpty(defaultReturn)
            && !StringUtil.equalsIgnoreCase(defaultReturn, "null")) {
            ruleValue = (String) localTransferData.getObject("XMLParse" + defaultReturn);
        }

        String msage = null;

        if (StringUtil.equalsIgnoreCase((String) data.getProperties().get("AnymockIsPERFORM"),
            "TRUE")) {
            msage = StringUtil.defaultIfEmpty(ruleValue, "没有指定默认返回值!");
        } else {
            /**XML解析报文内容 */

            if (StringUtil.isNotEmpty(coderule) && StringUtil.isEmpty(ruleValue)) {
                ruleValue = (String) localTransferData.getObject("XMLParse" + coderule);
            }

            switch (Enum.valueOf(XMLParseTypeEnum.class, StringUtil.toUpperCase(msgType))) {
                case XML:
                    msage = ruleValue;
                    break;

                case FIXED:
                    String requestXML = (String) data.getProperties().get(
                        DataMapDict.SERVER_FORWARD_CONTENT);
                    msage = getSerialXmlContent(requestXML, ruleValue);
                    break;

                default:
                    LoggerUtil.warn(logger, "XML解析出现异常, msgType=", msgType);
                    throw new AnymockException(TransactionErrorCode.ILLEGAL_PARAMETER);
            }
        }
        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, msage);
        ComponetFactory.componetSubHandler("ReturnMsghand", data, localTransferData);
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {

    }
}