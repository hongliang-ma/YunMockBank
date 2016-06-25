package com.mock.core.service.transaction.component.message.parser.util;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.vo.BytesMacGenerater;

/**
 * 工作密钥解密
 * 
 * 
 */
public final class WorkKeyDecrypt {
    /** logger */
    protected static final Logger logger           = LoggerFactory.getLogger(ComponetHandler.class);
    private static final String   FIRST_MAIN_KEY   = "0123456789ABCDEF0123456789ABCDEF";
    private static final String   SECOND_MAIN_KEY  = "00000000000000000000000000000000";
    private static final String   THIRD_MAIN_KEY   = "00000000000000000000000000000000";
    private static final String   ENCRYPT_WORK_KEY = "95856D0105C7E34795856D0105C7E347";
    private static final String   BODY             = "05102020008002C00015980000002092143831313237313931313231303333333034353831333238313100064130303130310012303031313434303030303733";

    /**
     * 
     * @return
     */
    public static byte[] generateDecryptWorkKey(byte[] workKey) {

        byte[] decryptMainkey = generateDecryptMainKey();
        logger.info("main key is :" + Converts.bytesToHexString(decryptMainkey));

        return DES.decrypt3DESofdouble(workKey, decryptMainkey);
    }

    /**
     * 
     * @return
     */
    private static byte[] generateDecryptMainKey() {

        String mainKey = FIRST_MAIN_KEY + SECOND_MAIN_KEY + THIRD_MAIN_KEY;
        byte[] tmpBuffer = Converts.hexStringToBytes(mainKey);
        byte[] checkData = new byte[16];
        System.arraycopy(tmpBuffer, 0, checkData, 0, 16);
        return getDecryptMainKeyBytes(checkData, tmpBuffer, 16);
    }

    /**
     * 
     * @param byteRes
     * @param tmpBuffer
     * @param length
     * @return
     */
    private static byte[] getDecryptMainKeyBytes(byte[] checkData, byte[] tmpBuffer, int length) {

        int i, j;
        j = tmpBuffer.length / length;
        for (i = 1; i < j; i++) {
            doXor(checkData, tmpBuffer, i * length);
        }
        byte[] tmp = new byte[length];
        System.arraycopy(checkData, 0, tmp, 0, length);
        return tmp;
    }

    /**
     * 异或运算
     * 
     * @param xor1
     * @param xor2
     * @return xor
     */
    public static byte[] doXor(byte[] xor1, byte[] xor2, int offset) {
        for (int i = 0; i < 8; i++) {
            xor1[i] = (byte) (xor1[i] ^ xor2[i + offset]);
        }
        return xor1;
    }

    /**
     * 异或运算
     * 
     * @param xor1
     * @param xor2
     * @return xor
     */
    public static byte[] doXor(byte[] xor1, byte[] xor2) {
        for (int i = 0; i < 8; i++)
            xor1[i] = (byte) (xor1[i] ^ xor2[i]);
        return xor1;
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        byte[] bs = Converts.hexStringToBytes("95856D0105C7E34795856D0105C7E347");
        System.out.println("主密钥解密：" + generateDecryptMainKey().length);
        System.out.println("工作密钥解密："
                           + Converts.bytesToHexString(generateDecryptWorkKey(Converts
                               .hexStringToBytes(ENCRYPT_WORK_KEY))));
        System.out
            .println("3des:"
                     + Converts.bytesToHexString(DES.encrypt3DESofdouble(
                         BytesMacGenerater.generateBytesForMac(Converts.hexStringToBytes(BODY)),
                         generateDecryptWorkKey(Converts.hexStringToBytes(ENCRYPT_WORK_KEY)))));
    }
}
