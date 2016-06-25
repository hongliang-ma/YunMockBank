/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;

/**
 *  提供各种小工具
 * @author hongliang.ma
 * @version $Id: ApiUtilTool.java, v 0.1 2011-12-15 下午4:49:57 hongliang.ma Exp $
 */
public final class ApiUtilTool {
    private static final Logger logger = LoggerFactory.getLogger(ApiUtilTool.class);

    /**
     *  根据名字获取枚举的对象
     * 
     * @param enumClass  枚举对象
     * @param name    枚举值
     * @return
     */

    @SuppressWarnings("rawtypes")
    public static Object getEnumByName(Class enumClass, String name) {
        for (Object iden : enumClass.getEnumConstants()) {
            if (name.equals(iden.toString())) {
                return iden;
            }
        }
        return null;
    }

    /**
     *  获取本机的服务器名字
     * 
     * @return
     */
    public static String getHostName() {
        InetAddress addr;
        String ip = null;
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostName();//获得本机IP    
        } catch (UnknownHostException e) {
            LoggerUtil.warn(logger, "获取本机地址失败");
        }
        return ip;
    }

    /**
     * 获取本机的IP地址
     * 
     * @return
     */
    public static String getHostIp() {
        InetAddress addr;
        String ip = null;
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();//获得本机IP    
        } catch (UnknownHostException e) {
            LoggerUtil.warn(logger, "获取本机地址失败");
        }
        return ip;
    }

    /**
     * 获取cookie值
     * 
     * @param cookieName
     * @param request
     * @return
     */
    public static String getCookieValue(final String cookieName, final HttpServletRequest request) {
        if (cookieName == null || cookieName.equals("")) {
            return "";
        }

        Cookie[] cookies = request.getCookies();
        int length = 0;

        if (cookies != null && cookies.length > 0) {
            length = cookies.length;
            for (int i = 0; i < length; i++) {
                String cname = cookies[i].getName();
                if (cname != null && cname.equals(cookieName)) {
                    String cValue;
                    try {
                        cValue = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                        return cValue;
                    } catch (UnsupportedEncodingException e) {
                        logger.error("抛出异常错误", e);
                        return "";
                    }
                } else {
                    continue;
                }
            }
            return "";
        } else {
            return "";
        }
    }

}
