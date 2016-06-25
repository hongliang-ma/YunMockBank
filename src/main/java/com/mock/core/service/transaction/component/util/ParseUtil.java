package com.mock.core.service.transaction.component.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;

/**
 * 解析工具类
 * @author jun.qi
 * @version $Id: ParseUtil.java, v 0.1 2012-7-3 下午01:01:21 jun.qi Exp $
 */
public final class ParseUtil {

    /**
     * 默认Socket字符集
     */
    public static final String DEFAULT_CHARSET = "GBK";

    /**模板分隔符组装 */
    public static final String take(String type, String s, String t) throws Exception {
        String str = null;
        if (type.equals("start")) {
            str = "<!--" + s + ":" + t + "-->";
        } else if (type.equals("end")) {
            str = "<!--" + s + ":/" + t + "-->";
        }
        return str;
    }


    /**模板取值*/
    public static final String gettemplat(String coderule, Map<String, String> wrappers)
                                                                                        throws Exception {
        String ruleValue = null;
        for (String wrapper : wrappers.keySet()) {
            if (wrapper.equals(coderule)) {
                ruleValue = wrappers.get(wrapper);
            }
        }
        return ruleValue;
    }

    /**
     * 取出detailElement
     * @param elem
     * @return
     * @throws Exception
     */
    public static final Map<String, String> gettempMap(Map<String, Map<String, String>> elem)
                                                                                             throws Exception {
        Map<String, String> tempMap = new HashMap<String, String>();
        for (String key : elem.keySet()) {
            if (StringUtil.isNotBlank(key)) {
                tempMap = elem.get(DataMapDict.TEMPLATE);
            }
        }
        return tempMap;
    }

    public static final String getparam(Map<String, String> map, String s) throws Exception {
        String rs = null;
        for (String key : map.keySet()) {
            if (StringUtil.isNotBlank(key)) {
                rs = map.get(s);
            }
        }
        return rs;
    }

    /**
     * 取得字符串的长度，一个中文字符占2个字符
     * @param src
     * @return
     */
    public static final int length(String src) {
        if (src == null) {
            return 0;
        }
        try {
            return src.getBytes(DEFAULT_CHARSET).length;
        } catch (UnsupportedEncodingException e) {
            return src.getBytes().length;
        }
    }
}