/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */

package com.mock.core.service.transaction.component.message;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.mock.core.model.shared.enums.CodeRuleEnum;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.MessageFactory;
import com.mock.core.service.transaction.component.message.parser.vo.FieldParseInfo;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.MyWrapper;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.ByteUtil;
import com.mock.common.util.lang.StringUtil;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;

/**
 * 协议码解析组件
 * @author jun.qi
 */
public final class CodeRule extends ComponetHandler {

    /**
     * 获得XML类型的协议码
     * @param message 整个消息
     * @param 第一个协议码，第二个协议码
     * @return
     */
    public String[] getXMLCodeRule(final String[] listCodeRule, final String message,
                                   TransferData data) {

        try {
            Document doc = null;
            if (data.getProperties().get(DataMapDict.XMLBEFOREHAND) == null) {
                doc = DecodeXmlContent.buildDocByString(message);
                if (null == doc) {
                    return null;
                }
                data.getProperties().put(DataMapDict.XMLBEFOREHAND, doc);
            } else {
                doc = (Document) data.getProperties().get(DataMapDict.XMLBEFOREHAND);
            }
            final PrefixResolver resolver = new PrefixResolverDefault(doc.getDocumentElement());
            NamespaceContext ctx = new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    return resolver.getNamespaceForPrefix(prefix);
                }

                // Dummy implementation - not used!
                @SuppressWarnings("rawtypes")
                public Iterator getPrefixes(String val) {
                    return null;
                }

                // Dummy implemenation - not used!
                public String getPrefix(String uri) {
                    return null;
                }
            };
            /** XPath解析器 */
            XPathFactory xpathFact = XPathFactory.newInstance();
            XPath xpath = xpathFact.newXPath();
            xpath.setNamespaceContext(ctx);

            // 使用XPath解析
            String[] listReturn = new String[3];
            int iCount = 0;
            for (String codeRule : listCodeRule) {
                if (StringUtil.isNotEmpty(codeRule)) {
                    listReturn[iCount] = DecodeXmlContent.getOneXmlCodeRule(codeRule, xpath, doc);
                } else {
                    listReturn[iCount] = null;
                }
                iCount++;
            }

