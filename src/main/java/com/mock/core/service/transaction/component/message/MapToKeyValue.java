
package com.mock.core.service.transaction.component.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;

/**
 * MAP报文转键值对
 * @author hongliang.ma
 * @version $Id: MapToKeyValue.java, v 0.1 2012-11-30 下午3:13:36 hongliang.ma Exp $
 */
public class MapToKeyValue extends ComponetHandler {

    /** 
     * @see com.mock.transaction.component.ComponetHandler#process(com.mock.model.detail.TransferData, com.mock.model.detail.TransferData)
     */
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> strGetMsg = (HashMap<String, String>) data.getProperties().get(
                DataMapDict.MSGBODY);

            StringBuilder sbfGetKeyValue = new StringBuilder();
            for (@SuppressWarnings("rawtypes")
            Iterator ite = strGetMsg.entrySet().iterator(); ite.hasNext();) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) ite.next();
                sbfGetKeyValue.append(entry.getKey()).append("=").append(entry.getValue())
                    .append("&");
            }

            data.setObject(DataMapDict.MSGBODY, StringUtil.trim(sbfGetKeyValue.toString(), "&"));
        } catch (Exception e) {
            ExceptionUtil.caught(e, "加密出现错误!");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData, com.mock.model.detail.TransferData)
     */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }

}
