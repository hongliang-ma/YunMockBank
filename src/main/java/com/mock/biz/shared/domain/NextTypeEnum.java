
package com.mock.biz.shared.domain;

/**
 * 第一个时，设置下一个的配置
 * @author hongliang.ma
 * @version $Id: NextType.java, v 0.1 2012-10-27 下午12:13:03 hongliang.ma Exp $
 */
public enum NextTypeEnum {
    /**
     * 没有下一个
     */
    NONEXT("nonext"),
    /**
     * 转发
     */
    TRANSFER("transfer"),

    /**
     * XML截取指定字符串
     */
    MERGENEXT("mergenext");

    public final String code;

    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private NextTypeEnum(String code) {
        this.code = code;
    }

}
