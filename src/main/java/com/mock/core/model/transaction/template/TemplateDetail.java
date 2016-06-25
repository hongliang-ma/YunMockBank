
package com.mock.core.model.transaction.template;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.transaction.detail.DetailMsg;

/**
 *  最终的用户模板中的一个模板详情
 *  
 * @author hongliang.ma
 * @version $Id: TemplateDetail.java, v 0.1 2012-5-24 下午2:35:40 hongliang.ma Exp $
 */
public final class TemplateDetail implements Comparable<TemplateDetail> {

    private long            id;

    /**内部ID号，用来标示一个统一的用户模板 */
    private String          innerid;

    /** 模板ID的实际内容 */
    private final DetailMsg detailMsg;

    /** 该项的排序 */
    private int             sequence;

    public TemplateDetail(DetailMsg detailMsg) {
        AssertUtil.isNotNull(detailMsg, SystemErrorCode.ILLEGAL_PARAMETER, "detailMsg不允许为空");
        this.detailMsg = detailMsg;
    }

    /**
     * Getter method for property <tt>detailValue</tt>.
     * 
     * @return property value of detailValue
     */
    public DetailMsg getDetailValue() {
        return detailMsg;
    }

    /**
     * Getter method for property <tt>sequence</tt>.
     * 
     * @return property value of sequence
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Setter method for property <tt>sequence</tt>.
     * 
     * @param sequence value to be assigned to property sequence
     */
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * Getter method for property <tt>innerid</tt>.
     * 
     * @return property value of innerid
     */
    public String getInnerid() {
        return innerid;
    }

    /**
     * Setter method for property <tt>innerid</tt>.
     * 
     * @param innerid value to be assigned to property innerid
     */
    public void setInnerid(String innerid) {
        this.innerid = innerid;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(long id) {
        this.id = id;
    }

    /** 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TemplateDetail arg0) {
        AssertUtil.isNotNull(arg0, SystemErrorCode.ILLEGAL_PARAMETER, "组件详情为空，无法排序");
        return sequence - arg0.sequence;
    }
}
