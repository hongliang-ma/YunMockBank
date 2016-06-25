/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.util;

import com.mock.core.service.transaction.filestory.enums.MessageTypeEnum;

/**
 * 清算对账通知对象。<br><br>
 * 
 * 发送方：银行 <br>
 * 接收方：支付宝
 * 
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public class CCNotify extends FileUploadNotify {

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
        return MessageTypeEnum.CLEARING_CHECK_NOTIFY;
    }
}
