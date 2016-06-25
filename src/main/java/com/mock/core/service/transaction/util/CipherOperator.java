package com.mock.core.service.transaction.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * <pre>
 * 字符编码类处理
 * </pre>
 *
 * @author 
 * @version $Id: CipherOperator.java, v 0.1 2010-3-26 下午07:45:49 peng.lanqp Exp $
 */
public class CipherOperator {

    private static char ASCII[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F' };

    /**
     * <pre>
     * 随机产生密钥
     * </pre>
     *
     * @return 随机产生ATM的16位工作密钥
     */
    public static String genKey() {
        //随即产生KEY
        long lKey = (long) (0x1000000000000000L * Math.random());
        String key = Long.toHexString(lKey);
        if (key.length() < 16)
            key += "0000000000000000".substring(0, 16 - key.length());
        return key.toUpperCase();
    }

    /**
     * <pre>
     * 异或函数
     * </pre>
     *
     * @param a
     * @param b
     * @param len
     * @return
     */
    static byte[] XOR(byte[] a, byte[] b, int len) {
        byte[] s = new byte[len];
        for (int i = 0; i < len; i++) {
            s[i] = (byte) (a[i] ^ b[i]);
        }

        return s;
    }

    /**
     * <pre>
     * 进行数字BCD码到ASCII码的转换
     * </pre>
     *
     * @param b 压缩后BCD码
     * @param len 数据长度
     * @return BCD码 => ASCII码 
     */
    public static String BCD2ASCII(byte[] b, int len) {
        String s = "";
        if (len % 2 == 0) {
            s += ASCII[(b[0] & 0xf0) >> 4];
            s += ASCII[b[0] & 0x0f];
        } else
            s += ASCII[b[0] & 0x0f];
        for (int i = 1; i < (len + 1) / 2; i++) {
            s += ASCII[(b[i] & 0xf0) >> 4];
            s += ASCII[b[i] & 0x0f];
        }
        return s;
    }

    /**
     * <pre>
     * BCD码转为10进制串(阿拉伯数据)
     * </pre>
     *
     * @param bytes BCD码
     * @return 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1)
            : temp.toString();
    }

    /**
     * <pre>
     * BCD码转10进制的字符串
     * </pre>
     *
     * @param bytes
     * @return
     */
    public static String bcd2StrWithoutClipe(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString();
    }

    /**
     * <pre>
     * 进行数字ASCII码到BCD码的转换
     * </pre>
     *
     * @param val 压缩后的ASCII码
     * @param len 数据长度
     * @return ASCII码 => BCD码 
     */
    public static byte[] ASCII2BCD(byte[] val, int len) {
        byte[] valByte = new byte[(len + 1) / 2];
        if (len % 2 == 0) {
            for (int i = 0; i < len; i++) {
                byte b = val[i];
                if (b > '9')
                    b = (byte) (b % 0x10 + 9);
                else
                    b = (byte) (b % 0x10);
                if (i % 2 == 0)
                    valByte[i / 2] = (byte) (b * 0x10);
                else
                    valByte[i / 2] += b;
            }
        } else {
            valByte[0] = (byte) (val[0] % 0x10);
            for (int i = 1; i < len; i++) {
                byte b = val[i];
                if (b > '9')
                    b = (byte) (b % 0x10 + 9);
                else
                    b = (byte) (b % 0x10);
                if (i % 2 == 1)
                    valByte[(1 + i) / 2] = (byte) (b * 0x10);
                else
                    valByte[(1 + i) / 2] += b;

            }
        }

        return valByte;
    }
    
    
    
    /**
     * 通讯前4位算法(实际长度转字节)
     * 
     * @param mesgLength 实际长度     * @return 转换后的4位字节     */   
    public static byte[] encode(int mesgLength) {

        byte[] dst = new byte[4];
        dst[0] = 0x00;
        dst[1] = 0x00;
        dst[2] = (byte) (mesgLength / 256);
        dst[3] = (byte) (mesgLength % 256);

        return dst;
    }
    
    
    /**
     * 通讯前4位算法(实际字节转长度)
     * @param mesgLengthByte
     * @return
     */
    public static int decode(byte[] mesgLengthByte) {

        if (mesgLengthByte.length != 4) {
            return 0;
        }
        // int ai = 0;        
        int ai = mesgLengthByte[2];

        if (ai < 0) {
            ai += 256;
        }
        

        int bi = mesgLengthByte[3];
        if (bi < 0) {
            bi += 256;
        }

        return ai * 256 + bi;      //        return 888;
        
    }

    /**
     * 进行DES加解密
     * @param data  DES加密数据
     * @param key   DES加密密钥
     * @param mode  加密模式    true    －   加密 
     *                      false   －   解密
     * 
     * @return  DES算法处理后的数据
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    /**
     * <pre>
     * 进行DES加解密
     * </pre>
     *
     * @param data DES加密数据
     * @param key DES加密密钥
     * @param mode 加密模式(true:加密   false:解密)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String operateDES(String data, String key, boolean mode)
                                                                          throws NoSuchAlgorithmException,
                                                                          NoSuchPaddingException,
                                                                          InvalidKeyException,
                                                                          IllegalBlockSizeException,
                                                                          BadPaddingException {

        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

        // String to BCD
        byte[] bKey = ASCII2BCD(key.getBytes(), 16);
        byte[] bData = ASCII2BCD(data.getBytes(), 16);

        // 指定密钥
        SecretKey skey = new javax.crypto.spec.SecretKeySpec(bKey, "DES");

        //加密、解密后byte数据
        byte[] cipherText;
        if (mode) {
            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
        } else {

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
        }

        return BCD2ASCII(cipherText, 16);

    }

    /**
     * <pre>
     * 进行DES加解密
     * </pre>
     * 
     * @param data DES加密数据
     * @param key DES加密密钥
     * @param mode 加密模式 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String operateDES(byte[] data, String key, boolean mode)
                                                                          throws NoSuchAlgorithmException,
                                                                          NoSuchPaddingException,
                                                                          InvalidKeyException,
                                                                          IllegalBlockSizeException,
                                                                          BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

        // String to BCD
        byte[] bKey = ASCII2BCD(key.getBytes(), 16);

        // 指定密钥
        SecretKey skey = new javax.crypto.spec.SecretKeySpec(bKey, "DES");

        //加密、解密后byte数据
        byte[] cipherText;
        if (mode) {
            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            cipherText = cipher.doFinal(data);
        } else {

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, skey);
            cipherText = cipher.doFinal(data);
        }

        return BCD2ASCII(cipherText, 16);
    }

    /**
     * <pre>
     * 进行TripleDES加解密
     * </pre>
     *
     * @param data TripleDES加密数据
     * @param key TripleDES加密密钥
     * @param mode 加密模式
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static String operator3DES(String data, String key, boolean mode)
                                                                            throws NoSuchAlgorithmException,
                                                                            NoSuchPaddingException,
                                                                            IllegalBlockSizeException,
                                                                            BadPaddingException,
                                                                            InvalidKeyException {

        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");

        // String to BCD
        byte[] bKey = ASCII2BCD(key.getBytes(), 48);
        byte[] bData = ASCII2BCD(data.getBytes(), 16);
        // 指定密钥
        SecretKey skey = new javax.crypto.spec.SecretKeySpec(bKey, "DESede");

        //加密、解密后byte数据
        byte[] cipherText;
        if (mode) {
            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
        } else {
            // 解密
            cipher.init(Cipher.DECRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
        }

        return BCD2ASCII(cipherText, 16);
    }

    /**
     * <pre>
     * 进行 ANSI99MAC 加解密
     * </pre>
     *
     * @param macbuf 
     * @param bKey 明文密钥
     * @param length macbuf的长度
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static String operatorANSI99MAC(String macbuf, String bKey, int length)
                                                                                  throws NoSuchAlgorithmException,
                                                                                  NoSuchPaddingException,
                                                                                  IllegalStateException,
                                                                                  IllegalBlockSizeException,
                                                                                  BadPaddingException,
                                                                                  InvalidKeyException {

        // byte mackey[] = new byte[8];
        byte byteMacBuf[] = macbuf.getBytes();
        byte buf[] = new byte[8];

        byte[] macValue = new byte[8];

        //加密、解密后byte数据
        byte[] cipherText;

        int i = 0;
        while ((i + 8) <= length) {
            System.arraycopy(byteMacBuf, i, buf, 0, 8);

            for (int j = 0; j < 8; j++) {
                macValue[j] ^= buf[j];
            }
            SecretKey key = new javax.crypto.spec.SecretKeySpec(ASCII2BCD(bKey.getBytes(), 16),
                "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(macValue);
            System.arraycopy(cipherText, 0, macValue, 0, 8);
            i += 8;
        }
        if (i != length) {

            byte[] unbuf = new byte[8];
            System.arraycopy(byteMacBuf, i, unbuf, 0, length - i);

            for (int j = 0; j < 8; j++) {
                macValue[j] ^= unbuf[j];
            }
            SecretKey key = new javax.crypto.spec.SecretKeySpec(ASCII2BCD(bKey.getBytes(), 16),
                "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(macValue);
            System.arraycopy(cipherText, 0, macValue, 0, 8);
        }

        return BCD2ASCII(macValue, 16);
    }

    /**
     * <pre>
     * 进行 ANSI98 加解密
     * </pre>
     *
     * @param pinblock
     * @param panblock 两字节密钥长度+账号的后13位的前12位转BCD码，共8字节
     * @param key 加解密的密钥
     * @param isEncrypt 加解密标志（true ：加密， false：解密）
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static String ANSI98(String pinblock, String panblock, String key, boolean isEncrypt)
                                                                                                throws NoSuchAlgorithmException,
                                                                                                NoSuchPaddingException,
                                                                                                IllegalStateException,
                                                                                                IllegalBlockSizeException,
                                                                                                BadPaddingException,
                                                                                                InvalidKeyException {

        //String to BCD
        byte[] bKey = ASCII2BCD(key.getBytes(), 16);
        byte[] bPin = ASCII2BCD(pinblock.getBytes(), 16);
        byte[] bPan = ASCII2BCD(panblock.getBytes(), 16);
        byte[] bData;
        // 指定密钥
        SecretKey skey = new javax.crypto.spec.SecretKeySpec(bKey, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        byte[] cipherText;
        String m_pinblock;
        if (isEncrypt) //加密
        {
            bData = XOR(bPin, bPan, 8);
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
            m_pinblock = BCD2ASCII(cipherText, 16);
        } else //解密
        {
            bData = bPin;
            cipher.init(Cipher.DECRYPT_MODE, skey);
            cipherText = cipher.doFinal(bData);
            bData = XOR(cipherText, bPan, 8);
            cipherText = bData;
            m_pinblock = BCD2ASCII(cipherText, 16);
            m_pinblock = m_pinblock.substring(2, (cipherText[0] & 0x0f) + 2);
        }
        return m_pinblock;
    }

    /**
     * <pre>
     * 进行数字-》字节的转换，而且该转换是先将数字转换成16进制
     * eg :  int 96   -- >  [0x00][0x60] 
     *       int 59   -- >  [0x00][0x3b]
     * 
     * </pre>
     *
     * @param n
     * @return
     */
    public static byte[] intoByteArray(int n) {
        int tmp = n;
        byte[] b = new byte[2];
        b[1] = new Integer(Integer.toHexString(tmp % 256)).byteValue();
        tmp = tmp >> 8;
        b[0] = new Integer(Integer.toHexString(tmp)).byteValue();
        return b;
    }
}



