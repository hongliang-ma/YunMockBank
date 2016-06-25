
package com.mock.core.model.shared.enums;

/**
 * 键值对枚举值
 * @author jun.qi
 * @version $Id: KeyValueTypeEnum.java, v 0.1 2012-6-29 下午01:39:20 jun.qi Exp $
 */
public enum KeyValueTypeEnum {

    //键值对
    KEYVALUE("Keyvalue"),
    //键值对与XML混合
    XMLMAX("xmlmax");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private KeyValueTypeEnum(String code) {
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
