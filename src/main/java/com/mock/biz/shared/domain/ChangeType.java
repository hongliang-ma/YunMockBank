
package com.mock.biz.shared.domain;

/**
 * 
 * @author hongliang.ma
 * @version $Id: ChangeType.java, v 0.1 2012-8-3 上午11:12:38 hongliang.ma Exp $
 */
public enum ChangeType {
    /** 修改匹配方式 */
    MATCHWAY,
    /** 修改匹配方式说明 */
    MATCHDES,
    /**更改匹配字符串 */
    MATCHSTR,
    /**一般工具的MAP内容 */
    NORMALVALUE,
    /**模板的MAP内容 */
    TEMPLATEVALUE,
    /*** 8583直接修改字符串 */
    MESSAGEPARSER,
    /**更改模板名字 */
    TEMPLATENAME
}
