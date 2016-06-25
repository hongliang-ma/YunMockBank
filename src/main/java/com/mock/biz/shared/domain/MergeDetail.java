
package com.mock.biz.shared.domain;

import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author hongliang.ma
 * @version $Id: MergeDetail.java, v 0.1 2012-10-26 上午11:20:58 hongliang.ma Exp $
 */
public final class MergeDetail {

    private Set<String> listMergeId;

    /**
     * Getter method for property <tt>listMergeId</tt>.
     * 
     * @return property value of listMergeId
     */
    public Set<String> getListMergeId() {
        return listMergeId;
    }

    /**
     * Setter method for property <tt>listMergeId</tt>.
     * 
     * @param listMergeId value to be assigned to property listMergeId
     */
    public void setListMergeId(Set<String> listMergeId) {
        this.listMergeId = listMergeId;
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
