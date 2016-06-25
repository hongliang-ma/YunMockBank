/**
s * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.mock.core.model.transaction.J8583;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 8583对应页面的基本模板
 * 
 * @author hongliang.ma
 * @version $Id: j8583Model.java, v 0.1 2012-8-27 下午1:47:57 hongliang.ma Exp $
 */
public final class J8583Model {

    private ArrayList<J8583Header>    arrHeaders;
    private ArrayList<J8583template>  allTemplateMap;
    private ArrayList<J8583Parseinfo> parseinfoList;

    /**
     * Getter method for property <tt>arrHeaders</tt>.
     * 
     * @return property value of arrHeaders
     */
    public ArrayList<J8583Header> getArrHeaders() {
        return arrHeaders;
    }

    /**
     * Setter method for property <tt>arrHeaders</tt>.
     * 
     * @param arrHeaders value to be assigned to property arrHeaders
     */
    public void setArrHeaders(ArrayList<J8583Header> arrHeaders) {
        this.arrHeaders = arrHeaders;
    }

    /**
     * Getter method for property <tt>allTemplateMap</tt>.
     * 
     * @return property value of allTemplateMap
     */
    public ArrayList<J8583template> getAllTemplateMap() {
        return allTemplateMap;
    }

    /**
     * Setter method for property <tt>allTemplateMap</tt>.
     * 
     * @param allTemplateMap value to be assigned to property allTemplateMap
     */
    public void setAllTemplateMap(ArrayList<J8583template> allTemplateMap) {
        this.allTemplateMap = allTemplateMap;
    }

    /**
     * Getter method for property <tt>parseinfoList</tt>.
     * 
     * @return property value of parseinfoList
     */
    public ArrayList<J8583Parseinfo> getParseinfoList() {
        return parseinfoList;
    }

    /**
     * Setter method for property <tt>parseinfoList</tt>.
     * 
     * @param parseinfoList value to be assigned to property parseinfoList
     */
    public void setParseinfoList(ArrayList<J8583Parseinfo> parseinfoList) {
        this.parseinfoList = parseinfoList;
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
