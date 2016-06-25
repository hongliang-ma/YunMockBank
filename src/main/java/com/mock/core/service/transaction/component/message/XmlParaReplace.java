
package com.mock.core.service.transaction.component.message;

import org.w3c.dom.Document;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: XmlParaReplace.java, v 0.1 2012-11-14 下午5:03:02 hongliang.ma Exp $
 */
public final class XmlParaReplace extends ComponetHandler {

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
        try {
            Document reqXML = null;
            String strTemplate = (String) data.getProperties().get(
                DataMapDict.SERVER_FORWARD_CONTENT);
            String requestXML = (String) data.getProperties().get(DataMapDict.MSGBODY);
            if (requestXML.indexOf("<?xml") != -1) {
                int idxLeftAngularBracket = StringUtil.indexOf(requestXML, '<');
                /**去除固定长度部分的子串 */
                String req = StringUtil.substring(requestXML, idxLeftAngularBracket);
                reqXML = DecodeXmlContent.buildDocByString(req);
            }
            if (reqXML == null) {
                reqXML = (Document) data.getProperties().get(DataMapDict.XMLBEFOREHAND);
            }
            String str = DecodeXmlContent.replaceXmlValue(reqXML, strTemplate);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, str);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "XML替换失败");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

}
