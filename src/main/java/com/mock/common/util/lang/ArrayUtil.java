
package com.mock.common.util.lang;

import org.apache.commons.lang.ArrayUtils;

/**
 * 队列工具类
 * @author zhao.xiong
 * @version $Id: ArrayUtil.java, v 0.1 2012-2-6 下午05:09:37 zhao.xiong Exp $
 */
public class ArrayUtil extends ArrayUtils {

    /**
     * 获取随机数组中的任意一个值
     * 
     * @param strArray
     * @return
     */
    public static String getRandomStr(String... strArray) {
        return strArray[(int) (Math.random() * strArray.length)];
    }

    /**
     * 从字符串中截取字符串数组
     * 
     * @param str
     * @param splitor
     * @return
     */
    public static String getRandomStrBySplitor(String str, String splitor) {
        return getRandomStr(StringUtil.split(str, splitor));
    }

}
