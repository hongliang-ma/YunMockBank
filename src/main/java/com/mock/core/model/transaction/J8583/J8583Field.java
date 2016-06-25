
package com.mock.core.model.transaction.J8583;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 8583的Field的部分
 * @author hongliang.ma
 * @version $Id: J8583Field.java, v 0.1 2012-8-27 下午1:58:07 hongliang.ma Exp $
 */
public final class J8583Field {

    private String id;
    private String datatype;
    private String crule;
    private String length;
    private String name;
    private String fieldValue;

    /**
     * Setter method for property <tt>fieldValue</tt>.
     * 
     * @param fieldValue value to be assigned to property fieldValue
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>datatype</tt>.
     * 
     * @return property value of datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Setter method for property <tt>datatype</tt>.
     * 
     * @param datatype value to be assigned to property datatype
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * Getter method for property <tt>length</tt>.
     * 
     * @return property value of length
     */
    public String getLength() {
        return length;
    }

    /**
     * Setter method for property <tt>length</tt>.
     * 
     * @param length value to be assigned to property length
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for property <tt>name</tt>.
     * 
     * @param name value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property <tt>fieldValue</tt>.
     * 
     * @return property value of fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Getter method for property <tt>crule</tt>.
     * 
     * @return property value of crule
     */
    public String getCrule() {
        return crule;
    }

    /**
     * Setter method for property <tt>crule</tt>.
     * 
     * @param crule value to be assigned to property crule
     */
    public void setCrule(String crule) {
        this.crule = crule;
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
