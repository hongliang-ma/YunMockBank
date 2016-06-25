package com.mock.core.service.transaction.component.message.parser;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.Converts;
import com.mock.common.util.ExceptionUtil;

/**
 * 位数增加工具类（交行快捷签名用）
 * @author jun.qi
 * @version $Id: InsertFieldParser.java, v 0.1 2013-2-4 下午04:41:29 jun.qi Exp $
 */
public class InsertFieldParser extends ComponetHandler{

    /** 增加位数,0:aaa;4:bbb;last:999 */
    public static final String  CD_LENGTH        = "addLength";

    private static final String ENCRYPT_WORK_KEY = "CCC32A2109E81FE3";

    private static final String ENCRYPT_MAIN_KEY = "abcdefgh";
    
    public void takeField(TransferData data) throws Exception {

        byte[] by = (byte[]) data.getObject(DataMapDict.MSGBODY);

        byte[] ne = new byte[by.length - 3];

        System.arraycopy(by, 1, ne, 0, ne.length);

        System.err.print("out:" + Converts.bytesToHexString(ne));

        data.getProperties().put(DataMapDict.MSGBODY, ne);
    }
    
    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        try {
            takeField(data);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "InsertFieldParser异常");
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }

}
