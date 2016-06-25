/**
 * Alipay.com Inc.
 * Copyright ("c") 2004-2012 All Rights Reserved.
 */
package com.mock.biz.shared.domain;

/**
 * 用于在配置的时候，判断哪个用户配置的枚举
 * 
 * @author hongliang.ma
 * @version $Id: UserValueEnum.java, v 0.1 2012-9-8 上午11:35:47 hongliang.ma Exp $
 */
public enum UserValueEnum {
    /**
     * 
     */
    CODERULE("CodeRule"),
    /**
     * 
     */
    XMLPARSE("XMLParse"),
    /**
     * 
     */
    SERIALPARSE("SerialParse"),
    /**
     * 
     */
    KEYVALUEPARSE("KeyValueParse"),
    /**
     * 
     */
    DELAYACTION("DelayAction"),
    /**
     * 
     */
    LENGTHCODERULE("LengthCodeRule"),
    /**
     * 
     */
    BUILDLENGTHADAPTOR("BuildLengthAdaptor"),
    /**
     * 
     */
    MESSAGEPARSER("MessageParser"),
    /**
     * 
     */
    MESSAGEBUILD("MessageBuild"),
    /**
     * 
     */
    INSERTFIELDBUILD("InsertFieldBuild"),
    /**
     * 
     */
    MSGLENGTHPARSE("MsgLengthParse"),
    /**
     * 
     */
    MSGLENGTHBUILD("MsgLengthBuild"),
    /**
     * 
     */
    DECODER("Decoder"),
    /**
     * 
     */
    DESMACADAPTOR("DESMacAdaptor"),
    /**
     * 
     */
    KTFILEUPLOADADAPTOR("KTFileUploadAdaptor"),
    /**
     * 
     */
    FILEUPLOADADAPTOR("FileUploadAdaptor"),
    /**
     * 
     */
    FILEREADERADAPTOR("FileReaderAdaptor"),
    /**
     * 
     */
    DBCONNECTIONADAPTOR("DBConnectionAdaptor"),
    /**
     * 
     */
    LOGGERTODATAADAPTOR("LoggerToDataAdaptor"),
    /**
     * 
     */
    MAPPINGVALUEACTION("MappingValueAction"),
    /**
     * 
     */
    XMLSUBADAPTOR("XMLSubAdaptor"),
    /**
     * 
     */
    XMLTOMAPPING("XMLToMapping"),
    /**
     * 
     */
    DESSH1SIGNER("DESSH1Signer"),
    /**
     * 
     */
    STRINGSUBADAPTOR("StringSubAdaptor"),
    /**
     * 
     */
    MOENYCONVERT("MoenyConvert"),
    /**
     * 
     */
    MD5SIGNER("MD5Signer"),
    /**
     * 
     */
    CALLBACKMESSAGEPARSER("CallBackMessageParser"),
    /**
     * 
     */
    CALLBACKMESSAGEBUILD("CallBackMessageBuild");

    public final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private UserValueEnum(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     */
    public String getCode() {
        return code;
    }
}
