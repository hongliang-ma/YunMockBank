package com.mock.core.service.transaction.component.message.parser.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.mock.core.service.transaction.component.message.parser.vo.FieldParseInfo;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.Type;

/**
 * 这是一个中国版的8583格式标准的类，初始代码来源于类ConfigParser
 * This class is used to parse a XML configuration file 
 * and configure a MessageFactory with the values from it.
 * 
 * @author zyplanke
 */
public class ConfigParser {

    /**
     * <pre>
     * Creates a message factory from the specified path( full path and filename).
     * </pre>
     *
     * @param filepath
     * @return
     * @throws IOException
     */
    public static MessageFactory createFromXMLConfigFile(MessageFactory mfact, String filepath)
                                                                                               throws IOException {
        InputStream ins = new FileInputStream(filepath);
        // mfact.setUseBinary(true);

        try {
            parse(mfact, ins);
        } finally {
            ins.close();
        }

        return mfact;
    }

    /**
     * <pre>
     * 从资源文件中加载配置
     * </pre>
     *
     * @param ins
     * @return
     * @throws IOException
     */
    public static MessageFactory createFromResource(MessageFactory mfact, InputStream ins)
                                                                                          throws IOException {
        //mfact.setUseBinary(true);
        if (ins != null) {
            try {
                parse(mfact, ins);
            } finally {
                ins.close();
            }
        } else {
            System.out.println("读取资源文件为空");
        }

        return mfact;
    }

    /**
     * <pre>
     * Creates a message factory from the file located at the specified URL.
     * </pre>
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static MessageFactory createFromUrl(MessageFactory mfact, URL url) throws IOException {
        InputStream stream = url.openStream();
        try {
            parse(mfact, stream);
        } finally {
            stream.close();
        }
        return mfact;
    }

    /**
     * <pre>
     * Reads the XML from the stream and configures the message factory with its
     * values.
     * </pre>
     *
     * @param mfact The message factory to be configured with the values read from
     *              the XML.
     * @param stream The InputStream containing the XML configuration.
     * @throws IOException
     */
    protected static void parse(MessageFactory mfact, InputStream stream) throws IOException {
        final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docb = null;
        Document doc = null;
        try {
            docb = docfact.newDocumentBuilder();
            doc = docb.parse(stream);
        } catch (ParserConfigurationException ex) {
            System.out.println("j8583配置文件解析出现异常");
            return;
        } catch (SAXException ex) {
            System.out.println("j8583配置文件不服务SAX规范");
            return;
        }

        final Element root = doc.getDocumentElement();

        // Read the 8583 message configure headers
        NodeList nodes = root.getElementsByTagName("header");
        Element elem = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            elem = (Element) nodes.item(i);
            int headerlen = Integer.parseInt(elem.getAttribute("length"));
            if (elem.getChildNodes() == null || elem.getChildNodes().getLength() == 0) {
                throw new IOException("Invalid header element");
            }
            String msgtypeid = elem.getChildNodes().item(0).getNodeValue();

            /*if (msgtypeid.length() != 4) {
                throw new IOException("Invalid msgtypeid for header: "
                                      + elem.getAttribute("msgtypeid"));
            }*/
            mfact.setHeaderLengthAttr(msgtypeid, headerlen);
        }

        // Read the message templates
        nodes = root.getElementsByTagName("template");
        for (int i = 0; i < nodes.getLength(); i++) {
            elem = (Element) nodes.item(i);
            String msgtypeid = elem.getAttribute("msgtypeid");
            /*if (msgtypeid.length() != 4) {
                throw new IOException("Invalid type for template: " + msgtypeid);
            }*/
            NodeList fields = elem.getElementsByTagName("field");
            Message m = new Message();
            m.setMsgTypeId(msgtypeid);
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element) fields.item(j);
                int fieldid = Integer.parseInt(f.getAttribute("id"));
                Type datatype = Type.valueOf(f.getAttribute("datatype"));
                int length = 0;
                if (f.getAttribute("length").length() > 0) {
                    length = Integer.parseInt(f.getAttribute("length"));
                }
                String name = "";
                if (f.getAttribute("name").length() > 0) {
                    name = f.getAttribute("name");
                }
                boolean pos = true;
                if (f.getAttribute("crule").length() > 0) {
                    String cRule = f.getAttribute("crule");
                    pos = cRule.equals("right") ? false : true;
                }

                String init_filed_data = f.getChildNodes().item(0).getNodeValue();
                m.setValue(fieldid, init_filed_data, datatype, length, name, pos);
            }
            mfact.addMessageTemplate(m);
        }

        // Read the parsing guides
        nodes = root.getElementsByTagName("parseinfo");
        for (int i = 0; i < nodes.getLength(); i++) {
            elem = (Element) nodes.item(i);
            String msgtypeid = elem.getAttribute("msgtypeid");

            NodeList fields = elem.getElementsByTagName("field");
            HashMap<Integer, FieldParseInfo> parseMap = new HashMap<Integer, FieldParseInfo>();
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element) fields.item(j);
                int fieldid = Integer.parseInt(f.getAttribute("id"));
                Type datatype = Type.valueOf(f.getAttribute("datatype"));
                int length = 0;
                if (f.getAttribute("length").length() > 0) {
                    length = Integer.parseInt(f.getAttribute("length"));
                }
                String name = f.getAttribute("name");
                String val = "";
                if (f.getChildNodes().item(0) != null) {
                    val = (String) f.getChildNodes().item(0).getNodeValue();
                }
                boolean pos = true;
                if (f.getAttribute("crule").length() > 0) {
                    String cRule = f.getAttribute("crule");
                    pos = cRule.equals("right") ? false : true;
                }
                boolean unq = false;
                if (f.getAttribute("unique").length() > 0) {
                    String unique = f.getAttribute("unique");
                    unq = Boolean.valueOf(unique);
                }
                parseMap.put(fieldid, new FieldParseInfo(fieldid, datatype, length, name, val, pos,
                    unq));
            }
            mfact.setParseMap(msgtypeid, parseMap);
        }
    }
}
