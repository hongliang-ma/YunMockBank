
package com.mock.core.model.shared.enums;

/**
 * MD5加签枚举值
 * @author jun.qi
 * @version $Id: MD5SignerEnum.java, v 0.1 2012-7-4 下午02:07:19 jun.qi Exp $
 */
public enum MD5SignerEnum {

  //放到返回报文中
    FORWARD("forward"),
    //放到请求报文中
    ORIGIN("origin");
    
    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private MD5SignerEnum(String code) {
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
