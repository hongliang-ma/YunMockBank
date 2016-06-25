/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.communication.framework.util.tcp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.alibaba.common.lang.StringUtil;

/**
 * 连接管理器，主要做连接管理，包括最小load查询
 * 
 * @author hongliang.ma
 * @version $Id: ConnectionManager.java, v 0.1 2012-6-25 下午3:33:03 hongliang.ma Exp $
 */
public class ConnectionManager {

    /** 连接管理map key=connection的key   **/
    private final Set<String>          connSet        = Collections
                                                          .synchronizedSet(new HashSet<String>());
    /** 连接队列  **/
    private final List<SockConnection> connectionList = Collections
                                                          .synchronizedList(new ArrayList<SockConnection>());

    /**
     * 查询最小load连接
     * @return
     */
    SockConnection searchConnection() {
        if (CollectionUtils.isEmpty(connectionList)) {
            return null;
        }
        synchronized (connectionList) {
            Collections.sort(connectionList);
            return connectionList.get(0);
        }
    }

    /**
     * 添加连接
     * @param conn
     */
    void addConnection(SockConnection conn) {
        if (connSet.contains(conn.getKey())) {
            return;
        }
        synchronized (connSet) {
            connSet.add(conn.getKey());
            connectionList.add(conn);
        }
    }

    /**
     * 移除连接
     * @param conn
     */
    void removeConnection(SockConnection conn) {
        if (conn == null) {
            return;
        }

        synchronized (connSet) {
            connSet.remove(conn.getKey());
            for (Iterator<SockConnection> iter = connectionList.iterator(); iter.hasNext();) {
                if (StringUtil.equals(conn.getKey(), iter.next().getKey())) {
                    conn.dispose();
                    iter.remove();
                    break;
                }
            }
        }
    }

    /**
     * 移除所有连接
     */
    void removeAllConnection() {
        for (Iterator<SockConnection> iter = connectionList.iterator(); iter.hasNext();) {
            SockConnection conn = iter.next();
            conn.dispose();
        }
        connectionList.clear();
        connSet.clear();
    }

    /**
     * 获取管理连接数量
     * 
     * @return
     */
    int getConnectionSize() {
        synchronized (connSet) {
            return connSet.size();
        }
    }
}
