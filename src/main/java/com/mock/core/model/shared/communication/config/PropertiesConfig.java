/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.shared.communication.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.mock.core.model.shared.enums.PropertiesConfigEnum;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

/**
  * 属性配置，为区别各种协议，加入动态扩展。属性配置格式如下：<br>
 * k1=v1<br>
 * k2=v2<br>
 * 属性之间使用\n或者;作为分隔符 
 * @author hongliang.ma
 * @version $Id: PropertiesConfig.java, v 0.1 2012-6-19 下午7:24:52 hongliang.ma Exp $
 */
public class PropertiesConfig {

    /** 配置分隔符 */
    private static final Pattern                    CONFIG_SEPARATOR = Pattern.compile("\r*\n+|;");

    /** 值/对分隔符 */
    private static final Pattern                    VALUE_SEPARATOR  = Pattern.compile("=");

    /** 参数map */
    private final Map<PropertiesConfigEnum, String> configMap        = new EnumMap<PropertiesConfigEnum, String>(
                                                                         PropertiesConfigEnum.class);

    /**
     * 根据属性名获取值
     * 
     * @param propertyName
     * @return
     */
    public String getPropertyByName(PropertiesConfigEnum propertyName) {
        return configMap.get(propertyName);
    }

    /**
     * 设置协议的属性字符串  k1=v1\nk2=v2
     * 
     * @param properties
     */
    public void setProperties(String properties) {
        configMap.clear();
        if (StringUtil.isEmpty(properties)) {
            return;
        }

        String[] configs = CONFIG_SEPARATOR.split(properties);
        for (String config : configs) {
            String[] entry = VALUE_SEPARATOR.split(config);
            PropertiesConfigEnum key = null;
            try {
                key = PropertiesConfigEnum.valueOf(StringUtil.trim(entry[0]));
            } catch (RuntimeException e) {
                ExceptionUtil.caught(e, "记录中含有未定义的枚举值：", StringUtil.trim(entry[0]));
                // 不抛出异常，只是不处理脏数据
                continue;
            }

            configMap.put(key, StringUtil.trim(entry[1]));
        }
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return configMap.toString();
    }
}
