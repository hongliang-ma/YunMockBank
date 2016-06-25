/*
 * Alipay.com Inc.
 * Copyright (c) 2004 - 2010 All Rights Reserved.
 * Powered By [rapid-generator]
 */
package com.mock.dal.daointerface;

import java.util.List;
import org.springframework.dao.DataAccessException;
import com.mock.dal.dataobject.TemplateUserDO;

/**
 * TemplateUserDAO
 * database table: anymock_template_user
 * database table comments: TemplateUser
 * This file is generated by <tt>dalgen</tt>, a DAL (Data Access Layer)
 * 
 * PLEASE DO NOT MODIFY THIS FILE MANUALLY, or else your modification may
 * be OVERWRITTEN by someone else. To modify the file, you should go to 
 * directory <tt>(project-home)/dalgen</tt>
 * @author badqiu(zhongxuan)
 *
 * 
 */
public interface TemplateUserDAO {

    /**
     * 
     * sql:
     * <pre>select         id,         innerid,         system_innerid,         bind_innerid,         matchstr,         isdefault,         transcoderule,         template_name,         count,         username,         record_log,           gmt_create,           gmt_modified          from         anymock_template_user          order by         count</pre> 
     */
    public List<TemplateUserDO> loadAll() throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         innerid          from         anymock_template_user          where         isdefault="TRUE"          and system_innerid = ?</pre> 
     */
    public String selectByDefault(String systemInnerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         id,         innerid,         system_innerid,         bind_innerid,         matchstr,         isdefault,         transcoderule,         template_name,         count,         username,         record_log,           gmt_create,           gmt_modified          from         anymock_template_user          where         innerid=?</pre> 
     */
    public TemplateUserDO selectByInnerId(String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         id,         innerid,         system_innerid,         bind_innerid,         matchstr,         isdefault,         transcoderule,         template_name,         count,         username,         record_log,           gmt_create,           gmt_modified          from         anymock_template_user          where         system_innerid = ?</pre> 
     */
    public List<TemplateUserDO> selectBySysId(String systemInnerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         id,         innerid,         system_innerid,         bind_innerid,         matchstr,         isdefault,         transcoderule,         template_name,         count,         username,         record_log,           gmt_create,           gmt_modified          from         anymock_template_user          where         username = ?</pre> 
     */
    public List<TemplateUserDO> selectByUser(String username) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>insert      into         anymock_template_user         (      innerid,      system_innerid,      bind_innerid,      matchstr,      isdefault,      transcoderule,      template_name,      username,      record_log,           gmt_create,           gmt_modified     )      values         (?,?,?,?,?,?,?,?,?,sysdate(),sysdate())</pre> 
     */
    public long insert(TemplateUserDO templateUser) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>select         count          from         anymock_template_user          where         innerid=?</pre> 
     */
    public int selectCount(String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_user      set         count = count+1,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateCount(String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_user      set         matchstr =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateMatchstr(String matchstr, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_user      set         template_name =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateTemplateName(String templateName, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_user      set         isdefault =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updateDefault(String isdefault, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>update         anymock_template_user      set         bind_innerid =?,      gmt_modified =sysdate()          where         innerid =?</pre> 
     */
    public int updatetBind(String bindInnerid, String innerid) throws DataAccessException;

    /**
     * 
     * sql:
     * <pre>delete      from         anymock_template_user          where         innerid = ?</pre> 
     */
    public int deleteByInnerid(String innerid) throws DataAccessException;

}
