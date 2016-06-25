
package com.mock.biz.shared.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.shared.OperationEdit;
import com.mock.biz.shared.domain.ChangeType;
import com.mock.dal.daointerface.UserDAO;
import com.mock.dal.dataobject.UserDO;
import com.mock.common.service.facade.CacheManagerService;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.enums.MessageRunMode;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.shared.cache.impl.AnymockLablesCache;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.service.shared.repository.AnymockLableRepository;
import com.mock.core.service.shared.repository.CommunicationRepository;
import com.mock.core.service.shared.repository.SystemTemplateRepository;
import com.mock.core.service.shared.repository.TemplateDetailRepository;
import com.mock.core.service.shared.repository.UserRepository;
import com.mock.core.service.shared.repository.UserTemplateRepository;
import com.mock.core.model.transaction.J8583.J8583Field;
import com.mock.core.model.transaction.J8583.J8583Model;
import com.mock.core.model.transaction.J8583.J8583template;
import com.mock.core.model.transaction.J8583.J8683Envelope;
import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.TemplateDetail;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: OperationEditImpl.java, v 0.1 2012-8-1 下午1:05:19 hongliang.ma Exp $
 */
public class OperationEditImpl implements OperationEdit {

    private static final Logger      logger = LoggerFactory.getLogger(OperationEditImpl.class);

    private UserTemplateRepository   userTemplateRepository;

    private SystemTemplateRepository systemTemplateRepository;

    private TemplateDetailRepository templateDetailRepository;

    private CommunicationRepository  communicationRepository;

    private AnymockLableRepository   anymockLableRepository;

    private UserDAO                  userDAO;

    private UserRepository           userRepository;

    /** 
     * @see com.mock.biz.shared.OperationEdit#addToUserNormal(java.lang.String, java.lang.String)
     */
    public String addToUserNormal(String nameToCopy, String innerIdToCopy) {

        String newInnerID = null;
        try {
            LoggerUtil.info(logger, "开始拷贝，用户名为", nameToCopy, "拷贝的ID号为", innerIdToCopy);
            newInnerID = userTemplateRepository.addUserTemplateToUser(innerIdToCopy, nameToCopy);
            Usertemplate usertemplate = userTemplateRepository.loadUserTemplateById(newInnerID);
            UserTemplateCache.putNew(usertemplate);
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "拷贝配置到用户失败!");
        }

        return newInnerID;

    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#delConfigByIDNormal(java.lang.String)
     */
    public Boolean delConfigByIDNormal(String innerIdToDel) {
        Boolean newInnerID = true;
        try {
            LoggerUtil.info(logger, "开始删除，删除的ID号为", innerIdToDel);
            final String sysId = UserTemplateCache.getUsertemplateById(innerIdToDel).getSystemId();
            userTemplateRepository.deleteUserTemplate(innerIdToDel);
            DeleteTask(sysId, innerIdToDel);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "拷贝配置到用户失败!");
            newInnerID = false;
        }
        return newInnerID;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#addToUserTransfer(java.lang.String, java.lang.String, java.lang.String)
     */
    public String addToUserTransfer(final String nameToCopy, final String innerid,
                                    final String transferID) {
        try {
            LoggerUtil.info(logger, "开始拷贝，用户名为", nameToCopy, "拷贝的ID号为", innerid);
            String strBindInnerID = UserTemplateCache.getUsertemplateById(innerid).getBindInnerID();
            Boolean btransfer = true;
            List<String> idsToCopy = new ArrayList<String>();
            idsToCopy.add(innerid);
            if (StringUtil.equals(transferID, strBindInnerID)) {
                btransfer = false;
                String[] listTranferID = StringUtil.split(transferID, ",");
                for (String tranferID : listTranferID) {
                    idsToCopy.add(tranferID);
                }
            } else {
                idsToCopy.add(transferID);
            }

            List<String> newInnerID = userTemplateRepository.addManyUserTemplateToUser(idsToCopy,
                btransfer, nameToCopy);
            Usertemplate usertemplate = null;
            for (String innerID : newInnerID) {
                usertemplate = userTemplateRepository.loadUserTemplateById(innerID);
                if (null == usertemplate) {
                    continue;
                }
                UserTemplateCache.putNew(usertemplate);
            }
            return newInnerID.get(0);
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "拷贝配置到用户失败!");
        }

