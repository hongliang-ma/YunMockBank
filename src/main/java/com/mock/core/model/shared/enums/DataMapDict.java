/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */

package com.mock.core.model.shared.enums;

/**
 * 报文公共对象字符串常量定义
 * 
 * @author hongliang.ma
 * @version $Id: DataMapDict.java, v 0.1 2012-6-28 上午9:47:49 hongliang.ma Exp $
 */
public interface DataMapDict {

    //原始报文部分
    /**
     * 报文内容,存放最初的报文
     */
    public static final String MSGBODY                = "msgbody";

    /**
     * 报文内容处理
     */
    public static final String MSGCONTENT             = "msgcontent";

    /**
     * 转发报文的内容
     */
    public static final String TRANSFERMSG            = "transferMsg";

    /**
     * 是否是转发，默认false
     */
    public static final String ISTRANSFER             = "isTransfer";

    /**
     * 报文内容的格式
     */
    public static final String MSGFORMAT              = "msgformat";

    /**
     * 发送报文的格式
     */
    public static final String SENDFORMAT             = "sendformat";

    /**
     * 被处理的XML之前的报文
     */
    public static final String XMLBEFOREHAND          = "xmlBeforeHand";

    /**
     * 通讯的附加信息,取到的是key-value对
     */
    public static final String EXTRACONTENT           = "extraContent";

    /**
     *发送通讯的附加信息,取到的是key-value对
     */
    public static final String SENDEXTRACONTENT       = "sendExtraContent";

    //设置入口的系统和通讯
    /**
     * 内部处理的第一次的系统模板ID
     */
    public static final String ORIGINSYSID            = "originSysId";
    /**
     *  是否异步发送，true为异步，false为异步
     */
    public static final String ISASYNC                = "isAsync";

    //设置发送的系统和通讯
    /**
     *  发送的的通讯ID
     */
    public static final String SENDCOMID              = "sendComId";
    /**
     *发送的的系统模板ID
     */
    public static final String SENDSYSID              = "sendSysId";

    /**
     * 发送是否异步
     */
    public static final String ORIGINCOMID            = "originComId";

    /**
     * 协议码
     */
    public static final String CODERULE               = "codeRule";

    /**
     * 规则
     */
    public static final String TEMPLATE               = "template";

    /**
     * 
     *forward（放到返回报文中） 和 origin（放到请求报文中）
     */
    public static final String STYLE                  = "style";

    /**
     * 系统处理码标志，用来寻找用户模板，值为true或者false
     */
    public static final String SELETCTTAG             = "seletctTag";
    /**
     * 系统码的内容，可以通过该值找到具体的用户配置
     */
    public static final String SYSRULE                = "sysrule";

    /**
     * 第二个系统码的内容，可以通过该值找到具体的用户配置
     */
    public static final String SYSRULES               = "sysruleS";

    /**
     * 消息类型 0 xml  1定长 2键值对  3键值对和xml混合  4正则表达式
     */
    public static final String STATE                  = "state";
    /**
     * Map，从请求中获取值组件 
     */
    public static final String MAPKEY                 = "Mapkey";
    /**
     * 存储组件处理完之后又的消息
     */
    public static final String SERVER_FORWARD_CONTENT = "repscontent";

    /**
     * 请求方式
     */
    public static final String REQ_TYPE               = "reqType";
    /**
     * 用户模板，通过该值，取的是DetailMsg
     */
    public static final String USER_TEMPLATE          = "user_template";

    /**
     * 系统模板，通过该值，取的是一个List列表
     */
    public static final String SYS_TEMPLATE           = "sys_template";
    /**
     * 系统模板内容，通过该值，取的是一个Map列表
     */
    public static final String SYS_TEMPLATE_VALUE     = "sys_template_Value";

    /**
     * 是否为异步
     */
    public static final String CALLFLAG               = "callflag";

}
