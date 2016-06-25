
package com.mock.biz.shared.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.biz.shared.OperationEdit;
import com.mock.biz.shared.SetupConfigFacade;
import com.mock.biz.shared.domain.SysValueEnum;
import com.mock.biz.shared.domain.UserValueEnum;
import com.mock.dal.dataobject.CommunicationDO;
import com.mock.dal.dataobject.LableDO;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.enums.TransportProtocol;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.InstanceManagerFactory;
import com.mock.core.service.communication.framework.instance.ServerInstanceManager;
import com.mock.core.service.shared.cache.impl.AnymockLablesCache;
import com.mock.core.service.shared.cache.impl.SystemTemplateCache;
import com.mock.core.service.shared.cache.impl.UserTemplateCache;
import com.mock.core.service.shared.repository.AllConfigRepository;
import com.mock.core.service.shared.repository.CommunicationRepository;
import com.mock.core.service.shared.repository.SystemTemplateRepository;
import com.mock.core.service.shared.repository.UserTemplateRepository;
import com.mock.core.service.shared.repository.SequenceGenTool;
import com.mock.core.service.shared.thread.ThreadPoolService;
import com.mock.core.service.shared.thread.ThreadPoolService.PoolType;
import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.AnymockLable;
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
 * @version $Id: SetupConfigFacadeImpl.java, v 0.1 2012-9-6 下午2:24:11 hongliang.ma Exp $
 */
public class SetupConfigFacadeImpl implements SetupConfigFacade {

    private static final Logger      logger = LoggerFactory.getLogger(SetupConfigFacadeImpl.class);

    private AllConfigRepository      allConfigRepository;
    private CommunicationRepository  communicationRepository;
    private SystemTemplateRepository systemTemplateRepository;
    private UserTemplateRepository   userTemplateRepository;
    private OperationEdit            operationEdit;

