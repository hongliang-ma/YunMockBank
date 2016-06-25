/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util.lang;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

/**
 * 增强Alibaba StringUtil的填充字符串的功能，专门处理一些包含中文的字符串
 * 
 * @author hongliang.ma
 * @version $Id: AtsframeStrUtil.java, v 0.1 2011-12-19 下午2:44:20 hongliang.ma Exp $
 */
public class AtsframeStrUtil extends com.alibaba.common.lang.StringUtil {

    /**
     * 扩展并左对齐字符串，用指定字符串填充右边。
     * 新增对中文字符串的支持，注意方法名称为
     * 
     * <pre>
     * StringUtil.alignLeft(null, *, *)      = null
     * StringUtil.alignLeft("", 3, "z")      = "zzz"
     * StringUtil.alignLeft("bat", 3, "yz")  = "bat"
     * StringUtil.alignLeft("bat", 5, "yz")  = "batyz"
     * StringUtil.alignLeft("bat", 8, "yz")  = "batyzyzy"
     * StringUtil.alignLeft("bat", 1, "yz")  = "bat"
     * StringUtil.alignLeft("bat", -1, "yz") = "bat"
     * StringUtil.alignLeft("bat", 5, null)  = "bat  "
     * StringUtil.alignLeft("bat", 5, "")    = "bat  "
     * StringUtil.alignLeft("中文", 5, "")    = "中文 "
     * </pre>
     *
     * @param str 要对齐的字符串
     * @param size 扩展字符串到指定宽度
     * @param padStr 填充字符串
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignLefts(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        String padStringFinal = (isEmpty(padStr)) ? EMPTY_STRING : padStr;
        int padLen = padStringFinal.length();
        int strLen = str.getBytes().length;
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat(padStringFinal);
        } else if (pads < padLen) {
            return str.concat(padStringFinal.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStringFinal.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return str.concat(new String(padding));
        }
    }

    /**
     * 扩展并右对齐字符串，用指定字符串填充左边。
     * 新增对中文字符串的支持，注意方法名称为
     * 
     * <pre>
     * StringUtil.alignRight(null, *, *)      = null
     * StringUtil.alignRight("", 3, "z")      = "zzz"
     * StringUtil.alignRight("bat", 3, "yz")  = "bat"
     * StringUtil.alignRight("bat", 5, "yz")  = "yzbat"
     * StringUtil.alignRight("bat", 8, "yz")  = "yzyzybat"
     * StringUtil.alignRight("bat", 1, "yz")  = "bat"
     * StringUtil.alignRight("bat", -1, "yz") = "bat"
     * StringUtil.alignRight("bat", 5, null)  = "  bat"
     * StringUtil.alignRight("bat", 5, "")    = "  bat"
     * StringUtil.alignRight("中文", 5, "")    = " 中文"
     * </pre>
     *
     * @param str 要对齐的字符串
     * @param size 扩展字符串到指定宽度
     * @param padStr 填充字符串
     * @return 扩展后的字符串，如果字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String alignRights(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        String padStringFinal = (isEmpty(padStr)) ? EMPTY_STRING : padStr;
        int padLen = padStringFinal.length();
        int strLen = str.getBytes().length;
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return padStringFinal.concat(str);
        } else if (pads < padLen) {
            return padStringFinal.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStringFinal.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
    * 取指定字符串的子串，新增对中文字符串的支持
    * 注意方法名称为
    *
    * @param str 字符串
    * @param start 起始索引，如果为负数，表示从尾部计算
    * @param end 结束索引（不含），如果为负数，表示从尾部计算
    *
    * @return 子串，如果原始串为
    */
    public static String substrings(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        int length = end - start;
        byte[] dest = new byte[length];
        System.arraycopy(str.getBytes(), start, dest, 0, length);
        return new String(dest);
    }

