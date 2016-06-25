
package com.mock.biz.shared.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.shared.GetUserList;
import com.mock.biz.shared.domain.DetailInfoInner;
import com.mock.biz.shared.domain.NextTypeEnum;
import com.mock.biz.shared.domain.TemplateType;
import com.mock.biz.shared.domain.UserInfoList;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.PropertiesConfigEnum;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.code.TransferErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.shared.cache.impl.AnymockLablesCache;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.model.transaction.J8583.J8583Model;
import com.mock.core.model.transaction.J8583.J8683Envelope;
import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.TemplateDetail;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.ApiUtilTool;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: GetUserListImpl.java, v 0.1 2012-7-25 下午12:51:18 hongliang.ma Exp $
 */
public final class GetUserListImpl implements GetUserList {

    private static final Logger logger = LoggerFactory.getLogger(GetUserListImpl.class);

    /** 
     * @see com.mock.biz.shared.GetUserList#getTotalListByName(java.lang.String)
     */
    public List<UserInfoList> getTotalListByName(final String userName) {
        AssertUtil.isNotNull(userName, SystemErrorCode.ILLEGAL_PARAMETER);
        LoggerUtil.info(logger, "请求用户名为", userName);
        List<Usertemplate> listUsertemplate = UserTemplateCache.getUsertemplateByName(userName);

        List<UserInfoList> pageUserInfo = new ArrayList<UserInfoList>();
        SystemTemplate mySystemTemplate;
        CommunicationConfig communicationConfig;
        String LocalIp = ApiUtilTool.getHostName();
        for (Usertemplate usertemplate : listUsertemplate) {
            if (usertemplate == null) {
                continue;
            }

            mySystemTemplate = SystemTemplateCache
                .getSystemTemplateById(usertemplate.getSystemId());
            if (mySystemTemplate == null) {
                continue;
            }

            communicationConfig = NetworkConfigCache.getCommunicationConfigById(mySystemTemplate
                .getUrlId());
            if (communicationConfig == null) {
                continue;
            }

            //不显示转发属性的地址
            if (communicationConfig.isIstransfer()) {
                continue;
            }

            UserInfoList userInfoList = getOneUserInfoList(usertemplate, LocalIp,
                communicationConfig, null, false);

            pageUserInfo.add(userInfoList);
        }
        return pageUserInfo;
    }

    /**
     * 设置一个UserInfoList
     * 
     * @param usertemplate
     * @param LocalIp
     * @param communicationConfig
     * @return
     */
    private UserInfoList getOneUserInfoList(Usertemplate usertemplate, String LocalIp,
                                            CommunicationConfig communicationConfig,
                                            String userName, Boolean bAdd) {
        UserInfoList myUserInfoList = null;
        try {
            myUserInfoList = new UserInfoList();
            myUserInfoList.setTemplateInnerId(usertemplate.getInnerid());

            String templateUrl = communicationConfig.getUri().getUrl();
            AssertUtil.isNotBlank(templateUrl, SystemErrorCode.ILLEGAL_PARAMETER);
            myUserInfoList.setTemplateUrl(AtsframeStrUtil
                .replace(templateUrl, "localhost", LocalIp));
            myUserInfoList.setTemplateName(usertemplate.getTemplateName());
            myUserInfoList.setMatchString(usertemplate.getMatchstr());
            myUserInfoList.setTemplateCount(usertemplate.getUserCount());

            String templateLable = AnymockLablesCache.getLabes(communicationConfig
                .getCommunicationId());

            Boolean isServer = communicationConfig.isServer();

            String strBindIds = usertemplate.getBindInnerID();
            String strPara = null;
            TemplateType myTemplateType = null;
            if (StringUtil.isNotEmpty(strBindIds)) {
                String strBindId = StringUtil.split(strBindIds, ",")[0];
                String sysInnerid = UserTemplateCache.getUsertemplateById(strBindId).getSystemId();
                AssertUtil.isNotBlank(sysInnerid, SystemErrorCode.ILLEGAL_PARAMETER);

                SystemTemplate nextSystemTemplate = SystemTemplateCache
                    .getSystemTemplateById(sysInnerid);
                AssertUtil.isNotNull(nextSystemTemplate, SystemErrorCode.DB_ACCESS_ERROR);

                Boolean isNextServer = NetworkConfigCache.getCommunicationConfigById(
                    nextSystemTemplate.getUrlId()).isServer();
                myTemplateType = getTemplateType(isServer, isNextServer);
                strPara = "innerid=" + usertemplate.getInnerid() + "&transferId=" + strBindId;
            } else {
                String transferUr = getTransferUrb(usertemplate);
                if (AtsframeStrUtil.isNotEmpty(transferUr)) {
                    String sysInnerid = AtsframeStrUtil.substringBefore(
                        AtsframeStrUtil.substringAfter(transferUr, "sysInnerid="), "&");
                    AssertUtil.isNotBlank(sysInnerid, SystemErrorCode.ILLEGAL_PARAMETER);

                    SystemTemplate nextSystemTemplate = SystemTemplateCache
                        .getSystemTemplateById(sysInnerid);
                    AssertUtil.isNotNull(nextSystemTemplate, SystemErrorCode.DB_ACCESS_ERROR);

                    Boolean isNextServer = NetworkConfigCache.getCommunicationConfigById(
                        nextSystemTemplate.getUrlId()).isServer();
                    myTemplateType = getTemplateType(isServer, isNextServer);

                    String transferId = AtsframeStrUtil.substringAfter(transferUr, "transferId=");
                    AssertUtil.isNotBlank(transferId, SystemErrorCode.ILLEGAL_PARAMETER);
                    strPara = "innerid=" + usertemplate.getInnerid() + "&transferId=" + transferId;
                } else {
                    myTemplateType = getTemplateType(isServer);
                    strPara = "innerid=" + usertemplate.getInnerid();
                }
            }
            if (bAdd) {
                strPara += "&newName=" + userName;
            }
            myUserInfoList.setTemplateGetPara(strPara);
            myUserInfoList.setTemplateType(myTemplateType);
            myUserInfoList.setTemplateLable(templateLable);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "获取完整配置失败，ID为", usertemplate.getInnerid());
        }

