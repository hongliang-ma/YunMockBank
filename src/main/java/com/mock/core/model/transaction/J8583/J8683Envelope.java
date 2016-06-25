
package com.mock.core.model.transaction.J8583;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author hongliang.ma
 * @version $Id: J8683Envelope.java, v 0.1 2012-8-27 下午2:09:56 hongliang.ma Exp $
 */
public final class J8683Envelope {
    private final static XStream xstream = new XStream(new DomDriver());

    private static void configBefore() {
        xstream.registerConverter(new J8583Converter());
        xstream.alias("j8583cn-config", J8583Model.class);
        xstream.alias("template", J8583template.class);
        xstream.alias("header", J8583Header.class);
        xstream.alias("field", J8583Field.class);
        xstream.alias("parseinfo", J8583Parseinfo.class);

        //  head部分
        xstream.useAttributeFor(J8583Header.class, "length");
        xstream.useAttributeFor(J8583Header.class, "headerValue");

        //Filed部分
        xstream.useAttributeFor(J8583Field.class, "id");
        xstream.useAttributeFor(J8583Field.class, "datatype");
        xstream.useAttributeFor(J8583Field.class, "crule");
        xstream.useAttributeFor(J8583Field.class, "length");
        xstream.useAttributeFor(J8583Field.class, "name");
        xstream.useAttributeFor(J8583Field.class, "fieldValue");

        //template部分
        xstream.useAttributeFor(J8583template.class, "msgtypeid");
        xstream.addImplicitCollection(J8583template.class, "templateField");

        //J8583Parseinfo部分
        xstream.useAttributeFor(J8583Parseinfo.class, "msgtypeid");
        xstream.addImplicitCollection(J8583Parseinfo.class, "templateField");

        //整体的8583
        xstream.addImplicitCollection(J8583Model.class, "arrHeaders");
        xstream.addImplicitCollection(J8583Model.class, "allTemplateMap");
        xstream.addImplicitCollection(J8583Model.class, "parseinfoList");
    }

    /**
     * 将一个J8583Model设置成XML
     * 
     * @param detailMsg
     * @return XML保存到TemplateDetail的detailMsg
     */
    public static String formatJ8583Model(J8583Model j8583Model) {
        configBefore();
        String before = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";
        String strXml = xstream.toXML(j8583Model);

        return before + strXml;
    }

