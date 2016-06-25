/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.file.FileReaderAdaptor;
import com.mock.core.service.transaction.component.file.FileUploadAdaptor;
import com.mock.core.service.transaction.component.file.KTFileUploadAdaptor;
import com.mock.core.service.transaction.component.file.PostFileAdaptor;
import com.mock.core.service.transaction.component.message.AssertAdaptor;
import com.mock.core.service.transaction.component.message.BuildLengthAdaptor;
import com.mock.core.service.transaction.component.message.BuildToString;
import com.mock.core.service.transaction.component.message.CodeRule;
import com.mock.core.service.transaction.component.message.DBConnectionAdaptor;
import com.mock.core.service.transaction.component.message.DESMacAdaptor;
import com.mock.core.service.transaction.component.message.DESSH1Signer;
import com.mock.core.service.transaction.component.message.DateCode;
import com.mock.core.service.transaction.component.message.Decoder;
import com.mock.core.service.transaction.component.message.DelayAction;
import com.mock.core.service.transaction.component.message.Encoder;
import com.mock.core.service.transaction.component.message.KeyValueParse;
import com.mock.core.service.transaction.component.message.LengthCodeRule;
import com.mock.core.service.transaction.component.message.LoggerToDataAdaptor;
import com.mock.core.service.transaction.component.message.MD5Signer;
import com.mock.core.service.transaction.component.message.MapToKeyValue;
import com.mock.core.service.transaction.component.message.MappingValueAction;
import com.mock.core.service.transaction.component.message.MoenyConvert;
import com.mock.core.service.transaction.component.message.RandomCode;
import com.mock.core.service.transaction.component.message.RepeatCode;
import com.mock.core.service.transaction.component.message.ReturnMsghand;
import com.mock.core.service.transaction.component.message.SerialParse;
import com.mock.core.service.transaction.component.message.StringSubAdaptor;
import com.mock.core.service.transaction.component.message.XMLParse;
import com.mock.core.service.transaction.component.message.XMLSubAdaptor;
import com.mock.core.service.transaction.component.message.XMLToMapping;
import com.mock.core.service.transaction.component.message.XmlMaxReplace;
import com.mock.core.service.transaction.component.message.XmlParaReplace;
import com.mock.core.service.transaction.component.message.parser.CallBackMessageBuild;
import com.mock.core.service.transaction.component.message.parser.CallBackMessageParser;
import com.mock.core.service.transaction.component.message.parser.InsertFieldBuild;
import com.mock.core.service.transaction.component.message.parser.InsertFieldParser;
import com.mock.core.service.transaction.component.message.parser.MessageBuild;
import com.mock.core.service.transaction.component.message.parser.MessageParser;
import com.mock.core.service.transaction.component.message.parser.MsgLengthBuild;
import com.mock.core.service.transaction.component.message.parser.MsgLengthParse;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.MonitorLogUtil;

/**
 * 协议工厂类,实例需要获取某个协议处理类型，用此工厂类
 * 
 * @author zhao.xiong
 * @version $Id: ProtocolFactory.java, v 0.1 2011-9-5 下午07:27:32 zhao.xiong Exp $
 */
public final class ComponetFactory {

    /** logger */
    private static final Logger                 logger      = LoggerFactory
                                                                .getLogger(ComponetFactory.class);

    private static Map<String, ComponetHandler> protocolMap = new HashMap<String, ComponetHandler>();

    /**
     * 禁用构造函数
     */
    private ComponetFactory() {
        //禁用构造函数
    }

    /** 
     * 所有的工具类注册在此
     * 
     */

