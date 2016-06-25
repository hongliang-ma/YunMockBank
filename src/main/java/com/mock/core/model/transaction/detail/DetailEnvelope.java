
package com.mock.core.model.transaction.detail;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *    DetailMsg组装成XML的工具
 *    
 * @author hongliang.ma
 * @version $Id: DetailEnvelope.java, v 0.1 2012-6-5 上午10:41:01 hongliang.ma Exp $
 */
public final class DetailEnvelope {

    private final static XStream xstream = new XStream(new DomDriver());

    private static void configBefore() {
        //等待优化，现在的暂时太难看了
        xstream.registerConverter(new MapCustomConverter());
        xstream.alias("component", DetailMsg.class);

        xstream.aliasField("classid", DetailMsg.class, "classid");
        xstream.aliasField("cname", DetailMsg.class, "cname");
        xstream.aliasField("sendUrl", DetailMsg.class, "sendUrl");

        xstream.useAttributeFor(DetailMsg.class, "classid");
        xstream.useAttributeFor(DetailMsg.class, "cname");
        xstream.useAttributeFor(DetailMsg.class, "sendUrl");

    }

    /**
     * 将一个detailMsg设置成XML保存到TemplateDetail的detailMsg
     * 
     * @param detailMsg
     * @return XML保存到TemplateDetail的detailMsg
     */
    public static String formatDetail(DetailMsg detailMsg) {
        configBefore();

        return xstream.toXML(detailMsg);
    }

    /**
     * 从TemplateDetail读到detailMsg
     * 
     * @param strXml
     * @return DetailMsg
     */
    public static DetailMsg getDetailMsg(String strXml) {
        configBefore();

        return (DetailMsg) xstream.fromXML(strXml);
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
