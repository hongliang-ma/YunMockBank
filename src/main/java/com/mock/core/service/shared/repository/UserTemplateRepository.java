
package com.mock.core.service.shared.repository;

import java.util.List;

import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.Usertemplate;

/**
 *  用户最后配置的用户模板仓库
 * 
 * @author hongliang.ma
 * @version $Id: UserTemplateRepositoryProxy.java, v 0.1 2012-5-30 下午2:50:54 hongliang.ma Exp $
 */
public interface UserTemplateRepository {

    /**
     * 获取所有的用户模板
     * 
     * @return  用户模板列表
     */
    public List<Usertemplate> loadAllUserTemplate();

    /**
     * 根据系统ID获取该有多少个用户配置
     * 
     * @param sysId
     * @return
     */
    public int getUserTemplateCountBySysId(final String sysId);

    /**
     * 记载指定的用户模板
     * 
     * @param innerid
     * @return 指定的用户模板
     */
    public Usertemplate loadUserTemplateById(final String innerid);

    /**
     * 根据用户名查找
     * 
     * @param userName
     * @return
     */
    public List<Usertemplate> loadUserTemplateByUser(final String userName);

    /**
     * 根据公共ID查找用户模板
     * 
     * @param sysId
     * @return
     */
    public List<Usertemplate> loadUserTemplateBySysId(final String sysId);

    /**
     * 更新使用次数
     * 
     * @param innerid
     */
    public void upDateUseCount(final String innerid);

    /**
     * 更新某个配置的为默认
     * 
     * @param innerid
     * @param isTrue
     */
    public void updateIsDefault(final String innerid, final Boolean isTrue);

    /**
     * 获取到默认的用户模板
     * @param  systemInnerid 系统模板ID
     * @return  返回默认的模板ID
     */
    public String getDefaultTemplate(String systemInnerid);

    /**
     * 更新匹配值
     * 
     * @param innerid
     * @param newMatch  新的匹配值
     */
    public void updateMatchstr(final String innerid, final String newMatch);

    /**
     * 更新模板的名字
     *  
     * @param innerid
     * @param newName
     */
    public void updateTemplateName(final String innerid, final String newName);

    /**
     * 根据ID复制
     * 
     * @param innerid  模板ID号
     * @param userName  新的用户名
     * @return
     */
    public String addUserTemplateToUser(final String innerid, final String userName);

    /**
     * 根据ID和转发ID，复制内容
     * 
     * @param idsToCopy    模板ID号
     * @param btransfer 是否转发 true为转发，false为合并
     * @param userName  新的用户名
     * @return
     */
    public List<String> addManyUserTemplateToUser(final List<String> idsToCopy,
                                                  final Boolean btransfer, final String userName);

    /**
     * 新增用户模板
     * 
     * @param sysInnerID  系统模板的ID
     * @param match   匹配值
     * @param templateName  模板名字
     * @param userName  用户名
     * @param detailMsgs  模板内容
     * @return
     */
    public String addUserTemplate(final String sysInnerID, final String match,
                                  final String templateName, final String userName,
                                  final List<DetailMsg> detailMsgs);

    /**
     * 删除用户模板
     * 
     * @param innerid  内部流水Id
     */
    public void deleteUserTemplate(final String innerid);

    /**
     * 根据ID和转发ID删除
     * 
     * @param idsToDel  删除的模板ID
     */
    public void deleteUserTemplate(final List<String> idsToDel);

    /**
     * 更新BindId
     * 
     * @param bindId
     * @param innerid
     */
    public void updateBindId(final String innerid, final String bindId);

    /**
     * 删除原先的模板详情，插入新的模板详情
     * 
     * @param innerid  用户模板ID
     * @param listNewDetail
     * @return
     */
    public void updateAllDetail(final String innerid, final List<String> listNewDetail);

}
