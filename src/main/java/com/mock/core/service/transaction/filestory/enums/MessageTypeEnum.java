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
public class MessageTypeEnum extends IntegerEnum {

    /** 错误报文*/
    public static final MessageTypeEnum ERROR                                       = (MessageTypeEnum) create("Error");

    /** 通知接收报文*/
    public static final MessageTypeEnum NOTIFY_ACCEPT                               = (MessageTypeEnum) create("NotifyAccept");

    /** 余额查询请求报文*/
    public static final MessageTypeEnum BALANCE_QUERY_REQUEST                       = (MessageTypeEnum) create("BQReq");

    /** 余额查询应答报文*/
    public static final MessageTypeEnum BALANCE_QUERY_RESPONSE                      = (MessageTypeEnum) create("BQRes");

    /** 柜台签约请求报文*/
    public static final MessageTypeEnum CARD_SIGN_REQUEST                           = (MessageTypeEnum) create("CSReq");

    /** 柜台签约应答报文*/
    public static final MessageTypeEnum CARD_SIGN_RESPONSE                          = (MessageTypeEnum) create("CSRes");

    /** 签约验证请求报文*/
    public static final MessageTypeEnum CARD_SIGN_VALIDATION_REQUEST                = (MessageTypeEnum) create("CSVReq");

    /** 签约验证应答报文*/
    public static final MessageTypeEnum CARD_SIGN_VALIDATION_RESPONSE               = (MessageTypeEnum) create("CSVRes");

    /**撤销签约请求报文*/
    public static final MessageTypeEnum CARD_SIGN_CANCEL_REQUEST                    = (MessageTypeEnum) create("CSCReq");

    /**撤销签约返回报文*/
    public static final MessageTypeEnum CARD_SIGN_CANCEL_RESPONSE                   = (MessageTypeEnum) create("CSCRes");

    /** 撤消签约通知报文*/
    public static final MessageTypeEnum CARD_SIGN_CANCEL_NOTIFY                     = (MessageTypeEnum) create("CSCNotify");

    /** 支付限额查询请求报文*/
    public static final MessageTypeEnum PAYMENT_LIMIT_QUERY_REQUEST                 = (MessageTypeEnum) create("PLQReq");

    /** 支付限额查询应答报文*/
    public static final MessageTypeEnum PAYMENT_LIMIT_QUERY_RESPONSE                = (MessageTypeEnum) create("PLQRes");

    /** 网上支付请求报文*/
    public static final MessageTypeEnum CARD_PAYMENT_REQUEST                        = (MessageTypeEnum) create("CPReq");

    /** 网上支付应答报文*/
    public static final MessageTypeEnum CARD_PAYMENT_RESPONSE                       = (MessageTypeEnum) create("CPRes");

    /** 实时提现请求报文*/
    public static final MessageTypeEnum CARD_WITHDRAW_REQUEST                       = (MessageTypeEnum) create("SWReq");

    /** 实时提现应答报文*/
    public static final MessageTypeEnum CARD_WITHDRAW_RESPONSE                      = (MessageTypeEnum) create("SWRes");

    /** 网上支付申请请求报文(邮政卡通专用)*/
    public static final MessageTypeEnum CARD_PAYMENT_APPLY_REQUEST                  = (MessageTypeEnum) create("CPAReq");

    /** 网上支付申请应答报文(邮政卡通专用)*/
    public static final MessageTypeEnum CARD_PAYMENT_APPLY_RESPONSE                 = (MessageTypeEnum) create("CPARes");

    /** 批量退货通知报文*/
    public static final MessageTypeEnum BATCH_REFUND_NOTIFY                         = (MessageTypeEnum) create("BRNotify");

    /** 批量退货结果通知报文*/
    public static final MessageTypeEnum BATCH_REFUND_RESULT_NOTIFY                  = (MessageTypeEnum) create("BRRNotify");

    /** 实时交易查询请求报文*/
    public static final MessageTypeEnum TRADE_QUERY_REQUEST                         = (MessageTypeEnum) create("TQReq");

