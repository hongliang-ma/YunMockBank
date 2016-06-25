package com.mock.core.service.transaction.component.message.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.Converts;
import com.mock.core.service.transaction.component.message.parser.util.DES;
import com.mock.core.service.transaction.component.message.parser.util.MessageFactory;
import com.mock.core.service.transaction.component.message.parser.util.WorkKeyDecrypt;
import com.mock.core.service.transaction.component.message.parser.vo.BytesMacGenerater;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.MyWrapper;
import com.mock.common.util.ExceptionUtil;

/**
 * 8583 解析报文
 * @author jun.qi
 * @version $Id: MessageParser.java, v 0.1 2012-7-24 下午06:52:10 jun.qi Exp $
 */
public class MessageParser extends ComponetHandler {

    private static final String ENCRYPT_WORK_KEY  = "E8B001C0CFB7B101E8B001C0CFB7B100";

    /**TPDU值,16进制TPDU，将自动转化为ASCII，可以为空值（空String）。*/
    public static final String  CD_TPDU           = "TPDU";

    /**长度位类型，通信长度位的类型。*/
    public static final String  CD_LENGTH_TYPE    = "MLtype";

    /**MTI长度, Message Type Identifier 标识报文类型，如0210,0100等。*/
    public static final String  CD_MTI_LENGTH     = "MTIlength";

    /**MTI类型, Message Type Identifier 标识报文类型，如0210,0100等。*/
    public static final String  CD_MTI_TYPE       = "MTItype";

    /**bitmap编码格式, 分二进制binary，十六进制Hex 加下划线_连接 64bit及128bit。*/
    public static final String  CD_BITMAP_TYPE    = "bitmap";

    /**内容类型, 二进制 binary 非二进制 nob */
    public static final String  CD_CONTENT_BITSET = "isbinary";

    /**请求/返回对应, 格式为 请求MTI：返回MTI；冒号连接分号隔开 */
    public static final String  CD_TRANS_MAP      = "transMap";

    /**J.8583配置, 详细请见J8583文档 */
    public static final String  CD_TEMPLATE       = "template";

    /**
     * 8583解析报文 parameter:parserBuf | msgheaderlength
     */
    public Message parser(byte[] parserBuf, MessageFactory fc) throws Exception {
        Message message = fc.parseMessage(parserBuf);
        return message;
    }

    //  mac加密
    public static byte[] macdcode(int msgheaderlength, String type, byte[] signMessagebytes) {
        logger.info("signMessagebytes:" + Converts.bytesToHexString(signMessagebytes));
        byte[] srcBytes = new byte[signMessagebytes.length - 7 - 6];
        System.arraycopy(signMessagebytes, 5, srcBytes, 0, signMessagebytes.length - 7 - 6);
        logger.info("srcBytes:" + Converts.bytesToHexString(srcBytes));
        byte[] macBytes = BytesMacGenerater.generateBytesForMac(srcBytes);
        logger.info("macBytes:" + Converts.bytesToHexString(macBytes));
        byte[] finalMacBytes = DES.encrypt3DESofdouble(macBytes,
            WorkKeyDecrypt.generateDecryptWorkKey(Converts.hexStringToBytes(ENCRYPT_WORK_KEY)));
        byte[] totalBytes = new byte[signMessagebytes.length];
        logger.info("FinalmacBytes:" + Converts.bytesToHexString(finalMacBytes));
        System.arraycopy(signMessagebytes, 0, totalBytes, 0, signMessagebytes.length);
        System.arraycopy(finalMacBytes, 0, totalBytes, signMessagebytes.length - 8,
            finalMacBytes.length);
        logger.info("totalBytes:" + Converts.bytesToHexString(totalBytes));
        if (msgheaderlength == 5 && type.equals("0810") || msgheaderlength == 11) {
            return signMessagebytes;
        } else {
            return totalBytes;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        MessageFactory factory = null;
        Map<String, String> ms = null;
        Map<String, Object> messagesRepository = new HashMap<String, Object>();
        String j8583xml = (String) localTransferData.getObject("MessageParser" + "j8583");
        String strbitset = (String) localTransferData.getObject("MessageParser" + CD_BITMAP_TYPE);
        String typelength = (String) localTransferData.getObject("MessageParser" + CD_MTI_LENGTH);
        String isbinary = (String) localTransferData.getObject("MessageParser" + CD_CONTENT_BITSET);
        String tpdu = (String) localTransferData.getObject("MessageParser" + CD_TPDU);
        String mtitype = (String) localTransferData.getObject("MessageParser" + CD_MTI_TYPE);
        Pattern p = Pattern.compile("\\<[ ]\\>");
        Matcher m = p.matcher(tpdu);
        tpdu = tpdu.substring(m.regionStart() + 1, m.regionEnd() - 1);
        factory = new MessageFactory(j8583xml, isbinary, tpdu, Integer.parseInt(typelength),
            mtitype, strbitset);
        String trans = (String) localTransferData.getObject("MessageParser" + CD_TRANS_MAP);
        String map[] = trans.split(";");
        Map<String, String> pairs = new HashMap<String, String>();
        for (String pair : map) {
            String pairArray[] = pair.split(":");
            pairs.put(pairArray[0], pairArray[1]);
        }

        String inKey = null;
        Message msg = null;
        byte[] content = (byte[]) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            msg = parser(content, factory);
            inKey = msg.getMsgTypeId();
            messagesRepository = getRepository(factory);
            ms = (Map<String, String>) messagesRepository.get(pairs.get(inKey));
            Map<String, String> in = new HashMap<String, String>();
            for (MyWrapper dat : msg.getMsgData()) {
                in.put(dat.getKey(), dat.getValue());
            }
            Map<String, String> out = new HashMap<String, String>();
            out.putAll(ms);
            data.getProperties().put(DataMapDict.MSGCONTENT, in);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, out);
            localTransferData.setObject("pairs", pairs);
            localTransferData.setObject("inKey", inKey);
            localTransferData.setObject("factory", factory);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "MessageParser异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    public Map<String, Object> getRepository(MessageFactory fc) {
        Map<String, Object> messagesRepository = new HashMap<String, Object>();
        Map<String, Message> tt = fc.getTypeTemplates();
        for (String key : tt.keySet()) {
            Message tmsg = tt.get(key);
            Map<String, String> indat = new HashMap<String, String>();
            for (MyWrapper mwp : tmsg.getMsgData()) {
                indat.put(mwp.getKey(), mwp.getValue());
            }
            messagesRepository.put(key, indat);
        }
        return messagesRepository;
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}