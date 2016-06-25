package com.mock.core.service.transaction.component.message.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.CipherOperator;
import com.mock.core.service.transaction.component.message.parser.util.Converts;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 8583长度位解析
 * @author jun.qi
 * @version $Id: MsgLengthParse.java, v 0.1 2012-8-3 下午01:47:34 jun.qi Exp $
 */
public class MsgLengthParse extends ComponetHandler {

    /**长度位长度, 详细请见J8583文档 */
    public static final String CD_LENGTH          = "length";

    /**长度位间隔,详细请见J8583文档 */
    public static final String CD_INTER           = "interChar";

    /**长度为加码,详细请见J8583文档 */
    public static final String CD_LENGTH_ENCODING = "lengthEncoding";

    /**加解码颠倒, 异步模式，加解码颠倒 */
    public static final String CD_REVERSE         = "filterreverse";

    public void addLengthField(TransferData data, TransferData localTransferData) throws Exception {

        Pattern p = Pattern.compile("\\<[ ]\\>");
        String inter = (String) localTransferData.getObject("MsgLengthParse" + CD_INTER);
        Matcher m = p.matcher(inter);
        inter = inter.substring(m.regionStart() + 1, m.regionEnd() - 1);
        byte[] content = null;

        int len = Integer.parseInt((String) localTransferData.getObject("MsgLengthParse"
                                                                        + CD_LENGTH));
        String encoding = (String) localTransferData.getObject("MsgLengthParse"
                                                               + CD_LENGTH_ENCODING);

        Object obj = data.getProperties().get(DataMapDict.MSGBODY);

        if (obj == null) {
            content = (byte[]) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        } else {
            content = (byte[]) obj;
        }

        int strlength = content.length;
        String lengthStr = Integer.toHexString(strlength);

        byte[] bytt = new byte[len];
        int nl = 0;

        byte[] srcByte = null;

        if (encoding.equals("BCD")) {
            if (strlength < 256) {
                srcByte = CipherOperator.ASCII2BCD(lengthStr.getBytes(), 2);
            } else {
                srcByte = CipherOperator.ASCII2BCD(lengthStr.getBytes(), 3);
            }

        }
        if (encoding.equals("ASCII")) {
            srcByte = ("0" + lengthStr).getBytes();
        }

        if (encoding.equals("ASCII10")) {
            srcByte = ("0" + strlength).getBytes();
        }

        if (encoding.equals("ASCII_8")) {
            strlength = strlength + 4;
            srcByte = ("0" + strlength + "0" + strlength).getBytes();
        }

        if (encoding.equals("ASCII_10")) {

            if (strlength >= 100) {
                srcByte = Converts.hexStringToBytes("0" + strlength);
            }
            if (strlength < 100) {
                srcByte = Converts.hexStringToBytes(String.valueOf(strlength));
            }

        }
        if (encoding.equals("ASCIIP")) {
            srcByte = CipherOperator.encode(strlength);
        }
        if (strlength < 256 || encoding.equals("ASCII")) {
            nl = len - srcByte.length;
            if (nl < 0) {
                nl = -nl;
            }
        } else {
            nl = 0;
        }

        System.arraycopy(srcByte, 0, bytt, nl, srcByte.length);

        if (encoding.equals("ASCII_10")) {
            LoggerUtil.info(logger, "Add Message Length Field: ", Converts.bytesToHexString(bytt));
        } else {
            LoggerUtil.info(logger, "Add Message Length Field: ",
                Converts.StringToHexString(new String(bytt) + inter));
        }

        LoggerUtil.info(logger, "Message Field", Converts.bytesToHexString(content));

        byte[] rs = new byte[len + inter.length() + strlength];

        System.arraycopy(bytt, 0, rs, 0, bytt.length);
        System.arraycopy(inter.getBytes(), 0, rs, len, inter.length());
        System.arraycopy(content, 0, rs, len + inter.length(), content.length);

        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, rs);

    }

    public void takeLengthField(TransferData data, TransferData localTransferData) throws Exception {

        Pattern p = Pattern.compile("\\<[ ]\\>");
        String inter = (String) localTransferData.getObject("MsgLengthParse" + CD_INTER);
        Matcher m = p.matcher(inter);

        inter = inter.substring(m.regionStart() + 1, m.regionEnd() - 1);

        int len = Integer.parseInt((String) localTransferData.getObject("MsgLengthParse"
                                                                        + CD_LENGTH));

        byte[] content = (byte[]) data.getProperties().get((DataMapDict.MSGBODY));

        byte[] rs = new byte[content.length - len - inter.length()];

        byte[] lenf = new byte[len + inter.length()];

        System.arraycopy(content, 0, lenf, 0, lenf.length);
        System.arraycopy(content, len + inter.length(), rs, 0, rs.length);

        LoggerUtil.info(logger, "Take Message Length Field: ", Converts.bytesToHexString(lenf)
                                                               + inter);
        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, rs);
        // data.put(DataMapDict.CONTENT_LENGTH, reqLengthStr);

    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        boolean revs = Boolean.valueOf((String) localTransferData.getObject("MsgLengthParse"
                                                                            + CD_REVERSE));
        try {
            if (revs) {
                takeLengthField(data, localTransferData);
            } else {
                addLengthField(data, localTransferData);
            }
            localTransferData.setObject("revs", revs);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "8583长度位解析异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}