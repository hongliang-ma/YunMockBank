package com.mock.core.service.transaction.component.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mock.common.util.ExceptionUtil;

/**
 * 
 * @author jun.qi
 */
public class DBConnection {

    private Connection conn_ = null;

    public DBConnection(ConnectionParameter parameter) {
        try {
            Class.forName(parameter.getDbClass());
            conn_ = DriverManager.getConnection(parameter.getUrl(), parameter.getUser(),
                parameter.getPassword());
        }
        // 捕获加载驱动程序异常
        catch (ClassNotFoundException cnfex) {
            ExceptionUtil.caught(cnfex, "装载 JDBC 驱动程序失败！");
            cnfex.printStackTrace();
        } catch (SQLException ex) {
            ExceptionUtil.caught(ex, "connect failure!！");
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn_;
    }

    public void close(Connection conn_) {
        if (conn_ != null) {
            try {
                conn_.close();
                conn_ = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
