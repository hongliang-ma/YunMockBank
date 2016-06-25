
package com.mock.biz.shared.domain;

/**
 * 报文通讯枚举，标明当前的通讯时客户端还是服务器端
 * @author hongliang.ma
 * @version $Id: TemplateType.java, v 0.1 2012-8-14 下午1:17:32 hongliang.ma Exp $
 */
public enum TemplateType {
    SEVER_TYPE(true, null, "服务端"),

    CLIENT_TYPE(false, null, "客户端"),

    SEVER_TRANSFER_CLIENT(true, false, "服务端转客户端"),

    CLIENT_TRANSFER_SEVER(false, true, "客户端和服务端合并显示"),

    SEVER_TRANSFER_SEVER(true, true, "服务端和服务器合并显示"),

    CLIENT_TRANSFER_CLIENT(false, false, "客户端和客户端合并显示");

    /** 报文描述，作为后台配置的页面展示文案 */
    private final String  description;
    private final Boolean bFirst;
    private final Boolean bSecond;

    /**
     * 构造函数，第一个
     * @param bFirst
     * @param bSecond
     * @param description
     */
    private TemplateType(Boolean bFirst, Boolean bSecond, String description) {
        this.bFirst = bFirst;
        this.bSecond = bSecond;
        this.description = description;
    }

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter method for property <tt>bFirst</tt>.
     * 
     * @return property value of bFirst
     */
    public Boolean getbFirst() {
        return bFirst;
    }

    /**
     * Getter method for property <tt>bSecond</tt>.
     * 
     * @return property value of bSecond
     */
    public Boolean getbSecond() {
        return bSecond;
    }

}
