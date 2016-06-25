/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import org.apache.commons.codec.digest.DigestUtils;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.MD5SignerEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeSerialContent;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * MD5加签
 * @author jun.qi
 * @version $Id: MD5Signer.java, v 0.1 2012-7-4 下午02:16:29 jun.qi Exp $
 */
public final class MD5Signer extends ComponetHandler {

    /**模板分隔符组装 */
    private String take(String type, String s, String t) throws Exception {
        String str = null;
        if (type.equals("start")) {
            str = "<!--" + s + ":" + t + "-->";
        } else if (type.equals("end")) {
            str = "<!--" + s + ":/" + t + "-->";
        }
        return str;
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String content = null;
        String message = null;
        //加码字符串,支持XPATH，定长标识符+ 普通字符
        String template = (String) localTransferData.getObject("MD5Signer" + "node_template");
        //分 forward（从返回报文中取） 和 origin（从请求报文中取）
        String style = (String) localTransferData.getObject("MD5Signer" + "style");
        //String的方法名，即可处理该String
        String method = (String) localTransferData.getObject("MD5Signer" + "stringmethod");
        //取值模式,mode: 0: XML 1：定长
        int mode = Integer.parseInt((String) localTransferData.getObject("MD5Signer" + "mode"));
        try {
            switch (Enum.valueOf(MD5SignerEnum.class, style)) {
                case FORWARD:
                    content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
                    break;
                case ORIGIN:
                    content = (String) data.getProperties().get(DataMapDict.MSGBODY);
                    break;

                default:
                    content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
            }
            if (mode == 0) {
                message = DecodeXmlContent.replaceXmlValue(DecodeXmlContent.buildDocByString(content),
                    template);
            } else if (mode == 1) {
                message = DecodeSerialContent.decodeSerial(content, template);
            }
            AssertUtil.isNotBlank(message, TransactionErrorCode.COMPONENT_HANDLE_ERROR);

            String alipaykey = DigestUtils.md5Hex(message);
            LoggerUtil.info(logger, "生成MD5签名字段： ", message);
            LoggerUtil.info(logger, "生成MD5签名字段： ", alipaykey);

            if (StringUtil.isNotEmpty(method) || !StringUtil.equals(method, "[]")) {
                alipaykey = (String) String.class.getMethod(method).invoke(alipaykey);
            }
            String rs = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);

            rs = rs.replace(take("start", "sign", "md5"), alipaykey);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, rs);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "MD5加签异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}