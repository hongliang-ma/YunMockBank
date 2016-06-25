package com.mock.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mock.common.util.lang.StringUtil;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.dal.daointerface.UserDAO;
import com.mock.dal.dataobject.UserDO;
import com.mock.service.UserRepository;

@Service
public class UserRepositoryImpl implements UserRepository{
	
	@Autowired
	private UserDAO userDAO;

	/** 
     * @see com.mock.core.service.shared.repository.UserRepository#getUserInfo(java.lang.String, java.lang.String)
     */
    public String getUserInfo(String useName, String usePassword) {
        if (StringUtil.isEmpty(usePassword) || StringUtil.isEmpty(useName)) {
            return "用户名或者密码为空";
        }

        UserDO userDO = userDAO.selectByName(useName);
        String bCheckUser = "登陆成功";
        if (null == userDO) {
            try {
                UserDO myUserDO = new UserDO();
                myUserDO.setUsername(useName);
                myUserDO.setPassword(usePassword);
                userDAO.insert(myUserDO);
            } catch (Exception e) {
                //ExceptionUtil.caught(e, "新增用户失败，用户名", useName, "密码为", usePassword);
                bCheckUser = "新增用户失败";
                System.err.println("新增用户失败"+useName +usePassword);
            }
            System.err.println(userDO);
        } else {
            String getPassword = userDAO.selectByName(useName).getPassword();
            if (!StringUtil.equals(usePassword, getPassword)) {
                //LoggerUtil.info(logger, "密码匹配, 输入的密码为", usePassword, "真实密码为", getPassword);
                bCheckUser = "密码不匹配";
            }
        }
        return bCheckUser;
    }

    /** 
     * @see com.mock.core.service.shared.repository.UserRepository#changeUserRole(java.lang.String, java.lang.Boolean)
     */
    public void changeUserRole(final String useName, final Boolean bSuerp) {
        AssertUtil.isNotNull(useName, SystemErrorCode.ILLEGAL_PARAMETER);

        UserDO userDO = userDAO.selectByName(useName);
        AssertUtil.isNotNull(userDO, SystemErrorCode.ILLEGAL_PARAMETER);

        String superuser = bSuerp ? "TRUE" : "FALSE";
        userDAO.upSuperStatus(superuser, useName);
    }

    /** 
     * @see com.mock.core.service.shared.repository.UserRepository#getUserRole(java.lang.String)
     */
    public Boolean getUserRole(final String useName) {
        AssertUtil.isNotNull(useName, SystemErrorCode.ILLEGAL_PARAMETER);

        UserDO userDO = userDAO.selectByName(useName);
        AssertUtil.isNotNull(userDO, SystemErrorCode.ILLEGAL_PARAMETER);

        return BooleanUtils.toBoolean(userDO.getSuperuser());
    }

    /**
     * Setter method for property <tt>userDAO</tt>.
     * 
     * @param userDAO value to be assigned to property userDAO
     */
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
	

	/*public String getUserInfo(String useName, String usePassword) {
        if (usePassword.equals("")&& usePassword==null || useName.equals("") && useName==null) {
            return "用户名或者密码为空";
        }

        UserDO userDO = userDAO.selectByName(useName);
        String bCheckUser = "登陆成功";
        if (null == userDO) {
            try {
                UserDO myUserDO = new UserDO();
                myUserDO.setUsername(useName);
                myUserDO.setPassword(usePassword);
                userDAO.insert(myUserDO);
            } catch (Exception e) {
            	try {
					throw new Exception("新增用户失败");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        } else {
            String getPassword = userDAO.selectByName(useName).getPassword();
            if (!usePassword.equals(getPassword)) {
                bCheckUser = "密码不匹配";
            }
        }
        return bCheckUser;
    }*/

	
}
