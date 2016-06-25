/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2010 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.service.transaction.filestory.enums.MessageTypeEnum;

/**
 * 标准网银清算对账通知对象。<br><br>
 * 
 * 发送方：银行 <br>
 * 接收方：支付宝
 * 
 * @author wb-lij
 *
 * @version $Id: ECCNotify.java, v 0.1 2010-5-10 上午10:33:30 wb-lij Exp $
 */
public class ECCNotify extends FileUploadNotify {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6628101880586209180L;

    /** 对账流水号*/
    private String            serialNo;

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

    /* (non-Javadoc)
     * @see com.alipay.katong.domain.Message#getMsgType()
     */
    @Override
    public MessageTypeEnum getMsgType() {
        return MessageTypeEnum.EBANK_CLEARING_CHECK_NOTIFY;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
