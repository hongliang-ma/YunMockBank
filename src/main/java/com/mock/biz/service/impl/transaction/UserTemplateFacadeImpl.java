
package com.mock.biz.service.impl.transaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.messages.routing.RoutingEngine;
import com.mock.biz.shared.OperationEdit;
import com.mock.biz.shared.domain.ChangeType;
import com.mock.common.service.facade.UserTemplateFacade;
import com.mock.common.service.facade.common.J8583Template;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.service.shared.communication.MessageReceiver;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.TemplateDetail;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: UserTemplateFacadeImpl.java, v 0.1 2012-7-3 下午6:49:02 hongliang.ma Exp $
 */
public class UserTemplateFacadeImpl implements UserTemplateFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserTemplateFacadeImpl.class);

    private MessageReceiver     messageReceiver;

    private OperationEdit       operationEdit;

    private RoutingEngine       routingEngine;

    /** 
     * @return 
     * @see com.mock.common.service.facade.UserTemplateFsssacade#sendAsyncMessage(java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendAsyncMessage(String transferId, String transCodeRule) {
        //先去获取一下，该渠道是否存在
        MessageEnvelope returnMsg = null;
        try {
            LoggerUtil.info(logger, "收到异步发送报文", "transferId=", transferId);
            AssertUtil.isNotNull(transferId, SystemErrorCode.ILLEGAL_PARAMETER);

            Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(transferId);
            AssertUtil.isNotNull(usertemplate, SystemErrorCode.ILLEGAL_PARAMETER);

            String sysInnerid = usertemplate.getSystemId();
            AssertUtil.isNotNull(sysInnerid, SystemErrorCode.ILLEGAL_PARAMETER);

            SystemTemplate mySystemTemplate = SystemTemplateCache.getSystemTemplateById(sysInnerid);
            AssertUtil.isNotNull(mySystemTemplate, SystemErrorCode.DB_ACCESS_ERROR);

            MessageEnvelope myMessageEnvelope = new MessageEnvelope(MessageFormat.TEXT,
                "sysInnerid=" + sysInnerid + "&transferId=" + transferId + "&transCodeRule="
                        + transCodeRule);
            MessageDescription description = new MessageDescription(mySystemTemplate.getUrlId(),
                myMessageEnvelope);
            description.setSysInnerid(sysInnerid);
            description.setTransferId(transferId);
            description.setTransCodeRule(transCodeRule);

            //接收消息处理流程
            returnMsg = messageReceiver.receive(description, true);
        } catch (AnymockException e) {
            returnMsg = new MessageEnvelope(MessageFormat.TEXT, "发生错误");
        }
        return returnMsg.getContent().toString();

    }

    /** 
     * @see com.mock.common.service.facade.UserTemplateFacade#sendMessageDirect(java.lang.String, java.lang.String)
     */
    public String sendMessageDirect(String transferId, String MsgSend) {
        return routingEngine.sendDirectRoute(transferId, MsgSend);
    }

    /** 
     * @see com.mock.common.service.facade.UserTemplateFacade#setDefault(java.lang.String, java.lang.Boolean)
     */
    public String setDefault(String transferId, Boolean isDefault) {
        return operationEdit.setDefault("qijunlichong", transferId,
            BooleanUtils.toBooleanObject(isDefault));
    }

    /** 
     * @see com.mock.common.service.facade.UserTemplateFacade#changeReturnMessage(java.lang.Long, java.util.Map)
     */
    public Boolean changeReturnMessage(String transferId, Map<String, String> keyValues) {
        Boolean bChanged = false;
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(transferId);
        if (null != usertemplate) {
            List<TemplateDetail> listDetails = usertemplate.getTemplateDetail();
            for (TemplateDetail templateDetail : listDetails) {
                if (null != templateDetail
                    && (StringUtil.equalsIgnoreCase(templateDetail.getDetailValue().getClassid(),
                        "XMLParse")
                        || StringUtil.equalsIgnoreCase(
                            templateDetail.getDetailValue().getClassid(), "SerialParse") || StringUtil
                            .equalsIgnoreCase(templateDetail.getDetailValue().getClassid(),
                                "SerialParse"))) {

                    return operationEdit.changeToolsValue(ChangeType.TEMPLATEVALUE,
                        templateDetail.getId(), keyValues);
                }
            }
        }

        return bChanged;
    }

    /** 
     * @see com.mock.common.service.facade.UserTemplateFacade#change8583Message(java.lang.Long, java.util.Map)
     */
    public Boolean change8583Message(String transferId, final List<J8583Template> change8593Return) {
        Boolean bChanged = false;
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(transferId);
        if (null != usertemplate) {
            List<TemplateDetail> listDetails = usertemplate.getTemplateDetail();

            StringBuilder sbfNewTemplate = new StringBuilder();
            Map<String, String> mapNewTemplate = new HashMap<String, String>();
            Map<String, String> mapFileValue = null;
            String strKey = null;
            for (TemplateDetail templateDetail : listDetails) {
                if (null != templateDetail
                    && StringUtil.equalsIgnoreCase(templateDetail.getDetailValue().getClassid(),
                        "MessageParser")) {
                    for (J8583Template j8583Template : change8593Return) {
                        if (null != j8583Template) {
                            sbfNewTemplate.setLength(0);
                            sbfNewTemplate.append("template_").append(j8583Template.getMsgtypeid())
                                .append("_");
                            mapFileValue = j8583Template.getField();
                            for (@SuppressWarnings("rawtypes")
                            Iterator ite = mapFileValue.entrySet().iterator(); ite.hasNext();) {
                                @SuppressWarnings("rawtypes")
                                Map.Entry entry = (Map.Entry) ite.next();
                                strKey = sbfNewTemplate.toString() + entry.getKey();
                                mapNewTemplate.put(strKey, (String) entry.getValue());
                            }
                        }
                    }
                    if (CollectionUtils.isEmpty(mapNewTemplate)) {
                        return false;
                    }
                    return operationEdit.edit8583FValue(templateDetail.getId(), mapNewTemplate);
                }
            }
        }

        return bChanged;
    }

    /**
     * Setter method for property <tt>messageReceiver</tt>.
     * 
     * @param messageReceiver value to be assigned to property messageReceiver
     */
    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    /**
     * Setter method for property <tt>operationEdit</tt>.
     * 
     * @param operationEdit value to be assigned to property operationEdit
     */
    public void setOperationEdit(OperationEdit operationEdit) {
        this.operationEdit = operationEdit;
    }

    /**
     * Setter method for property <tt>routingEngine</tt>.
     * 
     * @param routingEngine value to be assigned to property routingEngine
     */
    public final void setRoutingEngine(RoutingEngine routingEngine) {
        this.routingEngine = routingEngine;
    }

    /** 
     * @see com.mock.common.service.facade.UserTemplateFacade#changeDelay(java.lang.Long, java.lang.String)
     */
    public Boolean changeDelay(String transferId, String delayTime) {
        Boolean bChanged = false;
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(transferId);
        if (null != usertemplate) {
            List<TemplateDetail> listDetails = usertemplate.getTemplateDetail();
            for (TemplateDetail templateDetail : listDetails) {
                if (null != templateDetail
                    && StringUtil.equalsIgnoreCase(templateDetail.getDetailValue().getClassid(),
                        "DelayAction")) {
                    Map<String, String> delayTimeMap = new HashMap<String, String>();
                    delayTimeMap.put("delay", delayTime);
                    return operationEdit.changeToolsValue(ChangeType.NORMALVALUE,
                        templateDetail.getId(), delayTimeMap);
                }
            }
        }

        return bChanged;
    }

}