    /** 实时交易查询应答报文*/
    public static final MessageTypeEnum TRADE_QUERY_RESPONSE                        = (MessageTypeEnum) create("TQRes");

    /** 单比交易查询报文*/
    public static final MessageTypeEnum SINGLE_TRADE_QUERY_REQUEST                  = (MessageTypeEnum) create("STQReq");

    /** 单比交易查询响应报文*/
    public static final MessageTypeEnum SINGLE_TRADE_QUERY_RESPONSE                 = (MessageTypeEnum) create("STQRes");

    /** 单比交易查询报文*/
    public static final MessageTypeEnum ACCOUNT_QUERY_REQUEST                       = (MessageTypeEnum) create("IADRReq");

    /** 单比交易查询响应报文*/
    public static final MessageTypeEnum ACCOUNT_QUERY_RESPONSE                      = (MessageTypeEnum) create("IADRRes");

    /** 实时交易查询结果通知报文*/
    public static final MessageTypeEnum TRADE_QUERY_RESULT_NOTIFY                   = (MessageTypeEnum) create("TQRNotify");

    /** 签约对账通知报文*/
    public static final MessageTypeEnum SIGN_CHECK_NOTIFY                           = (MessageTypeEnum) create("SCNotify");

    /** 清算对账通知报文*/
    public static final MessageTypeEnum CLEARING_CHECK_NOTIFY                       = (MessageTypeEnum) create("CCNotify");

    /** 文件接收报文*/
    public static final MessageTypeEnum FILE_ACCEPT                                 = (MessageTypeEnum) create("FileAccept");

    //**公共事业缴费业务报文

    /**
     * 杭州银行查询缴费单
     */
    public static final MessageTypeEnum HZCB_QUERY_BILL_INFO_REQUEST                = (MessageTypeEnum) create("HZCBQBIReq");

    /**
     * 杭州银行查询缴费单应答
     */
    public static final MessageTypeEnum HZCB_QUERY_BILL_INFO_RESPONSE               = (MessageTypeEnum) create("HZCBQBIRes");

    /**
     * 杭州银行缴费单销账请求
     */
    public static final MessageTypeEnum HZCB_BILL_CHARGE_REQUEST                    = (MessageTypeEnum) create("HZCBBCReq");

    /**
     * 杭州银行缴费单销账应答
     */
    public static final MessageTypeEnum HZCB_BILL_CHARGE_RESPONSE                   = (MessageTypeEnum) create("HZCBBCRes");

    /**
     * 杭州银行缴费单销账结果查询请求
     */
    public static final MessageTypeEnum HZCB_BILL_CHARGE_RESULT_REQUEST             = (MessageTypeEnum) create("HZCBBCRReq");

    /**
     * 杭州银行缴费单销账结果查询应答
     */
    public static final MessageTypeEnum HZCB_BILL_CHARGE_RESULT_RESPONSE            = (MessageTypeEnum) create("HZCBBCRRes");

    /**
     * 杭州银行机表号是否合法查询请求
     */
    public static final MessageTypeEnum HZCB_QUERY_BILL_KEY_AVAILABLE_REQUEST       = (MessageTypeEnum) create("HZCBQBKAReq");

    /**
     * 杭州银行机表号是否合法查询应答
     */
    public static final MessageTypeEnum HZCB_QUERY_BILL_KEY_AVAILABLE_RESPONSE      = (MessageTypeEnum) create("HZCBQBKARes");

    /**
     * 对账文件上传通知
     */
    public static final MessageTypeEnum BILL_CHECK_NOTIFY                           = (MessageTypeEnum) create("BCNotify");

    /** 撤消签约通知报文*/
    public static final MessageTypeEnum CARD_SIGN_ADJUST_NOTIFY                     = (MessageTypeEnum) create("CSANotify");

    // 城商行卡通提现接口标准化 add by Alcor 2010.04.02

    /** 通讯检查请求报文*/
    public static final MessageTypeEnum COMMUNICATION_CHECK_REQUEST                 = (MessageTypeEnum) create("CCReq");