        return myUserInfoList;
    }

    /** 
     * @see com.mock.biz.shared.GetUserList#getDetailInfo(java.lang.String)
     */
    public List<DetailInfoInner> getDetailInfo(final String innerID) {
        List<DetailInfoInner> listDetailInfoInner = new ArrayList<DetailInfoInner>();

        AssertUtil.isNotBlank(innerID, SystemErrorCode.ILLEGAL_PARAMETER);
        LoggerUtil.info(logger, "请求用户模板innerID为", innerID);

        //获取用户配置
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(innerID);
        AssertUtil.isNotNull(usertemplate, SystemErrorCode.DB_ACCESS_ERROR);

        //获取系统模板
        SystemTemplate systemTemplate = SystemTemplateCache.getSystemTemplateById(usertemplate
            .getSystemId());
        AssertUtil.isNotNull(systemTemplate, SystemErrorCode.DB_ACCESS_ERROR);

        //通讯配置
        CommunicationConfig communicationConfig = NetworkConfigCache
            .getCommunicationConfigById(systemTemplate.getUrlId());
        AssertUtil.isNotNull(communicationConfig, SystemErrorCode.DB_ACCESS_ERROR);

        String templateUrl = communicationConfig.getUri().getUrl();
        AssertUtil.isNotNull(templateUrl, SystemErrorCode.DB_ACCESS_ERROR);
        String strBindInnerId = usertemplate.getBindInnerID();

        if (StringUtil.isNotEmpty(strBindInnerId)) {
            //走合并的显示
            DetailInfoInner detailInfoInner = createDetailInfoInner(usertemplate, systemTemplate,
                communicationConfig);
            detailInfoInner.setNextTypeEnum(NextTypeEnum.MERGENEXT);
            listDetailInfoInner.add(detailInfoInner);

            String[] arrBindIds = StringUtil.split(strBindInnerId, ",");
            for (String bindId : arrBindIds) {
                if (StringUtil.isEmpty(bindId)) {
                    continue;
                }
                //获取用户配置
                usertemplate = UserTemplateCache.getUsertemplateById(bindId);
                if (null == usertemplate) {
                    logger.error("获取合并的用户配置失败,子id为" + bindId);
                    continue;
                }

                //获取系统模板
                systemTemplate = SystemTemplateCache.getSystemTemplateById(usertemplate
                    .getSystemId());
                if (null == systemTemplate) {
                    logger.error("获取合并的公共配置失败,子系统id为" + usertemplate.getSystemId());
                    continue;
                }

                //通讯配置
                communicationConfig = NetworkConfigCache.getCommunicationConfigById(systemTemplate
                    .getUrlId());
                if (null == communicationConfig) {
                    logger.error("获取合并的通讯配置失败,子通讯id为" + systemTemplate.getUrlId());
                    continue;
                }

                detailInfoInner = createDetailInfoInner(usertemplate, systemTemplate,
                    communicationConfig);
                listDetailInfoInner.add(detailInfoInner);
            }
        } else {
            //是否存在转发地址
            String transferUr = getTransferUrb(usertemplate);

            //完成第一个配置
            DetailInfoInner detailInfoInner = createDetailInfoInner(usertemplate, systemTemplate,
                communicationConfig);
            AssertUtil.isNotNull(detailInfoInner, SystemErrorCode.SYSTEM_ERROR);
            listDetailInfoInner.add(detailInfoInner);

            if (AtsframeStrUtil.isNotEmpty(transferUr)) {
                listDetailInfoInner.get(0).setNextTypeEnum(NextTypeEnum.TRANSFER);
                String sysInnerid = AtsframeStrUtil.substringBefore(
                    AtsframeStrUtil.substringAfter(transferUr, "sysInnerid="), "&");
                if (StringUtil.isEmpty(sysInnerid)) {
                    logger.error("获取转发的公共ID出错,传入的urlid为" + transferUr);
                } else {
                    systemTemplate = SystemTemplateCache.getSystemTemplateById(sysInnerid);
                    if (null == systemTemplate) {
                        logger.error("获取转发的公共配置出错,传入的sysInnerid为" + sysInnerid);
                    } else {
                        communicationConfig = NetworkConfigCache
                            .getCommunicationConfigById(systemTemplate.getUrlId());
                        if (null == communicationConfig) {
                            logger.error("获取转发的通讯配置出错,传入的UID为" + systemTemplate.getUrlId());
                        } else {
                            String macthSting = AtsframeStrUtil.substringAfter(transferUr,
                                "transferId=");
                            if (StringUtil.isEmpty(macthSting)) {
                                logger.error("无法获取transferId,传入的transferId为" + transferUr);
                            } else {
                                usertemplate = UserTemplateCache.getUsertemplateByMacht(macthSting,
                                    sysInnerid);
                                if (null == usertemplate) {
                                    logger.error("无法获取用户配置详情,传入的匹配为" + sysInnerid + macthSting);
                                } else {
                                    detailInfoInner = createDetailInfoInner(usertemplate,
                                        systemTemplate, communicationConfig);
                                    listDetailInfoInner.add(detailInfoInner);
                                }
                            }
                        }
                    }
                }
            } else {
                listDetailInfoInner.get(0).setNextTypeEnum(NextTypeEnum.NONEXT);
            }
        }

        return listDetailInfoInner;
    }

    /**
     * 根据用户模板查找转发的URL
     * 
     * @param fistUsertemplate
     * @return
     */
    private String getTransferUrb(Usertemplate usertemplate) {
        List<TemplateDetail> transferUrllist = usertemplate.getTemplateDetail();
        String transferUr = null;
        for (TemplateDetail templateDetail : transferUrllist) {
            if (null == templateDetail) {
                continue;
            }

            transferUr = templateDetail.getDetailValue().getSendUrl();
            if (AtsframeStrUtil.isNotEmpty(transferUr)) {
                break;
            }
        }
        return transferUr;
    }

    /**
     * 构造一个DetailInfoInner 对象
     * 
     * @param fistUsertemplate
     * @param systemTemplate
     * @param url
     * @param isServer
     * @param LocalIp
     * @return 
     */
    private DetailInfoInner createDetailInfoInner(final Usertemplate usertemplate,
                                                  final SystemTemplate systemTemplate,
                                                  final CommunicationConfig communicationConfig) {
        AssertUtil
            .isNotNull(usertemplate, SystemErrorCode.ILLEGAL_PARAMETER, "usertemplate参数为null");
        AssertUtil.isNotNull(systemTemplate, SystemErrorCode.ILLEGAL_PARAMETER,
            "systemTemplate参数为null");
        AssertUtil.isNotNull(communicationConfig, SystemErrorCode.ILLEGAL_PARAMETER,
            "communicationConfig参数为null");

        String LocalIp = ApiUtilTool.getHostName();

        DetailInfoInner detailInfoInner = new DetailInfoInner();
        detailInfoInner.setUsertemplate(usertemplate);
        Boolean isServer = communicationConfig.isServer();
        detailInfoInner.setIsServer(isServer);
        detailInfoInner.setCharset(communicationConfig.getCharset().toString());
        String sendType = communicationConfig.getProperties(PropertiesConfigEnum.HTTP_REQ_TYPE);
        if (AtsframeStrUtil.isNotBlank(sendType)) {
            detailInfoInner.setReqType(sendType);
        }
        detailInfoInner.setMessageRunMode(communicationConfig.getMsgRunMode());

        String url = communicationConfig.getUri().getUrl();
        detailInfoInner.setUrl(AtsframeStrUtil.replace(url, "localhost", LocalIp));
        if (isServer) {
            detailInfoInner.setSysId(systemTemplate.getSysId());

            detailInfoInner.setMachDescrption(systemTemplate.getMacthdescription());
            String macthWay = (String) systemTemplate.getProperties().getObject("CodeRulecodeRule");
            if (AtsframeStrUtil.isEmpty(macthWay)) {
                macthWay = (String) systemTemplate.getProperties().getObject("CodeRulej8583");
            }
            AssertUtil.isNotBlank(macthWay, TransferErrorCode.ILLEGAL_PARAMETER);
            detailInfoInner.setCodeRule(macthWay);

            String codeRuleS = (String) systemTemplate.getProperties().getObject(
                "CodeRulecodeRuleS");
            if (AtsframeStrUtil.isNotBlank(codeRuleS)) {
                detailInfoInner.setCodeRuleS(codeRuleS);
            }
            String codeRuleT = (String) systemTemplate.getProperties().getObject(
                "CodeRulecodeRuleT");
            if (AtsframeStrUtil.isNotBlank(codeRuleT)) {
                detailInfoInner.setCodeRuleT(codeRuleT);
            }
        }

        return detailInfoInner;
    }

    /**  
     * @see com.mock.biz.shared.GetUserList#getAlllList()
     */

    @SuppressWarnings("rawtypes")
    public List<UserInfoList> getAllConfigList(String userName) {
        AssertUtil.isNotNull(userName, SystemErrorCode.ILLEGAL_PARAMETER);
        LoggerUtil.info(logger, "当前用户名为", userName);

        Map<String, CommunicationConfig> mapAllConfig = NetworkConfigCache.getAllNorlmal();

        List<UserInfoList> listUserInfoList = new ArrayList<UserInfoList>();

        UserInfoList myUserInfoList;
        String communicationId;
        String sysInnerId;
        SystemTemplate mySystemTemplate = null;
        Usertemplate myUsertemplate = null;
        CommunicationConfig myCommunicationConfig = null;
        List<Usertemplate> listUsertemplate = null;

        String LocalIp = ApiUtilTool.getHostName();

        for (Iterator ite = mapAllConfig.entrySet().iterator(); ite.hasNext();) {
            Map.Entry entry = (Map.Entry) ite.next();
            communicationId = (String) entry.getKey();
            myCommunicationConfig = (CommunicationConfig) entry.getValue();
            if (null == myCommunicationConfig) {
                continue;
            }

            //不显示转发属性的地址
            if (myCommunicationConfig.isIstransfer()) {
                continue;
            }

            mySystemTemplate = SystemTemplateCache.getSystemTemplate(communicationId);
            if (null == mySystemTemplate) {
                continue;
            }

            sysInnerId = mySystemTemplate.getSysId();

            listUsertemplate = UserTemplateCache.getUsertemplateBySysId(sysInnerId);
            if (null == listUsertemplate || listUsertemplate.isEmpty()) {
                continue;
            }

            myUsertemplate = listUsertemplate.get(0);

            myUserInfoList = getOneUserInfoList(myUsertemplate, LocalIp, myCommunicationConfig,
                userName, true);
            if (null == myUserInfoList) {
                continue;
            }

            listUserInfoList.add(myUserInfoList);
        }

        return listUserInfoList;
    }

    private TemplateType getTemplateType(Boolean bFirst, Boolean bSecond) {
        if (bFirst && bSecond) {
            return TemplateType.SEVER_TRANSFER_SEVER;
        } else if (bFirst && !bSecond) {
            return TemplateType.SEVER_TRANSFER_CLIENT;
        } else if (!bFirst && bSecond) {
            return TemplateType.CLIENT_TRANSFER_SEVER;
        } else if (!bFirst && !bSecond) {
            return TemplateType.CLIENT_TRANSFER_CLIENT;
        }
        return null;
    }

    private TemplateType getTemplateType(Boolean bFirst) {
        if (bFirst) {
            return TemplateType.SEVER_TYPE;
        } else {
            return TemplateType.CLIENT_TYPE;
        }
    }

    /** 
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws XPathExpressionException 
     * @see com.mock.biz.shared.GetUserList#get8583Template(java.lang.String)
     */
    public J8583Model get8583Template(final String detailId) throws XPathExpressionException,
                                                            ParserConfigurationException,
                                                            SAXException, IOException {
        DetailMsg myDetailMsg = UserTemplateCache.getDetailMsg(Long.parseLong(detailId));
        AssertUtil.isNotNull(myDetailMsg, SystemErrorCode.SYSTEM_ERROR);

        String j8583String = myDetailMsg.getTemplateMsg().get("template").get("j8583");
        AssertUtil.isNotNull(j8583String, SystemErrorCode.SYSTEM_ERROR);

        return J8683Envelope.getJ8583Model(j8583String);
    }

}