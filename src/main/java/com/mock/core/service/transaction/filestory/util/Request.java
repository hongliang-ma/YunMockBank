package com.mock.core.service.transaction.filestory.util;

import java.util.Date;


/**
 * 卡通请求根对象。
 * 
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public abstract class Request extends Message {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5803729084496690403L;

    /** 报文发送时间*/
    protected Date            msgTime;

    /**
     * @return Returns the msgTime.
     */
    public Date getMsgTime() {
        return msgTime;
    }

    /**
     * @param msgTime The msgTime to set.
     */
    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }
}