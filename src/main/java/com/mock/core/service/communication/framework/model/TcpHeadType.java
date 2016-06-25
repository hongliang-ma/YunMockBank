/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.model;

/**
 * TCP报文头类型
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @author 松雪
 * @version $Id: TcpHeadType.java, v 0.2 2011-11-29 下午2:49:14 hao.zhang Exp $
 */
public enum TcpHeadType {

    /** ASCII头或一般的头**/
    ASCII(4),

    /** BCD头 */
    BCD(2),

    /** HEX_BCD头 */
    HEX_BCD(2);

    /** 报文头长度 */
    private final int length;

    /**
     * 私有构造函数。
     * @param length
     */
    private TcpHeadType(int length) {
        this.length = length;
    }

    /**
     * 
     * @return
     */
    public String getCode() {
        return this.name();
    }

    /**
     * Getter method for property <tt>length</tt>.
     * 
     * @return property value of length
     */
    public int getLength() {
        return length;
    }

}
