
package com.mock.biz.messages.routing.impl;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.messages.routing.RoutingEngine;
import com.mock.biz.messages.routing.RoutingResponse;
import com.mock.biz.messages.routing.SystemRoute;
import com.mock.biz.messages.routing.UserTemplateRoute;
import com.mock.biz.messages.routing.common.RoutingDispatcher;
import com.mock.dal.daointerface.CommunicationDAO;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.ExceptionType;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.shared.exception.code.TransferErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.model.shared.message.enums.MessageRunMode;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 路由引擎的实现
 * 
 * @author hongliang.ma
 * @version $Id: RoutingEngineImpl.java, v 0.1 2012-6-28 下午4:50:34 hongliang.ma Exp $
 */
public class RoutingEngineImpl implements RoutingEngine {

    private static final Logger logger = LoggerFactory.getLogger(RoutingEngineImpl.class);

    private SystemRoute         systemRoute;

    private UserTemplateRoute   userTemplateRoute;

    /** 报文发送 **/
    protected RoutingDispatcher routingDispatcher;

    private CommunicationDAO    communicationDAO;

    private RoutingResponse     routingResponse;

    /** 
     * @see com.mock.biz.messages.routing.RoutingEngine#route(com.mock.core.model.shared.message.MessageDescription, boolean)
     */

