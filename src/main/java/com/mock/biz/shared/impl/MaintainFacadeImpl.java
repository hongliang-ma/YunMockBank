
package com.mock.biz.shared.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.mock.biz.shared.MaintainFacade;
import com.mock.biz.shared.domain.MergeDetail;
import com.mock.biz.shared.domain.SysconfigDes;
import com.mock.dal.daointerface.CommunicationDAO;
import com.mock.common.service.facade.CacheManagerService;
import com.mock.common.service.facade.common.CacheName;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.shared.cache.impl.AnymockLablesCache;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.service.shared.repository.AnymockLableRepository;
import com.mock.core.service.shared.repository.CommunicationRepository;
import com.mock.core.service.shared.repository.SystemTemplateRepository;
import com.mock.core.service.shared.repository.UserTemplateRepository;
import com.mock.core.service.shared.thread.ThreadPoolService;
import com.mock.core.service.shared.thread.ThreadPoolService.PoolType;
import com.mock.core.model.transaction.template.CommunicationPart;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.TemplateDetail;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.ApiUtilTool;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: MaintainFacadeImpl.java, v 0.1 2012-10-26 下午1:24:54 hongliang.ma Exp $
 */
public class MaintainFacadeImpl implements MaintainFacade {

    private UserTemplateRepository   userTemplateRepository;
    private CommunicationDAO         communicationDAO;
    private CacheManagerService      cacheManagerService;

    private CommunicationRepository  communicationRepository;
    private AnymockLableRepository   anymockLableRepository;
    private SystemTemplateRepository systemTemplateRepository;

    /** 
     * @see com.mock.biz.shared.MaintainFacade#mergeFunction(com.mock.biz.shared.domain.MergeDetail)
     */
    public String mergeFunction(MergeDetail mergeDetail) {
        Set<String> listMergeId = mergeDetail.getListMergeId();
        Usertemplate usertemplate = null;
        int iCount = 0;
        int iMergeSize = listMergeId.size();
        StringBuilder sbfSetIds = new StringBuilder(iMergeSize << 1);
        String tranferid = null;
        String strInnerID = null;
        String strReturn = null;
        String strCommId = null;
        String strSystemId = null;
        int iTotal = 0;
        ArrayList<String> arrCommId = new ArrayList<String>(iMergeSize);
        CommunicationConfig communicationConfig = null;
        List<CommunicationConfig> commList = new ArrayList<CommunicationConfig>();
        try {
            for (String mergeId : listMergeId) {
                usertemplate = UserTemplateCache.getUsertemplateById(mergeId);
                if (null == usertemplate) {
                    continue;
                }

                if (0 != iCount && !StringUtil.contains(strInnerID, mergeId)) {
                    strSystemId = usertemplate.getSystemId();
                    strCommId = SystemTemplateCache.getSystemTemplateById(strSystemId).getUrlId();

                    iTotal = userTemplateRepository.getUserTemplateCountBySysId(strSystemId);
                    if (1 < iTotal) {
                        return "不允许合并经过复制之后的配置!";
                    }

                    communicationConfig = NetworkConfigCache.getCommunicationConfigById(strCommId);
                    //如果已经被其他设置为客户端了，那就不能设置了
                    if (!communicationConfig.isIstransfer()) {
                        sbfSetIds.append(mergeId);
                        sbfSetIds.append(",");
                        arrCommId.add(strCommId);
                        communicationDAO.updateIstransfer("TRUE", strCommId);
                        commList.add(communicationConfig);
                    }
                } else if (0 == iCount) {
                    tranferid = mergeId;
                    //设置第一个作为开始，后面的作为转发端                 

                    strInnerID = usertemplate.getBindInnerID();
                    strSystemId = usertemplate.getSystemId();
                    strCommId = SystemTemplateCache.getSystemTemplateById(strSystemId).getUrlId();
                    iTotal = userTemplateRepository.getUserTemplateCountBySysId(strSystemId);
                    if (1 < iTotal) {
                        return "不允许合并经过复制之后的配置!";
                    }
                    communicationConfig = NetworkConfigCache.getCommunicationConfigById(strCommId);
                    //如果已经被其他设置为客户端了，那就不能设置了
                    if (communicationConfig.isIstransfer()) {
                        return tranferid + "已经被其他ID合并";
                    }
                    commList.add(communicationConfig);
                }
                iCount++;
            }
            if (sbfSetIds.length() > 0) {
                String bindStr;
                if (StringUtil.isNotEmpty(strInnerID)) {
                    bindStr = sbfSetIds.append(strInnerID).toString();
                } else {
                    bindStr = StringUtil.trimEnd(sbfSetIds.toString(), ",");
                }
                userTemplateRepository.updateBindId(bindStr, tranferid);
            }
            if (StringUtil.isEmpty(tranferid)) {
                strReturn = "输入ID的找不到相关配置!";
            } else if (sbfSetIds.length() <= 0) {
                strReturn = "输入多个ID的找不到相关配置或者已经被设置为转发!";
            } else {
                strReturn = "合并成功";
                runTask(CacheName.USER_TEMPLATER);
                runTask(CacheName.COMMUNICATION);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "合并发生错误");
            userTemplateRepository.updateBindId(strInnerID, tranferid);
            for (String mergeId : arrCommId) {
                communicationDAO.updateIstransfer("FALSE", mergeId);
            }
            strReturn = "合并发生错误";
        }

        return strReturn;
    }

