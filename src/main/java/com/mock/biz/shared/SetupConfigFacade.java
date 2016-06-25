
package com.mock.biz.shared;

import javax.servlet.http.HttpServletRequest;

/**
 * anymock建立的配置
 * 
 * @author hongliang.ma
 * @version $Id: SetupConfigFacade.java, v 0.1 2012-9-6 下午2:19:02 hongliang.ma Exp $
 */
public interface SetupConfigFacade {

    /**
     * 新建配置,普通配置
     * 
     * @param request  请求
     * @param bRefresh 建完之后，是否异步刷新
     * @param transferEx  新建的前缀
     * @param  transferUrl 转发的URL
     * @return
     */
    public String setUpNormal(final HttpServletRequest request, final String transferUrl,
                              final String transferEx);

    /**
     * 新建带有转发的配置
     * 
     * @param request
     * @return
     */
    public String setUpTransfer(final HttpServletRequest request);
}
