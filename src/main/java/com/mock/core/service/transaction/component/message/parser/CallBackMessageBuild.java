package com.mock.core.service.transaction.component.message.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.ConfigParser;
import com.mock.core.service.transaction.component.message.parser.util.MessageFactory;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.MyWrapper;
import com.mock.common.util.ExceptionUtil;

/**
 * 8583组装(异步) 过滤器
 * @author jun.qi
 * @version $Id: CallBackMessageBuild.java, v 0.1 2012-8-3 上午10:44:22 jun.qi Exp $
 */
public class CallBackMessageBuild extends ComponetHandler {

    /**
     * 8583解析报文 parameter:parserBuf | msgheaderlength
     * @throws IOException 
     * @throws ParseException 
     */
    public Message parser(byte[] parserBuf, MessageFactory fc) throws ParseException, IOException {
        MessageFactory j8583factory = buildfactory(fc);
        Message message = j8583factory.parseMessage(parserBuf);
        return message;
    }

    public MessageFactory buildfactory(MessageFactory messageFactory) throws IOException {
        MessageFactory j8583factory = null;
        InputStream ins = new ByteArrayInputStream(messageFactory.getJ8583xml().getBytes());
        j8583factory = ConfigParser.createFromResource(messageFactory, ins);
        return j8583factory;
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        byte[] content = null;
        Message m;
        @SuppressWarnings("unused")
        String inKey;
        MessageFactory factory = (MessageFactory) localTransferData.getObject("factory");
        try {
            content = (byte[]) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
            m = parser(content, factory);
            inKey = m.getMsgTypeId();
            Map<String, String> in = new HashMap<String, String>();

            for (MyWrapper dat : m.getMsgData()) {
                in.put(dat.getKey(), dat.getValue());
            }
            data.setObject("responseAfter", in);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "CallBackMessageBuild异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}