    static {
        MonitorLogUtil.init("ComponetFactory工具开始初始化");
        registerProtocol("LengthCodeRule", LengthCodeRule.class);
        registerProtocol("BuildLengthAdaptor", BuildLengthAdaptor.class);
        registerProtocol("CodeRule", CodeRule.class);
        registerProtocol("MoenyConvert", MoenyConvert.class);
        registerProtocol("DateCode", DateCode.class);
        registerProtocol("XMLParse", XMLParse.class);
        registerProtocol("SerialParse", SerialParse.class);
        registerProtocol("KeyValueParse", KeyValueParse.class);
        registerProtocol("StringSubAdaptor", StringSubAdaptor.class);
        registerProtocol("XMLSubAdaptor", XMLSubAdaptor.class);
        registerProtocol("MappingValueAction", MappingValueAction.class);
        registerProtocol("XMLToMapping", XMLToMapping.class);
        registerProtocol("DESSH1Signer", DESSH1Signer.class);
        registerProtocol("DelayAction", DelayAction.class);
        registerProtocol("RandomCode", RandomCode.class);
        registerProtocol("RepeatCode", RepeatCode.class);
        registerProtocol("LoggerToDataAdaptor", LoggerToDataAdaptor.class);
        registerProtocol("DBConnectionAdaptor", DBConnectionAdaptor.class);
        registerProtocol("FileReaderAdaptor", FileReaderAdaptor.class);
        registerProtocol("FileUploadAdaptor", FileUploadAdaptor.class);
        registerProtocol("KTFileUploadAdaptor", KTFileUploadAdaptor.class);
        registerProtocol("Decoder", Decoder.class);
        registerProtocol("DESMacAdaptor", DESMacAdaptor.class);
        registerProtocol("Encoder", Encoder.class);
        registerProtocol("MD5Signer", MD5Signer.class);
        registerProtocol("MessageParser", MessageParser.class);
        registerProtocol("MessageBuild", MessageBuild.class);
        registerProtocol("MsgLengthParse", MsgLengthParse.class);
        registerProtocol("MsgLengthBuild", MsgLengthBuild.class);
        registerProtocol("InsertFieldParser", InsertFieldParser.class);
        registerProtocol("InsertFieldBuild", InsertFieldBuild.class);
        registerProtocol("CallBackMessageParser", CallBackMessageParser.class);
        registerProtocol("CallBackMessageBuild", CallBackMessageBuild.class);
        registerProtocol("AssertAdaptor", AssertAdaptor.class);
        registerProtocol("XmlParaReplace", XmlParaReplace.class);
        registerProtocol("ReturnMsghand", ReturnMsghand.class);
        registerProtocol("BuildToString", BuildToString.class);
        registerProtocol("MapToKeyValue", MapToKeyValue.class);
        registerProtocol("PostFileAdaptor", PostFileAdaptor.class);
        registerProtocol("XmlMaxReplace", XmlMaxReplace.class);
    }

    public static void startInit() {
        MonitorLogUtil.init("ComponetFactory  初始化结束");
    }

    /**
     * 获取具体的工具类议处理
     * @param <T>
     * @param protocol 工具类
     * @param  transferData 从路由传送过来的数据
     * @param localTransferData 该配置本身数据库中的配置
     * @return
     * @throws Exception 
     */
    public static void componetHandler(String protocol, TransferData transferData,
                                       final TransferData localTransferData)
                                                                            throws AnymockException {
        if (!protocolMap.containsKey(protocol)) {
            throw new AnymockException(TransactionErrorCode.CANNOT_FIND_COMPONENT, protocol + "不存在");
        }
        protocolMap.get(protocol).process(transferData, localTransferData);
    }

    /**
     * 获取内部具体的工具类议处理
     * @param <T>
     * @param protocol 工具类
     * @param  transferData 从路由传送过来的数据
     * @param localTransferData 该配置本身数据库中的配置
     * @return
     * @throws Exception 
     */
    public static void componetSubHandler(String protocol, TransferData transferData,
                                          final TransferData localTransferData)
                                                                               throws AnymockException {
        if (!protocolMap.containsKey(protocol)) {
            throw new AnymockException(TransactionErrorCode.CANNOT_FIND_COMPONENT, protocol + "不存在");
        }
        protocolMap.get(protocol).processInner(transferData, localTransferData);
    }

    /**
     *  
     * 注册协议处理器
     * @param <T>
     * @param protocol
     * @param clz
     */
    private static void registerProtocol(String protocol, Class<? extends ComponetHandler> clz) {

        try {
            protocolMap.put(protocol, clz.newInstance());
        } catch (InstantiationException e) {
            ExceptionUtil.caught(e, "启动实例失败");
            throw new AnymockException(SystemErrorCode.TOOL_INIT_ERROR);
        } catch (IllegalAccessException e) {
            ExceptionUtil.caught(e, "启动实例IllegalAccessException");
            throw new AnymockException(SystemErrorCode.TOOL_START_ERROR);
        }

        LoggerUtil.info(logger, "注册协议", protocol);
    }

}
