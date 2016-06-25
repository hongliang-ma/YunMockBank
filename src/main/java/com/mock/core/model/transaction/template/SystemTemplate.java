
package com.mock.core.model.transaction.template;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.model.transaction.detail.SysTemplateConfig;
import com.mock.core.model.transaction.detail.TransferData;

/**
 *  系统模板
 * @author hongliang.ma
 * @version $Id: SystemTemplate.java, v 0.1 2012-6-19 上午11:32:15 hongliang.ma Exp $
 */
public final class SystemTemplate {

    /**  系统模板ID */
    private String                  sysId;

    /** 通讯地址的Id */
    private String                  urlId;

    /** 匹配方式描述的Id */
    private String                  macthdescription;
    /**
     * 系统模板内容，内容样子AssertAdaptor(aaaa)&AssertAdaptor(aaa)        db_column: SYS_TEMPLATE 
     */
    private String                  sysTemplate;

    /** 系统模板的内容,实现上是设置TransferData */
    private final SysTemplateConfig properties = new SysTemplateConfig();

    /**
     * Getter method for property <tt>sysId</tt>.
     * 
     * @return property value of sysId
     */
    public String getSysId() {
        return sysId;
    }

    /**
     * Setter method for property <tt>sysId</tt>.
     * 
     * @param sysId value to be assigned to property sysId
     */
    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    /**
     * Getter method for property <tt>macthdescription</tt>.
     * 
     * @return property value of macthdescription
     */
    public String getMacthdescription() {
        return macthdescription;
    }

    /**
     * Setter method for property <tt>macthdescription</tt>.
     * 
     * @param macthdescription value to be assigned to property macthdescription
     */
    public void setMacthdescription(String macthdescription) {
        this.macthdescription = macthdescription;
    }

    /**
     * Getter method for property <tt>urlId</tt>.
     * 
     * @return property value of urlId
     */
    public String getUrlId() {
        return urlId;
    }

    /**
     * Setter method for property <tt>urlId</tt>.
     * 
     * @param urlId value to be assigned to property urlId
     */
    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    /**
     * Getter method for property <tt>properties</tt>.
     * 
     * @return property value of properties
     */
    public TransferData getProperties() {
        return this.properties.getConfigTransferData();
    }

    /**
     * Setter method for property <tt>properties</tt>.
     * 
     * @param properties value to be assigned to property properties
     */
    public void setProperties(String properties) {
        this.properties.setConfigTransferData(properties);
    }

    /**
     * Getter method for property <tt>sysTemplate</tt>.
     * 
     * @return property value of sysTemplate
     */
    public String getSysTemplate() {
        return sysTemplate;
    }

    /**
     * Setter method for property <tt>sysTemplate</tt>.
     * 
     * @param sysTemplate value to be assigned to property sysTemplate
     */
    public void setSysTemplate(String sysTemplate) {
        this.sysTemplate = sysTemplate;
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
