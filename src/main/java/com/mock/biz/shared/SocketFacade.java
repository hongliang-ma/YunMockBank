
package com.mock.biz.shared;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Socket操作Facade，WEB层访问BIZ层的入口
 * @author hongliang.ma
 * @version $Id: SocketFacade.java, v 0.1 2012-8-22 下午5:00:55 hongliang.ma Exp $
 */
public interface SocketFacade {

    /**
     * 发送TCP消息
     * 
     * @param request
     * @param response
     * @return
     */
    public void sendSocketMessage(HttpServletRequest request, HttpServletResponse response);

}
