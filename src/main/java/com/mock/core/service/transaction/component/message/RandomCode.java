
package com.mock.core.service.transaction.component.message;

import java.util.Random;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;

/**
 * 消息中随机数标识解析
 * @author jun.qi
 * @version $Id: RandomCode.java, v 0.1 2012-7-2 下午07:54:39 jun.qi Exp $
 */
public final class RandomCode extends ComponetHandler {
    /**
     * 取得定长的随机数
     */
    public String getRandomNumber(int length) {
        /** 随机数生成器 */
        Random random = new Random();
        /** 数字数组 */
        String numbers = "0123456789";
        int number = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            number = random.nextInt(numbers.length());
            result.append(numbers.charAt(number));
        }
        return result.toString();
    }

    /**
     * 解析消息中的随机数
     * @param template
     * @return 解析后的消息
     */
    private String decodeRandom(String template) {
        //占位符
        StringBuilder sbfSplite = new StringBuilder(template);
        StringBuilder sbfSpliteAfter = new StringBuilder();
        String strStart = "${random(";
        String strEnd = ")}";
        String rules = null;
        while (StringUtil.contains(sbfSplite.toString(), strStart)) {
            sbfSpliteAfter.setLength(0);
            sbfSpliteAfter.append(StringUtil.substringBefore(sbfSplite.toString(), strStart));
            rules = StringUtil.substringBetween(sbfSplite.toString(), strStart, strEnd);
            sbfSpliteAfter.append(this.getRandomNumber(Integer.parseInt(rules)));
            sbfSpliteAfter.append(StringUtil.substringAfter(sbfSplite.toString(), rules + strEnd));
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
        String str = null;
        String template = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            str = decodeRandom(template);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "随机数解析出现异常，template=", template);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, str);
    }
}