/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.model.transaction.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.common.util.lang.StringUtil;

/**
  * 属性配置，为区别各种协议，加入动态扩展。属性配置格式如下：<br>
 * aaa(123,123)<br>
 *  aaa(323,13333)<br>
 * 属性之间使用&&作为分隔符 
 * @author hongliang.ma
 * @version $Id: PropertiesConfig.java, v 0.1 2012-6-19 下午7:24:52 hongliang.ma Exp $
 */
public final class SysTemplateConfig {

    /** 配置分隔符 */
    private static final Pattern CONFIG_SEPARATOR   = Pattern.compile("&&");
    private static final Pattern KEYSEPARAT         = Pattern.compile("\\(\\(|\\)\\)");
    private static final Pattern PARAVALUE          = Pattern.compile("=");

    private final TransferData   configTransferData = new TransferData();

    Map<String, Object>          keyValue           = new HashMap<String, Object>();

    /**
     * 设置协议的属性字符串 aaa(123,123)&&aaa(323,13333
     * 
     * @param properties
     */
    public void setConfigTransferData(String properties) {
        if (StringUtil.isEmpty(properties)) {
            return;
        }

        String[] configs = CONFIG_SEPARATOR.split(properties);
        List<String> listConifg = new ArrayList<String>();
        for (String config : configs) {
            listConifg.add(spliteConfig(config));
        }
        configTransferData.setObject(DataMapDict.SYS_TEMPLATE, listConifg);
        configTransferData.setObject(DataMapDict.SELETCTTAG, "true");
    }

    /**
     * 切割配置成为key-value
     * 
     * @param config
     * @return 
     */
    private String spliteConfig(String config) {
        if (StringUtil.contains(config, "(")) {
            String[] elements = KEYSEPARAT.split(config);
            String[] listPara = PARAVALUE.split(elements[1].replaceAll(",", "="));
            StringBuilder val = new StringBuilder();
            for (int i = 0, ilength = (listPara.length) >> 1; i < ilength; i++) {
                if (StringUtil.contains((elements[0].trim() + listPara[i * 2].trim()), "j8583")) {
                    val.setLength(0);
                    val.append(XmlUtil.decodeString(listPara[i * 2 + 1].trim().replaceAll(
                        "-equal-", "=")));

                    keyValue.put(elements[0].trim() + listPara[i * 2].trim(),
                        StringUtil.replace(val.toString(), "&uot;", "\""));
                    continue;
                }
                keyValue.put(elements[0].trim() + listPara[i * 2].trim(),
                    listPara[i * 2 + 1].trim());
            }
            configTransferData.setProperties(keyValue);

            return elements[0].trim();
        } else {
            return config;
        }
    }

    /**
     * Getter method for property <tt>configTransferData</tt>.
     * 
     * @return property value of configTransferData
     */
    public TransferData getConfigTransferData() {
        return configTransferData;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return configTransferData.toString();
    }
}
