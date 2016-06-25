
package com.mock.core.model.transaction.J8583;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 8583的基本头部信息
 * @author hongliang.ma
 * @version $Id: J8583Header.java, v 0.1 2012-8-27 下午1:51:54 hongliang.ma Exp $
 */
public final class J8583Header {
    private String length;
    private String headerValue;

    /**
     * Setter method for property <tt>headerValue</tt>.
     * 
     * @param headerValue value to be assigned to property headerValue
     */
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
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
     * Getter method for property <tt>headerValue</tt>.
     * 
     * @return property value of headerValue
     */
    public String getHeaderValue() {
        return headerValue;
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