            return listReturn;
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "getXMLCodeRule异常 ");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "getXMLCodeRule异常 ");
        }
        return null;
    }

    /**
     * 获取到混合的XML的协议码
     * 
     * @param listCodeRule
     * @param message
     * @return
     */
    public String getMaxXmlCodeRule(final String codeRule, final Map<String, String> paras,
                                    TransferData data) {
        String[] xmlcodeRule = new String[1];
        String strSplite[] = StringUtil.split(codeRule, "|");
        String msg = paras.get(strSplite[0].replace("[", "").replace("]", ""));
        int idxVeticalLine = StringUtil.indexOf(codeRule, '|');
        // 去除固定部分后的协议码规则
        xmlcodeRule[0] = StringUtil.substring(codeRule, idxVeticalLine + 1);

        return getXMLCodeRule(xmlcodeRule, msg, data)[0];
    }

    /**
     * 获得定长类型的协议码
     * @param codeRule 协议码规则
     * @param message 整个消息
     */
    private String[] getFixCodeRule(final String[] listCodeRule, final String message) {
        String[] listReturn = new String[3];
        try {
            int iCount = 0;
            int idx = 0;
            int start = 0;
            int end = 0;
            byte[] bts = null;
            for (String codeRule : listCodeRule) {
                if (StringUtil.isNotEmpty(codeRule)) {
                    idx = codeRule.indexOf("-");
                    start = Integer.parseInt(codeRule.substring(0, idx));
                    end = Integer.parseInt(codeRule.substring(idx + 1));
                    bts = message.getBytes("GBK");
                    listReturn[iCount] = new String(bts, start, end - start + 1);
                } else {
                    listReturn[iCount] = null;
                }
                iCount++;
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "getFixCodeRule异常 ");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

        return listReturn;
    }

    /**
     * 8583解析报文 parameter:parserBuf | msgheaderlength
     */
    public Message parser(byte[] parserBuf, MessageFactory fc) throws Exception {
        Message m = fc.parseMessage(parserBuf);

        // print(message);
        return m;
    }

    /**
     * 解析8583唯一值
     * @param fc
     * @param mes
     * @return
     * @throws Exception
     */
    public Integer getUnique(MessageFactory fc, Message mes) throws Exception {
        Map<Integer, FieldParseInfo> map = fc.getParseMap(mes.getMsgTypeId());
        FieldParseInfo fpi = null;
        for (Integer i : map.keySet()) {
            fpi = map.get(i);
            if (fpi.isUnique()) {
                return i;
            }
            fpi = null;
        }
        return null;
    }

    @Override
    public void process(TransferData data, TransferData localTransferData) throws AnymockException {
        //获取协议码和消息类型
        String[] codeRule = new String[3];
        String msgType = (String) localTransferData.getObject("CodeRule" + DataMapDict.STATE);
        codeRule[0] = (String) localTransferData.getObject("CodeRule" + DataMapDict.CODERULE);
        codeRule[1] = (String) localTransferData.getObject("CodeRulecodeRuleS");
        codeRule[2] = (String) localTransferData.getObject("CodeRulecodeRuleT");
        Object message = data.getProperties().get(DataMapDict.MSGBODY);

        String[] xmlcodeRule = new String[3];

        Boolean j8583flag = false;
        byte[] rs = null;
        try {
            msgType = StringUtil.toUpperCase(msgType);
            switch (Enum.valueOf(CodeRuleEnum.class, msgType)) {
                case XML: {
                    String flag = (String) localTransferData.getObject("CodeRule"
                                                                       + DataMapDict.CALLFLAG);
                    if (StringUtil.equals(flag, "true")) {
                        System.arraycopy(codeRule, 0, xmlcodeRule, 0, 3);
                    } else {
                        xmlcodeRule = getXMLCodeRule(codeRule, (String) message, data);
                    }
                }
                    break;
                case FIXED: {
                    xmlcodeRule = getFixCodeRule(codeRule, (String) message);
                }
                    break;
                case KEYVALUE: {
                    String splite = (String) localTransferData.getObject("CodeRulesplite");
                    if (StringUtil.isEmpty(splite) || StringUtil.equalsIgnoreCase(splite, "null")) {
                        splite = "&";
                    }
                    Map<String, String> paras = AtsframeStrUtil.stringTomap((String) message,
                        splite);
                    Object temp = paras.get(codeRule[0].replace("[", "").replace("]", ""));
                    xmlcodeRule[0] = ((String) temp);
                    if (StringUtil.isNotEmpty(codeRule[1])) {
                        Object tempS = paras.get(codeRule[1].replace("[", "").replace("]", ""));
                        xmlcodeRule[1] = ((String) tempS);
                    }
                    if (StringUtil.isNotEmpty(codeRule[2])) {
                        Object tempS = paras.get(codeRule[2].replace("[", "").replace("]", ""));
                        xmlcodeRule[2] = ((String) tempS);
                    }
                }
                    break;
                //键值对XML混合
                case XMLMAX: {
                    String splite = (String) localTransferData.getObject("CodeRulesplite");
                    if (StringUtil.isEmpty(splite) || StringUtil.equalsIgnoreCase(splite, "null")) {
                        splite = "&";
                    }
                    Map<String, String> paras = AtsframeStrUtil.stringTomap((String) message,
                        splite);
                    xmlcodeRule[0] = getMaxXmlCodeRule(codeRule[0], paras, data);
                    if (StringUtil.isNotEmpty(codeRule[1])) {
                        xmlcodeRule[1] = getMaxXmlCodeRule(codeRule[1], paras, data);
                    }
                    if (StringUtil.isNotEmpty(codeRule[2])) {
                        xmlcodeRule[2] = getMaxXmlCodeRule(codeRule[2], paras, data);
                    }
                }
                    break;
                //正则表达式
                case REGULAR: {
                    Pattern p = Pattern.compile("\\<" + message + "\\>");
                    Matcher m = p.matcher(codeRule[0]);
                    xmlcodeRule[0] = codeRule[0].substring(m.regionStart(), m.regionEnd());
                    if (StringUtil.isNotEmpty(codeRule[1])) {
                        m = p.matcher(codeRule[1]);
                        xmlcodeRule[1] = codeRule[1].substring(m.regionStart(), m.regionEnd());
                    }
                    if (StringUtil.isNotEmpty(codeRule[2])) {
                        m = p.matcher(codeRule[2]);
                        xmlcodeRule[2] = codeRule[2].substring(m.regionStart(), m.regionEnd());
                    }
                }
                    break;
                case IS8583: {
                    String j8583xml = (String) localTransferData.getObject("CodeRule" + "j8583");
                    String isbinary = (String) localTransferData.getObject("CodeRule" + "isbinary");
                    String tpdu = (String) localTransferData.getObject("CodeRule" + "tpdu");
                    String typelength = (String) localTransferData.getObject("CodeRule"
                                                                             + "typelength");
                    String mtitype = (String) localTransferData.getObject("CodeRule" + "mtitype");
                    String strbitset = (String) localTransferData.getObject("CodeRule"
                                                                            + "strbitset");

                    Pattern p = Pattern.compile("\\<[ ]\\>");
                    Matcher m = p.matcher(tpdu);
                    tpdu = tpdu.substring(m.regionStart() + 1, m.regionEnd() - 1);
                    rs = (byte[]) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
                    if (StringUtil.isEmpty(ByteUtil.formatByte(rs))) {
                        rs = (byte[]) message;
                    }
                    MessageFactory fc = new MessageFactory(j8583xml, isbinary, tpdu,
                        Integer.parseInt(typelength), mtitype, strbitset);
                    Message mes = parser(rs, fc);
                    Integer key = getUnique(fc, mes);
                    int i = 0;
                    for (MyWrapper dat : mes.getUniqueData(key)) {
                        if (StringUtil.isNotBlank(dat.getValue())) {
                            xmlcodeRule[i] = dat.getValue();
                            j8583flag = true;
                        }
                        i++;
                    }
                }

                    break;
                default: {
                    LoggerUtil.warn(logger, "枚举值错误，msgType=", msgType);
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
                }
            }
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "工具内部处理异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_UNKWON_ERROR);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "工具内部处理异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_UNKWON_ERROR);
        }
        LoggerUtil.info(logger, "解析后的协议码RuleCode为[", xmlcodeRule[0], "]", "RuleCodeS为[",
            xmlcodeRule[1], "RuleCodeT为[", xmlcodeRule[2], "]");

        String startSys = (String) localTransferData.getObject(DataMapDict.SELETCTTAG);
        if (StringUtil.equals(startSys, "false")) {
            data.getProperties().put(DataMapDict.CODERULE, xmlcodeRule[0]);
            if (StringUtil.isNotEmpty(xmlcodeRule[1])) {
                data.getProperties().put("codeRuleS", xmlcodeRule[1]);
            }
            if (StringUtil.isNotEmpty(xmlcodeRule[2])) {
                data.getProperties().put("codeRuleT", xmlcodeRule[2]);
            }

        } else {
            data.getProperties().put(DataMapDict.SYSRULE, StringUtil.join(xmlcodeRule, "||"));
        }
        if (j8583flag) {
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, rs);
        } else {
            String mesg = (String) data.getProperties().get(DataMapDict.MSGBODY);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, mesg);
        }
    }

    /** 
     * @see com.mock.transaction.component.ComponetHandler#processInner(com.mock.model.detail.TransferData)
    */
    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}