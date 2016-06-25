package com.mock.core.service.transaction.component.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.extension.PostConstants;
import com.mock.common.util.LoggerUtil;

/**
 * XML处理工具类
 * @author jun.qi
 * @version $Id: PostXMLUtil.java, v 0.1 2012-7-3 下午01:00:21 jun.qi Exp $
 */
public class PostXMLUtil {

    /** logger */
    protected static final Logger logger = LoggerFactory.getLogger(ComponetHandler.class);

    public static String getXML(Map<String, String> content) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("package");
        root.addAttribute("version", "1.0");
        for (Entry<String, String> entry : content.entrySet()) {
            Element field = root.addElement("field");
            field.addElement("name").setText(entry.getKey());
            field.addElement("value").setText(entry.getValue() != null ? entry.getValue() : "");
        }
        return toString(document, "GB2312");
    }

    private static String toString(Document document, String encoding) {
        String xml = null;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputFormat format = new OutputFormat();

            format.setEncoding(encoding);

            XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(out, encoding), format);

            xmlWriter.write(document);
            xmlWriter.close();
            xml = out.toString(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    private String mac(String src, String key) {
        String hex = new String(Hex.encodeHex(src.getBytes()));
        int padding = hex.length() % 16;

        if (padding > 0) {
            padding = 16 - padding;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < padding; i++) {
            sb.append("0");
        }

        hex += sb.toString();
        String out = null;

        for (int i = 0; i < hex.length(); i += 16) {
            String in = hex.substring(i, i + 16);
            if (i > 0) {
                in = xor(out, in);
            }
            try {
                out = SecurityTool.encryptDES(in, key);
            } catch (Exception e) {
                LoggerUtil.info(logger, "DES mac encrypt failed. ");
            }
        }

        if (out == null) {
            LoggerUtil.info(logger, "DES mac resulted null. ");
        }

        return out.substring(0, 8);
    }

    private String xor(String left, String right) {
        byte[] l = null;
        byte[] r = null;
        try {
            l = Hex.decodeHex(left.toCharArray());
            r = Hex.decodeHex(right.toCharArray());
        } catch (Exception e) {
            LoggerUtil.info(logger, "Decoder.");
        }

        byte[] arr = new byte[l.length];

        for (int i = 0; i < l.length; i++) {
            arr[i] = (byte) (l[i] ^ r[i]);
        }

        return new String(Hex.encodeHex(arr));
    }

    private String getMAC(String macSrc) {
        LoggerUtil.info(logger, "The string to mac is: ", macSrc);
        String mak = null;
        mak = "6EAB200E0EEC8968A3B6F1F9AAF14E0C";
        LoggerUtil.info(logger, "MAK: ", mak);

        String macResult = mac(macSrc, mak);

        LoggerUtil.info(logger, "Mac result is: ", macResult);
        return macResult;
    }

    private String getMacString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        String transCode = map.get("TransCode");

        if (PostConstants.DEPOSIT_APPLY_RES == Integer.parseInt(transCode)) {
            for (int i = 0; i < PostConstants.DEPOSIT_APPLY_RES_MAC.length; i++) {
                String str = map.get(PostConstants.DEPOSIT_APPLY_RES_MAC[i]);
                if (StringUtil.isNotEmpty(str)) {
                    sb.append(str.trim() + " ");
                }
            }
        } else if (PostConstants.BILL_STATUS_QUERY_RES == Integer.parseInt(transCode)) {
            for (int i = 0; i < PostConstants.DEPOSIT_QUERY_RES_MAC.length; i++) {
                String str = map.get(PostConstants.DEPOSIT_QUERY_RES_MAC[i]);
                if (StringUtil.isNotEmpty(str)) {
                    sb.append(str.trim() + " ");
                }
            }
        } else {
            LoggerUtil.info(logger, "未知的TRANSCODE：", transCode);
        }

        return sb.toString().trim();
    }

    public String getResultString(Map<String, String> map) {
        String xml = getXML(map);
        if (xml != null) {
            String str2mac = getMacString(map);
            String macstr = getMAC(str2mac);
            String lenSrc = xml + macstr;
            LoggerUtil.info(logger, lenSrc.length());

            String len = lenSrc.length() + "";
            if (len.length() != 4) {
                String tmp = "0000" + len;
                len = tmp.substring(tmp.length() - 4);
            }
            String rt = len + lenSrc;
            return rt;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> ParseXML(String xml) throws DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        List<Element> list = root.selectNodes("field");
        Element field = null;
        String name = "";
        String value = "";
        Element content = null;
        for (int i = 0, length = list.size(); i < length; i++) {
            field = list.get(i);
            while (field.elementIterator().hasNext()) {
                content = (Element) field.elementIterator().next();
                if (content.getName().equals("name")) {
                    name = content.getText();
                }
                if (content.getName().equals("value")) {
                    value = content.getText();
                }
            }
            map.put(name, value);
        }
        return map;
    }

    public static String replaceValue(Map<String, String> req, Map<String, String> res) {
        String val = null;
        String value = null;
        for (String key : res.keySet()) {
            if (res.get(key).indexOf("Get") != -1) {
                for (String key1 : req.keySet()) {
                    if (key1.equals(key)) {
                        val = req.get(key1);
                    }
                }
                res.put(key, val);
            }
        }
        value = getXML(res);
        return value;
    }
}
