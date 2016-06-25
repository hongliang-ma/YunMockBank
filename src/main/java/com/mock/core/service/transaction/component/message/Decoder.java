
package com.mock.core.service.transaction.component.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.DecoderEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 解密解析
 * @author jun.qi
 * @version $Id: Decoder.java, v 0.1 2012-7-4 上午10:47:36 jun.qi Exp $
 */
public final class Decoder extends ComponetHandler {

    /**
     * Base64解密
     * 
     * @param requestXml
     * @param ruleValue
     * @return
     * @throws Exception
     */
    public String basedcodeValue(String requestXML, String ruleValue) throws Exception {
        byte[] bytes = ruleValue.getBytes();
        byte[] data = Base64.decodeBase64(bytes);
        String paramVal = new String(data);
        LoggerUtil.info(logger, "[Base64解密]", "值：", paramVal);
        ruleValue = StringUtil.replace(requestXML, ruleValue, paramVal);
        return ruleValue;
    }

    public String decode(String requestXML, String ruleValue, String type) throws Exception {
        String paramVal = null;
        if (type.equals("base64")) {
            paramVal = basedcodeValue(requestXML, ruleValue);
        }
        return paramVal;
    }

    public Object getobj(Object obj) {
        if (obj instanceof Map || obj instanceof String) {
            return obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        //解码类型
        String type = (String) localTransferData.getObject("Decoder" + "Encodetype");
        //协议码
        String codeRule = (String) localTransferData.getObject("Decoder" + "codeRule");
        //请求消息的类型，分为：0.XML，1.原值，2.键值对

        Object obj = data.getProperties().get(DataMapDict.MSGBODY);
        String xmlcodeRule = null;
        String decodeMsg = null;
        String content = null;
        @SuppressWarnings("rawtypes")
        Map par = new HashMap();
        try {
            switch (Enum.valueOf(DecoderEnum.class, StringUtil.toUpperCase(type))) {
                case XML:
                    content = (String) getobj(obj);
                    xmlcodeRule = DecodeXmlContent.getOneXmlCodeRule(codeRule, null, DecodeXmlContent.buildDocByString(content));
                    decodeMsg = decode(content, xmlcodeRule, type);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, decodeMsg);
                    break;
                case ORIGVALUE:
                    content = (String) getobj(obj);
                    decodeMsg = decode(content, content, type);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, decodeMsg);
                    break;
                case KEYVALUE:
                    @SuppressWarnings("rawtypes")
                    Map paras = (Map) getobj(obj);
                    Object temp = paras.get(codeRule.replace("[", "").replace("]", ""));
                    xmlcodeRule = ((String) temp);
                    decodeMsg = decode(xmlcodeRule, xmlcodeRule, type);
                    par.put(codeRule.replace("[", "").replace("]", ""), decodeMsg);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, par);
                    break;

                default:
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
            LoggerUtil.info(logger, "Base64解密为[", decodeMsg, "]");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "解密解析异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}