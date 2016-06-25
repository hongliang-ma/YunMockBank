/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.util;

import java.util.Date;

import com.mock.core.service.transaction.filestory.enums.MessageTypeEnum;

/**
 * 批量退货结果通知
 * 
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public class BRRNotify extends FileUploadNotify {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3417251494994122126L;

    private String            serialNo;

    private String            orginalSerialNo;

    private Date              originalDate;

    /* (non-Javadoc)
     * @see com.alipay.katong.domain.Message#getMsgType()
     */
    @Override
    public MessageTypeEnum getMsgType() {
        return MessageTypeEnum.BATCH_REFUND_RESULT_NOTIFY;
    }

    /**
     * @return Returns the serialNo.
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo The serialNo to set.
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return Returns the orginalSerialNo.
     */
    public String getOrginalSerialNo() {
        return orginalSerialNo;
    }

    /**
     * @param orginalSerialNo The orginalSerialNo to set.
     */
    public void setOrginalSerialNo(String orginalSerialNo) {
        this.orginalSerialNo = orginalSerialNo;
    }

    /**
     * @return Returns the originalDate.
     */
    public Date getOriginalDate() {
        return originalDate;
    }

    /**
     * @param originalDate The originalDate to set.
     */
    public void setOriginalDate(Date originalDate) {
        this.originalDate = originalDate;
    }

}
