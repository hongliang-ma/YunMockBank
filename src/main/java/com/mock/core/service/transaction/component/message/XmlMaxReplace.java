/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import java.util.Map;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;

/**
 *   键值对XML混合报文的替换
 *   
 * @author 马洪良
 * @version $Id: XmlMaxReplace.java, v 0.1 2013-1-23 下午1:57:40 马洪良 Exp $
 */
public final class XmlMaxReplace extends ComponetHandler {

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
            //原始报文
            String splite = (String) localTransferData.getObject("CodeRulesplite");
            if (StringUtil.isEmpty(splite) || StringUtil.equalsIgnoreCase(splite, "null")) {
                splite = "&";
            }
            Object message = data.getProperties().get(DataMapDict.MSGBODY);
            Map<String, String> paras = AtsframeStrUtil.stringTomap((String) message, splite);
            //返回报文
            String strTemplate = (String) data.getProperties().get(
                DataMapDict.SERVER_FORWARD_CONTENT);

            String returnMsg = DecodeXmlContent.replaceMaxXml(paras, strTemplate);

            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, returnMsg);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "XML替换失败");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }
}
