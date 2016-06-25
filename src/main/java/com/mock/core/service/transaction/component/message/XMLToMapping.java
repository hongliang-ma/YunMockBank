
package com.mock.core.service.transaction.component.message;

import java.util.Map;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.PostXMLUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 
 * XML转换Mapping
 * @author jun.qi
 * @version $Id: XMLToMapping.java, v 0.1 2012-7-2 下午07:21:48 jun.qi Exp $
 */
public final class XMLToMapping extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        try {
            String content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
            Map<String, String> map = PostXMLUtil.ParseXML(content);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, map);
            LoggerUtil.info(logger, "转换Map为: ", map);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "XMLToMapping异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}