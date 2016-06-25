
package com.mock.core.service.transaction.component.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.extension.PostConstants;
import com.mock.core.service.transaction.component.util.SecurityTool;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * DES加密
 * @author jun.qi
 * @version $Id: DESMacAdaptor.java, v 0.1 2012-7-4 下午01:35:03 jun.qi Exp $
 */
public final class DESMacAdaptor extends ComponetHandler {

    /**
     * 转换XML
     * @param content
     * @return
     * @throws IOException 
     */
    private String getXML(Map<String, String> content) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("package");
        root.addAttribute("version", "1.0");
        Element field = null;
        for (Entry<String, String> entry : content.entrySet()) {
            field = root.addElement("field");
            field.addElement("name").setText(entry.getKey());
            field.addElement("value").setText(entry.getValue() != null ? entry.getValue() : "");
            field = null;
        }
        return toString(document, "GB2312");
    }

    /**
     * 转换XMLdocument
     * @param document
     * @param encoding
     * @return
     * @throws IOException 
     */
    private String toString(Document document, String encoding) throws IOException {
        String xml = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputFormat format = new OutputFormat();

        format.setEncoding(encoding);

        XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(out, encoding), format);

        xmlWriter.write(document);
        xmlWriter.close();
        xml = out.toString(encoding);
        return xml;
    }

    /**
     * 加密
     * @param src
     * @param key
     * @return
     * @throws DecoderException 
     */
    private String mac(String src, String key) throws DecoderException {
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
        String in = null;
        for (int i = 0, iCount = hex.length(); i < iCount; i += 16) {
            in = hex.substring(i, i + 16);
            if (i > 0) {
                in = xor(out, in);
            }
            out = SecurityTool.encryptDES(in, key);
        }

        if (out == null) {
            LoggerUtil.info(logger, "DES mac resulted null. ");
        }

        return out.substring(0, 8);
    }

    private String xor(String left, String right) throws DecoderException {
        byte[] l = null;
        byte[] r = null;
        l = Hex.decodeHex(left.toCharArray());
        r = Hex.decodeHex(right.toCharArray());
        byte[] arr = new byte[l.length];

        for (int i = 0; i < l.length; i++) {
            arr[i] = (byte) (l[i] ^ r[i]);
        }

        return new String(Hex.encodeHex(arr));
    }

    private String getMAC(String macSrc) throws DecoderException {
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
        String str = null;
        if (PostConstants.DEPOSIT_APPLY_RES == Integer.parseInt(transCode)) {
            for (int i = 0; i < PostConstants.DEPOSIT_APPLY_RES_MAC.length; i++) {
                str = map.get(PostConstants.DEPOSIT_APPLY_RES_MAC[i]);
                if (StringUtil.isNotEmpty(str)) {
                    sb.append(str.trim() + " ");
                }
                str = null;
            }
        } else if (PostConstants.BILL_STATUS_QUERY_RES == Integer.parseInt(transCode)) {
            for (int i = 0; i < PostConstants.DEPOSIT_QUERY_RES_MAC.length; i++) {
                str = map.get(PostConstants.DEPOSIT_QUERY_RES_MAC[i]);
                if (StringUtil.isNotEmpty(str)) {
                    sb.append(str.trim() + " ");
                }
                str = null;
            }
        } else {
            LoggerUtil.info(logger, "未知的TRANSCODE：", transCode);
        }

        return sb.toString().trim();
    }

    public String getResultString(Map<String, String> map) throws IOException, DecoderException {
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

    public Map<String, String> ParseXML(String xml) throws DocumentException {
        Map<String, String> map = new HashMap<String, String>();

        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> list = root.selectNodes("field");
        Element field = null;
        Element content = null;
        String name = null;
        String value = null;
        for (int i = 0; i < list.size(); i++) {
            field = list.get(i);
            @SuppressWarnings("rawtypes")
            Iterator it = field.elementIterator();

            name = StringUtil.EMPTY_STRING;
            value = StringUtil.EMPTY_STRING;
            while (it.hasNext()) {
                content = (Element) it.next();
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

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        Map<String, String> map;
        try {
            map = ParseXML(content);
            String ruleValue = getResultString(map);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, ruleValue);
            LoggerUtil.info(logger, "XML返回值: ", ruleValue);
        } catch (DocumentException e) {
            ExceptionUtil.caught(e, "ParseXML异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "getResultString异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        } catch (DecoderException e) {
            ExceptionUtil.caught(e, "getResultString异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }

}