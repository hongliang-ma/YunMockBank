package com.mock.core.service.transaction.component.message.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.ConfigParser;
import com.mock.core.service.transaction.component.message.parser.util.MessageFactory;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.MyWrapper;
import com.mock.core.service.transaction.component.message.parser.vo.Value;
import com.mock.common.util.ExceptionUtil;

/**
 * 8583解析(异步) 过滤器
 * @author jun.qi
 * @version $Id: CallBackMessageParser.java, v 0.1 2012-7-24 下午06:51:03 jun.qi Exp $
 */
public class CallBackMessageParser extends ComponetHandler {

    /** TPDU值, 16进制TPDU，将自动转化为ASCII，可以为空值（空String）。*/
    public static final String CD_TPDU           = "TPDU";

    /**长度位类型, 通信长度位的类型。*/
    public static final String CD_LENGTH_TYPE    = "MLtype";

    /**MTI长度, Message Type Identifier 标识报文类型，如0210,0100等。*/
    public static final String CD_MTI_LENGTH     = "MTIlength";

    /**MTI类型, Message Type Identifier 标识报文类型，如0210,0100等。*/
    public static final String CD_MTI_TYPE       = "MTItype";

    /**bitmap编码格式, 分二进制binary，十六进制Hex 加下划线_连接 64bit及128bit。*/
    public static final String CD_BITMAP_TYPE    = "bitmap";

    /**内容类型, 二进制 binary 非二进制 nob */
    public static final String CD_CONTENT_BITSET = "isbinary";

    /**请求/返回对应, 格式为 请求MTI：返回MTI；冒号连接分号隔开 */
    public static final String CD_TRANS_MAP      = "transMap";

    /**J.8583配置, 详细请见J8583文档 */
    public static final String CD_TEMPLATE       = "template";

    public MessageFactory buildfactory(MessageFactory messageFactory) throws IOException {
        MessageFactory j8583factory = null;
        InputStream ins = new ByteArrayInputStream(messageFactory.getJ8583xml().getBytes());
        j8583factory = ConfigParser.createFromResource(messageFactory, ins);
        return j8583factory;
    }

    public int getmsgheaderlength(int msgheaderlength) {
        return msgheaderlength;
    }

    /**
     * 8583解析报文 parameter:parserBuf | msgheaderlength
     */
    public Message parser(byte[] parserBuf, MessageFactory fc) throws Exception {
        MessageFactory j8583factory = buildfactory(fc);
        Message message = j8583factory.parseMessage(parserBuf);
        return message;
    }

    /**
     * 8583组装报文 parameter:msgheaderlength | type | input
     * 
     * @throws Exception
     */
    public byte[] builder(String type, Map<String, String> input, MessageFactory fc)
                                                                                    throws Exception {
        MessageFactory j8583factory = buildfactory(fc);
        byte[] signMessagebytes = build(j8583factory, type, input);
        return signMessagebytes;
    }

    public byte[] build(MessageFactory fc, String type, Map<String, String> input) throws Exception {
        Message message = fc.newMessagefromTemplate(type);
        Message msg = fc.getTypeTemplates().get(type);
        for (String fieldid : input.keySet()) {
            Integer fieldID = new Integer(fieldid);
            Value<?> field = msg.getField(fieldID);
            message.setValue(fieldID, input.get(fieldid), field.getType(), field.getLength(), "",
                field.isPosition());
        }
        byte[] signMessagebytes = fc.build(message);
        return signMessagebytes;
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

    @SuppressWarnings("unchecked")
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        MessageFactory factory = null;
        Map<String, String> ms = null;
        Map<String, Object> messagesRepository = new HashMap<String, Object>();
        String strbitset = (String) localTransferData.getObject("CallBackMessageParser"
                                                                + CD_BITMAP_TYPE);
        String typelength = (String) localTransferData.getObject("CallBackMessageParser"
                                                                 + CD_MTI_LENGTH);
        String isbinary = (String) localTransferData.getObject("CallBackMessageParser"
                                                               + CD_CONTENT_BITSET);
        String tpdu = (String) localTransferData.getObject("CallBackMessageParser" + CD_TPDU);
        String mtitype = (String) localTransferData
            .getObject("CallBackMessageParser" + CD_MTI_TYPE);
        String j8583xml = (String) localTransferData.getObject("CallBackMessageParser" + "j8583");
        Pattern p = Pattern.compile("\\<[ ]\\>");
        Matcher m = p.matcher(tpdu);
        tpdu = tpdu.substring(m.regionStart() + 1, m.regionEnd() - 1);
        factory = new MessageFactory(j8583xml, isbinary, tpdu, Integer.parseInt(typelength),
            mtitype, strbitset);

        String inKey;
        try {
            inKey = (String) localTransferData.getObject("CallBackMessageParser" + "requestBefore");
            messagesRepository = getRepository(factory);
            ms = (Map<String, String>) messagesRepository.get(inKey);
            byte[] val = builder(inKey, ms, factory);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, val);
            localTransferData.setObject("factory", factory);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "CallBackMessageParser异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}