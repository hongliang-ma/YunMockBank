
package com.mock.core.model.shared.enums;

/**
 * 
 * XML解析枚举值
 * @author jun.qi
 * @version $Id: XMLParseTypeEnum.java, v 0.1 2012-6-28 下午04:22:00 jun.qi Exp $
 */
public enum XMLParseTypeEnum {

    //请求消息的类型， XML
    XML("xml"),
    //请求消息的类型， 定长
    FIXED("Fixed");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private XMLParseTypeEnum(String code) {
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