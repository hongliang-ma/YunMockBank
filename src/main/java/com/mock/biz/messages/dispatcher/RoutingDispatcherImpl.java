
package com.mock.biz.messages.dispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.BooleanUtils;

import com.alibaba.common.lang.diagnostic.Profiler;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.messages.routing.common.RoutingDispatcher;
import com.mock.core.model.shared.common.Constant;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.MessageType;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.model.shared.message.enums.MessageFormat;
import com.mock.core.service.shared.communication.MessageSender;
import com.mock.core.service.shared.thread.ControllerContext;
import com.mock.core.service.shared.thread.ThreadPoolService;
import com.mock.core.service.shared.thread.ThreadPoolService.PoolType;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 *  消息分发接口实现
 * @author hongliang.ma
 * @version $Id: RoutingDispatcherImpl.java, v 0.1 2012-7-2 下午4:11:57 hongliang.ma Exp $
 */
public class RoutingDispatcherImpl implements RoutingDispatcher {

    /** logger */
    protected static final Logger     logger     = LoggerFactory
                                                     .getLogger(RoutingDispatcherImpl.class);

    /** 报文发送 **/
    private MessageSender             messageSender;

    /** 长连接全双工消息变量 Object为获取到的通讯内容**/
    private final Map<String, Object> messageMap = new ConcurrentHashMap<String, Object>();

    /** 
     * @see com.mock.biz.messages.routing.common.RoutingDispatcher#dispatch(com.mock.model.detail.TransferData, boolean)
     */
    public MessageSendResult dispatch(TransferData myTransferData, boolean asyncReceiver) {

        if (!asyncReceiver) {
            //直接发送,包含发送请求和服务请求转发送
            return sendDirect(myTransferData);
        } else {
            //接收后，异步发送，启动新进程发送消息
            runTask(myTransferData);
        }
        return null;
    }

    /**
     * 客户端异步的方式，但不是异步回调的话，全部采用新线程处理交易
     * 
     * @param transaction
     */
    private void runTask(final TransferData transaction) {

        ThreadPoolService.addTask(PoolType.ASYN_TRANSACTION, new Runnable() {

            /** 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                try {
                    //异步
                    LoggerUtil.info(logger, "异步发送暂时不实现");
                    //  sendTransaction(transaction);
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "异步发送处理异常:", transaction);
                }
            }
        });

    }

    @SuppressWarnings("unused")
    private void sendTransaction(TransferData transaction) {
        LoggerUtil.info(logger, "异步发送开始执行:", transaction);

        LoggerUtil.info(logger, "异步发送执行完成:", transaction);

    }

    /**
     * 直接发送，包含了接收后返回、直接发送
     * 
     * @param myTransferData
     * @return 
     */
    private MessageSendResult sendDirect(TransferData myTransferData) {
        MessageSendResult result = null;
        //直接发送
        try {
            String msgKey = beforeSend(myTransferData);
            //组装报文
            MessageEnvelope requestMessage = assembleMessage(myTransferData);
            //发送报文
            result = messageSender.send((String) myTransferData.getObject(DataMapDict.SENDCOMID),
                requestMessage);

            LoggerUtil.info(logger, "服务端响应:", result);

            if (result.isResponse()) {
                afterSend(myTransferData, msgKey);
            }

        } finally {
            LoggerUtil.info(logger, "sendDirect执行完成:");

            //清除threadlocal
            ControllerContext.clear();
        }

        return result;
    }

    /**
     *  是否是长连接
     * 
     * @return
     */
    private boolean isKeepAlive() {
        return BooleanUtils.toBoolean(ControllerContext
            .getAttributeByName(ControllerContext.KEEP_ALIVE));
    }

    /**
     * 发送流水前处理，只针对长连接
     * 
     * @param transaction
     * @return
     */
    private String beforeSend(TransferData myTransferData) {
        if (isKeepAlive()) {
            String msgKey = genCacheKey(myTransferData, false);
            messageMap.put(msgKey, null);
            return msgKey;
        }
        return null;
    }

    /**
     * 勾兑流水，处理消息返回,只针对长连接
     * 
     * @param transaction
     * @param msgKey
     */
    private void afterSend(TransferData myTransferData, String msgKey) {
        if (isKeepAlive() && msgKey != null) {
            //1.存储接收的消息
            String recvMsgKey = genCacheKey(myTransferData, true);

            //只有包含了注册的消息才放入
            if (messageMap.get(recvMsgKey) == null) {
                messageMap.put(recvMsgKey,
                    myTransferData.getObject(MessageType.SEND_RESPONSE.toString()));
            }

            //2.等待当前消息msgKey返回，重置返回的消息
            boolean waitSuccess = waitForTimeout(msgKey);
            if (waitSuccess) {
                myTransferData.setObject(MessageType.SEND_RESPONSE.toString(),
                    messageMap.get(recvMsgKey));
            }
        }
    }

    /**
     *  阻塞等待流水超时,只针对长连接
     * 
     * @param msgKey
     * @return
     */
    private boolean waitForTimeout(String msgKey) {
        if (messageMap.get(msgKey) != null) {
            return true;
        }
        int times = Integer.parseInt(ControllerContext
            .getAttributeByName(ControllerContext.TIMEOUT)) / 100;
        try {
            for (int i = 0; i < times; i++) {
                if (messageMap.get(msgKey) != null) {
                    return true;
                }
                Thread.sleep(100);
            }

        } catch (Exception e) {
            ExceptionUtil.caught(e, "长连接等待流水号", msgKey, "超时");
        }
        return false;
    }

    /**
     * 
     * @param transaction
     * @return
     */
    private String genCacheKey(TransferData myTransferData, boolean afterParser) {
        String communcationID = (String) myTransferData.getObject(DataMapDict.SENDCOMID);
        return Constant.getKey(communcationID);
    }

    /**
     * 封装发送给服务端的报文
     * 
     * @param TransferData
     * @return
     */
    protected MessageEnvelope assembleMessage(TransferData myTransferData) {
        try {
            Profiler.enter("报文组装");

            String format = (String) myTransferData.getObject(DataMapDict.SENDFORMAT);

            @SuppressWarnings("unchecked")
            MessageEnvelope request = new MessageEnvelope(
                Enum.valueOf(MessageFormat.class, format),

                myTransferData.getObject(DataMapDict.SERVER_FORWARD_CONTENT),
                (Map<String, String>) (myTransferData.getObject(DataMapDict.SENDEXTRACONTENT)));

            LoggerUtil.info(logger, "报文封装结果:", request);

            return request;

        } finally {

            Profiler.release();
        }
    }

    /**
     * Setter method for property <tt>messageSender</tt>.
     * 
     * @param messageSender value to be assigned to property messageSender
     */
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

}
