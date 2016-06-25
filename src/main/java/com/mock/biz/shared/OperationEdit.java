
package com.mock.biz.shared;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mock.biz.shared.domain.ChangeType;

/**
 *  页面编辑的普通类型，包含新增，删除，修改
 * @author hongliang.ma
 * @version $Id: OperationEdit.java, v 0.1 2012-8-1 上午11:39:30 hongliang.ma Exp $
 */
public interface OperationEdit {

    /**
     * 根据ID号，拷贝一个用户配置
     * 
     * @param nameToCopy  等待拷贝的用户名
     * @param innerIdToCopy  等待拷贝的用户模板流水ID
     * @return
     */
    public String addToUserNormal(final String nameToCopy, final String innerIdToCopy);

    /**
     * 删除用户模板
     * 
     * @param innerIdToDel 待删除的用户模板流水ID
     * @return
     */
    public Boolean delConfigByIDNormal(final String innerIdToDel);

    /**
     * 更改用户部分配置
     * 
     * @param changeType  
     * @param idToChange
     * @param newString
     * @return
     */
    public Boolean changeNormal(ChangeType changeType, final String idToChange,
                                final String newString);

    /**
     * 添加一个assert工具
     * 
     * @param idToChange
     * @param assertType
     * @param assertExcept
     * @return
     */
    public Boolean addAssert(final String idToChange, final String assertType,
                             final String assertExcept);

    /**
     * 根据ID号，拷贝一个用户配置 ,包含了转发的配置
     * 
     * @param nameToCopy 等待拷贝的用户名
     * @param innerid 等待拷贝的用户模板流水ID
     * @param   转发或者合并的ID
     * @return
     */
    public String addToUserTransfer(String nameToCopy, String innerid, String transferID);

    /**
     * 删除用户模板 ,包含了转发的配置
     * 
     * @param idsToDel   等待删除的用户模板流水ID
     * @return
     */
    public Boolean delConfigWithTransfer(final List<String> idsToDel);

    /**
     * 添加文件路径到某个配置
     * 
     * @param detailId
     * @param StrPath
     * @return
     */
    public Boolean addFilePath(final String detailId, final String StrPath);

    /**
     * 修改工具集中的值
     * 
     * @param changeType  修改的类型
     * @param idToChange  等待修改的ID
     * @param keyValues  修改的Key-value对
     * @return
     */
    public Boolean changeToolsValue(ChangeType changeType, final Long idToChange,
                                    final Map<String, String> keyValues);

    /**
     * 更改8583的内容,主要是8583中的某个字段
     * 
     * @param detailID
     * @param request
     * @return
     */
    public Boolean edit8583FValue(final String detailID, final HttpServletRequest request);

    /**
     * 更改8583的内容,主要是8583中的某个字段
     * 
     * @param detailID
     * @param request
     * @return
     */
    public Boolean edit8583FValue(final long detailID, final Map<String, String> mapNewTemplate);

    /**
     * 获取该配置下是否存在assert配置
     * 
     * @param innerId
     * @return
     */
    public Boolean isAssertExise(final String innerId);

    /**
     * 删除指定的assert工具
     * 
     * @param innerId
     * @return
     */
    public Boolean deleteAssert(final String innerId);

    /**
     * 设置成默认值
     * 
     * @param innerid
     * @param bSetResult
     * @return
     */
    public String setDefault(final String userName, final String innerid, final Boolean bSetResult);

    /**
     *  设置成性能测试模式
     *  
     * @param innerid
     * @param bSetResult
     * @return
     */
    public String setPerform(String innerid, Boolean bSetResult);

}
