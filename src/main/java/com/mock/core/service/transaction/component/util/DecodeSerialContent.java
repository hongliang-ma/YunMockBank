
package com.mock.core.service.transaction.component.util;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;

/**
 * 定长报文解析内容替换
 * 
 * @author hongliang.ma
 * @version $Id: decodeSerialContent.java, v 0.1 2012-12-7 下午4:52:39 hongliang.ma Exp $
 */
public final class DecodeSerialContent {
    /** logger */
    protected static final Logger logger = LoggerFactory.getLogger(ComponetHandler.class);

    /**
     * 
     * 获取原文的的某些字段的内容
     * 
     * @param message  原始报文
     * @param template  返回报文
     * @return
     */
    public static String decodeSerial(final String message, final String template) {
        try {
            if (StringUtil.containsNone(template, "${")) {
                return template;
            }
            /**解析 */
            String sexpr, value = null;
            int start = 0;
            int end = 0;

            String strStart = "${";
            String strEnd = "}";
            StringBuilder sbfReturnMsg = new StringBuilder();
            StringBuilder sbfSpliteMsg = new StringBuilder(template);
            final String DEFAULT_CHARSET = "GBK";
            while (sbfSpliteMsg.indexOf(strStart) != -1) {
                //计算出替换值
                sexpr = StringUtil.substringBetween(sbfSpliteMsg.toString(), strStart, strEnd);
                if (StringUtil.contains(sexpr, "-")) {
                    sbfReturnMsg.setLength(0);
                    sbfReturnMsg.append(StringUtil.substringBefore(sbfSpliteMsg.toString(),
                        strStart));

                    start = Integer.parseInt(StringUtil.substringBefore(sexpr, "-"));
                    end = Integer.parseInt(StringUtil.substringAfter(sexpr, "-"));
                    value = StringUtil.substring(message, start, end - start + 1);
                    value = new String(message.getBytes(DEFAULT_CHARSET), start, end - start + 1);
                    //替换掉
                    sbfReturnMsg.append(value);
                    sbfReturnMsg.append(StringUtil.substringAfter(sbfSpliteMsg.toString(), strEnd));
                    sbfSpliteMsg.setLength(0);
                    sbfSpliteMsg.append(sbfReturnMsg);
                } else {
                    break;
                }
            }

            return sbfSpliteMsg.toString();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "decodeSerialContent失败，message为", message, " template=",
                template);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }
}
