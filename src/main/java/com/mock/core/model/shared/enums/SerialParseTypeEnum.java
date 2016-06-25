
package com.mock.core.model.shared.enums;

/**
 * 定长解析枚举值
 * @author jun.qi
 * @version $Id: SerialParseTypeEnum.java, v 0.1 2012-6-28 下午07:57:12 jun.qi Exp $
 */
public enum SerialParseTypeEnum {

    STRING("string"),

    BYTE("byte");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private SerialParseTypeEnum(String code) {
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