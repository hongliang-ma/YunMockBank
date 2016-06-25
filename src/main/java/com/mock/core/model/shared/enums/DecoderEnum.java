
package com.mock.core.model.shared.enums;

/**
 * 解密枚举值
 * @author jun.qi
 * @version $Id: DecoderEnum.java, v 0.1 2012-7-4 上午10:33:50 jun.qi Exp $
 */
public enum DecoderEnum {

    /**请求消息的类型， XML */
    XML("xml"),

    /**键值对*/
    KEYVALUE("Keyvalue"),

    /**原值*/
    ORIGVALUE("OrigValue");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private DecoderEnum(String code) {
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
