/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.enums;

import com.alibaba.common.lang.enumeration.IntegerEnum;

/**
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public class TransTypeEnum extends IntegerEnum {

    /** 提现*/
    public static final TransTypeEnum WITHDRAW = (TransTypeEnum) create(0);

    /** 支付*/
    public static final TransTypeEnum PAY      = (TransTypeEnum) create(1);

    /** 退款*/
    public static final TransTypeEnum REFUND   = (TransTypeEnum) create(2);

}
