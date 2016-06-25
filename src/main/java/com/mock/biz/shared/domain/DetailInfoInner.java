
package com.mock.biz.shared.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.model.shared.message.enums.MessageRunMode;
import com.mock.core.model.transaction.template.Usertemplate;

/**
 * 页面展示用的数据
 * 
 * @author hongliang.ma
 * @version $Id: DetailInfoInner.java, v 0.1 2012-8-9 上午10:00:47 hongliang.ma Exp $
 */
public class DetailInfoInner {
    /**
    * 用户模板
    */
    Usertemplate   usertemplate;

    /**
     * 通讯地址
     */
    String         url;

    /**
     * 请求方式，POST，GET，TCP
     */
    String         reqType;

    /**
     * 编码格式
     */
    String         charset;

    /**
     * 转发地址 none transfer
     */
    NextTypeEnum   nextTypeEnum;

    /**
     * 服务器端还是客服端
     */
    Boolean        isServer;

    /**
     *运行模式
     */
    MessageRunMode messageRunMode;

    /**
     * 公共模板ID
     */
    String         sysId;

    /**
     * 匹配方式
     */
    String         codeRule;

    /**
     * 匹配方式
     */
    String         codeRuleS;

    /**
     * 匹配方式
     */
    String         codeRuleT;

    /**
     * 匹配方式说明
     */
    String         machDescrption;

    /**
     * Getter method for property <tt>usertemplate</tt>.
     * 
     * @return property value of usertemplate
     */
    public Usertemplate getUsertemplate() {
        return usertemplate;
    }

    /**
     * Setter method for property <tt>usertemplate</tt>.
     * 
     * @param usertemplate value to be assigned to property usertemplate
     */
    public void setUsertemplate(Usertemplate usertemplate) {
        this.usertemplate = usertemplate;
    }

    /**
     * Getter method for property <tt>url</tt>.
     * 
     * @return property value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for property <tt>url</tt>.
     * 
     * @param url value to be assigned to property url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for property <tt>isServer</tt>.
     * 
     * @return property value of isServer
     */
    public Boolean getIsServer() {
        return isServer;
    }

    /**
     * Setter method for property <tt>isServer</tt>.
     * 
     * @param isServer value to be assigned to property isServer
     */
    public void setIsServer(Boolean isServer) {
        this.isServer = isServer;
    }

    /**
     * Getter method for property <tt>sysId</tt>.
     * 
     * @return property value of sysId
     */
    public String getSysId() {
        return sysId;
    }

    /**
     * Setter method for property <tt>sysId</tt>.
     * 
     * @param sysId value to be assigned to property sysId
     */
    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    /**
     * Getter method for property <tt>codeRule</tt>.
     * 
     * @return property value of codeRule
     */
    public final String getCodeRule() {
        return codeRule;
    }

    /**
     * Setter method for property <tt>codeRule</tt>.
     * 
     * @param codeRule value to be assigned to property codeRule
     */
    public final void setCodeRule(String codeRule) {
        this.codeRule = codeRule;
    }

    /**
     * Getter method for property <tt>codeRuleS</tt>.
     * 
     * @return property value of codeRuleS
     */
    public final String getCodeRuleS() {
        return codeRuleS;
    }

    /**
     * Setter method for property <tt>codeRuleS</tt>.
     * 
     * @param codeRuleS value to be assigned to property codeRuleS
     */
    public final void setCodeRuleS(String codeRuleS) {
        this.codeRuleS = codeRuleS;
    }

    /**
     * Getter method for property <tt>codeRuleT</tt>.
     * 
     * @return property value of codeRuleT
     */
    public final String getCodeRuleT() {
        return codeRuleT;
    }

    /**
     * Setter method for property <tt>codeRuleT</tt>.
     * 
     * @param codeRuleT value to be assigned to property codeRuleT
     */
    public final void setCodeRuleT(String codeRuleT) {
        this.codeRuleT = codeRuleT;
    }

    /**
     * Getter method for property <tt>machDescrption</tt>.
     * 
     * @return property value of machDescrption
     */
    public String getMachDescrption() {
        return machDescrption;
    }

    /**
     * Setter method for property <tt>machDescrption</tt>.
     * 
     * @param machDescrption value to be assigned to property machDescrption
     */
    public void setMachDescrption(String machDescrption) {
        this.machDescrption = machDescrption;
    }

    /**
     * Getter method for property <tt>nextTypeEnum</tt>.
     * 
     * @return property value of nextTypeEnum
     */
    public NextTypeEnum getNextTypeEnum() {
        return nextTypeEnum;
    }

    /**
     * Setter method for property <tt>nextTypeEnum</tt>.
     * 
     * @param nextTypeEnum value to be assigned to property nextTypeEnum
     */
    public void setNextTypeEnum(NextTypeEnum nextTypeEnum) {
        this.nextTypeEnum = nextTypeEnum;
    }

    /**
     * Getter method for property <tt>reqType</tt>.
     * 
     * @return property value of reqType
     */
    public String getReqType() {
        return reqType;
    }

    /**
     * Setter method for property <tt>reqType</tt>.
     * 
     * @param reqType value to be assigned to property reqType
     */
    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    /**
     * Getter method for property <tt>charset</tt>.
     * 
     * @return property value of charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Setter method for property <tt>charset</tt>.
     * 
     * @param charset value to be assigned to property charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Getter method for property <tt>messageRunMode</tt>.
     * 
     * @return property value of messageRunMode
     */
    public final MessageRunMode getMessageRunMode() {
        return messageRunMode;
    }

    /**
     * Setter method for property <tt>messageRunMode</tt>.
     * 
     * @param messageRunMode value to be assigned to property messageRunMode
     */
    public final void setMessageRunMode(MessageRunMode messageRunMode) {
        this.messageRunMode = messageRunMode;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
