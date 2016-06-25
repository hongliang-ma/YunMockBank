
package com.mock.core.service.transaction.component.util;

/**
 * 数据库连接
 * @author jun.qi
 * @version $Id: ConnectionParameter.java, v 0.1 2012-7-3 下午01:11:09 jun.qi Exp $
 */
public class ConnectionParameter {

    String classname = null;

    String url       = null;

    String user      = null;

    String password  = null;

    public ConnectionParameter() {

    }

    public ConnectionParameter(String type, String name, String conClass, String url, String user,
                               String password) {
        this.classname = conClass;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getDbClass() {
        return classname;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setDbClass(String conClass) {
        this.classname = conClass;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}