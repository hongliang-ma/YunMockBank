
package com.mock.core.service.transaction.component.message;

import org.apache.commons.codec.binary.Base64;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 加码解析
 * @author jun.qi
 * @version $Id: Encoder.java, v 0.1 2012-7-4 下午01:49:10 jun.qi Exp $
 */
public final class Encoder extends ComponetHandler {

    /**
     * Base64加密
     * @param requestXml
     * @param ruleValue
     * @return
     * @throws Exception 
     */
    public String basedcodeValue(String ruleValue) throws Exception {
        StringBuilder sbfSplite = new StringBuilder(ruleValue);
        StringBuilder sbfSpliteAfter = new StringBuilder();
        String strStart = "${<!--encoder:base64-->";
        String strEnd = "<!--encoder:/base64-->}";
        while (StringUtil.contains(sbfSplite.toString(), strStart)) {
            sbfSpliteAfter.setLength(0);
            sbfSpliteAfter.append(StringUtil.substringBefore(sbfSplite.toString(), strStart));
            sbfSpliteAfter.append(new String(Base64.encodeBase64((StringUtil.substringBetween(
                sbfSplite.toString(), strStart, strEnd).getBytes()))));
            sbfSpliteAfter.append(StringUtil.substringAfter(sbfSplite.toString(), strEnd));
            sbfSplite.setLength(0);
            sbfSplite.append(sbfSpliteAfter);
        }

        return sbfSplite.toString();
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
        try {
            String content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
            String str = basedcodeValue(content);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, str);
            LoggerUtil.info(logger, "Base64加密为:", str);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "加密出现错误!");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }
}