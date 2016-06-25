
package com.mock.biz.messages.routing.impl;

import java.util.List;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.messages.routing.SystemRoute;
import com.mock.dal.daointerface.TemplateUserDAO;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransferErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 公共处理部分的路由实现
 * 
 * @author hongliang.ma
 * @version $Id: SystemRoutingImpl.java, v 0.1 2012-6-28 下午5:29:58 hongliang.ma Exp $
 */
public class SystemRoutingImpl implements SystemRoute {
    private static final Logger logger = LoggerFactory.getLogger(SystemRoutingImpl.class);

    private TemplateUserDAO     templateUserDAO;

    /** 
     * @see com.mock.biz.messages.routing.SystemRoute#route(com.mock.model.template.SystemTemplate, com.mock.model.detail.TransferData)
     */
    public Usertemplate route(String urlId, TransferData transferData) {
        //查找模板
        SystemTemplate mySysTemplate = SystemTemplateCache.getSystemTemplate(urlId);
        AssertUtil.isNotNull(mySysTemplate, TransferErrorCode.SYSTEMPLATE_NOT_FIND, "缓存中找不到通讯ID为"
                                                                                    + urlId
                                                                                    + "指向的系统模板");
        String sysID = mySysTemplate.getSysId();
        LoggerUtil.info(logger, "找到的系统模板值为", sysID);

        //根据系统模板，开始循环处理
        final TransferData myConfig = mySysTemplate.getProperties();
        AssertUtil.isNotNull(myConfig, TransferErrorCode.LOAD_TOOLS_ERROR, "缓存中找不到通讯ID为" + urlId
                                                                           + "用户组件配置");

        //开始按照名字开始寻找，然后处理
        @SuppressWarnings("unchecked")
        List<String> listSysName = (List<String>) myConfig.getObject(DataMapDict.SYS_TEMPLATE);
        String handTools = null;
        try {
            for (String strTool : listSysName) {
                handTools = strTool;
                LoggerUtil.info(logger, "开始公共处理部分，工具为", handTools);
                ComponetFactory.componetHandler(strTool, transferData, myConfig);
            }
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "公共处理部分失败，工具为", handTools);
            throw new AnymockException(TransferErrorCode.TOOLS_EXCUES_ERROR);

        }
        //开始寻找用户Id
        String strToSelect = (String) transferData.getObject(DataMapDict.SYSRULE);
        LoggerUtil.info(logger, "找到的匹配值为", strToSelect);
        Usertemplate selectUsertemplate = UserTemplateCache.getUsertemplateByMacht(strToSelect,
            sysID);
        if (selectUsertemplate == null) {
            LoggerUtil.info(logger, "找不到的匹配值", strToSelect, "\t 开始寻找默认值");
            String strId = templateUserDAO.selectByDefault(mySysTemplate.getSysId());
            selectUsertemplate = UserTemplateCache.getUsertemplateById(strId);

        }
        AssertUtil.isNotNull(selectUsertemplate, TransferErrorCode.NO_USERTEMPLATE);
        LoggerUtil.info(logger, "找到的匹配的用户Id为", selectUsertemplate.getInnerid(), "用户名字为",
            selectUsertemplate.getUserName(), "用户模板名字叫", selectUsertemplate.getTemplateName());

        return selectUsertemplate;
    }

    /**
     * Setter method for property <tt>templateUserDAO</tt>.
     * 
     * @param templateUserDAO value to be assigned to property templateUserDAO
     */
    public void setTemplateUserDAO(TemplateUserDAO templateUserDAO) {
        this.templateUserDAO = templateUserDAO;
    }

}
