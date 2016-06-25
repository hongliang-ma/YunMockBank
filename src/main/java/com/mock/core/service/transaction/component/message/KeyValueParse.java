
package com.mock.core.service.transaction.component.message;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.KeyValueTypeEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.specific.Data;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 键值对解析
 * @author jun.qi
 * @version $Id: KeyValueParse.java, v 0.1 2012-6-29 下午02:09:52 jun.qi Exp $
 */
public class KeyValueParse extends ComponetHandler {

    /**
     * 规则解析--原值替换
     * @param ruleValue 替换前的规则值
     * @param request 当前Web请求
     * @return 原值替换后的规则值
     */
    public String replaceOrigValue(String ruleValue, Map<String, String> map) {
        try {
            String paramValue = null;
            /**占位符 */
            String strStart = "[";
            String strEnd = "]";
            /**起止位置 */
            int idxStart = ruleValue.indexOf(strStart);
            int idxEnd = ruleValue.indexOf(strEnd, idxStart);
            int iStartLength = strStart.length();
            int iEndLength = strEnd.length();
            String strName = null;
            String paramName = null;
            while (idxStart > -1 && idxEnd > -1) {
                strName = ruleValue.substring(idxStart + iStartLength, idxEnd);
                paramValue = map.get(strName);
                if (StringUtil.isEmpty(paramValue)) {
                    idxStart = ruleValue.indexOf(strStart, idxEnd);
                    idxEnd = ruleValue.indexOf(strEnd, idxStart);
                } else {
                    paramName = ruleValue.substring(idxStart, idxEnd + iEndLength);
                    ruleValue = StringUtil.replace(ruleValue, paramName, paramValue);
                    idxStart = ruleValue.indexOf(strStart, idxStart);
                    idxEnd = ruleValue.indexOf(strEnd, idxStart);
                }
            }
            return ruleValue;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "replaceOrigValue失败，ruleValue为", ruleValue);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    /**
     * 解析从KEY里获取值
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public String replacePathValue(String ruleValue, Map<String, String> map) {
        String paramValue = null;
        String strName = null;

        /**占位符 */
        String strStart = "[";
        String strEnd = "]";

        /**起止位置 */
        int idxStart = ruleValue.indexOf(strStart);
        int idxEnd = ruleValue.indexOf(strEnd, idxStart);
        int iLength = strStart.length();
        int iEndLength = strEnd.length();
        String paramName = null;
        XmlParaReplace xmlParaReplace = null;
        try {
            while (idxStart > -1 && idxEnd > -1) {
                strName = ruleValue.substring(idxStart + iLength, idxEnd);
                paramValue = map.get(strName);
                if (paramValue == null || paramValue.equals("")) {
                    idxStart = ruleValue.indexOf(strStart, idxEnd);
                    idxEnd = ruleValue.indexOf(strEnd, idxStart);
                } else {
                    paramName = ruleValue.substring(idxStart, idxEnd + iEndLength);
                    ruleValue = StringUtil.replaceOnce(ruleValue, paramName, "");
                    xmlParaReplace = new XmlParaReplace();
                    if (xmlParaReplace != null) {
                        ruleValue = DecodeXmlContent.replaceXmlValue(
                            DecodeXmlContent.buildDocByString(paramValue), ruleValue);
                    }
                    xmlParaReplace = null;
                    idxStart = ruleValue.indexOf(strStart, idxStart);
                    idxEnd = ruleValue.indexOf(strEnd, idxStart);
                }
            }
            return ruleValue;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "replacePathValue失败，ruleValue为", ruleValue);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String ruleValue = null;
        String defaultReturn = (String) localTransferData.getProperties().get(
            "KeyValueParsedefaultReturn");
        if (StringUtil.isNotEmpty(defaultReturn)
            && !StringUtil.equalsIgnoreCase(defaultReturn, "null")) {
            ruleValue = (String) localTransferData.getObject("KeyValueParse" + defaultReturn);
        }

        String msage = null;

        if (StringUtil.equalsIgnoreCase((String) data.getProperties().get("AnymockIsPERFORM"),
            "TRUE")) {
            msage = StringUtil.defaultIfEmpty(ruleValue, "没有指定默认返回值!");
        } else {
            String splite = (String) localTransferData.getObject("CodeRulesplite");
            if (StringUtil.isEmpty(splite) || StringUtil.equalsIgnoreCase(splite, "null")) {
                splite = "&";
            }

            Map<String, String> reqMap = AtsframeStrUtil.stringTomap(
                (String) data.getObject(DataMapDict.SERVER_FORWARD_CONTENT), splite);

            String amount = null;

            if (reqMap.get("refund_status_url") != null) {
                amount = reqMap.get("amount");
                Data.amount = amount;
            }

            if (reqMap.get("action") != null) {
                if (StringUtil.contains(reqMap.get("action"), "refund")) {
                    reqMap.put("amount", Data.amount);
                }
            }

            String coderule = (String) data.getProperties().get(DataMapDict.CODERULE);
            if (StringUtil.isNotEmpty(coderule) && StringUtil.isEmpty(ruleValue)) {
                ruleValue = (String) localTransferData.getObject("KeyValueParse" + coderule);
            }

            /**取出返回规则  */

            /**键值对解析  */
            String msgType = (String) localTransferData.getObject("KeyValueParse"
                                                                  + DataMapDict.STATE);
            switch (Enum.valueOf(KeyValueTypeEnum.class, StringUtil.toUpperCase(msgType))) {
                case KEYVALUE:
                    msage = replaceOrigValue(ruleValue, reqMap);
                    break;

                case XMLMAX:
                    try {
                        msage = DecodeXmlContent.replaceMaxXml(reqMap, ruleValue);
                    } catch (XPathExpressionException e) {
                        ExceptionUtil.caught(e, "键值对替换出现异常, ruleValue=", ruleValue);
                        throw new AnymockException(TransactionErrorCode.ILLEGAL_PARAMETER);
                    }
                    //msage = replacePathValue(ruleValue, reqMap);
                    break;

                default:
                    LoggerUtil.warn(logger, "键值对解析出现异常, msgType=", msgType);
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