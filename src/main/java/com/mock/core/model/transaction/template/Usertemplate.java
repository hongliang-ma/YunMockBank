/**
  * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.mock.core.model.transaction.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.detail.TransferData;


/**
 *  最终的用户模板
 * @author hongliang.ma
 * @version $Id: Usertemplate.java, v 0.1 2012-5-24 下午2:33:43 hongliang.ma Exp $
 */
public final class Usertemplate {

    /** 内部ID号，用来标示一个统一的用户模板 */
    private String               innerid;

    /** 系统模板ID*/
    private String               systemId;

    /*绑定的ID号，用来显示在一个界面*/
    private String               bindInnerID;

    /** 匹配的字符串*/
    private String               matchstr;

    /**是否是默认的值,当设置该值时，如果找不到匹配的字段，则走该配置*/
    private Boolean              isdefault          = false;

    /**  自定义的用户模板名称 */
    private String               templateName;

    /** 模板的使用次数 */
    private int                  userCount;

    /** 用户Id号 */
    private String               userName;

    /**是否是记录日志,当设置该值时，如果为true时，则记录通讯报文日志*/
    private Boolean              isRecordLog        = false;

    /** 所有下一级子元素的集合*/
    private List<TemplateDetail> templateDetail;

    /**  user_template的内容 */
    private final TransferData   configTransferData = new TransferData();

    /**
     * Getter method for property <tt>configTransferData</tt>.
     * 
     * @return property value of configTransferData
     */
    public TransferData getConfigTransferData() {
        return configTransferData;
    }

    /**
     * Setter method for property <tt>configTransferData</tt>.
     * 
     * @param configTransferData value to be assigned to property configTransferData
     */
    public void setConfigTransferData(List<TemplateDetail> templateDetails) {
        AssertUtil.isNotNull(templateDetails, SystemErrorCode.DB_ACCESS_ERROR);
        DetailMsg myDetailMsg = null;
        List<DetailMsg> listDetailMsg = new ArrayList<DetailMsg>();
        Map<String, String> mapKeyValue = null;
        Map<String, String> templateDetailMap;
        for (TemplateDetail templateDetail : templateDetails) {
            //预防各种错误
            if (templateDetail == null || templateDetail.getDetailValue() == null) {
                continue;
            }
            myDetailMsg = templateDetail.getDetailValue();
            mapKeyValue = myDetailMsg.getKeyValues();
            if (mapKeyValue == null) {
                configTransferData.setObject(myDetailMsg.getClassid() + "getdate", "getval");
                listDetailMsg.add(myDetailMsg);
                continue;
            }
            //开始执行正常流程
            for (@SuppressWarnings("rawtypes")
            Map.Entry entry : mapKeyValue.entrySet()) {
                configTransferData.setObject(myDetailMsg.getClassid() + entry.getKey(),
                    entry.getValue());
            }
            if (null != myDetailMsg.getTemplateMsg() && !myDetailMsg.getTemplateMsg().isEmpty()) {
                templateDetailMap = myDetailMsg.getTemplateMsg().get("template");
                for (@SuppressWarnings("rawtypes")
                Map.Entry entry : templateDetailMap.entrySet()) {
                    if (null == entry) {
                        continue;
                    }
                    configTransferData.setObject(myDetailMsg.getClassid() + entry.getKey(),
                        entry.getValue());

                }
            }
            listDetailMsg.add(myDetailMsg);
        }

        configTransferData.setObject(DataMapDict.USER_TEMPLATE, listDetailMsg);
        configTransferData.setObject(DataMapDict.SELETCTTAG, "false");
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
     * Getter method for property <tt>systemId</tt>.
     * 
     * @return property value of systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Setter method for property <tt>systemId</tt>.
     * 
     * @param systemId value to be assigned to property systemId
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * Getter method for property <tt>matchstr</tt>.
     * 
     * @return property value of matchstr
     */
    public String getMatchstr() {
        return matchstr;
    }

    /**
     * Setter method for property <tt>matchstr</tt>.
     * 
     * @param matchstr value to be assigned to property matchstr
     */
    public void setMatchstr(String matchstr) {
        this.matchstr = matchstr;
    }

    /**
     * Getter method for property <tt>isRecordLog</tt>.
     * 
     * @return property value of isRecordLog
     */
    public Boolean getIsRecordLog() {
        return isRecordLog;
    }

    /**
     * Setter method for property <tt>isRecordLog</tt>.
     * 
     * @param isRecordLog value to be assigned to property isRecordLog
     */
    public void setIsRecordLog(String isRecordLog) {
        this.isRecordLog = BooleanUtils.toBoolean(isRecordLog);
    }

    /**
     * Getter method for property <tt>isdefault</tt>.
     * 
     * @return property value of isdefault
     */
    public Boolean getIsdefault() {
        return isdefault;
    }

    /**
     * Setter method for property <tt>isdefault</tt>.
     * 
     * @param isdefault value to be assigned to property isdefault
     */
    public void setIsdefault(String isdefault) {
        this.isdefault = BooleanUtils.toBoolean(isdefault);
    }

    /**
     * Getter method for property <tt>templateName</tt>.
     * 
     * @return property value of templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Setter method for prope   rty <tt>templateName</tt>.
     * 
     * @param templateName value to be assigned to property templateName
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Getter method for property <tt>userCount</tt>.
     * 
     * @return property value of userCount
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Setter method for property <tt>userCount</tt>.
     * 
     * @param userCount value to be assigned to property userCount
     */
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    /**
     * Getter method for property <tt>userName</tt>.
     * 
     * @return property value of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter method for property <tt>userName</tt>.
     * 
     * @param userName value to be assigned to property userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter method for property <tt>templateDetail</tt>.
     * 
     * @return property value of templateDetail
     */
    public List<TemplateDetail> getTemplateDetail() {
        return templateDetail;
    }

    /**
     * Setter method for property <tt>templateDetail</tt>.
     * 
     * @param templateDetail value to be assigned to property templateDetail
     */
    public void setTemplateDetail(List<TemplateDetail> templateDetail) {
        this.templateDetail = templateDetail;
    }

    /**
     * Getter method for property <tt>bindInnerID</tt>.
     * 
     * @return property value of bindInnerID
     */
    public String getBindInnerID() {
        return bindInnerID;
    }

    /**
     * Setter method for property <tt>bindInnerID</tt>.
     * 
     * @param bindInnerID value to be assigned to property bindInnerID
     */
    public void setBindInnerID(String bindInnerID) {
        this.bindInnerID = bindInnerID;
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
