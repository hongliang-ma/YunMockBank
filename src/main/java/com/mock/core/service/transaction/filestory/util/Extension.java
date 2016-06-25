package com.mock.core.service.transaction.filestory.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 扩展字段
 * 
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public class Extension implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** 扩展字段名称*/
    private String            name;

    /** 扩展字段数值*/
    private String            value;

    public Extension() {

    }

    public Extension(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
