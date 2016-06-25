/**
 * Copyright (c) 2004-2010 All Rights Reserved.
 * Alipay.com Inc.
 */
package com.mock.common.util;

/**
 * 系统常量统一配置
 * 
 * @author peng.lanqp
 * @author zhao.xiong
 * @author 松雪
 * @version $Id: Constant.java, v 0.2 2012-5-10 下午1:52:32 hao.zhang Exp $
 */
public final class Constant {

    /** KEY分隔符 */
    public static final char KEY_SAPERATOR = '-';

    /**************************监控数据字典  *************************************/

    /**
     * 禁用构造函数
     */
    private Constant() {
        // 禁用构造函数
    }

    /**
     * 组装KEY值，默认使用'-'作为分割符号
     * 
     * @param inputs
     * @return
     */
    public static String getKey(String... inputs) {
        return getKey(KEY_SAPERATOR, inputs);
    }

    /**
     * 组装KEY值
     * 
     * @param saperator
     * @param inputs
     * @return
     */
    public static String getKey(char saperator, String... inputs) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < inputs.length; i++) {
            String input = inputs[i];
            result.append(input);

            if (i != inputs.length - 1) {
                result.append(saperator);
            }
        }

        return result.toString();
    }

}
