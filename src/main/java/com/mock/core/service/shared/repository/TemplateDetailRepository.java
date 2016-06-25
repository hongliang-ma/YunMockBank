
package com.mock.core.service.shared.repository;

import java.util.List;

import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.TemplateDetail;

/**
 * 模板详情仓库的操作
 * 
 * @author hongliang.ma
 * @version $Id: TemplateDetailRepository.java, v 0.1 2012-5-30 下午3:30:01 hongliang.ma Exp $
 */
public interface TemplateDetailRepository {

    /**
     * 根据内部流水ID按顺序列出详情列表
     * 
     * @param innerid 内部流水ID
     * @return 详情列表，按照顺序排序
     */
    public List<TemplateDetail> loadTemplateDetail(String innerid);

    /**
     * 根据ID找到具体的TemplateDetail
     * 
     * @param id
     * @return
     */
    public TemplateDetail loadTemplateById(Long id);

    /**
     *  新增用户模板详情
     * 
     * @param innerid  内部流水ID
     * @param componentId  组件ID
     * @param detailMsg  
     * @param sequence 组件的顺序
     * @return 返回新增的模板的ID号
     * 
     */
    public TemplateDetail addTemplateDetail(String innerid, DetailMsg detailMsg, int sequence);

    /**
     *  更新某个具体组件详情的内容
     * 
     * @param id  详情ID
     * @param detailMsg
     */
    public void updateValue(Long id, DetailMsg detailMsg);

    /**
     * 更新某个组件详情的顺序
     * 
     * @param id 详情ID
     * @param sequence
     */
    public void updateSequence(Long id, int sequence);

    /**
     * 删除某个具体的组件详情
     * 
     * @param id  组件的ID
     */
    public void deleteDetailbyId(Long id);

    /**
     *删除所有某个内部流水的组件详情
     * 
     * @param innerId  组件的ID
     */
    public void deleteDetailByInnerId(String innerId);

}
