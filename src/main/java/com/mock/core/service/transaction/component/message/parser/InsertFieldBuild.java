package com.mock.core.service.transaction.component.message.parser;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.Converts;
import com.mock.core.service.transaction.component.message.parser.util.DES;
import com.mock.common.util.ExceptionUtil;
/**
 * 位数增加工具类（交行快捷签名用）
 * @author jun.qi
 * @version $Id: InsertFieldBuild.java, v 0.1 2013-2-4 下午04:45:27 jun.qi Exp $
 */
public class InsertFieldBuild extends ComponetHandler{
    
    /** 增加位数,0:aaa;4:bbb;last:999 */
    public static final String  CD_LENGTH        = "addLength";

    private static final String ENCRYPT_WORK_KEY = "CCC32A2109E81FE3";

    private static final String ENCRYPT_MAIN_KEY = "abcdefgh";
    
    private void generateMacData(TransferData data) throws Exception{
        byte[] totalBytes = (byte[]) data.getObject(DataMapDict.SERVER_FORWARD_CONTENT);
        byte[] encryptBytes = new byte[totalBytes.length - 7 - 8];
        System.arraycopy(totalBytes, 7, encryptBytes, 0, totalBytes.length - 7 - 8);
        byte[] deWorkKey = DES.ECBdecrypt(Converts.hexStringToBytes(ENCRYPT_WORK_KEY), ENCRYPT_MAIN_KEY.getBytes());
        byte[] finalMacBytes = getBytesForMac(encryptBytes, 8, deWorkKey);
        System.arraycopy(finalMacBytes, 0, totalBytes, totalBytes.length - 8,
            finalMacBytes.length);
        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, totalBytes);
    }
    
    private byte[] getBytesForMac(byte[] tmpBuffer, int length, byte[] key) {
        int remain;
        int packetSize = tmpBuffer.length;
        remain = packetSize % length;
        int size = length;
        byte[] checkData = new byte[8];

        System.arraycopy(tmpBuffer, 0, checkData, 0, 8);
        checkData = DES.ECBencrypt(checkData, key);
        while (packetSize > size) {
            if ((packetSize - size) < length) {
                doXor(checkData, tmpBuffer, size, remain);
            } else {
                doXor(checkData, tmpBuffer, size, length);
            }
            size += length;
            checkData = DES.ECBencrypt(checkData, key);            
        }
        return checkData;
    }
    
    private static byte[] doXor(byte[] xor1, byte[] xor2, int offset, int remain) {
        for (int i = 0; i < remain; i++) {
            xor1[i] = (byte) (xor1[i] ^ xor2[i + offset]);
        }
        return xor1;
    }
    
    public void addField(TransferData data) throws Exception {

        byte[] by = (byte[]) data.getObject(DataMapDict.SERVER_FORWARD_CONTENT);

        byte[] ne = new byte[by.length + 3];
        System.arraycopy(by, 0, ne, 1, by.length);

        System.err.print("in:" + Converts.bytesToHexString(ne));

        data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, ne);
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        try {
            generateMacData(data);
            addField(data);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "InsertFieldBuild异常");
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }

}
