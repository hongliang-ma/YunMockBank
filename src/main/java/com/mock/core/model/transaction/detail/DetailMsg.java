
package com.mock.core.model.transaction.detail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.lang.StringUtil;


/**
 * 用户模板的详情，对应数据库中某一个配置<br/>
*&lt;component classid="XMLParse" cname="测试呢测试测试" sendUrl="false"&gt;<br/>
*  &lt;mapValues&gt;<br/>
*    &lt;entry&gt;<br/>
*      &lt;string&gt;template_error&lt;/string&gt;<br/>
*      &lt;string&gt;56231222&lt;/string&gt;<br/>
*    &lt;/entry&gt;<br/>
*    &lt;entry&gt;<br/>
*      &lt;string&gt;defaultrule&lt;/string&gt;<br/>
*      &lt;string&gt;null&lt;/string&gt;<br/>
*    &lt;/entry&gt;<br/>
*    &lt;entry&gt;<br/>
*      &lt;string&gt;state&lt;/string&gt;<br/>
*      &lt;string&gt;0&lt;/string&gt;<br/>
*    &lt;/entry&gt;<br/>
*  &lt;/mapValues&gt;<br/>
*  &lt;detailElement&gt;<br/>
*    &lt;entry&gt;<br/>
*      &lt;string&gt;template&lt;/string&gt;<br/>
*      &lt;map&gt;<br/>
*       &lt;entry&gt;<br/>
*          &lt;string&gt;PBVReq&lt;/string&gt;<br/>
*          &lt;string&gt;123&lt;/string&gt;<br/>
*        &lt;/entry&gt;<br/>
*        &lt;entry&gt;<br/>
*          &lt;string&gt;BillQryReq&lt;/string&gt;<br/>
*          &lt;string&gt;89955252&lt;/string&gt;<br/>
*        &lt;/entry&gt;<br/>
*      &lt;/map&gt;<br/>
*    &lt;/entry&gt;<br/>
*  &lt;/detailElement&gt;<br/>
* &lt;/component&gt;<br/>
 * 
 * 
 * 
 * @author hongliang.ma
 * @version $Id: Detail.java, v 0.1 2012-5-29 下午1:25:58 hongliang.ma Exp $
 */
public final class DetailMsg implements Serializable {
    /**  */
    private static final long                serialVersionUID = -5398556757324589253L;

    /** 处理该类的方法 */
    private String                           classid;

    /** 数据库中该组件的名字 */
    private String                           cname;

    /** 转发地址 */
    private String                           sendUrl;

    /** 直接的key-value值,主要用来做工具类的参数*/
    private Map<String, String>              keyValues;

    /**  template之类的存在多个key-value的，用来做返回值使用 */
    private Map<String, Map<String, String>> templateMsg;

    /** 注释，主要是给templateMsg的某个Map的key作为注释的 */
    private Map<String, String>              mem;

    /**
     * Getter method for property <tt>keyValues</tt>.
     * 
     * @return property value of keyValues
     */
    public Map<String, String> getKeyValues() {
        return keyValues;
    }

    /**
     * Setter method for property <tt>keyValues</tt>.
     * 
     * @param keyValues value to be assigned to property keyValues
     */
    public void setKeyValues(Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    /**
     * Getter method for property <tt>templateMsg</tt>.
     * 
     * @return property value of templateMsg
     */
    @SuppressWarnings("rawtypes")
    public Map<String, Map<String, String>> getTemplateMsg() {
        if (templateMsg == null || templateMsg.isEmpty()) {
            return null;
        }
        Map<String, String> template = templateMsg.get("template");
        Map<String, String> changeTemplate = new HashMap<String, String>();
        Iterator iter = template.entrySet().iterator();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key.setLength(0);
            value.setLength(0);
            key.append((String) entry.getKey());
            value.append((String) entry.getValue());
            if (StringUtil.contains(key.toString(), "numtag")) {
                changeTemplate.put(AtsframeStrUtil.substringAfter(key.toString(), "numtag"),
                    value.toString());
            } else {
                changeTemplate.put(key.toString(), value.toString());
            }
        }
        templateMsg.put("template", changeTemplate);

        return templateMsg;
    }

    /**
     * Setter method for property <tt>templateMsg</tt>.
     * 
     * @param templateMsg value to be assigned to property templateMsg
     */
    @SuppressWarnings("rawtypes")
    public void setTemplateMsg(Map<String, Map<String, String>> templateMsg) {
        if (templateMsg == null || templateMsg.isEmpty()) {
            return;
        }

        Map<String, String> template = templateMsg.get("template");
        Map<String, String> changeTemplate = new HashMap<String, String>();
        Iterator iter = template.entrySet().iterator();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key.setLength(0);
            value.setLength(0);
            key.append(StringUtil.trim((String) entry.getKey()));
            value.append((String) entry.getValue());
            //取得左边的第一个，如果是数字，这添加numtag
            if (StringUtil.isNumeric(StringUtil.left(key.toString(), 1))) {
                changeTemplate.put("numtag" + key.toString(), value.toString());
            } else {
                changeTemplate.put(key.toString(), value.toString());
            }
        }
        templateMsg.put("template", changeTemplate);
        this.templateMsg = templateMsg;
    }

    /**
     * Getter method for property <tt>classid</tt>.
     * 
     * @return property value of classid
     */
    public String getClassid() {
        return classid;
    }

    /**
     * Setter method for property <tt>classid</tt>.
     * 
     * @param classid value to be assigned to property classid
     */
    public void setClassid(String classid) {
        this.classid = classid;
    }

    /**
     * Getter method for property <tt>cname</tt>.
     * 
     * @return property value of cname
     */
    public String getCname() {
        return cname;
    }

    /**
     * Setter method for property <tt>cname</tt>.
     * 
     * @param cname value to be assigned to property cname
     */
    public void setCname(String cname) {
        this.cname = cname;
    }

    /**
     * Getter method for property <tt>sendUrl</tt>.
     * 
     * @return property value of sendUrl
     */
    public String getSendUrl() {
        return sendUrl;
    }

    /**
     * Setter method for property <tt>sendUrl</tt>.
     * 
     * @param sendUrl value to be assigned to property sendUrl
     */
    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    /**
     * Getter method for property <tt>mem</tt>.
     * 
     * @return property value of mem
     */
    public final Map<String, String> getMem() {
        return mem;
    }

    /**
     * Setter method for property <tt>mem</tt>.
     * 
     * @param mem value to be assigned to property mem
     */
    public final void setMem(Map<String, String> mem) {
        this.mem = mem;
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
