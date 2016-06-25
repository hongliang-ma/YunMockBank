
package com.mock.biz.messages.routing;

import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.model.transaction.template.Usertemplate;

/**
 * 用户模板处理器路由
 * 
 * @author hongliang.ma
 * @version $Id: UserTemplateRouting.java, v 0.1 2012-6-28 下午4:21:31 hongliang.ma Exp $
 */
public interface UserTemplateRoute {

    /**
     * 用户模板的路由
     * 
     * @param usertemplate    用户模板
     * @param transferData  
     * @return  存在转发时的地址
     */
    public String route(Usertemplate usertemplate, TransferData transferData);
}
