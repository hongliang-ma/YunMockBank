/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2010 All Rights Reserved.
 */
package com.mock.core.model.shared.exception.util;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.mock.common.util.lang.StringUtil;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.ErrorCode;


/**
 *  参数检查断言工具类，可以减少一些<code>if</code>代码逻辑<br>
 * 当断言不成立时，抛出指定错误代码的AnymockException异常
 * 
 * @author hongliang.ma
 * @version $Id: AssertUtil.java, v 0.1 2012-6-20 上午10:55:53 hongliang.ma Exp $
 */
public final class AssertUtil {

    /**
     * 禁用构造函数
     */
    private AssertUtil() {
        // 禁用构造函数
    }

    /**
     * 期待对象为非空，如果检查的对象为<code>null</code>，抛出异常<code>AnymockException</code>
     * @param object
     * @param resutlCode
     * @throws AnymockException
     */
    public static void isNotNull(Object object, ErrorCode resutlCode) throws AnymockException {
        if (object == null) {
            throw new AnymockException(resutlCode);
        }
    }

    /**
     * 期待对象为非空，如果检查的对象为<code>null</code>，抛出异常<code>AnymockException</code>
     * @param object
     * @param resutlCode
     * @param message 异常说明
     * @throws AnymockException
     */
    public static void isNotNull(Object object, ErrorCode resutlCode, String message)
                                                                                     throws AnymockException {
        if (object == null) {
            throw new AnymockException(resutlCode, message);
        }
    }

    /**
     * 期待字符串为非空，如果检查字符串是空白：<code>null</code>、空字符串""或只有空白字符，抛出异常<code>AnymockException</code>
     * 
     * @param text 待检查的字符串
     * @param resutlCode 异常代码
     * @throws AnymockException
     */
    public static void isNotBlank(String text, ErrorCode resutlCode) throws AnymockException {
        if (StringUtil.isBlank(text)) {
            throw new AnymockException(resutlCode);
        }
    }

    /**
     * 期待集合对象为非空，如果检查集合对象是否为null或者空数据，抛出异常<code>AnymockException</code>
     * 
     * @param collection 集合对象
     * @param resutlCode 异常代码
     * @throws AnymockException
     */
    public static void notEmpty(Collection<?> collection, ErrorCode resutlCode)
                                                                               throws AnymockException {
        if (CollectionUtils.isEmpty(collection)) {
            throw new AnymockException(resutlCode);
        }
    }

    /**
     * 期待的正确值为true，如果实际为false，抛出异常<code>AnymockException</code>
     * 
     * @param expression 
     * @param resutlCode 异常代码
     * @throws AnymockException
     */
    public static void isTrue(boolean expression, ErrorCode resutlCode) throws AnymockException {
        if (!expression) {
            throw new AnymockException(resutlCode);
        }
    }

    /**
     * 期待的正确值为false，如果实际为true，抛出异常<code>AnymockException</code>
     * 
     * @param expression 
     * @param resutlCode 异常代码
     * @throws AnymockException
     */
    public static void isFalse(boolean expression, ErrorCode resutlCode) throws AnymockException {
        if (expression) {
            throw new AnymockException(resutlCode);
        }
    }

    /**
     * 期待的正确值为true，如果实际为false，抛出异常<code>AnymockException</code>
     * 
     * @param expression 表达式
     * @param resutlCode 错误代码
     * @param message 异常说明
     * @throws AnymockException
     */
    public static void isTrue(boolean expression, ErrorCode resutlCode, String message)
                                                                                       throws AnymockException {
        if (!expression) {
            throw new AnymockException(resutlCode, message);
        }
    }

    /**
     * 期待的正确值为false，如果实际为true，抛出异常<code>AnymockException</code>
     * 
     * @param expression 表达式
     * @param resutlCode 错误代码
     * @param message 异常说明
     * @throws AnymockException
     */
    public static void isFalse(boolean expression, ErrorCode resutlCode, String message)
                                                                                        throws AnymockException {
        if (expression) {
            throw new AnymockException(resutlCode, message);
        }
    }
}
