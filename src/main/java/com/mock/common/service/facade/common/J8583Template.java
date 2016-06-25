
package com.mock.common.service.facade.common;

import java.util.Map;

/**
 * 修改8583的返回值
 * 
 * @author 马洪良
 * @version $Id: J8583Template.java, v 0.1 2012-12-14 下午4:57:38 马洪良 Exp $
 */
public final class J8583Template {
    /**
     * template ID
     */
    private String              msgtypeid;
    /**
     * 该模板下的FiledID和返回值
     */
    private Map<String, String> Field;

    /**
     * Getter method for property <tt>msgtypeid</tt>.
     * 
     * @return property value of msgtypeid
     */
    public final String getMsgtypeid() {
        return msgtypeid;
    }

    /**
     * Setter method for property <tt>msgtypeid</tt>.
     * 
     * @param msgtypeid value to be assigned to property msgtypeid
     */
    public final void setMsgtypeid(String msgtypeid) {
        this.msgtypeid = msgtypeid;
    }

    /**
     * Getter method for property <tt>field</tt>.
     * 
     * @return property value of Field
     */
    public final Map<String, String> getField() {
        return Field;
    }

    /**
     * Setter method for property <tt>field</tt>.
     * 
     * @param Field value to be assigned to property field
     */
    public final void setField(Map<String, String> field) {
        Field = field;
    }

}
