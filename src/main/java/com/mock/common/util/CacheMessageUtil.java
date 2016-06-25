/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util;

import java.util.Iterator;
import java.util.Map;

/**
 * 缓存信息转换工具，以便dump出更友好的缓存信息
 * 
 * @author peng.lanqp
 * @version $Id: CacheMessageUtil.java, v 0.1 2011-4-9 下午04:52:05 peng.lanqp Exp $
 */
public final class CacheMessageUtil {

    /** Map 等于符号 */
    private static final char MAP_EQUAL = '=';

    /**
     * 禁用构造函数
     */
    private CacheMessageUtil() {
        // 禁用构造函数
    }

    /**
     * 缓存信息转换工具，以便dump出更友好的缓存信息<br>
     * 对于Map<String, Object>的类型转换
     * 
     * @param map
     * @return
     */
    public static StringBuilder mapStringAndObject(Map<String, ?> map) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
            String name = i.next();

            builder.append(LoggerUtil.ENTERSTR);
            builder.append(name).append(MAP_EQUAL);
            builder.append(map.get(name));
            builder.append(LoggerUtil.ENTERSTR);
        }

        return builder;
    }

}
