
package com.mock.core.service.transaction.component.message;

import java.util.HashMap;
import java.util.Map;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.enums.MappingValueEnum;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.DecodeXmlContent;
import com.mock.core.service.transaction.component.util.PostXMLUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * Map，从请求中获取值
 * 
 * @author jun.qi
 * @version $Id: MappingValueAction.java, v 0.1 2012-7-2 下午02:39:09 jun.qi Exp $
 */
public final class MappingValueAction extends ComponetHandler {

    public Map<String, String> getValue(String par) {
        String s[] = StringUtil.split(par, "|");
        Map<String, String> map = new HashMap<String, String>();
        String[] s1 = null;
        for (int i = 0, iCount = s.length; i < iCount; i++) {
            s1 = StringUtil.split(s[i], "=");
            for (int j = 1, iLength = s1.length; j < iLength; j++) {
                map.put(s1[j - 1], s1[j]);
            }
        }
        return map;
    }

    public String replaceValue(Map<String, String> req, Map<String, String> res) {
        String val = null;
        String value = null;
        for (String key : res.keySet()) {
            if (res.get(key).indexOf("Get") != -1) {
                for (String key1 : req.keySet()) {
                    if (key1.equals(key)) {
                        val = req.get(key1);
                    }
                }
                res.put(key, val);
            }
        }
        value = mapToString(res);
        return value;
    }

    public String mapToString(Map<String, String> res) {
        StringBuffer bf = new StringBuffer();
        String st = null;
        for (String key : res.keySet()) {
            bf.append(key).append("=").append(res.get(key)).append("|");
        }

        if (bf.toString().lastIndexOf("|") != -1) {
            st = bf.toString().substring(0, bf.toString().lastIndexOf("|"));
        }
        return st;
    }

    private String getKey(String value, String ek) {
        String rs = value.replace(ek, "");
        if (!rs.equals("")) {
            return rs;
        }
        return null;
    }

    private void getVal(String key, Map<String, String> in, Map<String, String> retn) {
        for (String ekey : retn.keySet()) {

            String value = retn.get(ekey);
            if (value.indexOf(key) != -1) {
                String ek = getKey(value, key);
                if (ek != null) {
                    ekey = ek;
                }
                String newVal = in.get(ekey);
                if (newVal != null && !newVal.equals("")) {
                    retn.put(ekey, newVal);
                } else {
                    retn.put(ekey, "qijuncf");
                }
                LoggerUtil.info(logger, "Log Mapping Value : ", ekey, " : ", in.get(ekey));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String msgType = (String) localTransferData.getObject("MappingValueAction"
                                                              + DataMapDict.STATE);

        String res = null;
        try {
            switch (Enum.valueOf(MappingValueEnum.class, msgType)) {
                case KEYVALUE:
                    Map<String, String> in = (Map<String, String>) data.getProperties().get(
                        DataMapDict.MSGCONTENT);
                    Map<String, String> retn = (Map<String, String>) data.getProperties().get(
                        DataMapDict.SERVER_FORWARD_CONTENT);
                    String key = (String) localTransferData.getObject("MappingValueAction" + "key");
                    getVal(key, in, retn);
                    break;
                case XMLMAX:
                    String coderule = (String) localTransferData.getProperties().get(
                        "MappingValueAction" + DataMapDict.CODERULE);

                    String req = (String) data.getProperties().get(DataMapDict.MSGBODY);
                    res = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
                    String par = DecodeXmlContent.getOneXmlCodeRule(coderule, null, DecodeXmlContent.buildDocByString(req));
                    Map<String, String> mapr = getValue(par);
                    String pas = DecodeXmlContent.getOneXmlCodeRule(coderule,null,DecodeXmlContent.buildDocByString(res));
                    Map<String, String> maps = getValue(pas);
                    String value = replaceValue(mapr, maps);

                    res = StringUtil.replace(res, pas, value);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, res);
                    LoggerUtil.info(logger, "[XML返回值]：", res);
                    break;

                case XML:
                    res = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
                    Map<String, String> ine = (Map<String, String>) data.getProperties().get(
                        DataMapDict.MSGBODY);
                    Map<String, String> re = PostXMLUtil.ParseXML(res);
                    String ruleValue = PostXMLUtil.replaceValue(ine, re);
                    data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, ruleValue);
                    LoggerUtil.info(logger, "[XML返回值]：", res);
                    break;

                default:
                    throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "MappingValueAction方法出现异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}