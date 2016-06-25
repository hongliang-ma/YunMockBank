/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */

package com.mock.core.model.transaction.detail;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 组件之间数据传输的内容
 * note:TransferData 中无论如何，必须有一个对象，那就是整个消息体，对象MSGBODY
 * DataMapDict 中记录了基本的常用参数，有参数新增时，记录到该文件中
 * 
 * @author hongliang.ma
 * @version $Id: TransferData.java, v 0.1 2012-6-27 下午2:05:54 hongliang.ma Exp $
 */
public final class TransferData {

    //工具处理的对象和返回的就结果
    private Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Getter method for property <tt>properties</tt>.
     * 
     * @return property value of properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getObject(String myString) {
        return properties.get(myString);
    }

    public void setObject(String myString, Object myObject) {
        properties.put(myString, myObject);
        setProperties(properties);
    }

    /**
     * Setter method for property <tt>properties</tt>.
     * 
     * @param properties value to be assigned to property properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
