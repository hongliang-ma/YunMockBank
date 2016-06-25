
package com.mock.biz.shared.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 公共部分修改的模板
 * @author hongliang.ma
 * @version $Id: SysconfigDes.java, v 0.1 2012-11-15 下午5:05:11 hongliang.ma Exp $
 */
public final class SysconfigDes {
    private String modifyId;
    private String modifyTempalte;

    /**
     * Getter method for property <tt>modifyId</tt>.
     * 
     * @return property value of modifyId
     */
    public String getModifyId() {
        return modifyId;
    }

    /**
     * Setter method for property <tt>modifyId</tt>.
     * 
     * @param modifyId value to be assigned to property modifyId
     */
    public void setModifyId(String modifyId) {
        this.modifyId = modifyId;
    }

    /**
     * Getter method for property <tt>modifyTempalte</tt>.
     * 
     * @return property value of modifyTempalte
     */
    public String getModifyTempalte() {
        return modifyTempalte;
    }

    /**
     * Setter method for property <tt>modifyTempalte</tt>.
     * 
     * @param modifyTempalte value to be assigned to property modifyTempalte
     */
    public void setModifyTempalte(String modifyTempalte) {
        this.modifyTempalte = modifyTempalte;
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
