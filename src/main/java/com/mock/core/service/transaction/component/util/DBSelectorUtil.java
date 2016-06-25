
package com.mock.core.service.transaction.component.util;

/**
 * 数据库连接工具类
 * @author jun.qi
 * @version $Id: DBSelectorUtil.java, v 0.1 2012-7-3 下午01:12:34 jun.qi Exp $
 */
public class DBSelectorUtil {

    public String getAcctransIP(String dbType) {
        if (dbType.equals("devdb01")) {
            return "pay1.devdb.alipay.net";
        }
        if (dbType.equals("devdb02")) {
            return "pay2.devdb.alipay.net";
        }
        if (dbType.equals("devdb03")) {
            return "pay3.devdb.alipay.net";
        }
        if (dbType.equals("devdb04")) {
            return "pay4.devdb.alipay.net";
        }
        if (dbType.equals("devdb05")) {
            return "Pay5.devdb.alipay.net";
        }
        if (dbType.equals("testdb01")) {
            return "pay1.testdb.alipay.net";
        }
        if (dbType.equals("testdb02")) {
            return "pay2.testdb.alipay.net";
        }
        if (dbType.equals("testdb03")) {
            return "pay3.testdb.alipay.net";
        }
        if (dbType.equals("testdb04")) {
            return "pay4.testdb.alipay.net";
        }
        if (dbType.equals("testdb05")) {
            return "Pay5.testdb.alipay.net";
        }
        if (dbType.equals("PAYCORE01")) {
            return "pay1.devdb.alipay.net";
        }
        if (dbType.equals("PAYCORE02")) {
            return "pay2.devdb.alipay.net";
        }
        if (dbType.equals("PAYCORE03")) {
            return "pay3.devdb.alipay.net";
        }
        if (dbType.equals("PAYCORE01_SIT")) {
            return "pay1.testdb.alipay.net";
        }
        if (dbType.equals("PAYCORE02_SIT")) {
            return "pay2.testdb.alipay.net";
        }
        if (dbType.equals("PAYCORE03_SIT")) {
            return "pay3.testdb.alipay.net";
        }

        return null;
    }

    public String getAcctransSID(String dbType) {
        if (dbType.equals("devdb01")) {
            return "devpay1";
        }
        if (dbType.equals("devdb02")) {
            return "devpay1";
        }
        if (dbType.equals("devdb03")) {
            return "devpay2";
        }
        if (dbType.equals("devdb04")) {
            return "pay";
        }
        if (dbType.equals("devdb05")) {
            return "devpay5";
        }
        if (dbType.equals("testdb01")) {
            return "tstpay1";
        }
        if (dbType.equals("testdb02")) {
            return "tstpay1";
        }
        if (dbType.equals("testdb03")) {
            return "tstpay2";
        }
        if (dbType.equals("testdb04")) {
            return "tstpay1";
        }
        if (dbType.equals("testdb05")) {
            return "tstpay5";
        }
        if (dbType.equals("PAYCORE01")) {
            return "devpay1";
        }
        if (dbType.equals("PAYCORE02")) {
            return "devpay1";
        }
        if (dbType.equals("PAYCORE03")) {
            return "devpay2";
        }
        if (dbType.equals("PAYCORE01_SIT")) {
            return "tstpay1";
        }
        if (dbType.equals("PAYCORE02_SIT")) {
            return "tstpay1";
        }
        if (dbType.equals("PAYCORE03_SIT")) {
            return "tstpay2";
        }

        return null;
    }
}