
package com.mock.core.service.shared.repository;

import java.util.List;

import com.mock.core.model.transaction.template.AnymockLable;

/**
 * 组件和组之间关系的仓库接口
 * 
 * @author hongliang.ma
 * @version $Id: AnymockLableRepository.java, v 0.1 2012-6-1 下午1:34:38 hongliang.ma Exp $
 */
public interface AnymockLableRepository {

    /**
     * 加载所有标签
     * 
     * @return
     */
    public List<AnymockLable> loadAll();

    /**
     * 根据通讯ID删除标签
     * 
     * @param CommuId
     */
    public void deleteByCommuId(final String commuId);

    /**
     * 更新标签
     * 
     * @param CommuId
     * @param labs
     * @return
     */
    public Boolean updateLabes(final String commuId, final String labs);

}
