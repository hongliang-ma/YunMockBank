
package com.mock.core.service.transaction.component.message;

import java.util.HashMap;
import java.util.Map;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 *  WP转换组装组件
 *  
 * @author hongliang.ma
 * @version $Id: BuildToString.java, v 0.1 2012-11-15 下午6:12:39 hongliang.ma Exp $
 */
public final class BuildToString extends ComponetHandler {

    /** 
     * @see com.mock.transaction.component.ComponetHandler#process(com.mock.model.detail.TransferData, com.mock.model.detail.TransferData)
     */
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        //获取Key
        String key = (String) localTransferData.getObject("BuildToString" + "key");
        @SuppressWarnings("unchecked")
        Map<String, String> strGetMsg = (HashMap<String, String>) data.getProperties().get(
            DataMapDict.MSGBODY);
        try {
            StringBuffer sbfMap = new StringBuffer();
            String[] keys = StringUtil.split(key, ",");
            for (String oneKye : keys) {
                sbfMap.append(oneKye).append("=").append(strGetMsg.get(key));
            }
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, sbfMap.toString());
            data.getProperties().put(DataMapDict.MSGBODY, sbfMap.toString());
            LoggerUtil.info(logger, "WP转换后组装的消息为：[", sbfMap.toString(), "]");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "WP转换后组装失败");
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
