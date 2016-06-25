
package com.mock.core.service.transaction.component.message;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 消息中内容重复标识解析
 * @author jun.qi
 * @version $Id: RepeatCode.java, v 0.1 2012-7-2 下午07:48:07 jun.qi Exp $
 */
public final class RepeatCode extends ComponetHandler {

    /**
     * 解析消息中重复的内容
     * @param template
     * @return
     */
    public String decodeRepeat(String template, String strStart, String strEnd) {
        // 规则形式 [repeat("#",5)]或则 ${repeat(" ",32)}
        if (StringUtil.containsNone(template, strStart)
            || StringUtil.containsNone(template, strEnd)) {
            return template;
        }

        String rule = null;
        String repeatStr = null;
        int repeatCnt = 0;

        StringBuffer sfbReturnMsg = new StringBuffer();
        StringBuffer sbfMsgSplite = new StringBuffer(template);
        while (StringUtil.contains(sbfMsgSplite.toString(), strStart)) {
            sfbReturnMsg.setLength(0);
            sfbReturnMsg.append(StringUtil.substringBefore(sbfMsgSplite.toString(), strStart));

            rule = StringUtil.substringBetween(sbfMsgSplite.toString(), strStart, strEnd);
            repeatStr = StringUtil.substringBefore(rule, ",").split("\"")[1];
            repeatCnt = Integer.parseInt(StringUtil.substringAfter(rule, ","));
            for (int i = 0; i < repeatCnt; i++) {
                sfbReturnMsg.append(repeatStr);
            }
            sfbReturnMsg.append(StringUtil.substringAfter(sbfMsgSplite.toString(), strEnd));

            sbfMsgSplite.setLength(0);
            sbfMsgSplite.append(sfbReturnMsg);
        }

        return sbfMsgSplite.toString();
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
        String meage = null;
        String template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            meage = decodeRepeat(template, "[repeat(", ")]");
            meage = decodeRepeat(meage, "${repeat(", ")}");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解析消息中重复出现异常，template=", template);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
        data.setObject(DataMapDict.SERVER_FORWARD_CONTENT, meage);
    }
}