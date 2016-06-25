
package com.mock.biz.shared;

import java.util.List;

import com.mock.biz.shared.domain.MergeDetail;
import com.mock.biz.shared.domain.SysconfigDes;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.transaction.template.CommunicationPart;
import com.mock.core.model.transaction.template.TemplateDetail;

/**
 *  各种维护的入口参数
 *  
 * @author hongliang.ma
 * @version $Id: MaintainFacade.java, v 0.1 2012-10-26 下午1:17:41 hongliang.ma Exp $
 */
public interface MaintainFacade {
    /**
     * 维护合并结果
     * 
     * @param mergeDetail
     * @return
     */
    public String mergeFunction(final MergeDetail mergeDetail);

    /**
     * 查找通讯
     * @param seachValue
     * @return
     */

    public List<CommunicationConfig> FindCom(final String seachValue);

    /**
     * 查找公共模板
     * @param seachValue
     * @return
     */
    public List<SysconfigDes> findSysTem(final String seachValue);

    /**
     * 查找用户模板
     * @param seachValue
     * @return
     */
    public List<TemplateDetail> FindUserTemp(final String seachValue);

    /**
     * 更改通讯相关的内容
     * @param strChangeID通讯ID
     * @param labels通讯标签
     * @param communicationChange
     * @return
     */
    public String changeCom(final String strChangeID, final CommunicationPart communicationPart,
                            final String labels);

    /**
     * 更改公共部分报文
     * 
     * @param sysconfigDes
     * @return
     */
    public String changeSysTemplate(SysconfigDes sysconfigDes);

    /**
     * 开始更新模板
     * 
     * @param userTemplateId
     * @param listDetailMsg
     * @return
     */
    public String changeUserDetailTemp(final String userTemplateId, final List<String> listDetailMsg);
}
