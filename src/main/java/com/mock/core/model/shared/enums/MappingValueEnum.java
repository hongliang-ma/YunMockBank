
package com.mock.core.model.shared.enums;

/**
 * 
 * 从请求中获取值枚举值
 * @author jun.qi
 * @version $Id: MappingValueEnum.java, v 0.1 2012-7-2 下午04:51:39 jun.qi Exp $
 */
public enum MappingValueEnum {

    //请求消息的类型， XML
    XML("xml"),
    
    /**键值对与XML混合*/
    XMLMAX("xmlmax"),

    //键值对
    KEYVALUE("Keyvalue");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private MappingValueEnum(String code) {
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