    /** 
     * @see com.mock.biz.shared.SetupConfigFacade#setUpNormal(javax.servlet.http.HttpServletRequest)
     */
    public String setUpNormal(final HttpServletRequest request, final String transferUrl,
                              final String transferEx) {
        //先检查该地址是否存在,只有非转发的情况下的时候才去检查
        if (StringUtil.isEmpty(transferUrl)) {
            String bCheckBefore = checkBefore(request, "");
            String bCheckTransfer = checkBefore(request, "transfer_");
            if (StringUtil.isNotEmpty(bCheckBefore) || StringUtil.isNotEmpty(bCheckTransfer)) {
                String exiseId = StringUtil.isNotEmpty(bCheckBefore) ? bCheckBefore
                    : bCheckTransfer;
                return "errorHappen:该地址已经存在，地址为" + exiseId;
            }

            String wrongExise = CheckUrllegal(request, "");
            String wrongTrasnfer = CheckUrllegal(request, "transfer_");
            if (StringUtil.isNotEmpty(wrongExise) || StringUtil.isNotEmpty(wrongTrasnfer)) {
                String exiseId = StringUtil.isNotEmpty(wrongExise) ? wrongExise : wrongTrasnfer;
                return exiseId;
            }
        }

        CommunicationDO headPart = getHeadContext(request, transferEx);
        Boolean isClient = StringUtil.equalsIgnoreCase(headPart.getConnectType(), "CLIENT") ? true
            : false;
        SystemTemplate systemTemplate = new SystemTemplate();

        //先获取公共部分配置，按顺序配置
        String[] orderSysValue = AtsframeStrUtil.split(
            request.getParameter(transferEx + "orderSysValue"), ",");
        String[] orderUserValue = AtsframeStrUtil.split(
            request.getParameter(transferEx + "orderUserValue"), ",");
        StringBuilder sbfGetValue = new StringBuilder();
        List<TemplateDetail> templateDetails = null;
        Usertemplate usertemplate = new Usertemplate();
        try {
            JoinToSysTemplate(request, orderSysValue, sbfGetValue, transferEx);
            templateDetails = getTemplateList(request, orderUserValue, transferUrl, transferEx);
        } catch (Exception e) {
            LoggerUtil.warn(logger, "JoinToSysTemplate发生错误");
            return "errorHappen:参数有误，请重新确认参数!";
        }

        systemTemplate.setSysId(SequenceGenTool.genComponetInnerid());
        systemTemplate.setSysTemplate(sbfGetValue.toString());
        systemTemplate.setUrlId(headPart.getId());
        systemTemplate.setMacthdescription(request.getParameter(transferEx
                                                                + "sys-CodeRule-Description"));

        //然后按照用户部分配置，按照顺序配置
        if (!isClient) {
            usertemplate.setMatchstr(request.getParameter(transferEx + "user_matchstr"));
        }
        usertemplate.setTemplateName(request.getParameter(transferEx + "user_templatename"));
        String cookieName = ApiUtilTool.getCookieValue("anymock_cookie", request);
        String userName = AtsframeStrUtil.substringBefore(cookieName, "SPDFS");
        usertemplate.setUserName(userName);
        usertemplate.setTemplateDetail(templateDetails);
        String getSysId = systemTemplate.getSysId();
        try {
            LableDO lableDO = getHeadLableDO(request, headPart.getId(), transferEx);
            LableDO refreshDo = getHeadLableDO(request, headPart.getId(), transferEx);

            Boolean bReadOnly = false;
            bReadOnly = BooleanUtils.toBoolean(request.getParameter("isReadOnly"));

            allConfigRepository.InsertAllConfig(headPart, lableDO, systemTemplate, bReadOnly,
                usertemplate);
            //刷新缓存
            runTask(getSysId, refreshDo);
        } catch (Exception e) {
            LoggerUtil.warn(logger, "InsertAllConfig发生错误");
            return "errorHappen:数据库操作发生错误，请检查参数!";
        }

        return userTemplateRepository.loadUserTemplateBySysId(getSysId).get(0).getInnerid();

    }

    /**
     * 经常TCP是否是合法的地址
     * @param request
     */
    private String CheckUrllegal(final HttpServletRequest request, final String strTransfer) {
        String URl = request.getParameter(strTransfer + "URI");
        if (StringUtil.contains(StringUtil.toLowerCase(URl), "tcp")) {
            String openPort = StringUtil.substringAfterLast(URl, ":");
            if (!StringUtil.isNumeric(openPort)) {
                return "errorHappen:TCP端口号设置错误!";
            }
            if (StringUtil.countMatches(URl, ":") != 2) {
                return "errorHappen:TCP端口号设置必须为tcp://xxxx:端口号!";
            }
        }
        return null;
    }

    /** 
     * @see com.mock.biz.shared.SetupConfigFacade#setUpTransfer(javax.servlet.http.HttpServletRequest)
     */
    public String setUpTransfer(HttpServletRequest request) {
        //先搞好第二个
        String secondUriId = setUpNormal(request, "", "transfer_");
        if (StringUtil.contains(secondUriId, "errorHappen")) {
            return "errorHappen:新建转发配置发生错误";
        }
        //建第一个，转发到第二个
        StringBuilder sbfTranferUrl = new StringBuilder(5);
        sbfTranferUrl.append(request.getParameter("transfer_URI"));
        sbfTranferUrl.append("?sysInnerid=");
        sbfTranferUrl
            .append(userTemplateRepository.loadUserTemplateById(secondUriId).getSystemId());
        sbfTranferUrl.append("&transferId=");
        sbfTranferUrl.append(secondUriId);
        String firstUriId = setUpNormal(request, sbfTranferUrl.toString(), "");
        if (StringUtil.contains(firstUriId, "errorHappen")) {
            operationEdit.delConfigByIDNormal(secondUriId);
        }

        return firstUriId;
    }

