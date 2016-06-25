
package com.mock.core.service.transaction.component.util;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;

/**
 *XML报文解析内容替换
 * 
 * @author hongliang.ma
 * @version $Id: DecodeXmlContent.java, v 0.1 2012-12-7 下午5:08:18 hongliang.ma Exp $
 */
public final class DecodeXmlContent {

    /**
     * 将XML的报文的内容替换掉
     * 
     * @param doc  提取报文的Document
     * @param  paras键值对XML报文的键值对报文
     * @param strTemplate  返回报文
     * @return
     */
    public static String replaceXmlValue(Document doc, String strTemplate) {
        if (StringUtil.containsNone(strTemplate, "${/")
            || StringUtil.containsNone(strTemplate, "}")) {
            return strTemplate;
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

        /**解析 */
        String xexpr = null;
        String value = null;
        /**占位符 */
        String strStart = "${/";
        String strEnd = "}";
        try {
            StringBuilder sbfSplite = new StringBuilder(strTemplate);
            StringBuilder sbfReturnMsg = new StringBuilder();
            while (StringUtil.contains(sbfSplite.toString(), strStart)) {
                sbfReturnMsg.setLength(0);
                sbfReturnMsg.append(StringUtil.substringBefore(sbfSplite.toString(), strStart));
                xexpr = StringUtil.substringBetween(sbfSplite.toString(), strStart, strEnd);
                value = getOneXmlCodeRule(xexpr, xpath, doc);

                sbfReturnMsg.append(value);
                sbfReturnMsg
                    .append(StringUtil.substringAfter(sbfSplite.toString(), xexpr + strEnd));
                sbfSplite.setLength(0);
                sbfSplite.append(sbfReturnMsg);
            }
            return sbfSplite.toString();
        } catch (Exception e) {
            ExceptionUtil.caught(e, "XML值替换失败", xexpr, "XPathExpression");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    /**
     * 替换键值对XML的方法
     * 
     * @param paras  原始报文键值对
     * @param strTemplate 返回报文
     * @return  返回替换后的XML报文
     * @throws XPathExpressionException 
     */
    public static String replaceMaxXml(final Map<String, String> paras, final String strTemplate)
                                                                                                 throws XPathExpressionException {
        StringBuilder sbfSplite = new StringBuilder(strTemplate);
        StringBuilder sbfSpliteAfter = new StringBuilder();
        String strStart = "${[";
        String strEnd = "}";
        String codeRule = null;
        while (StringUtil.contains(sbfSplite.toString(), strStart)) {
            sbfSpliteAfter.setLength(0);
            sbfSpliteAfter.append(StringUtil.substringBefore(sbfSplite.toString(), strStart));
            codeRule = StringUtil.substringBetween(sbfSplite.toString(), strStart, strEnd);
            sbfSpliteAfter.append(getMaxXmlValue(codeRule, paras));

            sbfSpliteAfter
                .append(StringUtil.substringAfter(sbfSplite.toString(), codeRule + strEnd));
            sbfSplite.setLength(0);
            sbfSplite.append(sbfSpliteAfter);
        }

        return sbfSplite.toString();
    }

    /**
     *  根据协议码，获取XML指定xpath的内容
     *  
     * @param codeRule  协议码
     * @param xpath  xpath
     * @param doc  原始报文的Document
     * @return
     * @throws XPathExpressionException
     */
    public static String getOneXmlCodeRule(final String codeRule, XPath xpath, Document doc)
                                                                                            throws XPathExpressionException {
        try {
            if (null == xpath) {
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
                xpath = xpathFact.newXPath();
                xpath.setNamespaceContext(ctx);
            }

            Node node = (Node) xpath.evaluate(codeRule, doc, XPathConstants.NODE);
            if (null != node) {
                if (node.getNodeValue() != null) {
                    return node.getNodeValue();
                } else {
                    return node.getNodeName();
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "getXMLCodeRule异常 ");
            throw new AnymockException(TransactionErrorCode.COMPONENT_UNKWON_ERROR);
        }
    }

    /**
     * 获取到混合的XML的定义的值
     * 
     * @param listCodeRule
     * @param message
     * @return
     * @throws XPathExpressionException 
     */
    public static String getMaxXmlValue(final String codeRule, final Map<String, String> paras)
                                                                                               throws XPathExpressionException {
        if (StringUtil.containsNone(codeRule, "]")) {
            return null;
        }

        String msg = paras.get(StringUtil.substringBefore(codeRule, "]"));
        // 去除固定部分后的协议码规则
        String xmlcodeRule = StringUtil.substringAfter(codeRule, "|");

        return getOneXmlCodeRule(xmlcodeRule, null, buildDocByString(msg));
    }

    /**
     * 根据请求报文生成一个Document
     * 
     * @param requetTempalte  请求报文
     * @return
     */
    public static Document buildDocByString(final String requetTempalte) {
        if (StringUtil.isBlank(requetTempalte)) {
            return null;
        }

        // XML Path
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false);
        builderFactory.setNamespaceAware(true);
        /** XML文档构造器 */
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            ExceptionUtil.caught(e, " builderFactory.newDocumentBuilder失败");
            return null;
        }

        try {
            return builder.parse(new InputSource(new StringReader(requetTempalte)));
        } catch (Exception e) {
            ExceptionUtil.caught(e, "[Http同步调用]:XML值替换时，无法生成Document");
            return null;
        }
    }

}
