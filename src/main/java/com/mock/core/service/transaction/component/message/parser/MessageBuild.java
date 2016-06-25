package com.mock.core.service.transaction.component.message.parser;

import java.util.Map;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.util.Converts;
import com.mock.core.service.transaction.component.message.parser.util.MessageFactory;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.Value;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 8583 组装报文
 * @author jun.qi
 * @version $Id: MessageBuild.java, v 0.1 2012-8-2 下午07:02:59 jun.qi Exp $
 */
public class MessageBuild extends ComponetHandler {

    /**
     * 8583组装报文 parameter:msgheaderlength | type | input
     * 
     * @throws Exception
     */
    public byte[] builder(String type, Map<String, String> input, MessageFactory fc)
                                                                                    throws Exception {
        Message message = fc.newMessagefromTemplate(type);
        Message msg = fc.getTypeTemplates().get(type);
        for (String fieldid : input.keySet()) {
            Integer fieldID = new Integer(fieldid);
            Value<?> field = msg.getField(fieldID);
            message.setValue(fieldID, input.get(fieldid), field.getType(), field.getLength(), "",
                field.isPosition());
        }
        byte[] signMessagebytes = fc.build(message);

        //sign message
        //need to change
        byte[] Messagebytes = MessageParser.macdcode(fc.TPDU.length() / 2, type, signMessagebytes);
        return Messagebytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        try {
            Map<String, String> mrs = (Map<String, String>) data.getProperties().get(
                DataMapDict.SERVER_FORWARD_CONTENT);
            Map<String, String> pairs = (Map<String, String>) localTransferData.getObject("pairs");
            String inKey = (String) localTransferData.getObject("inKey");
            MessageFactory factory = (MessageFactory) localTransferData.getObject("factory");
            byte[] val = builder(pairs.get(inKey), mrs, factory);
            LoggerUtil.info(logger, "Message Parser write String : ",
                Converts.bytesToHexString(val));
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, val);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "MessageBuild异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}