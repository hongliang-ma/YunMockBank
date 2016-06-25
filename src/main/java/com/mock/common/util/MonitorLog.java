/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util;

import org.apache.commons.lang.time.StopWatch;

/**
 * 监控日志信息
 * 
 * @author peng.lanqp
 * @author zhao.xiong
 * @version $Id: MonitorLog.java, v 0.1 2011-4-1 下午08:32:10 peng.lanqp Exp $
 */
public class MonitorLog {

    /** 被代理拦截的方法名  */
    private String    interceptorMethod;

    /** 被代理拦截的类   */
    private String    interceptorClass;

    /** 计时器 */
    private StopWatch stopWatch;

    /** 方法执行结果 */
    private boolean   invokeSuccess;

    /** 数据库名称 */
    private String    databaseName;

    /** 系统名称 */
    private String    systemName;

    /** 交易类型 */
    private String    transTypeId;

    /** 渠道系统 */
    private String    channelSystemId;

    /** 订单号 **/
    private String    outOrderNo;

    /** 交易结果**/
    private String    transResult;

    /**
     * 构造函数
     * @param invokeSuccess
     * @param stopWatch
     */
    public MonitorLog(boolean invokeSuccess, StopWatch stopWatch) {
        this.invokeSuccess = invokeSuccess;
        this.stopWatch = stopWatch;
    }

    /**
     * Getter method for property <tt>interceptorMethod</tt>.
     * 
     * @return property value of interceptorMethod
     */
    public String getInterceptorMethod() {
        return interceptorMethod;
    }

    /**
     * Setter method for property <tt>interceptorMethod</tt>.
     * 
     * @param interceptorMethod value to be assigned to property interceptorMethod
     */
    public void setInterceptorMethod(String interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
    }

    /**
     * Getter method for property <tt>interceptorClass</tt>.
     * 
     * @return property value of interceptorClass
     */
    public String getInterceptorClass() {
        return interceptorClass;
    }

    /**
     * Setter method for property <tt>interceptorClass</tt>.
     * 
     * @param interceptorClass value to be assigned to property interceptorClass
     */
    public void setInterceptorClass(String interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    /**
     * Getter method for property <tt>stopWatch</tt>.
     * 
     * @return property value of stopWatch
     */
    public StopWatch getStopWatch() {
        return stopWatch;
    }

    /**
     * Setter method for property <tt>stopWatch</tt>.
     * 
     * @param stopWatch value to be assigned to property stopWatch
     */
    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    /**
     * Getter method for property <tt>invokeSuccess</tt>.
     * 
     * @return property value of invokeSuccess
     */
    public boolean isInvokeSuccess() {
        return invokeSuccess;
    }

    /**
     * Setter method for property <tt>invokeSuccess</tt>.
     * 
     * @param invokeSuccess value to be assigned to property invokeSuccess
     */
    public void setInvokeSuccess(boolean invokeSuccess) {
        this.invokeSuccess = invokeSuccess;
    }

    /**
     * Getter method for property <tt>databaseName</tt>.
     * 
     * @return property value of databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Setter method for property <tt>databaseName</tt>.
     * 
     * @param databaseName value to be assigned to property databaseName
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Getter method for property <tt>systemName</tt>.
     * 
     * @return property value of systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * Setter method for property <tt>systemName</tt>.
     * 
     * @param systemName value to be assigned to property systemName
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * Getter method for property <tt>transTypeId</tt>.
     * 
     * @return property value of transTypeId
     */
    public String getTransTypeId() {
        return transTypeId;
    }

    /**
     * Getter method for property <tt>channelSystemId</tt>.
     * 
     * @return property value of channelSystemId
     */
    public String getChannelSystemId() {
        return channelSystemId;
    }

    /**
     * Setter method for property <tt>transTypeId</tt>.
     * 
     * @param transTypeId value to be assigned to property transTypeId
     */
    public void setTransTypeId(String transTypeId) {
        this.transTypeId = transTypeId;
    }

    /**
     * Setter method for property <tt>channelSystemId</tt>.
     * 
     * @param channelSystemId value to be assigned to property channelSystemId
     */
    public void setChannelSystemId(String channelSystemId) {
        this.channelSystemId = channelSystemId;
    }

    /**
     * Getter method for property <tt>outOrderNo</tt>.
     * 
     * @return property value of outOrderNo
     */
    public String getOutOrderNo() {
        return outOrderNo;
    }

    /**
     * Setter method for property <tt>outOrderNo</tt>.
     * 
     * @param outOrderNo value to be assigned to property outOrderNo
     */
    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    /**
     * Getter method for property <tt>transResult</tt>.
     * 
     * @return property value of transResult
     */
    public String getTransResult() {
        return transResult;
    }

    /**
     * Setter method for property <tt>transResult</tt>.
     * 
     * @param transResult value to be assigned to property transResult
     */
    public void setTransResult(String transResult) {
        this.transResult = transResult;
    }

}
