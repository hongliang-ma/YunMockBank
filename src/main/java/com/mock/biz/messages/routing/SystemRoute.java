
package com.mock.biz.messages.routing;

import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.model.transaction.template.Usertemplate;

/**
 * 系统模板处理器路由
 * 
 * @author hongliang.ma
 * @version $Id: SystemRouting.java, v 0.1 2012-6-28 下午4:19:33 hongliang.ma Exp $
 */
public interface SystemRoute {

    /**
     * 系统模板处理路由，从系统模板缓存中取出所有的配置，然后进行操作
     * 和用户模板的重要区别是，系统模板最后一步一定存在路由的分发，分发到具体的用户模板
     * 
     * @param urlId  通讯地址ID
     * @param transferData
     * @return  返回寻找到的用户模板
     */
    public Usertemplate route(String urlId, TransferData transferData);
}