        return null;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#delConfigWithTransfer(java.lang.String, java.lang.String)
     */
    public Boolean delConfigWithTransfer(final List<String> idsToDel) {
        Boolean newInnerID = true;
        try {
            LoggerUtil.info(logger, "开始删除，删除的第一个ID号为", idsToDel.get(0));
            userTemplateRepository.deleteUserTemplate(idsToDel);
            String sysId;
            for (String innerIdToDel : idsToDel) {
                //查看是否存在多个，如果只有第一个时，全部删除
                sysId = UserTemplateCache.getUsertemplateById(innerIdToDel).getSystemId();
                DeleteTask(sysId, innerIdToDel);
            }
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "拷贝配置到用户失败!");
            newInnerID = false;
        }
        return newInnerID;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#changeNormal(com.mock.biz.shared.domain.ChangeType, java.lang.String, java.lang.String)
     */
    public Boolean changeNormal(ChangeType changeType, String idToChange, String newString) {
        Boolean bChange = true;

        if (StringUtil.isEmpty(idToChange) || StringUtil.isEmpty(newString)) {
            return false;
        }

        switch (changeType) {
            case TEMPLATENAME: {
                LoggerUtil.info(logger, "开始更改用户配置名字，用户模板ID号为", idToChange, "新的名字为", newString);
                try {
                    userTemplateRepository.updateTemplateName(idToChange, newString);
                    UserTemplateCache.getUsertemplateById(idToChange).setTemplateName(newString);
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改用户配置名字失败，用户模板ID为", idToChange, "新的名字为", newString);
                    bChange = false;
                }
            }
                break;
            case MATCHSTR: {
                LoggerUtil.info(logger, "开始更改唯一值的字符串，用户模板ID号为", idToChange, "新的唯一值", newString);
                try {
                    Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(idToChange);
                    if (null != usertemplate) {
                        Usertemplate nextUsertemplate = UserTemplateCache.getUsertemplateByMacht(
                            newString, usertemplate.getSystemId());
                        if (null == nextUsertemplate
                            || (nextUsertemplate.getInnerid() == usertemplate.getInnerid())) {
                            userTemplateRepository.updateMatchstr(idToChange, newString);

                            UserTemplateCache.changeMatch(idToChange, newString);
                        } else {
                            bChange = false;
                        }

                    } else {
                        bChange = false;
                    }
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改唯一值名字失败，用户模板ID为", idToChange, "新的唯一值", newString);
                    bChange = false;
                }
            }
                break;
            case MATCHWAY: {
                LoggerUtil.info(logger, "开始更改匹配方式，公共ID号为", idToChange, "新的匹配方式为", newString);
                try {
                    systemTemplateRepository.updateMacthWay(idToChange, newString);
                    SystemTemplate mySystemTemplate = systemTemplateRepository.loadById(idToChange);

                    SystemTemplateCache.getSystemTemplateById(idToChange).setSysTemplate(
                        mySystemTemplate.getSysTemplate());
                    SystemTemplateCache.getSystemTemplateById(idToChange).setProperties(
                        mySystemTemplate.getSysTemplate());
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改匹配方式失败，公共ID为", idToChange, "新的匹配方式为", newString);
                    bChange = false;
                }
            }
                break;
            case MATCHDES: {
                LoggerUtil.info(logger, "开始更改匹配描述，公共ID号为", idToChange, "新的匹配方式为", newString);
                try {
                    systemTemplateRepository.updateMactchDes(idToChange, newString);
                    SystemTemplateCache.getSystemTemplateById(idToChange).setMacthdescription(
                        newString);
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改匹配描述失败，公共ID为", idToChange, "新的匹配描述为", newString);
                    bChange = false;
                }
            }
                break;
            case MESSAGEPARSER: {
                LoggerUtil.info(logger, "开始更改8583，更改的用户ID", idToChange);
                try {
                    long detailID = Long.parseLong(idToChange);
                    TemplateDetail myTemplateDetail = templateDetailRepository
                        .loadTemplateById(detailID);
                    AssertUtil.isNotNull(myTemplateDetail, SystemErrorCode.DB_ACCESS_ERROR);
                    DetailMsg myDetailMsg = myTemplateDetail.getDetailValue();
                    Map<String, String> j8583Tempalte = new HashMap<String, String>();
                    j8583Tempalte.put("j8583", newString);
                    Map<String, Map<String, String>> endTemplate = new HashMap<String, Map<String, String>>();
                    endTemplate.put("template", j8583Tempalte);
                    myDetailMsg.setTemplateMsg(endTemplate);
                    templateDetailRepository.updateValue(detailID, myDetailMsg);

                    updateDetailCache(detailID);
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改8583失败，更改的用户ID", idToChange, "新值为", newString);
                    bChange = false;
                }
            }
                break;
            default: {
                LoggerUtil.warn(logger, "未知修改类型，ID号为", idToChange, "新的名字为", newString);
                bChange = false;
            }
                break;
        }

        return bChange;
    }

