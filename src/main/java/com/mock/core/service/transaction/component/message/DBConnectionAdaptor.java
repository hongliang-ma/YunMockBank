
package com.mock.core.service.transaction.component.message;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DBConnectionEnum;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.extension.ResultWrapper;
import com.mock.core.service.transaction.component.util.ConnectionParameter;
import com.mock.core.service.transaction.component.util.DBConnection;
import com.mock.core.service.transaction.component.util.DBSelectorUtil;
import com.mock.core.service.transaction.component.util.ParseUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 数据库连接
 * @author jun.qi
 * @version $Id: DBConnectionAdaptor.java, v 0.1 2012-7-3 下午01:13:37 jun.qi Exp $
 */
public final class DBConnectionAdaptor extends ComponetHandler {

    public String sqlRepValue(String ruleValue, ConnectionParameter parameter) throws Exception {
        /**标识符 */
        String strStart = ParseUtil.take("start", "query", "sql");
        String strEnd = ParseUtil.take("end", "query", "sql");
        int idxStart = ruleValue.indexOf(strStart);
        int idxEnd = ruleValue.indexOf(strEnd, idxStart);
        String strName = null;
        String paramName = null;
        String paramVal = null;
        List<ResultWrapper> wrappers = new ArrayList<ResultWrapper>();
        while (idxStart > -1 && idxEnd > -1) {
            strName = ruleValue.substring(idxStart + strStart.length(), idxEnd);
            paramName = ruleValue.substring(idxStart, idxEnd + strEnd.length());
            wrappers = getResultWrappers(strName, parameter);
            paramVal = buildList(wrappers);
            LoggerUtil.info(logger, "[数据库查询]", "值：", paramVal);
            ruleValue = StringUtil.replace(ruleValue, paramName, paramVal);
            idxStart = ruleValue.indexOf(strStart, idxStart);
            idxEnd = ruleValue.indexOf(strEnd, idxEnd);
        }
        return ruleValue;
    }

    /**
     * 数据库连接查询
     * @param sqlquery    SQL查询语句
     * @param parameter   数据库连接参数
     * @return
     */
    public List<ResultWrapper> getResultWrappers(String sqlquery, ConnectionParameter parameter) {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        List<ResultWrapper> rswrappers = new ArrayList<ResultWrapper>();
        ResultWrapper wrapper = null;

        DBConnection dbcon = new DBConnection(parameter);
        String value = StringUtil.substringBetween(sqlquery, "select", "from");
        try {
            con = dbcon.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(sqlquery);
            String result = null;
            while (rs.next()) {
                result = rs.getString(value.trim());
                wrapper = new ResultWrapper(result);
                rswrappers.add(wrapper);
                result = null;
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "连接数据库驱动异常! ");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        } finally {
            try {
                rs.close();
                st.close();
                dbcon.close(con);
            } catch (Exception e) {
                ExceptionUtil.caught(e, "连接数据库驱动异常! ");
                throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
        }
        return rswrappers;
    }

    public String buildList(List<ResultWrapper> wrappers) throws Exception {
        StringBuffer bf = new StringBuffer();
        for (ResultWrapper wrapper : wrappers) {
            bf.append(wrapper.getValue());
        }
        return bf.toString();
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        ConnectionParameter parameter = new ConnectionParameter();
        DBSelectorUtil dbutil = new DBSelectorUtil();
        //消息类型
        String reqType = (String) localTransferData.getObject("DBConnectionAdaptor" + "reqType");
        //数据库名称
        String dbType = (String) localTransferData.getObject("DBConnectionAdaptor" + "dbName");
        //数据库连接的用户名
        String userName = (String) localTransferData.getObject("DBConnectionAdaptor" + "userName");
        //数据库连接的密码
        String password = (String) localTransferData.getObject("DBConnectionAdaptor" + "password");
        String ip = dbutil.getAcctransIP(dbType);
        String sid = dbutil.getAcctransSID(dbType);
        parameter.setDbClass("oracle.jdbc.driver.OracleDriver");
        parameter.setUrl("jdbc:oracle:thin:@" + ip + ":" + "1521" + ":" + sid);
        parameter.setUser(userName);
        parameter.setPassword(password);
        try {
            switch (Enum.valueOf(DBConnectionEnum.class, reqType)) {
                case PARAM:
                    @SuppressWarnings("unchecked")
                    Map<String, String> pars = (Map<String, String>) data.getProperties().get(
                        DataMapDict.SERVER_FORWARD_CONTENT);
                    for (Entry<String, String> pa : pars.entrySet()) {
                        String neValue = sqlRepValue(pa.getValue(), parameter);
                        pars.put(pa.getKey(), neValue);
                    }
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, pars);
                    LoggerUtil.info(logger, "数据库查询为:", pars);
                    break;
                case CONTENT:
                    String content = (String) data.getProperties().get(
                        DataMapDict.SERVER_FORWARD_CONTENT);
                    String ruleValue = sqlRepValue(content, parameter);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, ruleValue);
                    LoggerUtil.info(logger, "数据库查询为:", ruleValue);
                    break;

                default:
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
        } catch (Exception e) {
            LoggerUtil.info(logger, "数据库连接异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}