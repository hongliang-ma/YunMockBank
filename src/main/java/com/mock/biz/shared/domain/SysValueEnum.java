
package com.mock.biz.shared.domain;

/**
 *  用于在配置的时候，判断哪个公共配置的枚举
 * @author hongliang.ma
 * @version $Id: SysValueEnum.java, v 0.1 2012-9-7 下午4:44:39 hongliang.ma Exp $
 */
public enum SysValueEnum {
    /**
     * 协议码
     */
    CODERULE("CodeRule"),
    /**
     * 截取指定长度
     */
    LENGTHCODERULE("LengthCodeRule"),
    /**
     * 8583
     */
    MSGLENGTHPARSE("MsgLengthParse"),
    /**
     * 8583 交行快捷
     */
    INSERTFIELDPARSER("InsertFieldParser"),
    /**
     * 解码器
     */
    DECODER("Decoder"),

    /**
     * 截取指定字符串配置
     */
    STRINGSUBADAPTOR("StringSubAdaptor"),

    /**  截取特殊的键值对为字符串报文*/
    BUILDTOSTRING("BuildToString"),

    /**
     * XML截取指定字符串
     */
    XMLSUBADAPTOR("XMLSubAdaptor"),

    /** MAP转键值对报文*/
    MAPTOKEYVALUE("MapToKeyValue");

    public final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private SysValueEnum(String code) {
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
