/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.mock.common.util.lang.StringUtil;

/**
 * dom解析工具类，使用dom4j xpath做解析,special for velocity
 * 
 * goNode用法:
 * 
 * <pre> 
 *    goNode('<?xml version=\"1.0\" encoding = \"GBK\"?><tree><node1>brucexx1</node1><node2>brucexx2</node2></tree>','/tree/node1')<br>
 *    结果为brucexx1
 * </pre>
 * 
 * goMap用法:
 * 
 * <pre>
 *    goNode('<?xml version=\"1.0\" encoding = \"GBK\"?><tree><node1>brucexx1</node1><node2>brucexx2</node2></tree>','/tree/*')<br>
 *    结果为{node1=brucexx1, node2=brucexx2}
 * </pre>
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: DomUtil.java, v 0.1 2011-11-21 上午10:32:01 zhao.xiong Exp $
 */
public class DomUtil {

    /**
     * 获取单结点值
     * 
     * @param xml
     * @param xpath
     * @return
     * @throws DocumentException
     */
    public static String goNode(String xml, String xpath) throws DocumentException {
        if (StringUtil.isBlank(xml)) {
            return StringUtil.EMPTY_STRING;
        }

        StringReader stringReader = null;
        try {
            stringReader = new StringReader(xml);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(stringReader);
            Node node = doc.selectSingleNode(xpath);
            return node.getText();
        } finally {
            IOUtils.closeQuietly(stringReader);
        }
    }

    /**
     * 把当前结点下所有结果返回为map
     * 
     * @param xml
     * @param xpath
     * @return
     * @throws DocumentException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> goMap(String xml, String xpath) throws DocumentException {
        if (StringUtil.isBlank(xml)) {
            return new HashMap<String, String>();
        }

        StringReader stringReader = null;
        try {
            stringReader = new StringReader(xml);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(stringReader);
            List<Element> eleList = doc.selectNodes(xpath);
            Map<String, String> map = new HashMap<String, String>();
            for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
                Element ele = iter.next();
                map.put(ele.getName(), ele.getTextTrim());
            }
            return map;
        } finally {
            IOUtils.closeQuietly(stringReader);
        }
    }

}
