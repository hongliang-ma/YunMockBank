
package com.mock.core.model.shared.enums;

/**
 * 协议码开关
 * 
 * @author hongliang.ma
 * @version $Id: StringSubAdaptorEnum.java, v 0.1 2012-7-3 下午1:13:13 hongliang.ma Exp $
 */
public enum StringSubAdaptorEnum {
    //协议码开关
    TRUE("true"),

    FALSE("false");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private StringSubAdaptorEnum(String code) {
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