    /**
     * 检查数据库中该地址是否存在,如果不存在，返回null
     * 
     * @param request
     * @return
     */
    private String checkBefore(HttpServletRequest request, String strTransfer) {
        String URl = request.getParameter(strTransfer + "URI");
        String localIp = ApiUtilTool.getHostName();
        String LocalUrl = "";
        if (StringUtil.contains(URl, localIp)) {
            LocalUrl = StringUtil.replace(URl, localIp, "localhost");
        }
        String serverType = request.getParameter(strTransfer + "connect_type");
        String commId = communicationRepository.getURlIdByURlAndType(URl, serverType);
        if (StringUtil.isEmpty(commId)) {
            commId = communicationRepository.getURlIdByURlAndType(LocalUrl, serverType);
            if (StringUtil.isNotEmpty(commId)) {
                return URl;
            }
        } else {
            return URl;
        }
        return null;
    }

    /**
     * 根据请求生成TemplateDetail列表
     * 
     * @param request 请求
     * @param orderUserValue  用户配置的列表
     * @param Url 存在转发时的转发ID
      * @return
     */
    private List<TemplateDetail> getTemplateList(final HttpServletRequest request,
                                                 final String[] orderUserValue, final String Url,
                                                 final String strTransfer) {
        List<TemplateDetail> templateDetails = new ArrayList<TemplateDetail>();
        TemplateDetail templateDetail = null;
        DetailMsg detailMsg = null;
        /** 直接的key-value值*/
        Map<String, String> keyValues = null;
        /**  template之类的存在多个key-value的 */
        Map<String, Map<String, String>> templateMsg = null;
        for (String userValue : orderUserValue) {
            detailMsg = new DetailMsg();
            detailMsg.setClassid(request.getParameter(userValue + "-classid"));
            detailMsg.setCname(request.getParameter(userValue + "-cname"));

            switch ((UserValueEnum) ApiUtilTool.getEnumByName(UserValueEnum.class,
                userValue.toUpperCase())) {
                case CODERULE: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "codeRule",
                            "state", "callflag", "j8583", "isbinary", "tpdu", "typelength",
                            "mtitype", "strbitset", "seletctTag", "splite" }, strTransfer);
                }
                    break;
                case XMLPARSE: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "state",
                            "namespace", "defaultrule", "template__error" }, strTransfer);
                    templateMsg = buildUserFinalTemplate(request, userValue, "key", "value",
                        strTransfer);
                }
                    break;
                case SERIALPARSE: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "state",
                            "template__error" }, strTransfer);
                    templateMsg = buildUserFinalTemplate(request, userValue, "key", "value",
                        strTransfer);
                }
                    break;
                case KEYVALUEPARSE: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "state",
                            "splite" }, strTransfer);
                    templateMsg = buildUserFinalTemplate(request, userValue, "key", "value",
                        strTransfer);

                }
                    break;
                case DELAYACTION: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "delay" },
                        strTransfer);
                }
                    break;
                case LENGTHCODERULE: {
                    keyValues = buildUserTemplateMap(request, userValue,
                        new String[] { "codeRule" }, strTransfer);
                }
                    break;
                case MESSAGEPARSER: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "MTIlength", "MTItype", "bitmap", "isbinary", "TPDU", "transMap" },
                        strTransfer);
                    templateMsg = buildUserFinalTemplate(request, userValue, "j8583", "value",
                        strTransfer);
                }
                    break;
                case MSGLENGTHPARSE: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "filterreverse", "length", "interChar", "lengthEncoding" }, strTransfer);
                }
                    break;
                case MSGLENGTHBUILD: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "filterreverse", "length", "interChar", "lengthEncoding" }, strTransfer);
                }
                    break;
                case DECODER: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "state",
                            "codeRule", "Encodetype" }, strTransfer);
                }
                    break;
                case FILEUPLOADADAPTOR: {
                    keyValues = buildUserTemplateMapWithNull(request, userValue, new String[] {
                            "template", "msgType" }, strTransfer);
                }
                    break;
                case FILEREADERADAPTOR: {
                    keyValues = buildUserTemplateMapWithNull(request, userValue, new String[] {
                            "path", "charset" }, strTransfer);
                }
                    break;
                case KTFILEUPLOADADAPTOR: {
                    keyValues = buildUserTemplateMapWithNull(request, userValue, new String[] {
                            "host", "fileType", "instId", "certId", "operateDate", "template" },
                        strTransfer);
                }
                    break;
                case DBCONNECTIONADAPTOR: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "reqType",
                            "dbName", "userName", "password" }, strTransfer);
                }
                    break;
                case LOGGERTODATAADAPTOR: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "msgType",
                            "directory" }, strTransfer);
                }
                    break;
                case MAPPINGVALUEACTION: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "state",
                            "key", "codeRule" }, strTransfer);
                }
                    break;
                case XMLSUBADAPTOR: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "codeRule",
                            "subRule", "split" }, strTransfer);
                }
                    break;
                case DESSH1SIGNER: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "encryptWholeMsg", "signFlag", "shaTarget", "encoding", "base64Flag",
                            "chunkFlag", "signTagName", "signParentTagName", "basekey", "workkey",
                            "shaTarget" }, strTransfer);
                }
                    break;
                case STRINGSUBADAPTOR: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "codeRule",
                            "state", "isRule" }, strTransfer);
                }
                    break;
                case MOENYCONVERT: {
                    keyValues = buildUserTemplateMap(request, userValue,
                        new String[] { "reqType" }, strTransfer);
                }
                    break;
                case MD5SIGNER: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "node_template", "stringmethod", "style", "mode" }, strTransfer);
                }
                    break;
                case CALLBACKMESSAGEPARSER: {
                    keyValues = buildUserTemplateMap(request, userValue, new String[] {
                            "MTIlength", "MTItype", "bitmap", "isbinary", "TPDU", "transMap" },
                        strTransfer);
                    templateMsg = buildUserFinalTemplate(request, userValue, "j8583", "value",
                        strTransfer);
                }
                    break;
                case MESSAGEBUILD: {
                }
                    break;
                case INSERTFIELDBUILD:{
                    
                }  
                    break;
                 
                case DESMACADAPTOR: {
                }
                    break;
                case XMLTOMAPPING: {
                }
                    break;
                case CALLBACKMESSAGEBUILD: {
                }
                    break;
                case BUILDLENGTHADAPTOR:{
                    keyValues = buildUserTemplateMap(request, userValue, new String[] { "Msglength",
                                                                                        "MsgType" }, strTransfer);
                }
                    break;
                default: {
                    LoggerUtil.warn(logger, "传入一个未知用户的类型,传入为", userValue);
                    throw new AnymockException(SystemErrorCode.ILLEGAL_PARAMETER, "传入一个未知用户的类型,传入为"
                                                                                  + userValue);
                }
            }

            if (null != keyValues && !keyValues.isEmpty()) {
                detailMsg.setKeyValues(keyValues);
            }

            if (null != templateMsg && !templateMsg.isEmpty()) {
                detailMsg.setTemplateMsg(templateMsg);
            }

            templateDetail = new TemplateDetail(detailMsg);
            templateDetails.add(templateDetail);
        }
        if (StringUtil.isNotEmpty(Url)) {
            templateDetail = templateDetails.get(templateDetails.size() - 1);
            templateDetail.getDetailValue().setSendUrl(Url);//这里要修改一下
        }

        return templateDetails;
    }

    /**
     * 拼接出真实的系统模板
     * @param request
     * @param orderSysValue
     * @param sbfGetValue
     * @param isClient 是否 直接的客户端
     */
    private void JoinToSysTemplate(final HttpServletRequest request, final String[] orderSysValue,
                                   final StringBuilder sbfGetValue, final String strTransfer) {
        for (String sysValue : orderSysValue) {
            switch ((SysValueEnum) ApiUtilTool.getEnumByName(SysValueEnum.class,
                sysValue.toUpperCase())) {
                case CODERULE: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }

                    sbfGetValue.append(buildSysTemplate(request, sysValue, new String[] {
                            "sys-CodeRule-codeRule", "sys-CodeRule-state", "sys-CodeRule-callflag",
                            "sys-CodeRule-j8583", "sys-CodeRule-isbinary", "sys-CodeRule-tpdu",
                            "sys-CodeRule-typelength", "sys-CodeRule-mtitype",
                            "sys-CodeRule-strbitset", "sys-CodeRule-seletctTag",
                            "sys-CodeRule-splite" }, strTransfer));

                }
                    break;
                case LENGTHCODERULE: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue,
                        new String[] { "sys-LengthCodeRule-codeRule" }, strTransfer));
                }
                    break;
                case BUILDTOSTRING: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue,
                        new String[] { "sys-BuildToString-key" }, strTransfer));

                }
                    break;
                case MSGLENGTHPARSE: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue, new String[] {
                            "sys-MsgLengthParse-filterreverse", "sys-MsgLengthParse-length",
                            "sys-MsgLengthParse-interChar", "sys-MsgLengthParse-lengthEncoding" },
                        strTransfer));
                }
                    break;
                case INSERTFIELDPARSER: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue, null, strTransfer));
                }
                    break;
                case DECODER: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue,
                        new String[] { "sys-Decoder-Encodetype", "sys-Decoder-codeRule",
                                "sys-Decoder-state" }, strTransfer));
                }
                    break;
                case STRINGSUBADAPTOR: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue, new String[] {
                            "sys-StringSubAdaptor-codeRule", "sys-StringSubAdaptor-state",
                            "sys-StringSubAdaptor-isRule" }, strTransfer));
                }
                    break;
                case MAPTOKEYVALUE: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue, null, strTransfer));
                }
                    break;
                case XMLSUBADAPTOR: {
                    if (sbfGetValue.length() > 0) {
                        sbfGetValue.append("&&");
                    }
                    sbfGetValue.append(buildSysTemplate(request, sysValue, new String[] {
                            "sys-XMLSubAdaptor-codeRule", "sys-XMLSubAdaptor-subRule",
                            "sys-XMLSubAdaptor-split" }, strTransfer));
                }
                default: {
                    LoggerUtil.warn(logger, "传入一个未知公共的值,传入为", orderSysValue);
                    throw new AnymockException(SystemErrorCode.ILLEGAL_PARAMETER, "传入一个未知公共的值,传入为"
                                                                                  + orderSysValue);
                }
            }
        }
    }

    /**
     * 获取头部的公共信息
     * 
     * @param request
     * @return
     */
    private CommunicationDO getHeadContext(HttpServletRequest request, String head) {
        CommunicationDO communicationDO = new CommunicationDO();
        communicationDO.setId(SequenceGenTool.genComponetInnerid());
        communicationDO.setConnectType(request.getParameter(head + "connect_type"));
        communicationDO.setProtocolType(request.getParameter(head + "protocol_type"));
        communicationDO.setCharset(request.getParameter(head + "charset"));
        communicationDO.setUri(request.getParameter(head + "URI"));
        communicationDO
            .setIstransfer(StringUtil.isEmpty(request.getParameter(head + "istransfer")) ? "FALSE"
                : "TRUE");
        communicationDO.setRecvDataType(request.getParameter(head + "recv_data_type"));
        communicationDO.setSendDataType(request.getParameter(head + "send_data_type"));
        communicationDO.setDescription(request.getParameter(head + "description"));
        communicationDO.setMaxCounter(0);
		communicationDO.setProperties(setDescription(request, head,
				new String[] {
                        "NEEDS_RESPONSE", "HTTP_REQ_TYPE", "TB_TOPIC",///////;
						"TB_EVENT_CODE", "IS_KEEPALIVE", "SSL_DBCHECK",
						"HTTPS_OPT" }));

        return communicationDO;
    }

    private String setDescription(final HttpServletRequest request, final String head,
                                  final String[] Desciptions) {
        StringBuilder description = new StringBuilder();
        String temp = null;
        for (String Desciption : Desciptions) {
            temp = request.getParameter(head + Desciption);
            if (StringUtil.isNotEmpty(temp)) {
                description.append(Desciption);
                description.append("=");
                description.append(temp);
                description.append("\n");
            }
        }

        return description.toString().trim();
    }

    private LableDO getHeadLableDO(final HttpServletRequest request, final String urlId,
                                   final String strTransfer) {
        LableDO lableDO = new LableDO();
        lableDO.setCommunicationId(urlId);
        lableDO.setLableName(request.getParameter(strTransfer.trim() + "anymock_labels"));
        return lableDO;
    }

    /**
     * 构建一个系统模板
     * 
     * @param request  HTTP请求
     * @param handMethd 工具类的类名
     * @param methdPara  该类下的参数
     */
    private String buildSysTemplate(final HttpServletRequest request, final String handMethd,
                                    final String[] methdParas, final String strTransfer) {
        String strGetFromReque = null;
        String strParaString = null;
        StringBuilder sbfGetValue = new StringBuilder();
        sbfGetValue.append(handMethd);
        if (null != methdParas) {
            sbfGetValue.append("((");
            //如果是客户端直接新建，则使用默认的键值对取值规则
            for (String methdPara : methdParas) {
                strGetFromReque = request.getParameter(strTransfer + methdPara);
                if (StringUtil.isNotEmpty(strGetFromReque)) {
                    sbfGetValue.append(StringUtil.substringAfterLast(methdPara, "-"));
                    sbfGetValue.append("=");
                    if (StringUtil.containsNone(methdPara, "j8583")) {
                        sbfGetValue.append(strGetFromReque);
                    } else {
                        strParaString = AtsframeStrUtil.filterXMLString(strGetFromReque);
                        strParaString = AtsframeStrUtil.replace(strParaString, "=", "-equal-");
                        sbfGetValue.append(strParaString);
                    }

                    sbfGetValue.append(",");
                }
            }

            sbfGetValue.deleteCharAt(sbfGetValue.length() - 1);
            sbfGetValue.append("))");
        }

        return sbfGetValue.toString();
    }

    /**
     * 构造一个Detail的MAp部分,内容不可以为空
     * 
     * @param request
     * @param handMethd
     * @param methdParas
     * @return
     */
    private HashMap<String, String> buildUserTemplateMap(final HttpServletRequest request,
                                                         final String handMethd,
                                                         final String[] methdParas,
                                                         final String strTransfer) {
        HashMap<String, String> keyValues = new HashMap<String, String>();
        String keyValue = null;
        String strParaString = null;
        for (String para : methdParas) {
            keyValue = request.getParameter(strTransfer + handMethd + "-" + para);
            if (StringUtil.isNotEmpty(keyValue)) {
                if (StringUtil.containsNone(para, "j8583")) {
                    keyValues.put(para, keyValue);
                } else {
                    strParaString = AtsframeStrUtil.filterXMLString(keyValue);
                    strParaString = AtsframeStrUtil.replace(strParaString, "=", "-equal-");
                    keyValues.put(para, strParaString);
                }
            }
        }

        return keyValues;
    }

    /**
     * 构造一个Detail的MAp部分,内容可以为空
     * 
     * @param request
     * @param handMethd
     * @param methdParas
     * @return
     */
    private HashMap<String, String> buildUserTemplateMapWithNull(final HttpServletRequest request,
                                                                 final String handMethd,
                                                                 final String[] methdParas,
                                                                 final String strTransfer) {
        HashMap<String, String> keyValues = new HashMap<String, String>();
        String keyValue = null;
        for (String para : methdParas) {
            keyValue = request.getParameter(strTransfer + handMethd + "-" + para);
            keyValues.put(para, keyValue);
        }

        return keyValues;
    }

    /**
     * 构造一个Detail的template部分,内容不可以为空
     * 
     * @param request
     * @param handMethd
     * @param methdParas
     * @return
     */
    private Map<String, Map<String, String>> buildUserFinalTemplate(final HttpServletRequest request,
                                                                    final String handMethd,
                                                                    final String key,
                                                                    final String value,
                                                                    final String strTransfer) {
        Map<String, Map<String, String>> templateMsg = new HashMap<String, Map<String, String>>();
        Map<String, String> templateMap = new HashMap<String, String>();
        if (!StringUtil.equalsIgnoreCase("j8583", key)) {
            templateMap.put(request.getParameter(strTransfer + handMethd + "-" + key),
                request.getParameter(handMethd + "-" + value));
        } else {
            templateMap.put("j8583", request.getParameter(strTransfer + handMethd + "-" + value));
        }
        templateMsg.put("template", templateMap);

        return templateMsg;
    }

    /**
     * 异步刷新缓存，全部采用新线程处理交易
     * 
     * @param transaction
     */
    public void runTask(final String sysId, final LableDO lableDO) {

        ThreadPoolService.addTask(PoolType.ASYN_REFRESH, new Runnable() {

            /** 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                try {
                    CommunicationConfig communicationConfig = communicationRepository
                        .getOneCommById(lableDO.getCommunicationId());
                    NetworkConfigCache.putOne(communicationConfig);
                    SystemTemplate systemTemplate = systemTemplateRepository.loadById(sysId);
                    SystemTemplateCache.modifyCache(systemTemplate);
                    UserTemplateCache.putNew(userTemplateRepository.loadUserTemplateBySysId(sysId)
                        .get(0));
                    AnymockLable anymockLable = new AnymockLable();
                    anymockLable.setCommunicationId(lableDO.getCommunicationId());
                    anymockLable.setLabList(lableDO.getLableName());
                    AnymockLablesCache.putNew(anymockLable);
                    Boolean isServer = communicationConfig.isServer();
                    if (isServer) {
                        getServerManager(communicationConfig.getProtocol()).startInstance(
                            communicationConfig.getCommunicationId());
                    }
                } catch (Exception e) {
                    ExceptionUtil.caught(e, "刷新用户缓存异常!");
                }
            }
        });

    }

    /**
     * 获取http的实例管理管理器,兼容HTTPS
     * @return
     */
    private ServerInstanceManager getServerManager(TransportProtocol protocal) {
        return InstanceManagerFactory.getServerInstanceManagerByProtocol(protocal);
    }

    /**
     * Setter method for property <tt>allConfigRepository</tt>.
     * 
     * @param allConfigRepository value to be assigned to property allConfigRepository
     */
    public void setAllConfigRepository(AllConfigRepository allConfigRepository) {
        this.allConfigRepository = allConfigRepository;
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
     * Setter method for property <tt>userTemplateRepository</tt>.
     * 
     * @param userTemplateRepository value to be assigned to property userTemplateRepository
     */
    public final void setUserTemplateRepository(UserTemplateRepository userTemplateRepository) {
        this.userTemplateRepository = userTemplateRepository;
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
     * Setter method for property <tt>systemTemplateRepository</tt>.
     * 
     * @param systemTemplateRepository value to be assigned to property systemTemplateRepository
     */
    public final void setSystemTemplateRepository(SystemTemplateRepository systemTemplateRepository) {
        this.systemTemplateRepository = systemTemplateRepository;
    }

}
