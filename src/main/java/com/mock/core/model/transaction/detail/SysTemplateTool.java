
package com.mock.core.model.transaction.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;

import com.mock.common.util.lang.StringUtil;

/**
 * 系统模板转化工具
 * @author hongliang.ma
 * @version $Id: SysTemplateTool.java, v 0.1 2012-11-7 下午5:09:21 hongliang.ma Exp $
 */
public final class SysTemplateTool {
    /** 配置分隔符 */
    private static final Pattern CONFIG_SEPARATOR = Pattern.compile("&&");
    private static final Pattern KEYSEPARAT       = Pattern.compile("\\(\\(|\\)\\)");
    private static final Pattern PARAVALUE        = Pattern.compile("=");

    /**
     * 根据字符串生对象
     * 
     * @param properties
     */
    public static List<Map<String, Map<String, Object>>> getSysTemplateList(String properties) {
        if (StringUtil.isEmpty(properties)) {
            return null;
        }

        String[] configs = CONFIG_SEPARATOR.split(properties);
        List<Map<String, Map<String, Object>>> listSysteMaps = new ArrayList<Map<String, Map<String, Object>>>();
        for (String config : configs) {
            listSysteMaps.add(spliteTpMap(config));
        }

        return listSysteMaps;
    }

    /**
     * 修改系统模板的值
     * 
     * @param properties  需要修改的模板内容
     * @param clsTochange  修改的类
     * @param keyToChange   修改的key 
     * @param ValueToChange  修改的value
     * @return
     */

    public static String changeValue(String properties, String clsTochange,
                                     ArrayList<String> keyToChange, ArrayList<Object> ValueToChange) {
        List<Map<String, Map<String, Object>>> sysTemplateReturn = getSysTemplateList(properties);
        Boolean bChange = false;
        int iCount = 0;
        for (Map<String, Map<String, Object>> map : sysTemplateReturn) {
            for (Map.Entry<String, Map<String, Object>> handTool : map.entrySet()) {
                if (StringUtil.equalsIgnoreCase(clsTochange, handTool.getKey())) {
                    for (String key : keyToChange) {
                        handTool.getValue().put(key, ValueToChange.get(iCount));
                        iCount++;
                    }

                    bChange = true;
                    break;
                }
            }
            if (bChange) {
                break;
            }
        }

        return getSystemTempalte(sysTemplateReturn);
    }

    /**
     * 根据对象转成键值对
     * 
     * @param listSysteMaps
     * @return
     */
    public static String getSystemTempalte(List<Map<String, Map<String, Object>>> listSysteMaps) {
        if (CollectionUtils.size(listSysteMaps) == 0) {
            return null;
        }

        StringBuffer sbfSystemTempalte = new StringBuffer();

        for (Map<String, Map<String, Object>> map : listSysteMaps) {
            for (Map.Entry<String, Map<String, Object>> handTool : map.entrySet()) {
                sbfSystemTempalte.append(handTool.getKey()).append("((");
                for (Map.Entry<String, Object> toolParas : handTool.getValue().entrySet()) {
                    sbfSystemTempalte.append(toolParas.getKey()).append("=")
                        .append(toolParas.getValue()).append(",");
                }
                sbfSystemTempalte.append("))&&");
            }
        }

        return StringUtil
            .replace(StringUtil.trimEnd(sbfSystemTempalte.toString(), "&&"), ",)", ")");
    }

    /**
     * 切割配置成为Map
     * 
     * @param config
     * @return 
     */
    private static Map<String, Map<String, Object>> spliteTpMap(String config) {
        String[] elements = KEYSEPARAT.split(config);
        String[] listPara = PARAVALUE.split(elements[1].replaceAll(",", "="));
        Map<String, Object> keyValue = new HashMap<String, Object>();
        StringBuilder val = new StringBuilder();
        for (int i = 0, ilength = (listPara.length) >> 1; i < ilength; i++) {
            if (StringUtil.contains((elements[0].trim() + listPara[i * 2].trim()), "j8583")) {
                val.setLength(0);
                val.append(XmlUtil.decodeString(listPara[i * 2 + 1].trim().replaceAll("-equal-",
                    "=")));

                keyValue.put(listPara[i * 2].trim(),
                    StringUtil.replace(val.toString(), "&uot;", "\""));
                continue;
            }
            keyValue.put(listPara[i * 2].trim(), listPara[i * 2 + 1].trim());
        }
        Map<String, Map<String, Object>> mapClassPara = new HashMap<String, Map<String, Object>>();
        mapClassPara.put(elements[0].trim(), keyValue);
        return mapClassPara;
    }

}
