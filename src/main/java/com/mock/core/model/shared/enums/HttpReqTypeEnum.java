
package com.mock.core.model.shared.enums;

/**
 * HTTP内容的枚举值
 * @author hongliang.ma
 * @version $Id: HttpReqTypeEnum.java, v 0.1 2012-6-27 下午4:27:53 hongliang.ma Exp $
 */
public enum HttpReqTypeEnum {
    /**键值对*/
    PARAMETER("parameter"),
    /**内容 content*/
    CONTENT("content"),
    /**键值对+内容  */
    CONTENTPARAMETER("contentparameter");

    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private HttpReqTypeEnum(String code) {
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
