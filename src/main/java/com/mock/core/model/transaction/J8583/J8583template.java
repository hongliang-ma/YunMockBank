/**
s * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.mock.core.model.transaction.J8583;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 8583的某一部分的内容
 * 
 * @author hongliang.ma
 * @version $Id: J8583template.java, v 0.1 2012-8-27 下午1:55:17 hongliang.ma Exp $
 */
public final class J8583template {
    private String                msgtypeid;
    private ArrayList<J8583Field> templateField;

    /**
     * Getter method for property <tt>msgtypeid</tt>.
     * 
     * @return property value of msgtypeid
     */
    public String getMsgtypeid() {
        return msgtypeid;
    }

    /**
     * Setter method for property <tt>msgtypeid</tt>.
     * 
     * @param msgtypeid value to be assigned to property msgtypeid
     */
    public void setMsgtypeid(String msgtypeid) {
        this.msgtypeid = msgtypeid;
    }

    /**
     * Getter method for property <tt>templateField</tt>.
     * 
     * @return property value of templateField
     */
    public ArrayList<J8583Field> getTemplateField() {
        return templateField;
    }

    /**
     * Setter method for property <tt>templateField</tt>.
     * 
     * @param templateField value to be assigned to property templateField
     */
    public void setTemplateField(ArrayList<J8583Field> templateField) {
        this.templateField = templateField;
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
