package com.mock.core.service.transaction.component.extension;

public class PostConstants {

    public static final String   POST_SERVER            = "post_server";
    public static final String   POST_CLIENT            = "post_client";

    public static final int      DEPOSIT_APPLY          = 3040;
    public static final int      DEPOSIT_APPLY_RES      = 3041;
    public static final int      ENCODE_KEY_APPLY       = 1240;
    public static final int      ENCODE_KEY_APPLY_RES   = 1241;
    public static final int      BILL_STATUS_QUERY      = 3042;
    public static final int      BILL_STATUS_QUERY_RES  = 3043;
    public static final int      ENCODE_KEY_RESET       = 4210;
    public static final int      ENCODE_KEY_RESET_RES   = 4211;
    public static final int      SETTLE_DATE_NOTIFY     = 4220;
    public static final int      SETTLE_DATE_NOTIFY_RES = 4221;

    public static final String[] DEPOSIT_APPLY_RES_MAC  = new String[] { "TransCode", "Amount",
            "TransSeqNo", "TransTime", "TransDate", "TranFlag", "TransInst", "RspCode", "WhdNo",
            "CustNo", "DestinstNo"                     };

    public static final String[] DEPOSIT_QUERY_RES_MAC  = new String[] { "TransCode", "Amount",
            "TransSeqNo", "TransTime", "TransDate", "TranFlag", "TransInst", "RspCode", "WhdNo",
            "CustNo", "DestinstNo"                     };

    public static final String[] POST_RES_CODE          = new String[] { "00", "01", "03", "06",
            "09", "11", "C3"                           };

}
