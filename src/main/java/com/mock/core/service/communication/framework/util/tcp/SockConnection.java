/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.mock.common.util.ExceptionUtil;

/**
 *  TCP长连接管理
 * 
 * @author hongliang.ma
 * @version $Id: SockConnection.java, v 0.1 2012-6-25 下午3:16:04 hongliang.ma Exp $
 */
public class SockConnection implements Comparable<SockConnection> {

    private Socket              sock;

    /** 保证写的网络顺序，写锁 **/
    private final ReentrantLock writeLock = new ReentrantLock();

    /** 保证读的网络顺序，读锁 **/
    private final ReentrantLock readLock  = new ReentrantLock();

    /** 连接上的写入 load **/
    private final AtomicInteger load      = new AtomicInteger();

    /**
     * @param sock
     */
    public SockConnection(Socket sock) {
        this.sock = sock;
    }

    public Socket getSock() {
        return sock;
    }

    /**
     * Setter method for property <tt>sock</tt>.
     * 
     * @param sock value to be assigned to property sock
     */
    public void setSock(Socket sock) {
        this.sock = sock;
    }

    /**
     * 获取连接里面的通讯load值,这里面的load只记录写入的即可
     * 
     * @return
     */
    public int getLoadValue() {
        return load.get();
    }

    /**
     * 开始写入，保证写的网络包顺序
     */
    public void startWrite() {
        load.getAndIncrement();
        writeLock.lock();
    }

    /**
     * 释放写资源
     */
    public void releaseWrite() {
        writeLock.unlock();
        load.getAndDecrement();
    }

    /**
     * 开始读取
     */
    public void startRead() {
        readLock.lock();
    }

    public void releaseRead() {
        readLock.unlock();
    }

    public void clearLoadValue() {
        load.set(0);
    }

    /** 
     * 关闭连接 
     **/
    public void dispose() {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
                // 关闭异常不处理
                ExceptionUtil.caught(e, "TCP长连接关闭异常");
            }
        }
    }

    /**
     * 获取连接标识
     * 
     * @return
     */
    public String getKey() {
        return sock.getLocalAddress() + ":" + sock.getLocalPort() + ":"
               + sock.getRemoteSocketAddress();
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getKey() + ",load=" + getLoadValue();
    }

    /** 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(SockConnection o) {
        return this.getLoadValue() - o.getLoadValue();
    }

}