    /**
     * 更新具体templateDetail的用户模板的缓存
     * 
     * @param detailID
     * @param endTemplate
     */
    private void updateDetailCache(long detailID) {
        String templateId = templateDetailRepository.loadTemplateById(detailID).getInnerid();
        List<TemplateDetail> templateDetails = templateDetailRepository
            .loadTemplateDetail(templateId);
        UserTemplateCache.getUsertemplateById(templateId).setConfigTransferData(templateDetails);
        UserTemplateCache.getUsertemplateById(templateId).setTemplateDetail(templateDetails);
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#changeToolsValue(com.mock.biz.shared.domain.ChangeType, java.lang.String, java.util.Map)
     */
    public Boolean changeToolsValue(ChangeType changeType, Long detailID,
                                    Map<String, String> keyValues) {
        Boolean bChange = true;

        DetailMsg detailMsg = UserTemplateCache.getDetailMsg(detailID);
        AssertUtil.isNotNull(detailMsg, TransactionErrorCode.CANNOT_FIND_COMPONENT);

        switch (changeType) {
            case NORMALVALUE: {
                LoggerUtil.info(logger, "开始更改用户模板详情普通部分，模板详情D号为", detailID);
                try {
                    detailMsg.setKeyValues(keyValues);
                    if (null != detailMsg.getTemplateMsg()) {
                        detailMsg.setTemplateMsg(detailMsg.getTemplateMsg());
                    }
                    templateDetailRepository.updateValue(detailID, detailMsg);
                    updateDetailCache(detailID);
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改用户模板详情普通部分失败，用户模板ID为", detailID);
                    bChange = false;
                }
            }
                break;
            case TEMPLATEVALUE: {
                LoggerUtil.info(logger, "开始更改用户模板详情TEMPLATE，用户模板ID号为", detailID);
                try {
                    Map<String, Map<String, String>> templateMsg = new HashMap<String, Map<String, String>>();
                    templateMsg.put("template", keyValues);
                    detailMsg.setTemplateMsg(templateMsg);
                    templateDetailRepository.updateValue(detailID, detailMsg);
                    updateDetailCache(detailID);
                } catch (AnymockException e) {
                    ExceptionUtil.caught(e, "更改用户模板详情TEMPLATE失败，用户模板ID为", detailID);
                    bChange = false;
                }
            }
                break;
            default: {
                LoggerUtil.warn(logger, "未知修改类型，ID号为", detailID);
                bChange = false;
            }
                break;
        }

        return bChange;
    }

    /**
     * 后台刷新，异步判断删除时是否还存在子配置
     * 
     * @param transaction
     */
    public void DeleteTask(final String sysId, final String innerId) {
        int totalSize = userTemplateRepository.getUserTemplateCountBySysId(sysId);
        if (0 == totalSize) {
            String urlId = SystemTemplateCache.getSystemTemplateById(sysId).getUrlId();
            systemTemplateRepository.deleteSysTemplateBySysId(sysId);
            communicationRepository.deleteURlById(urlId);
            anymockLableRepository.deleteByCommuId(urlId);
            SystemTemplateCache.deleteOne(sysId);
            AnymockLablesCache.deleteOne(urlId);
            NetworkConfigCache.deleteOne(urlId);
        } else {
            UserTemplateCache.deleteTotal(innerId);
        }
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#edit8583FValue(java.lang.String, javax.servlet.http.HttpServletResponse)
     */
    public Boolean edit8583FValue(final String detailID, final HttpServletRequest request) {
        Boolean bReturn = false;
        try {
            LoggerUtil.info(logger, "开始更改8583，用户模板ID号为", detailID);
            long lDetailId = Long.parseLong(detailID);
            DetailMsg myDetailMsg = UserTemplateCache.getDetailMsg(lDetailId);
            AssertUtil.isNotNull(myDetailMsg, SystemErrorCode.SYSTEM_ERROR);

            String j8583String = myDetailMsg.getTemplateMsg().get("template").get("j8583");
            AssertUtil.isNotNull(j8583String, SystemErrorCode.SYSTEM_ERROR);

            J8583Model myJ8583Model = J8683Envelope.getJ8583Model(j8583String);

            Map<String, String> getParaMap = getTextContent(request);
            //开始修改值
            LoggerUtil.info(logger, "执行change8583Model");
            setNew8583Value(lDetailId, getParaMap, myDetailMsg, myJ8583Model);
            bReturn = true;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "更改8583失败，用户模板ID号为", detailID);
        }
        return bReturn;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#edit8583FValue(long, java.util.Map)
     */
    public Boolean edit8583FValue(long detailID, Map<String, String> mapNewTemplate) {
        Boolean bReturn = false;
        try {
            LoggerUtil.info(logger, "开始更改8583，用户模板ID号为", detailID);
            DetailMsg myDetailMsg = UserTemplateCache.getDetailMsg(detailID);
            AssertUtil.isNotNull(myDetailMsg, SystemErrorCode.SYSTEM_ERROR);

            String j8583String = myDetailMsg.getTemplateMsg().get("template").get("j8583");
            AssertUtil.isNotNull(j8583String, SystemErrorCode.SYSTEM_ERROR);

            J8583Model myJ8583Model = J8683Envelope.getJ8583Model(j8583String);
            //开始修改值
            setNew8583Value(detailID, mapNewTemplate, myDetailMsg, myJ8583Model);
            bReturn = false;
        } catch (Exception e) {
            ExceptionUtil.caught(e, "更改8583失败，用户模板ID号为", detailID);
        }
        return bReturn;
    }

    /**
     * 
     * @param detailID
     * @param mapNewTemplate
     * @param myDetailMsg
     * @param myJ8583Model
     */
    private void setNew8583Value(long detailID, Map<String, String> mapNewTemplate,
                                 DetailMsg myDetailMsg, J8583Model myJ8583Model) {
        LoggerUtil.info(logger, "执行change8583Model");
        change8583Model(myJ8583Model, mapNewTemplate);
        //装成XML
        LoggerUtil.info(logger, "执行formatJ8583Model");
        String j8583Xml = J8683Envelope.formatJ8583Model(myJ8583Model);
        AssertUtil.isNotNull(j8583Xml, SystemErrorCode.SYSTEM_ERROR);

        Map<String, String> j8583Tempalte = new HashMap<String, String>();
        j8583Tempalte.put("j8583", j8583Xml);
        Map<String, Map<String, String>> endTemplate = new HashMap<String, Map<String, String>>();
        endTemplate.put("template", j8583Tempalte);
        myDetailMsg.setTemplateMsg(endTemplate);
        LoggerUtil.info(logger, "开始保存更改的8583，用户模板ID号为", detailID);
        templateDetailRepository.updateValue(detailID, myDetailMsg);

        //刷新缓存
        updateDetailCache(detailID);
    }

    /**
     * 更改8583配置
     * @param myJ8583Model
     * @param getParaMap
     */
    private void change8583Model(J8583Model myJ8583Model, final Map<String, String> getParaMap) {
        ArrayList<J8583template> tempJ8583templateList = myJ8583Model.getAllTemplateMap();
        ArrayList<J8583Field> templateFieldList = null;
        String changId = null;
        String strTemplate = null;
        String strTemplateId = null;
        String strFiledId = null;
        String strFiledValue = null;

        for (@SuppressWarnings("rawtypes")
        Map.Entry entry : getParaMap.entrySet()) {
            if (null == entry) {
                continue;
            }
            changId = (String) entry.getKey();
            strTemplate = AtsframeStrUtil.substringBeforeLast(changId, "_");
            strTemplateId = AtsframeStrUtil.substringAfter(strTemplate, "_");
            strFiledId = AtsframeStrUtil.substringAfterLast(changId, "_");
            strFiledValue = (String) entry.getValue();
            if (StringUtil.contains(changId, "template")) {
                for (J8583template j8583template : tempJ8583templateList) {
                    if (null != j8583template
                        && StringUtil.equals(strTemplateId, j8583template.getMsgtypeid())) {
                        templateFieldList = j8583template.getTemplateField();
                        for (J8583Field j8583Field : templateFieldList) {
                            if (null != j8583Field
                                && StringUtil.equals(strFiledId, j8583Field.getId())) {
                                j8583Field.setFieldValue(strFiledValue);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 从HttpServletRequest中读取Map的报文
     * 
     * @param request
     * @param config
     * @return
     */
    private Map<String, String> getTextContent(HttpServletRequest request) {
        Map<String, String> getParaMap = new HashMap<String, String>();

        @SuppressWarnings("rawtypes")
        Enumeration getParaSet = request.getParameterNames();
        String strTemp = null;
        while (getParaSet.hasMoreElements()) {
            strTemp = (String) getParaSet.nextElement();
            if (StringUtil.contains(strTemp, "template")
                || StringUtil.contains(strTemp, "parseinfo")) {
                getParaMap.put(strTemp, request.getParameter(strTemp));
            }
        }

        return getParaMap;
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
     * Setter method for property <tt>cacheManagerService</tt>.
     * 
     * @param cacheManagerService value to be assigned to property cacheManagerService
     */
    public void setCacheManagerService(CacheManagerService cacheManagerService) {
    }

    /**
     * Setter method for property <tt>systemTemplateRepository</tt>.
     * 
     * @param systemTemplateRepository value to be assigned to property systemTemplateRepository
     */
    public void setSystemTemplateRepository(SystemTemplateRepository systemTemplateRepository) {
        this.systemTemplateRepository = systemTemplateRepository;
    }

    /**
     * Setter method for property <tt>templateDetailRepository</tt>.
     * 
     * @param templateDetailRepository value to be assigned to property templateDetailRepository
     */
    public void setTemplateDetailRepository(TemplateDetailRepository templateDetailRepository) {
        this.templateDetailRepository = templateDetailRepository;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#isAssertExise(java.lang.String)
     */
    public Boolean isAssertExise(String innerId) {
        Boolean bIsExise = true;
        try {
            Usertemplate myUsertemplate = UserTemplateCache.getUsertemplateById(innerId);
            AssertUtil.isNotNull(myUsertemplate, SystemErrorCode.SYSTEM_ERROR);

            String classType = myUsertemplate.getTemplateDetail().get(0).getDetailValue()
                .getClassid();
            bIsExise = StringUtil.equalsIgnoreCase("AssertAdaptor", classType);
        } catch (AnymockException e) {
            LoggerUtil.warn(logger, "查询是否存在Assert异常!");
            bIsExise = false;
        }

        return bIsExise;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#addAssert(java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean addAssert(String idToChange, String assertType, String assertExcept) {
        Boolean bIsExise = true;
        try {
            DetailMsg detailMsg = new DetailMsg();
            detailMsg.setClassid("AssertAdaptor");
            detailMsg.setCname("Assert工具");
            Map<String, String> myMap = new HashMap<String, String>();
            myMap.put("reqType", assertType);
            myMap.put("expected", assertExcept);
            detailMsg.setKeyValues(myMap);

            TemplateDetail templateDetail = templateDetailRepository.addTemplateDetail(idToChange,
                detailMsg, 0);
            updateDetailCache(templateDetail.getId());
        } catch (Exception e) {
            LoggerUtil.warn(logger, "新增assert工具失败!");
            bIsExise = false;
        }
        return bIsExise;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#deleteAssert(java.lang.String)
     */
    public Boolean deleteAssert(String innerId) {
        Boolean bIsExise = true;
        try {
            Long id = Long.parseLong(innerId.trim());
            templateDetailRepository.deleteDetailbyId(id);
            updateDetailCache(id);
        } catch (Exception e) {
            LoggerUtil.warn(logger, "删除assert工具失败!", "innerId=", innerId);
            bIsExise = false;
        }
        return bIsExise;
    }

    /**
     * Setter method for property <tt>communicationRepository</tt>.
     * 
     * @param communicationRepository value to be assigned to property communicationRepository
     */
    public void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    /**
     * Setter method for property <tt>anymockLableRepository</tt>.
     * 
     * @param anymockLableRepository value to be assigned to property anymockLableRepository
     */
    public void setAnymockLableRepository(AnymockLableRepository anymockLableRepository) {
        this.anymockLableRepository = anymockLableRepository;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#setDefault(java.lang.String, java.lang.Boolean)
     */
    public String setDefault(String userName, String innerid, Boolean bSetResult) {
        UserDO userDO = userDAO.selectByName(userName);
        if (null == userDO) {
            return "用户不存在!";
        }

        Boolean bdeFault = userRepository.getUserRole(userName);
        if (!bdeFault) {
            return "没有权限，请联系本组有admin权限的用户!";
        }
        try {
            userTemplateRepository.updateIsDefault(innerid, bSetResult);
            String setResult = bSetResult ? "TRUE" : "FALSE";
            UserTemplateCache.getUsertemplateById(innerid).setIsdefault(setResult);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "数据库更新失败，ID为", innerid, "变更结果", bSetResult);
            return "数据库更新失败";
        }

        return "OK!变更成功";
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#addFilePath(java.lang.String, java.lang.String)
     */
    public Boolean addFilePath(String detailId, String StrPath) {
        Boolean bChange = false;
        try {
            Long lDetailId = Long.parseLong(detailId);
            DetailMsg detailMsg = UserTemplateCache.getDetailMsg(lDetailId);
            AssertUtil.isNotNull(detailMsg, TransactionErrorCode.CANNOT_FIND_COMPONENT);
            Map<String, String> mapDetail = detailMsg.getKeyValues();
            mapDetail.put("filePath", StrPath);
            changeToolsValue(ChangeType.NORMALVALUE, lDetailId, mapDetail);
            bChange = true;
        } catch (AnymockException e) {
            ExceptionUtil.caught(e, "更新是发生错误detailId=", detailId, "StrPath=", StrPath);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "未知错误detailId=", detailId, "StrPath=", StrPath);
        }

        return bChange;
    }

    /**
     * Setter method for property <tt>userDAO</tt>.
     * 
     * @param userDAO value to be assigned to property userDAO
     */
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Setter method for property <tt>userRepository</tt>.
     * 
     * @param userRepository value to be assigned to property userRepository
     */
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 
     * @see com.mock.biz.shared.OperationEdit#setPerform(java.lang.String, java.lang.Boolean)
     */
    public String setPerform(String innerid, Boolean bSetResult) {
        Usertemplate myUsertemplate = UserTemplateCache.getUsertemplateById(innerid);
        if (null == myUsertemplate) {
            return "用户配置不存在!";
        }

        SystemTemplate systemTemplate = SystemTemplateCache.getSystemTemplateById(myUsertemplate
            .getSystemId());
        if (null == systemTemplate) {
            return "公共配置不存在!";
        }

        if (bSetResult) {
            NetworkConfigCache.getCommunicationConfigById(systemTemplate.getUrlId()).setMsgRunMode(
                MessageRunMode.PERFORM);
            NetworkConfigCache.getCommunicationConfigById(systemTemplate.getUrlId())
                .setReadUserConfigId(innerid);
        } else {
            NetworkConfigCache.getCommunicationConfigById(systemTemplate.getUrlId()).setMsgRunMode(
                MessageRunMode.NORMAL);
            NetworkConfigCache.getCommunicationConfigById(systemTemplate.getUrlId())
                .setReadUserConfigId(null);
        }

        return "OK";
    }

}