    /**
     * 根据长度<code>length</code>把字符串切成两段，保存数组 
     * 确保中文不要被切成两半
     * 
     * @param message
     * @param length
     * @return
     */
    public static String[] cutString(String message, int length) {
        String normal = AtsframeStrUtil.substrings(message, 0, length);
        if (isContainChinese(message, length)) {
            normal = AtsframeStrUtil.substrings(message, 0, length - 1);
        }
        return new String[] { normal, AtsframeStrUtil.substringAfter(message, normal) };
    }

    /**
     * 字符串起始长度
     * 
     * @param message
     * @param length
     * @return
     */
    public static boolean isContainChinese(String message, int length) {
        char[] chars = AtsframeStrUtil.substrings(message, 0, length).toCharArray();
        char[] charsPlus = AtsframeStrUtil.substrings(message, 0, length + 1).toCharArray();
        return ArrayUtils.isSameLength(chars, charsPlus);
    }

    /**
     * 在字符串中查找指定字符集合中的字符，并返回第一个匹配的起始索引。 如果字符串为<code>null</code>，则返回<code>-1</code>
     * 如果字符集合为
     * <pre>
     * StringUtil.indexOfAny(null, *,0)                = -1
     * StringUtil.indexOfAny("", *,0)                  = -1
     * StringUtil.indexOfAny(*, null,0)                = -1
     * StringUtil.indexOfAny(*, [],0)                  = -1
     * StringUtil.indexOfAny("zzabyycdxx",['z','a'],0) = 0
     * StringUtil.indexOfAny("zzabyycdxx",['b','y'],0) = 3
     * StringUtil.indexOfAny("aba", ['z'],0)           = -1
     * </pre>
     *
     * @param str 要扫描的字符串
     * @param searchChars 要搜索的字符集合
     * @param startPos 开始搜索的索引值，如果小于0，则看作0
     *
     * @return 第一个匹配的索引值。如果字符串为
     */
    public static int indexOfAny(String str, char[] searchChars, int startPos) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
            || (searchChars.length == 0)) {
            return -1;
        }

        for (int i = startPos; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 过滤要输出到json的字符串，将'和"进行转义输出
     * @param input
     * @return
     */
    public static String filterJsonString(String input) {
        if (input == null) {
            return EMPTY_STRING;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\'': {
                    result.append("\\'");
                    break;
                }
                case '\"': {
                    result.append("\\\"");
                    break;
                }
                default: {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 过滤要输出到xml的字符串，将<,>,&,"进行转义输出
     * @param string
     * @return
     */
    public static String filterXMLString(String input) {
        if (input == null) {
            return EMPTY_STRING;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<': {
                    result.append("&lt;");
                    break;
                }
                case '>': {
                    result.append("&gt;");
                    break;
                }
                case '\"': {
                    result.append("&uot;");
                    break;
                }
                case '&': {
                    result.append("&amp;");
                    break;
                }
                default: {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 根据url获取系统名称
     * 如果url里面包括系统名就返回，否则直接返回域名
     * 如http://bops.alipay.com 返回bops
     * http://www.alipay.com  返回  alipay.com
     * @param url
     * @return
     * @throws MalformedURLException
     */

    public static String getSystemNameByURL(String url) throws MalformedURLException {
        URL netURL = new URL(url);
        String domain = netURL.getHost();
        if (domain.startsWith("www.")) {
            domain = domain.substring(5);
        }

        int offset = domain.indexOf("alipay");
        if (offset > 0) {
            return domain.substring(0, offset - 1);
        } else {
            return domain;
        }
    }

    /**
     * 字符串转MAP
     * 
     * @param content  传入的内容
     * @param separatorChars  分割符
     * @return
     */
    public static Map<String, String> stringTomap(final String content, final String separatorChars) {
        Map<String, String> map = new HashMap<String, String>();
        String[] tempArray = split(content, separatorChars);
        String key = null;
        String value = null;
        for (String string : tempArray) {
            key = substringBefore(string, "=");
            value = substringAfter(string, "=");
            map.put(key, value);
        }

        return map;
    }
}
