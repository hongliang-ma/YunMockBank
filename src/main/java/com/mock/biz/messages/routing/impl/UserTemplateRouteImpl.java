
package com.mock.biz.messages.routing.impl;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.messages.routing.UserTemplateRoute;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.service.shared.repository.UserTemplateRepository;
import com.mock.core.service.shared.thread.ThreadPoolService;
import com.mock.core.service.shared.thread.ThreadPoolService.PoolType;
import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 用户路由实现的部分
 * 
 * @author hongliang.ma
 * @version $Id: UserTemplateRouteImpl.java, v 0.1 2012-6-28 下午6:12:34 hongliang.ma Exp $
 */
public class UserTemplateRouteImpl implements UserTemplateRoute {

    private static final Logger    logger = LoggerFactory.getLogger(UserTemplateRouteImpl.class);

    private UserTemplateRepository userTemplateRepository;

    /** 
     * @return 
     * @see com.mock.biz.messages.routing.UserTemplateRoute#route(java.lang.String, com.mock.model.detail.TransferData)
     */
    public String route(Usertemplate usertemplate, TransferData transferData) {
        AssertUtil.isNotNull(usertemplate, TransactionErrorCode.CANNOT_FIND_COMPONENT,
            "usertemplate 为空");

        //设置TransferData
        final TransferData localTransferData = usertemplate.getConfigTransferData();
        @SuppressWarnings("unchecked")
        List<DetailMsg> listDetailMsg = (List<DetailMsg>) localTransferData
            .getObject(DataMapDict.USER_TEMPLATE);
        AssertUtil
            .isNotNull(listDetailMsg, TransactionErrorCode.TRANS_NEXT_ERROR, "DetailMsg的集合为空");
        String strSendUrl = null;
        //逐个处理
        String myUserName = usertemplate.getUserName();
        if (BooleanUtils.toBoolean((String) transferData.getObject(DataMapDict.ISTRANSFER))) {
            transferData.setObject(DataMapDict.MSGBODY,
                transferData.getObject(DataMapDict.TRANSFERMSG));
        }
        String handTools = null;
        String isPerform = (String) transferData.getProperties().get("AnymockIsPERFORM");
        try {
            //这里走普通的模式
            if (!StringUtil.equalsIgnoreCase(isPerform, "TRUE")) {
                for (DetailMsg detailMsg : listDetailMsg) {
                    handTools = detailMsg.getClassid();
                    LoggerUtil.info(logger, "开始处理用户内部数据", myUserName, "处理工具为", handTools);
                    ComponetFactory.componetHandler(detailMsg.getClassid(), transferData,
                        localTransferData);
                    strSendUrl = detailMsg.getSendUrl();
                    if (StringUtil.isNotBlank(strSendUrl)) {
                        LoggerUtil.info(logger, "找到的转发地址，地址为", strSendUrl);
                    }
                }
            } else {
                //这里走极速模式
                for (DetailMsg detailMsg : listDetailMsg) {
                    handTools = detailMsg.getClassid();
                    if (StringUtil.contains(handTools, "KeyValueParse")
                        || StringUtil.contains(handTools, "XMLParse")
                        || StringUtil.contains(handTools, "SerialParse")
                        || StringUtil.contains(handTools, "Message")
                        || StringUtil.contains(handTools, "MappingValueAction")
                        || StringUtil.contains(handTools, "Length")) {
                        LoggerUtil.info(logger, "开始处理用户内部数据", myUserName, "处理工具为", handTools);
                        ComponetFactory.componetHandler(detailMsg.getClassid(), transferData,
                            localTransferData);
                        strSendUrl = detailMsg.getSendUrl();
                        if (StringUtil.isNotBlank(strSendUrl)) {
                            LoggerUtil.info(logger, "找到的转发地址，地址为", strSendUrl);
                        }
                    }
                }
            }
        } catch (AnymockException anymockException) {
            ExceptionUtil.caught(anymockException, "用户处理部分失败，工具为", handTools);
            if (anymockException.getErrorCode() != TransactionErrorCode.ASSERT_FAILED) {
                throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }

        }

        //处理完毕之后，需要更新一下使用次数,异步更新次数和缓存
        runTask(usertemplate.getInnerid());

        return strSendUrl;
    }

    /**
     * 异步更新使用次数
     * 
     * @param transaction
     */
    public void runTask(final String userTemplateID) {

        ThreadPoolService.addTask(PoolType.ASYN_UPCOUNT, new Runnable() {

            /** 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                try {
                    userTemplateRepository.upDateUseCount(userTemplateID);
                    Usertemplate usertemplate = UserTemplateCache
                        .getUsertemplateById(userTemplateID);
                    usertemplate.setUserCount(userTemplateRepository.loadUserTemplateById(
                        userTemplateID).getUserCount());
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "刷新用户缓存异常!");
                }
            }
        });

    }

    /**
     * Setter method for property <tt>userTemplateRepository</tt>.
     * 
     * @param userTemplateRepository value to be assigned to property userTemplateRepository
     */
    public void setUserTemplateRepository(UserTemplateRepository userTemplateRepository) {
        this.userTemplateRepository = userTemplateRepository;
    }

}
