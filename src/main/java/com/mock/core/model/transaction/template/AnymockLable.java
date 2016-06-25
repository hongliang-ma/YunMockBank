
package com.mock.core.model.transaction.template;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author hongliang.ma
 * @version $Id: AnymockLable.java, v 0.1 2012-6-6 上午11:30:43 hongliang.ma Exp $
 */
public final class AnymockLable {

    /**
     * 通讯ID
     */
    String communicationId;

    /**
     * 标签列表
     */
    String labList;

    /**
     * Getter method for property <tt>communicationId</tt>.
     * 
     * @return property value of communicationId
     */
    public String getCommunicationId() {
        return communicationId;
    }

    /**
     * Setter method for property <tt>communicationId</tt>.
     * 
     * @param communicationId value to be assigned to property communicationId
     */
    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    /**
     * Getter method for property <tt>labList</tt>.
     * 
     * @return property value of labList
     */
    public String getLabList() {
        return labList;
    }

    /**
     * Setter method for property <tt>labList</tt>.
     * 
     * @param labList value to be assigned to property labList
     */
    public void setLabList(String labList) {
        this.labList = labList;
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
