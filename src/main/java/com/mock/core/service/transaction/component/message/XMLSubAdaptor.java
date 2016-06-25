
package com.mock.core.service.transaction.component.message;

import org.w3c.dom.Document;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * 截取XML协议码
 * @author jun.qi
 * @version $Id: XMLSubAdaptor.java, v 0.1 2012-7-2 下午01:09:06 jun.qi Exp $
 */
public final class XMLSubAdaptor extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String rule = (String) data.getProperties().get(DataMapDict.CODERULE);
        Document requestXML = (Document) data.getProperties().get(DataMapDict.XMLBEFOREHAND);
        String codeRule = (String) localTransferData.getObject("XMLSubAdaptorcodeRule");
        String subRule = (String) localTransferData.getObject("XMLSubAdaptorsubRule");
        String split = (String) localTransferData.getObject("XMLSubAdaptorsplit");
        String xmlRule = null;
        String[] s = null;
        String[] s1 = null;
        try {
            if (StringUtil.isNotBlank(rule)) {
                s = StringUtil.split(rule, "\\" + split);
                for (int i = 0, iLength = s.length; i < iLength; i++) {
                    if (StringUtil.indexOf(s[i], subRule) != -1) {
                        s1 = StringUtil.split(s[i], "=");
                        for (int j = 0, iCount = s1.length; j < iCount; j++) {
                            if (StringUtil.indexOf(s1[j], subRule) == -1) {
                                xmlRule = s1[j];
                            }
                        }
                    }
                }
            }
            if (StringUtil.isBlank(xmlRule)) {
                DecodeXmlContent.getOneXmlCodeRule(codeRule, null, requestXML);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "截取XML协议码异常，codeRule=", codeRule);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
        data.getProperties().put(DataMapDict.CODERULE, xmlRule);
        LoggerUtil.info(logger, "解析后的协议码RuleCode为[", xmlRule, "]");

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}
