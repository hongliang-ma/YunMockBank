
package com.mock.biz.shared.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *  展示到页面的对象
 * @author hongliang.ma
 * @version $Id: UserInfoList.java, v 0.1 2012-7-25 下午1:04:58 hongliang.ma Exp $
 */
public final class UserInfoList {

    /**
     * 模板内部ID
     */
    private String       templateInnerId;

    /**
     * 模板的名字
     */
    private String       templateName;
    /**
     * url地址 
     */
    private String       templateUrl;

    /** 通讯类型*/
    private TemplateType templateType;

    /**
     * 匹配值
     */
    private String       matchString;

    /**
     * 标签列表
     */
    private String       templateLable;

    /**
     * 使用次数
     */
    private int          templateCount;

    /**
     * 组成参数，由innerid=?构成 
     */
    private String       templateGetPara;

    /**
     * Getter method for property <tt>templateName</tt>.
     * 
     * @return property value of templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Setter method for property <tt>templateName</tt>.
     * 
     * @param templateName value to be assigned to property templateName
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Getter method for property <tt>templateUrl</tt>.
     * 
     * @return property value of templateUrl
     */
    public String getTemplateUrl() {
        return templateUrl;
    }

    /**
     * Setter method for property <tt>templateUrl</tt>.
     * 
     * @param templateUrl value to be assigned to property templateUrl
     */
    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    /**
     * Getter method for property <tt>templateLable</tt>.
     * 
     * @return property value of templateLable
     */
    public String getTemplateLable() {
        return templateLable;
    }

    /**
     * Setter method for property <tt>templateLable</tt>.
     * 
     * @param templateLable value to be assigned to property templateLable
     */
    public void setTemplateLable(String templateLable) {
        this.templateLable = templateLable;
    }

    /**
     * Getter method for property <tt>templateCount</tt>.
     * 
     * @return property value of templateCount
     */
    public int getTemplateCount() {
        return templateCount;
    }

    /**
     * Setter method for property <tt>templateCount</tt>.
     * 
     * @param templateCount value to be assigned to property templateCount
     */
    public void setTemplateCount(int templateCount) {
        this.templateCount = templateCount;
    }

    /**
     * Getter method for property <tt>templateGetPara</tt>.
     * 
     * @return property value of templateGetPara
     */
    public String getTemplateGetPara() {
        return templateGetPara;
    }

    /**
     * Setter method for property <tt>templateGetPara</tt>.
     * 
     * @param templateGetPara value to be assigned to property templateGetPara
     */
    public void setTemplateGetPara(String templateGetPara) {
        this.templateGetPara = templateGetPara;
    }

    /**
     * Getter method for property <tt>matchString</tt>.
     * 
     * @return property value of matchString
     */
    public String getMatchString() {
        return matchString;
    }

    /**
     * Setter method for property <tt>matchString</tt>.
     * 
     * @param matchString value to be assigned to property matchString
     */
    public void setMatchString(String matchString) {
        this.matchString = matchString;
    }

    /**
     * Getter method for property <tt>templateInnerId</tt>.
     * 
     * @return property value of templateInnerId
     */
    public String getTemplateInnerId() {
        return templateInnerId;
    }

    /**
     * Setter method for property <tt>templateInnerId</tt>.
     * 
     * @param templateInnerId value to be assigned to property templateInnerId
     */
    public void setTemplateInnerId(String templateInnerId) {
        this.templateInnerId = templateInnerId;
    }

    /**
     * Getter method for property <tt>templateType</tt>.
     * 
     * @return property value of templateType
     */
    public TemplateType getTemplateType() {
        return templateType;
    }

    /**
     * Setter method for property <tt>templateType</tt>.
     * 
     * @param templateType value to be assigned to property templateType
     */
    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
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
