package com.mock.core.model.shared.enums;

/**
 * 报文校验枚举值
 * @author jun.qi
 * @version $Id: AssertAEnum.java, v 0.1 2012-8-7 下午01:30:39 jun.qi Exp $
 */
public enum AssertAEnum {

    /**请求消息的类型， XML */
    XML("xml"),
    /**请求消息的类型， 定长*/
    FIXED("Fixed"),
    /**键值对*/
    KEYVALUE("Keyvalue"),
    /**键值对与XML混合*/
    XMLMAX("xmlmax");
    
    public final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private AssertAEnum(String code) {
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
