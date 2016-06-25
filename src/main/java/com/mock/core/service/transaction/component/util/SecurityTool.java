
package com.mock.core.service.transaction.component.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 
 * 处理各银行加密解密相关的操作
 * @author jun.qi
 * @version $Id: SecurityTool.java, v 0.1 2012-7-2 下午03:02:18 jun.qi Exp $
 */
public class SecurityTool {
    /**
     * DES加密
     *
     * @param src
     * @param key
     *
     * @return
     */
    public static String encryptDES(String src, String key) throws SecurityException {
        byte[] bsrc;

        try {
            bsrc = Hex.decodeHex(src.toCharArray());
        } catch (DecoderException e) {
            throw new SecurityException("Decoder error. ", e);
        }

        return encryptDES(bsrc, key);
    }

    /**
     * DES 加密个mimi
     *
     * @param src
     * @param key
     *
     * @return
     */
    public static String encryptDES(byte[] src, String key) throws SecurityException {
        try {
            char[] k = key.toCharArray();
            byte[] rawkey = Hex.decodeHex(k);
            DESKeySpec keySpec = new DESKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            SecretKey kk = keyfactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, kk);

            byte[] enc = cipher.doFinal(src);
            String e = new String(Hex.encodeHex(enc)).toUpperCase();

            return e;
        } catch (Exception e) {
            throw new SecurityException("DES encrypt fail. ", e);
        }
    }

    /**
     * DES 解密
     * @param src
     * @param key
     * @return
     * @throws SecurityException
     */
    public static String decryptDES(String src, String key) throws SecurityException {
        byte[] bsrc;

        try {
            bsrc = Hex.decodeHex(src.toCharArray());
        } catch (DecoderException e) {
            throw new SecurityException("Decoder error. ", e);
        }

        return decryptDES(bsrc, key);
    }

    /**
     * DES 解密
     * @param src
     * @param key
     * @return
     * @throws SecurityException
     */
    public static String decryptDES(byte[] src, String key) throws SecurityException {
        try {
            char[] k = key.toCharArray();
            byte[] rawkey = Hex.decodeHex(k);
            DESKeySpec keySpec = new DESKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            SecretKey kk = keyfactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

            cipher.init(Cipher.DECRYPT_MODE, kk);

            byte[] enc = cipher.doFinal(src);
            String e = new String(Hex.encodeHex(enc)).toUpperCase();

            return e;
        } catch (Exception e) {
            throw new SecurityException("DES decrypt fail. ", e);
        }
    }
}