    public MessageEnvelope route(MessageDescription description, boolean sendDirect) {
        MessageEnvelope result = null;
        TransferData myTransferData = new TransferData();
        MessageSendResult myResult = null;
        try {
            //开始检查公共路由的ID是否存在
            LoggerUtil.info(logger, "收到请求，开始处理", sendDirect ? "客户端发送报文" : "服务器接收报文");
            String sysTemplateID = description.getSysInnerid();
            AssertUtil.isNotBlank(sysTemplateID, TransferErrorCode.ILLEGAL_PARAMETER);
            LoggerUtil.info(logger, "开始处理的系统sysTemplateID=", sysTemplateID);
            String sendCommunicationID = null;
            //检查报文类型，是直接发送请求还是服务请求报文
            if (!sendDirect) {
                //开始走请求服务报文
                constructDateIn(description, myTransferData);

                //开始接收一次处理,并得到发送的URL地址
                String strSendUrl = processReceive(description.getConmunicaionId(), myTransferData,
                    sendDirect);
                if (StringUtil.isNotEmpty(strSendUrl)) {
                    sendCommunicationID = communicationDAO.selectByUrl(
                        AtsframeStrUtil.substringBefore(strSendUrl, "?"), "CLIENT");
                    AssertUtil.isNotBlank(sendCommunicationID,
                        CommunicationErrorCode.NO_CLIENT_FOUND);

                    String transferId = AtsframeStrUtil.substringAfter(strSendUrl, "transferId=");
                    AssertUtil.isNotBlank(transferId, CommunicationErrorCode.REQUEST_IS_NULL);

                    myTransferData.setObject(DataMapDict.MSGBODY, "transferId=" + transferId);
                    myTransferData.setObject(DataMapDict.ISTRANSFER, "true");
                    myTransferData.setObject(DataMapDict.TRANSFERMSG,
                        myTransferData.getObject(DataMapDict.SERVER_FORWARD_CONTENT));
                }
            } else {
                //直接发送，这个时候，知道了渠道和内部ID还有可能的内部转发码
                sendCommunicationID = description.getConmunicaionId();
                AssertUtil.isNotBlank(sendCommunicationID, CommunicationErrorCode.REQUEST_IS_NULL);
                myTransferData.setObject(DataMapDict.MSGBODY, description.getMessageEnvelope()
                    .getContent());
            }

            if (StringUtil.isNotEmpty(sendCommunicationID)) {

                //发送预处理
                processSendBefor(sendCommunicationID, myTransferData, sendDirect);

                //开始异步发送还是同步发送
                LoggerUtil.info(logger, "开始到相应的发送处理");
                myResult = routingDispatcher.dispatch(myTransferData, false);
            }
            //修改MessageEnvelope
        } catch (AnymockException anymockException) {
            ExceptionUtil.caught(anymockException, "[路由引擎]业务异常.", description);
            //获取异常的类型
            ExceptionType exceptionType = anymockException.getErrorCode().getType();
            switch (exceptionType) {
                case DISPATCH_ERROR: {
                    LoggerUtil.warn(logger, "公共部分处理异常");
                    if (anymockException.getErrorCode() == TransferErrorCode.NO_USERTEMPLATE) {
                        myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT,
                            "CAN'T FIND ONLYUSER");
                    } else {
                        myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT,
                            "COMM_TOOLS_HAND_ERROR");
                    }
                }
                    break;
                case TRANSACTION: {
                    LoggerUtil.warn(logger, "用户部分处理异常");
                    if (anymockException.getErrorCode() != TransactionErrorCode.ASSERT_FAILED) {
                        myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT,
                            "USER_TOOLS_HAND_ERROR");
                    }
                }
                    break;
                case COMMUNICATION: {
                    myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT,
                        "COMMUNICATION_HAND_ERROR");
                }
                    break;
                default: {
                    myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT, "UNKWON_ERROR");
                }
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "[路由引擎]系统异常.", description);
            myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT, "UNKWON_ERROR");
        } finally {
            LoggerUtil.info(logger, "[路由引擎]路由处理完成:");
            // 生成响应
            result = routingResponse.generate(myResult, myTransferData, null, sendDirect);
            LoggerUtil.info(logger, "[路由引擎]返回响应:", result);
        }

        return result;
    }

    /** 
     * @see com.mock.biz.messages.routing.RoutingEngine#sendDirectRoute(java.lang.String, java.lang.String)
     */
    public String sendDirectRoute(String transferId, String MsgSend) {
        MessageSendResult sendResult = null;
        MessageEnvelope realResult = null;
        TransferData myTransferData = null;
        AnymockException exception = null;
        try {
            LoggerUtil.info(logger, "收到异步发送报文", "transferId=", transferId);
            AssertUtil.isNotNull(transferId, SystemErrorCode.ILLEGAL_PARAMETER);

            Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(transferId);
            AssertUtil.isNotNull(usertemplate, SystemErrorCode.ILLEGAL_PARAMETER);

            SystemTemplate systemTemplate = SystemTemplateCache.getSystemTemplateById(usertemplate
                .getSystemId());
            AssertUtil.isNotNull(systemTemplate, SystemErrorCode.ILLEGAL_PARAMETER);

            CommunicationConfig communicationConfig = NetworkConfigCache
                .getCommunicationConfigById(systemTemplate.getUrlId());
            AssertUtil.isNotNull(communicationConfig, SystemErrorCode.ILLEGAL_PARAMETER);

            myTransferData = new TransferData();
            myTransferData.setObject(DataMapDict.SENDCOMID,
                communicationConfig.getCommunicationId());
            if (communicationConfig.isServer()) {
                myTransferData.setObject(DataMapDict.SENDFORMAT,
                    communicationConfig.getRecvMessageFormat());
            } else {
                myTransferData.setObject(DataMapDict.SENDFORMAT,
                    communicationConfig.getSendMessageFormat());
            }

            myTransferData.setObject(DataMapDict.SERVER_FORWARD_CONTENT, MsgSend);

            sendResult = routingDispatcher.dispatch(myTransferData, true);

        } catch (AnymockException ac) {
            ExceptionUtil.caught(ac, "sendMessageDirect失败!");
            exception = ac;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "sendMessageDirect失败!");
            exception = new AnymockException(SystemErrorCode.SYSTEM_ERROR);
        } finally {
            realResult = routingResponse.generate(sendResult, myTransferData, exception, true);
        }

        return realResult.getContent().toString();
    }

    /**
     *  发送预处理，根据URL找到通讯地址
     * @param myTransferData
     * @param strSendUrl
     */
    public void processSendBefor(String sendCommunicationID, TransferData myTransferData,
                                 boolean sendDirect) {
        LoggerUtil.info(logger, "找到的发送通讯ID为", sendCommunicationID);

        //开始转发发送
        String strSysTemplateID = SystemTemplateCache.getSystemTemplate(sendCommunicationID)
            .getSysId();
        AssertUtil.isNotNull(strSysTemplateID, TransferErrorCode.SYSTEMPLATE_NOT_FIND);
        LoggerUtil.info(logger, "找到的发送公共配置，ID为", strSysTemplateID);
        myTransferData.setObject(DataMapDict.SENDCOMID, sendCommunicationID);
        myTransferData.setObject(DataMapDict.SENDSYSID, strSysTemplateID);
        myTransferData.setObject(DataMapDict.SENDFORMAT,
            NetworkConfigCache.getClientConfig(sendCommunicationID).getSendMessageFormat()
                .toString());
        //开始处理公共路由
        LoggerUtil.info(logger, "开始处理发送消息前的处理");
        processReceive(sendCommunicationID, myTransferData, sendDirect);
    }

    /**
     * 设置发送消息属性-----同步
     * @param sendCommunicationID
     * @param myTransferData
     */
    public TransferData setSendPro(String sendCommunicationID, TransferData myTransferData) {
        myTransferData.setObject(DataMapDict.SENDFORMAT, NetworkConfigCache
            .getCommunicationConfigById(sendCommunicationID).getSendMessageFormat().toString());
        myTransferData.setObject("SEND_RESPONSE",
            myTransferData.getObject(DataMapDict.SERVER_FORWARD_CONTENT));
        return myTransferData;
    }

    /**
     * 创建报文收到时的 TransferData
     * @param description
     * @param myTransferData
     */
    private void constructDateIn(MessageDescription description, TransferData myTransferData) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DataMapDict.MSGBODY, description.getMessageEnvelope().getContent());
        properties.put(DataMapDict.MSGFORMAT, description.getMessageEnvelope().getMessageFormat());
        properties
            .put(DataMapDict.EXTRACONTENT, description.getMessageEnvelope().getExtraContent());
        properties.put(DataMapDict.ORIGINCOMID, description.getConmunicaionId());
        properties.put(DataMapDict.ORIGINSYSID, description.getSysInnerid());
        myTransferData.setProperties(properties);
    }

    /**
     * 一次基本的内部处理过程，先处理公共部分，然后再处理私有部分
     * 
     * @param sysTemplateID
     * @param myTransferData
     * @return
     */
    private String processReceive(String urlId, TransferData myTransferData, boolean sendDirect) {
        Usertemplate myUsertemplate = null;

        //判断性能测试或则唯一值模式
        MessageRunMode runMode = NetworkConfigCache.getCommunicationConfigById(urlId)
            .getMsgRunMode();
        if (runMode == MessageRunMode.PERFORM || runMode == MessageRunMode.ONLYONE) {
            LoggerUtil.info(logger, "开始直接走到用户部分");
            String userConfigId = NetworkConfigCache.getCommunicationConfigById(urlId)
                .getReadUserConfigId();
            AssertUtil.isNotBlank(userConfigId, TransferErrorCode.NO_USERTEMPLATE);
            myUsertemplate = UserTemplateCache.getUsertemplateById(userConfigId);
            if (runMode == MessageRunMode.PERFORM) {
                myTransferData.setObject("AnymockIsPERFORM", "TRUE");
            }
        } else {
            //开始处理公共路由
            LoggerUtil.info(logger, "开始处理公共部分");
            myUsertemplate = systemRoute.route(urlId, myTransferData);
        }

        //开始处理个人的路由部分
        LoggerUtil.info(logger, "开始处理用户部分");
        String sendUrl = userTemplateRoute.route(myUsertemplate, myTransferData);

        //设置发送消息属性
        if (!sendDirect) {
            myTransferData = setSendPro(urlId, myTransferData);
        }

        if (StringUtil.isNotBlank(sendUrl)) {
            LoggerUtil.info(logger, "需要转发，转发的地址为", sendUrl);
            return sendUrl;
        }

        return null;
    }

    /**
     * Setter method for property <tt>systemRoute</tt>.
     * 
     * @param systemRoute value to be assigned to property systemRoute
     */
    public void setSystemRoute(SystemRoute systemRoute) {
        this.systemRoute = systemRoute;
    }

    /**
     * Setter method for property <tt>userTemplateRoute</tt>.
     * 
     * @param userTemplateRoute value to be assigned to property userTemplateRoute
     */
    public void setUserTemplateRoute(UserTemplateRoute userTemplateRoute) {
        this.userTemplateRoute = userTemplateRoute;
    }

    /**
     * Setter method for property <tt>routingDispatcher</tt>.
     * 
     * @param routingDispatcher value to be assigned to property routingDispatcher
     */
    public void setRoutingDispatcher(RoutingDispatcher routingDispatcher) {
        this.routingDispatcher = routingDispatcher;
    }

    /**
     * Setter method for property <tt>communicationDAO</tt>.
     * 
     * @param communicationDAO value to be assigned to property communicationDAO
     */
    public void setCommunicationDAO(CommunicationDAO communicationDAO) {
        this.communicationDAO = communicationDAO;
    }

    /**
     * Setter method for property <tt>routingResponse</tt>.
     * 
     * @param routingResponse value to be assigned to property routingResponse
     */
    public void setRoutingResponse(RoutingResponse routingResponse) {
        this.routingResponse = routingResponse;
    }

}
