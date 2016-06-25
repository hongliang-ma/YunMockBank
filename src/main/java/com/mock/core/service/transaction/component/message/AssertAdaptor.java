package com.mock.core.service.transaction.component.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.AssertAEnum;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 报文校验
 * @author jun.qi
 * @version $Id: AssertAdaptor.java, v 0.1 2012-8-7 上午10:12:03 jun.qi Exp $
 */
public final class AssertAdaptor extends ComponetHandler {

    /**请求方式, 请求消息的类型，分为：0.XML，1.定长，2.键值对，3.键值对与XML混合 */
    public static final String CD_REQ_TYPE = "reqType";

    /**校验值, 填写报文中需要校验的值。校验字段以 ; 号间隔，例如：Message;Message/CSReq/instId */
    public static final String CD_EXPECTED = "expected";

    @SuppressWarnings({ "rawtypes", "unused" })
    public void parseXml(String splitstr, String content, String str1, Boolean flag,
                         TransferData data) throws AnymockException, DocumentException {
        String str[] = StringUtil.split(str1, "\\" + splitstr);
        boolean isValidateNode = true;
        Document validateNode;
        Element el = null;
        String qname = null;
        String selname = null;
        List nodeList = null;
        Element tempEle = null;
        Document tempDoc = null;
        StringBuilder qelnamebf = new StringBuilder();
        for (int i = 0, ilength = str.length; i < ilength; i++) {
            isValidateNode = true; // 默认所有节点都存在            
            validateNode = DocumentHelper.parseText(content);

            el = validateNode.getRootElement();
            qname = el.getQName().getName();

            if (flag == true) {
                qelnamebf.setLength(0);
                qelnamebf.append(qname);
                selname = qname + "/" + str[0];

                for (int j = 0, iCount = str.length; j < iCount; j++) {
                    qelnamebf.append("/").append(str[j]);
                }
                nodeList = validateNode.selectNodes(selname);
                for (Iterator iter = nodeList.iterator(); iter.hasNext();) {
                    tempEle = (Element) iter.next();
                    tempDoc = tempEle.getDocument();
                    if (tempDoc.selectSingleNode(qelnamebf.toString()) == null) {

                        isValidateNode = false; // 缺少指定节点时被置为false
                        data.getProperties()
                            .put(
                                DataMapDict.SERVER_FORWARD_CONTENT,
                                "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?><err><errmsg>请求报文格式错误,重新检查再试!</errmsg></err>");
                        LoggerUtil.info(logger, "校验字段:[" + str[i] + "]失败!");
                        throw new AnymockException(TransactionErrorCode.ASSERT_FAILED);
                    } else {
                        LoggerUtil.info(logger, "校验字段:[", str[i], "]", "成功!");
                    }
                }
            } else {

                if (str[i].indexOf("/") != -1) {
                    parseXml("/", content, str[i], true, data);
                } else {
                    if (validateNode.selectSingleNode(qname + "/" + str[i]) == null) {

                        isValidateNode = false; // 缺少指定节点时被置为false
                        data.getProperties()
                            .put(
                                DataMapDict.SERVER_FORWARD_CONTENT,
                                "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?><err><errmsg>请求报文格式错误,重新检查再试!</errmsg></err>");
                        LoggerUtil.info(logger, "校验字段:[" + str[i] + "]失败!");
                        throw new AnymockException(TransactionErrorCode.ASSERT_FAILED);
                    } else {
                        LoggerUtil.info(logger, "校验字段:[", str[i], "]", "成功!");
                    }
                }
            }
        }
    }

    private void parseSerial(String expected, String message, TransferData data)
                                                                                throws AnymockException {
        String str[] = expected.split("\\;");
        for (int i = 0; i < str.length; i++) {
            if (message.indexOf(str[i]) != -1) {
                LoggerUtil.info(logger, "校验字段:[", str[i], "]", "成功!");
            } else {
                data.getProperties()
                    .put(
                        DataMapDict.SERVER_FORWARD_CONTENT,
                        "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?><err><errmsg>请求报文格式错误,重新检查再试!</errmsg></err>");
                LoggerUtil.info(logger, "校验字段:[" + str[i] + "]失败!");
                throw new AnymockException(TransactionErrorCode.ASSERT_FAILED);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void parseKeyVal(String expected, Map<String, String> pars, TransferData data) {
        Map<String, String> par1 = new HashMap<String, String>();
        String str[] = expected.split("\\;");
        for (int i = 0; i < str.length; i++) {
            par1.put(str[i], "1");
        }

        Iterator iter_m1 = pars.keySet().iterator();
        Iterator iter_m2 = par1.keySet().iterator();

        while (iter_m1.hasNext()) {
            String m1Str = (String) iter_m1.next();
            while (iter_m2.hasNext()) {
                String m2Str = (String) iter_m2.next();
                if (m1Str.equals(m2Str)) {
                    LoggerUtil.info(logger, "校验字段:[", m2Str, "]", "成功!");
                    break;
                } else {
                    data.getProperties()
                        .put(
                            DataMapDict.SERVER_FORWARD_CONTENT,
                            "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"no\"?><err><errmsg>请求报文格式错误,重新检查再试!</errmsg></err>");
                    LoggerUtil.info(logger, "校验字段:[" + m2Str + "]失败!");
                    throw new AnymockException(TransactionErrorCode.ASSERT_FAILED);
                }
            }
        }
    }

    private void parseXmlKeyValue(String expected, Map<String, String> paras, TransferData data)
                                                                                                throws AnymockException,
                                                                                                DocumentException {
        String str[] = expected.split("\\|");
        Object temp = paras.get(str[0].replace("[", "").replace("]", ""));
        String msg = ((String) temp);
        int idxVeticalLine = StringUtil.indexOf(expected, '|');
        // 去除固定部分后的协议码规则
        String tempCodeRule = StringUtil.substring(expected, idxVeticalLine + 1);
        parseXml(";", msg, tempCodeRule, false, data);
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String expected = (String) localTransferData.getObject("AssertAdaptor" + CD_EXPECTED);
        String msgType = (String) localTransferData.getObject("AssertAdaptor" + CD_REQ_TYPE);
        String content = (String) data.getProperties().get(DataMapDict.MSGBODY);
        try {
            switch (Enum.valueOf(AssertAEnum.class, msgType)) {
                case XML:
                    parseXml(";", content, expected, false, data);
                    break;
                case FIXED:
                    parseSerial(expected, content, data);
                    break;
                case KEYVALUE:
                    @SuppressWarnings("unchecked")
                    Map<String, String> pars = (Map<String, String>) data.getProperties().get(
                        DataMapDict.MSGBODY);
                    parseKeyVal(expected, pars, localTransferData);
                    break;
                case XMLMAX:
                    @SuppressWarnings("unchecked")
                    Map<String, String> par = (Map<String, String>) data.getProperties().get(
                        DataMapDict.MSGBODY);
                    parseXmlKeyValue(expected, par, data);
                    break;

                default:
                    LoggerUtil.warn(logger, "msgType类型错误，msgType=", msgType);
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
        } catch (DocumentException e) {
            ExceptionUtil.caught(e, "parseXml解析异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "assert失败");
            throw new AnymockException(TransactionErrorCode.ASSERT_FAILED);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {

    }
}