    /** 通讯检查应答报文*/
    public static final MessageTypeEnum COMMUNICATION_CHECK_RESPONSE                = (MessageTypeEnum) create("CCRes");

    /** 账务明细查询请求报文*/
    public static final MessageTypeEnum FUND_TRANSFER_QUERY_REQUEST                 = (MessageTypeEnum) create("FTQReq");

    /** 账务明细查询应答报文*/
    public static final MessageTypeEnum FUND_TRANSFER_QUERY_RESPONSE                = (MessageTypeEnum) create("FTQRes");

    /** 批量普通提现通知报文*/
    public static final MessageTypeEnum BATCH_COMMON_WITHDRAW_NOTIFY                = (MessageTypeEnum) create("BCWNotify");

    /** 批量普通提现结果通知报文*/
    public static final MessageTypeEnum BATCH_COMMON_WITHDRAW_RESULT_NOTIFY         = (MessageTypeEnum) create("BCWRNotify");

    /** 单笔普通提现请求报文*/
    public static final MessageTypeEnum SINGLE_COMMON_WITHDRAW_REQUEST              = (MessageTypeEnum) create("SCWReq");

    /** 单笔普通提现应答报文*/
    public static final MessageTypeEnum SINGLE_COMMON_WITHDRAW_RESPONSE             = (MessageTypeEnum) create("SCWRes");

    /** 批量普通交易查询请求报文*/
    public static final MessageTypeEnum BATCH_COMMON_TRADE_QUERY_REQUEST            = (MessageTypeEnum) create("BCTQReq");

    /** 批量普通交易查询应答报文*/
    public static final MessageTypeEnum BATCH_COMMON_TRADE_QUERY_RESPONSE           = (MessageTypeEnum) create("BCTQRes");

    /** 单笔普通交易查询请求报文*/
    public static final MessageTypeEnum SINGLE_COMMON_TRADE_QUERY_REQUEST           = (MessageTypeEnum) create("SCTQReq");

    /** 单笔普通交易查询应答报文*/
    public static final MessageTypeEnum SINGLE_COMMON_TRADE_QUERY_RESPONSE          = (MessageTypeEnum) create("SCTQRes");

    /** 中间账户实时余额查询请求报文*/
    public static final MessageTypeEnum INTERMEDIATE_ACCOUNT_BALANCE_QUERY_REQUEST  = (MessageTypeEnum) create("IABQReq");

    /** 中间账户实时余额查询应答报文*/
    public static final MessageTypeEnum INTERMEDIATE_ACCOUNT_BALANCE_QUERY_RESPONSE = (MessageTypeEnum) create("IABQRes");

    /** 银行业务支持查询请求报文*/
    public static final MessageTypeEnum BUSINESS_SUPPORT_QUERY_REQUEST              = (MessageTypeEnum) create("BSQReq");

    /** 银行业务支持查询应答报文*/
    public static final MessageTypeEnum BUSINESS_SUPPORT_QUERY_RESPONSE             = (MessageTypeEnum) create("BSQRes");

    // 城商行卡通提现接口标准化 add end

    /*
     * 标准网银充退  add by caogang
     */
    /** 单笔充退请求报文 */
    public static final MessageTypeEnum EBANK_SIGNLE_REFUND_REQUEST                 = (MessageTypeEnum) create("ESRReq");

    /** 单笔充退应答报文 */
    public static final MessageTypeEnum EBANK_SINGLE_REFUND_RESPONSE                = (MessageTypeEnum) create("ESRRes");

    /**
     * 城商行标准网银 add lijie
     */
    /** 城商行标准网银清算对账通知报文*/
    public static final MessageTypeEnum EBANK_CLEARING_CHECK_NOTIFY                 = (MessageTypeEnum) create("ECCNotify");

    /**
     * 批量签约通知报文
     */
    public static final MessageTypeEnum BATCH_CARD_SIGN_NOTIFY                      = (MessageTypeEnum) create("BCSNotify");

    /**
     *  批量签约结果通知报文
     */
    public static final MessageTypeEnum BATCH_SIGN_RESULT_NOTIFY                    = (MessageTypeEnum) create("BSRNotify");

}