    /**
     * 异步刷新缓存，全部采用新线程处理交易
     * 
     * @param transaction
     */
    public void runTask(final CacheName CacheNameType) {

        ThreadPoolService.addTask(PoolType.ASYN_REFRESH, new Runnable() {

            /** 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                try {
                    cacheManagerService.refreshCache(CacheNameType);
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

    /**
     * Setter method for property <tt>communicationDAO</tt>.
     * 
     * @param communicationDAO value to be assigned to property communicationDAO
     */
    public void setCommunicationDAO(CommunicationDAO communicationDAO) {
        this.communicationDAO = communicationDAO;
    }

    /**
     * Setter method for property <tt>cacheManagerService</tt>.
     * 
     * @param cacheManagerService value to be assigned to property cacheManagerService
     */
    public void setCacheManagerService(CacheManagerService cacheManagerService) {
        this.cacheManagerService = cacheManagerService;
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#FindCom(java.lang.String)
     */
    public List<CommunicationConfig> FindCom(String seachValue) {
        List<String> commID = communicationDAO.selectByMutiUrl(seachValue);
        if (CollectionUtils.isEmpty(commID)) {
            commID = communicationDAO.selectByMutiUrl(StringUtil.replace(seachValue,
                ApiUtilTool.getHostName(), "localhost"));
            if (CollectionUtils.isEmpty(commID)) {
                return null;
            }
        }

        List<CommunicationConfig> listConfigs = new ArrayList<CommunicationConfig>();

        for (String id : commID) {
            listConfigs.add(NetworkConfigCache.getCommunicationConfigById(id));
        }

        return listConfigs;
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#findSysTem(java.lang.String)
     */
    public List<SysconfigDes> findSysTem(String seachValue) {
        List<String> commID = communicationDAO.selectByMutiUrl(seachValue);
        SystemTemplate systemTemplate = null;
        SystemTemplate nextSystemTemplate = null;

        if (CollectionUtils.isEmpty(commID)) {
            commID = communicationDAO.selectByMutiUrl(StringUtil.replace(seachValue,
                ApiUtilTool.getHostName(), "localhost"));
        }

        if (!CollectionUtils.isEmpty(commID)) {
            if (commID.size() >= 1 && StringUtil.isNotBlank(commID.get(0))) {
                systemTemplate = SystemTemplateCache.getSystemTemplate(commID.get(0));
            }
            if (commID.size() >= 2 && StringUtil.isNotBlank(commID.get(1))) {
                nextSystemTemplate = SystemTemplateCache.getSystemTemplate(commID.get(1));
            }
        } else {
            systemTemplate = SystemTemplateCache.getSystemTemplateById(seachValue);
        }

        List<SysconfigDes> listConfigs = new ArrayList<SysconfigDes>();

        if (null != systemTemplate) {
            SysconfigDes sysconfigDes = new SysconfigDes();
            sysconfigDes.setModifyId(systemTemplate.getSysId());
            sysconfigDes.setModifyTempalte(systemTemplate.getSysTemplate());
            listConfigs.add(sysconfigDes);
        }

        if (null != nextSystemTemplate) {
            SysconfigDes sysconfigDes = new SysconfigDes();
            sysconfigDes.setModifyId(nextSystemTemplate.getSysId());
            sysconfigDes.setModifyTempalte(nextSystemTemplate.getSysTemplate());
            listConfigs.add(sysconfigDes);
        }

        return listConfigs;
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#FindUserTemp(java.lang.String)
     */
    public List<TemplateDetail> FindUserTemp(String seachValue) {
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(seachValue);
        if (null == usertemplate) {
            return null;
        }

        return usertemplate.getTemplateDetail();
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#changeCom(com.mock.biz.shared.domain.CommunicationChange)
     */
    public String changeCom(String strChangeID, CommunicationPart communicationPart, String labels) {
        Boolean bChanged = communicationRepository.updateById(strChangeID, communicationPart);
        Boolean bChangelab = true;
        if (bChanged) {
            bChangelab = anymockLableRepository.updateLabes(strChangeID, labels);
            AnymockLablesCache.getLabesbyId(strChangeID).setLabList(labels);
            CommunicationConfig communicationConfig = NetworkConfigCache
                .getCommunicationConfigById(strChangeID);
            communicationConfig.setCharset(communicationPart.getCharset());
            communicationConfig.setDescription(communicationPart.getDescription());
            communicationConfig.setProperties(communicationPart.getMapProperties());
            communicationConfig.setProtocol(communicationPart.getProtocol().name());
            communicationConfig.setRecvMessageFormat(communicationPart.getRecvMessageFormat());
            communicationConfig.setSendMessageFormat(communicationPart.getSendMessageFormat());
            communicationConfig.setServer(communicationPart.isServerType());
            communicationConfig.setUri(communicationPart.getUrl());
            NetworkConfigCache.modifyCache(communicationConfig);
        }
        if (!bChanged) {
            return "更新通讯配置失败!";
        }

        if (!bChangelab) {
            return "更新通讯标签失败!";
        }

        return "更新成功";
    }

    /**
     * Setter method for property <tt>communicationRepository</tt>.
     * 
     * @param communicationRepository value to be assigned to property communicationRepository
     */
    public final void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    /**
     * Setter method for property <tt>anymockLableRepository</tt>.
     * 
     * @param anymockLableRepository value to be assigned to property anymockLableRepository
     */
    public final void setAnymockLableRepository(AnymockLableRepository anymockLableRepository) {
        this.anymockLableRepository = anymockLableRepository;
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#changeSysTemplate(com.mock.biz.shared.domain.SysconfigDes)
     */
    public String changeSysTemplate(SysconfigDes sysconfigDes) {
        String iSysId = sysconfigDes.getModifyId();
        String strSysTemplate = sysconfigDes.getModifyTempalte();
        Boolean bChanged = systemTemplateRepository.updateSysTemplate(iSysId, strSysTemplate);
        String sbReturn = null;
        if (bChanged) {
            sbReturn = "公共模板修改成功";
            SystemTemplateCache.getSystemTemplateById(iSysId).setProperties(strSysTemplate);
            SystemTemplateCache.getSystemTemplateById(iSysId).setSysTemplate(strSysTemplate);
        } else {
            sbReturn = "公共模板修改失败";
        }
        return sbReturn;
    }

    /**
     * Setter method for property <tt>systemTemplateRepository</tt>.
     * 
     * @param systemTemplateRepository value to be assigned to property systemTemplateRepository
     */
    public final void setSystemTemplateRepository(SystemTemplateRepository systemTemplateRepository) {
        this.systemTemplateRepository = systemTemplateRepository;
    }

    /** 
     * @see com.mock.biz.shared.MaintainFacade#changeUserDetailTemp(java.lang.String, java.util.List)
     */
    public String changeUserDetailTemp(String userTemplateId, List<String> listDetailMsg) {
        try {
            userTemplateRepository.updateAllDetail(userTemplateId, listDetailMsg);
            Usertemplate usertemplate = userTemplateRepository.loadUserTemplateById(userTemplateId);
            AssertUtil.isNotNull(usertemplate, SystemErrorCode.DB_ACCESS_ERROR);
            List<TemplateDetail> templateDetail = usertemplate.getTemplateDetail();
            AssertUtil.isNotNull(templateDetail, SystemErrorCode.SYSTEM_HAND_ERROR);
            String templateId = usertemplate.getInnerid();
            AssertUtil.isTrue(StringUtil.equals(templateId, userTemplateId),
                SystemErrorCode.ILLEGAL_PARAMETER);
            UserTemplateCache.getUsertemplateById(templateId).setConfigTransferData(templateDetail);
            UserTemplateCache.getUsertemplateById(templateId).setTemplateDetail(templateDetail);
        } catch (AnymockException exception) {
            return "用户模板详情修改失败";
        }
        return "用户模板详情修改成功";
    }
}
