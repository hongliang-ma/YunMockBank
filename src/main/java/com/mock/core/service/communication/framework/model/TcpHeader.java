/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.model;

import org.apache.commons.lang.math.NumberUtils;

import com.mock.common.util.lang.StringUtil;

/**
 * TCP报头信息详细
 * 
 * @author zhao.xiong
 * @author peng.lanqp
 * @version $Id: TcpHeader.java, v 0.1 2011-11-11 下午04:54:45 zhao.xiong Exp $
 */
public class TcpHeader {

    /** 默认偏移长度 */
    private static final int  DEFAULT_OFFSET = 4;

    /** 报头类型 **/
    private final TcpHeadType headType;

    /** 报头字节偏移量**/
    private final String      offset;

    /** 报头展开后的长度，如果没有经过压缩与offset相等 **/
    private final String      width;

    /** 报头数据子域，用于获取真正的报体内容长度，邮蓄里有 002XXXGG   其中002XXX是报头，002为子域 **/
    private String            substr;

    /**
     * @param headType
     * @param offset
     * @param width
     */
    public TcpHeader(TcpHeadType headType, String offset, String width) {
        this.headType = headType;
        this.offset = offset;
        this.width = width;
    }

    /**
     * 报头字节偏移量
     * @return
     */
    public int getHeaderLength() {
        if (StringUtil.isNotBlank(offset)) {
            return NumberUtils.toInt(offset, 0);
        }

        return headType.getLength();
    }

    /** 
     * 获取头部展开后的长度,用于BCD，HEX_BCD之类,
     * 由2位字节解算法后变为4位，,autobus..，
     * 否则为offset长度，默认为4
     * 
     * @return
     */
    public int getExHeaderLength() {
        if (StringUtil.isNotBlank(width)) {
            return NumberUtils.toInt(width, 0);
        }
        if (StringUtil.isNotBlank(offset)) {
            return NumberUtils.toInt(offset, DEFAULT_OFFSET);
        }
        return DEFAULT_OFFSET;
    }

    /**
     * Getter method for property <tt>headType</tt>.
     * 
     * @return property value of headType
     */
    public TcpHeadType getHeadType() {
        return headType;
    }

    /**
     * Setter method for property <tt>substr</tt>.
     * 
     * @param substr value to be assigned to property substr
     */
    public void setSubstr(String substr) {
        this.substr = substr;
    }

    /**
     * 获取子域的位移起始偏移量
     * @return
     */
    public int[] getSubStartAndEnd() {
        String[] subLenStr = substr.split(",");
        return new int[] { NumberUtils.toInt(subLenStr[0], 0), NumberUtils.toInt(subLenStr[1], 0) };
    }

    /**
     * 是否有子域
     * 
     * @return
     */
    public boolean hasSub() {
        return !StringUtil.isBlank(substr);
    }
}
