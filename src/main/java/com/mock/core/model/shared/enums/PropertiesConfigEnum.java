
package com.mock.core.model.shared.enums;

/**
 *  通讯属性配置枚举
 * 
 * @author hongliang.ma
 * @version $Id: PropertiesConfigEnum.java, v 0.1 2012-6-28 上午9:48:45 hongliang.ma Exp $
 */
public enum PropertiesConfigEnum {

    /****************************** COMMON 部分  *************************/

    /** 代理协议bean**/
    PROXY_PROTOCOL_BEAN("代理协议bean"),

    /** 是否异步接收**/
    IS_ASYN_RECEIVER("是否异步接收"),

    /** 发送或者接收后是否需要响应消息**/
    NEEDS_RESPONSE("是否需要响应"),

    /** 组件的发送线程线阀值，默认为50 **/
    THREAD_POOL_THRESHOLD("组件的发送线程线阀值"),
    /**单点连接限制 **/
    SINGLE_POOL_LIMIT("单点连接限制"),

    /** 连接超时时间**/
    CONNECT_TIMEOUT("连接超时时间"),

    /** HTTPS自定义协议类型，默认为渠道系统编号  */
    HTTPS_SCHEMA("HTTPS自定义协议类型，默认为渠道系统编号"),

    /****************************** TCP 部分  *************************/
    //
    //    /** TCP报头类型 **/
    //    HEAD_TYPE("TCP报头类型 "),
    //
    //    /** TCP报头类型 **/
    //    HEAD_OFFSET("TCP报头偏移量"),
    //
    //    /** TCP报头展开长度**/
    //    HEAD_WIDTH("TCP报头展开长度"),
    //
    //    /** TCP子域 **/
    //    HEAD_SUBSTR("TCP子域"),

    /** 是否长连接**/
    IS_KEEPALIVE("是否长连接"),

    /****************************** HTTP 部分  *************************/

    /** HTTP请求类型 */
    HTTP_REQ_TYPE("HTTP请求类型"),

    /** 是否进行重定向 */
    IS_REDIRECT("是否进行重定向"),

    /** 接收字符编码类型**/
    RECV_CHARSET("接收字符编码类型"),

    /****************************** WEBSERVICE 部分  *************************/

    /****************************** TBNOTIFY 部分  *************************/

    /** TBNotify主题 */
    TB_TOPIC("TBNotify主题"),

    /** TBNotify事件码 */
    TB_EVENT_CODE("TBNotify事件码");

    private final String description;

    /**
     * 私有构造函数。
     * 
     * @param code
     * @param desctrition
     */
    private PropertiesConfigEnum(String description) {
        this.description = description;
    }

    /** 
     * @see com.alipay.supergw.common.util.enums.BaseEnum#getDescription()
     */
    public String getDescription() {
        return description;
    }

}
