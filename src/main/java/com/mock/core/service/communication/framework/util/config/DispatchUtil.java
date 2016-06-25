
package com.mock.core.service.communication.framework.util.config;

/**
 * 通讯分流工具类
 * 
 * @author 松雪
 * @version $Id: DispatchUtil.java, v 0.1 2012-6-11 上午10:38:55 hao.zhang Exp $
 */
public final class DispatchUtil {

    /** 分流uri上下文**/
    private static final ThreadLocal<String> localURI = new ThreadLocal<String>();

    /**
     * 禁用构造函数
     */
    private DispatchUtil() {
        // 禁用构造函数
    }

    public static void setURI(String uri) {
        localURI.set(uri);
    }

    public static String getURI() {
        return localURI.get();
    }

    public static void cleanURI() {
        localURI.remove();
    }
}
