
package com.mock.core.service.shared.repository;

import java.util.List;

import com.mock.core.model.transaction.template.SystemTemplate;

/**
 * 公共部分，通常是和具体的URl绑定的
 * 
 * @author hongliang.ma
 * @version $Id: SystemTemplateRepository.java, v 0.1 2012-6-20 下午2:33:24 hongliang.ma Exp $
 */
public interface SystemTemplateRepository {

    /**
    * 获取所有的用户模板
    * 
    * @return  用户模板列表
    */
    public List<SystemTemplate> loadAll();

    /**
     * 根据ID号获取系统模板
     * 
     * @param sysInnerId
     * @return
     */
    public SystemTemplate loadById(final String sysInnerId);

    /**
     * 更新匹配方式
     * 
     * @param sysInnerId  系统模板ID
     * @param newMacthWay  新的匹配方式
     */
    public void updateMacthWay(final String sysInnerId, final String newMacthWay);

    /**
     * 更新匹配方式描述
     * 
     * @param sysInnerId
     * @param newMactchDes
     */
    public void updateMactchDes(final String sysInnerId, final String newMactchDes);

    /**
     * 根据系统ID删除系统配置
     * 
     * @param sysInnerId
     */
    public void deleteSysTemplateBySysId(final String sysInnerId);

    /**
     * 更新系统模板
     * 
     * @param sysInnerId
     * @param sysTemplate
     * @return
     */
    public Boolean updateSysTemplate(final String sysInnerId, final String sysTemplate);

}
