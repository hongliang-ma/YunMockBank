
package com.mock.core.service.shared.repository;

import com.mock.dal.dataobject.CommunicationDO;
import com.mock.dal.dataobject.LableDO;
import com.mock.core.model.transaction.template.SystemTemplate;
import com.mock.core.model.transaction.template.Usertemplate;

/**
 * anymock配置建立的操作，涉及到anymock全部的表
 * 
 * @author hongliang.ma
 * @version $Id: SetUpConfigRepository.java, v 0.1 2012-9-8 下午1:05:15 hongliang.ma Exp $
 */
public interface AllConfigRepository {

    /**
     * 新建一个配置
     * 
     * @param communicationDO 通讯配置
     * @param lableDO  标签
     * @param systemTemplate  公共模板
     * @param bReadOnly 是否个人模式
     * @param usertemplate  用户模板
     */
    public void InsertAllConfig(final CommunicationDO communicationDO, final LableDO lableDO,
                                final SystemTemplate systemTemplate, final Boolean bReadOnly,
                                final Usertemplate usertemplate);
}