    /**
     * 从一个文本转成J8583Model
     * 
     * @param strXml
     * @return DetailMsg
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public static J8583Model getJ8583Model(String strXml) throws ParserConfigurationException,
                                                         SAXException, IOException,
                                                         XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        InputStream in = new ByteArrayInputStream(strXml.getBytes("GBK"));
        Document doc = builder.parse(in);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        //开始获取Head部分
        XPathExpression headerExpr = xpath.compile("/j8583cn-config/header");
        NodeList nodes = (NodeList) headerExpr.evaluate(doc, XPathConstants.NODESET);
        ArrayList<J8583Header> arrHeaders = new ArrayList<J8583Header>();
        NamedNodeMap attrMap;
        J8583Header j8583Header = null;
        for (int i = 0, iLength = nodes.getLength(); i < iLength; i++) {
            j8583Header = new J8583Header();
            attrMap = nodes.item(i).getAttributes();

            j8583Header.setLength(attrMap.getNamedItem("length").getNodeValue());
            j8583Header.setHeaderValue(nodes.item(i).getTextContent());
            arrHeaders.add(j8583Header);
        }

        //开始获取template部分
        XPathExpression templateExpr = xpath.compile("/j8583cn-config/template");
        NodeList templateNodes = (NodeList) templateExpr.evaluate(doc, XPathConstants.NODESET);

        NodeList nodeList = null;
        ArrayList<J8583template> allTemplateList = new ArrayList<J8583template>();
        J8583template j8583template = null;
        ArrayList<J8583Field> j8583FieldList = null;
        J8583Field j8583Field = null;
        NamedNodeMap subAttrMap = null;
        for (int i = 0, tLength = templateNodes.getLength(); i < tLength; i++) {
            j8583FieldList = new ArrayList<J8583Field>();
            j8583template = new J8583template();
            attrMap = templateNodes.item(i).getAttributes();
            j8583template.setMsgtypeid(attrMap.getNamedItem("msgtypeid").getNodeValue());

            nodeList = templateNodes.item(i).getChildNodes();
            for (int j = 0, nodelenght = nodeList.getLength(); j < nodelenght; j++) {
                j8583Field = new J8583Field();
                subAttrMap = nodeList.item(j).getAttributes();
                if (null == subAttrMap) {
                    continue;
                }
                j8583Field.setId(subAttrMap.getNamedItem("id").getNodeValue());
                j8583Field.setDatatype(subAttrMap.getNamedItem("datatype").getNodeValue());
                if (null != subAttrMap.getNamedItem("length")) {
                    j8583Field.setLength(subAttrMap.getNamedItem("length").getNodeValue());
                }
                if (null != subAttrMap.getNamedItem("crule")) {
                    j8583Field.setCrule(subAttrMap.getNamedItem("crule").getNodeValue());
                }
                j8583Field.setName(subAttrMap.getNamedItem("name").getNodeValue());
                j8583Field.setFieldValue(nodeList.item(j).getTextContent());
                j8583FieldList.add(j8583Field);
            }
            j8583template.setTemplateField(j8583FieldList);

            allTemplateList.add(j8583template);
        }

        //开始获取J8583Parseinfo部分
        XPathExpression parseinfoExpr = xpath.compile("/j8583cn-config/parseinfo");
        NodeList parseinfoNodes = (NodeList) parseinfoExpr.evaluate(doc, XPathConstants.NODESET);

        ArrayList<J8583Parseinfo> allParseinfoList = new ArrayList<J8583Parseinfo>();
        J8583Parseinfo j8583Parseinfo = null;

        for (int i = 0; i < parseinfoNodes.getLength(); i++) {
            j8583FieldList = new ArrayList<J8583Field>();
            j8583Parseinfo = new J8583Parseinfo();
            attrMap = parseinfoNodes.item(i).getAttributes();
            j8583Parseinfo.setMsgtypeid(attrMap.getNamedItem("msgtypeid").getNodeValue());

            nodeList = parseinfoNodes.item(i).getChildNodes();
            for (int j = 0, jLength = nodeList.getLength(); j < jLength; j++) {
                j8583Field = new J8583Field();
                subAttrMap = nodeList.item(j).getAttributes();
                if (null == subAttrMap) {
                    continue;
                }
                j8583Field.setId(subAttrMap.getNamedItem("id").getNodeValue());
                j8583Field.setDatatype(subAttrMap.getNamedItem("datatype").getNodeValue());
                if (null != subAttrMap.getNamedItem("length")) {
                    j8583Field.setLength(subAttrMap.getNamedItem("length").getNodeValue());
                }
                if (null != subAttrMap.getNamedItem("crule")) {
                    j8583Field.setCrule(subAttrMap.getNamedItem("crule").getNodeValue());
                }
                if (null != subAttrMap.getNamedItem("name")) {
                    j8583Field.setName(subAttrMap.getNamedItem("name").getNodeValue());
                }
                j8583Field.setFieldValue(nodeList.item(j).getTextContent());
                j8583FieldList.add(j8583Field);
            }
            j8583Parseinfo.setTemplateField(j8583FieldList);

            allParseinfoList.add(j8583Parseinfo);
        }

        J8583Model myJ8583Model = new J8583Model();
        myJ8583Model.setArrHeaders(arrHeaders);
        myJ8583Model.setAllTemplateMap(allTemplateList);
        myJ8583Model.setParseinfoList(allParseinfoList);

        return myJ8583Model;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
