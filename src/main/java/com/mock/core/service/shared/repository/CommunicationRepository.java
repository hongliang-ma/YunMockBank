
package com.mock.core.service.shared.repository;

import java.util.List;

import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.transaction.template.CommunicationPart;

/**
 * 
 * @author hongliang.ma
 * @version $Id: CommunicationRepository.java, v 0.1 2012-6-21 下午7:05:23 hongliang.ma Exp $
 */
public interface CommunicationRepository {

    /**
     * 加载所有的通讯缓存
     * 
     * @return
     */
    public List<CommunicationConfig> loadAll();

    /**
     * 根据通讯id获取获取对象
     * 
     * @param urlId
     * @return
     */
    public CommunicationConfig getOneCommById(final String urlId);

    /**
     *  根据通讯ID获取通讯URl
     * 
     * @param urlId
     * @return 返回通讯url
     */
    public String getURlById(final String urlId);

    /**
     * 根据URL地址查找通讯ID
     * 
     * @param ulrStr
     * @return
     */
    public List<String> getURlIdByURl(final String ulrStr);

    /**
     * 根据URL地址查找通讯ID
     * 
     * @param ulrStr
     * @return
     */
    public String getURlIdByURlAndType(final String ulrStr, final String server);

    /**
     * 根据ID删除通讯配置
     * 
     * @param urlId
     */
    public void deleteURlById(final String urlId);

    /**
     * 更新通讯配置
     * 
     * @param urlId
     * @param communicationPart
     */
    public Boolean updateById(final String urlId, final CommunicationPart communicationPart);

    /**
     * 根据ID和参数名字删除通讯配置
     * 
     * @param urlId
     * @param paraName
     */
    public void deletePara(final String urlId, final String paraName);

    /**
     * 更新ID和参数通讯配置
     * 
     * @param urlId
     * @param paraName
     * @param paraValue
     */
    public Boolean updatePara(final String urlId, final String paraName, final String paraValue);

}
