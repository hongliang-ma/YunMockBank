
package com.mock.core.model.shared.enums;

/**
 * 协议码枚举值
 * 
 * @author hongliang.ma
 * @version $Id: CodeRuleEnum.java, v 0.1 2012-6-27 下午3:20:21 hongliang.ma Exp $
 */
public enum CodeRuleEnum {
    /**请求消息的类型， XML */
    XML("xml"),
    /**请求消息的类型， 定长*/
    FIXED("Fixed"),
    /**键值对*/
    KEYVALUE("Keyvalue"),
    /**键值对与XML混合*/
    XMLMAX("xmlmax"),
    /**正则表达式*/
    REGULAR("Regular"),
    /** MAP报文 */
    MAP("Map"),
    /** 8583 */
    IS8583("8583");

    public final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private CodeRuleEnum(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     */
    public String getCode() {
        return code;
    }
}
