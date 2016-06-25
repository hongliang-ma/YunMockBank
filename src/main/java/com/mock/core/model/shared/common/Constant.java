/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.common;

/**
 * 系统常量统一配置
 * 
 * @author hongliang.ma
 * @version $Id: Constant.java, v 0.1 2012-6-19 下午7:25:31 hongliang.ma Exp $
 */
public final class Constant {

    /** KEY分隔符 */
    public static final char   KEY_SAPERATOR     = '-';

    /** supergw做为server时，将请求过来的HttpServletRequest.getQueryString方法取到的内容放到map里面，key为HTTP_QUERY_STRING */
    public static final String HTTP_QUERY_STRING = "HTTP_QUERY_STRING";

    /**************************监控数据字典 *************************************/

    /** 错误码 */
    public static final String RESPONSE_CODE     = "responseCode";

    /** 调用结果 */
    public static final String INVOKE_RESULT     = "invokeResult";

    /** 调用耗时 */
    public static final String INVOKE_TIME       = "invokeTime";


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
