
package com.mock.core.model.shared.enums;

/**
 * 数据库连接枚举值
 * @author jun.qi
 * @version $Id: DBConnectionEnum.java, v 0.1 2012-7-3 下午01:25:04 jun.qi Exp $
 */
public enum DBConnectionEnum {
    
    //键值对
    PARAM("parameter"),
    
    //内容
    CONTENT("content");
    
    private final String code;

    /**
     * 私有构造函数。
     * 
     * @param code
     */
    private DBConnectionEnum(String code) {
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