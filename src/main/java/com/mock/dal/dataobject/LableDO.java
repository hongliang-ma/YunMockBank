/*
 * Alipay.com Inc.
 * Copyright (c) 2004 - 2010 All Rights Reserved.
 * Powered By [rapid-generator]
 */

package com.mock.dal.dataobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * LableDO
 * database table: anymock_lable
 * database table comments: Lable
 * This file is generated by <tt>dalgen</tt>, a DAL (Data Access Layer)
 * 
 * PLEASE DO NOT MODIFY THIS FILE MANUALLY, or else your modification may
 * be OVERWRITTEN by someone else. To modify the file, you should go to 
 * directory <tt>(project-home)/dalgen</tt>
 * @author badqiu(zhongxuan)
 *
 
 */
public class LableDO implements java.io.Serializable {
    private static final long serialVersionUID = -5216457518046898601L;

    /**
     * 通讯ID 		db_column: COMMUNICATION_ID 
     */
    private String            communicationId;
    /**
     * 标签，可以为多个，多个之间用逗号隔开 		db_column: LABLE_NAME 
     */
    private String            lableName;

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    public String getCommunicationId() {
        return this.communicationId;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }

    public String getLableName() {
        return this.lableName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("CommunicationId", getCommunicationId())
            .append("LableName", getLableName()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getCommunicationId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof LableDO == false)
            return false;
        LableDO other = (LableDO) obj;
        return new EqualsBuilder().append(getCommunicationId(), other.getCommunicationId())
            .isEquals();
    }
}
