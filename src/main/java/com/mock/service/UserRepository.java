package com.mock.service;

/**
 * 
 * @author hongliang.ma
 * @version $Id: UserRepository.java, v 0.1 2012-7-18 下午2:33:11 hongliang.ma Exp $
 */
public interface UserRepository {

    /**
     * 获取用户是否存在，如果不存在则添加
     * 
     * @param useName  用户名
     * @param usePassword  用户密码
     * @return
     */
    public String getUserInfo(final String useName, final String usePassword);

    /**
     * 更新用户的角色，true为管理员，false为一般用户。
     * 
     * @param useName
     * @param bSuerp
     * @return 
     */
    public void changeUserRole(final String useName, final Boolean bSuerp);

    /**
     * 获取该用户的角色，true为管理员，false为一般用户。
     * 
     * @param useName
     * @return
     */
    public Boolean getUserRole(final String useName);
}
