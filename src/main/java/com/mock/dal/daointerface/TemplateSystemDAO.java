/*
 * Alipay.com Inc.
 * Copyright (c) 2004 - 2010 All Rights Reserved.
 * Powered By [rapid-generator]
 */
package com.mock.dal.daointerface;

import java.util.List;
import org.springframework.dao.DataAccessException;
import com.mock.dal.dataobject.TemplateSystemDO;

/**
 * TemplateSystemDAO
 * database table: anymock_template_system
 * database table comments: TemplateSystem
 * This file is generated by <tt>dalgen</tt>, a DAL (Data Access Layer)
 * 
 * PLEASE DO NOT MODIFY THIS FILE MANUALLY, or else your modification may
 * be OVERWRITTEN by someone else. To modify the file, you should go to 
 * directory <tt>(project-home)/dalgen</tt>
 * @author badqiu(zhongxuan)
 *
 
 */
public interface TemplateSystemDAO {

    /**
     * 
     * sql:
     * <pre>select         innerid,      communication_id,      sys_template,      macthdescription,           gmt_create,           gmt_modified          from         anymock_template_system</pre> 
     */
    public List<TemplateSystemDO> loadAll() throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>insert      into         anymock_template_system         (      innerid,      communication_id,      sys_template,      macthdescription,           gmt_create,           gmt_modified     )      values         (?,?,?,"",sysdate(),sysdate())</pre> 
     */
    public java.lang.String insert(TemplateSystemDO templateSystem) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         innerid,      communication_id,      sys_template,      macthdescription,           gmt_create,           gmt_modified          from         anymock_template_system          where         communication_id = ?</pre> 
     */
    public TemplateSystemDO selectByUrlId(String communicationId) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         innerid,      communication_id,      sys_template,      macthdescription,           gmt_create,           gmt_modified          from         anymock_template_system          where         innerid = ?</pre> 
     */
    public TemplateSystemDO selectById(String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_system      set         sys_template =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateTemplate(String sysTemplate, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_system      set         macthdescription =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateMacth(String macthdescription, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>delete      from         anymock_template_system          where         innerid = ?</pre> 
     */
    public int deleteById(String innerid) throws DataAccessException;

}
