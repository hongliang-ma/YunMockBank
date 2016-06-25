package com.mock.core.service.transaction.component.message.parser.util;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES算法
 */
public class DES {
    private static final String EN_MODE = "DES/ECB/NoPadding";

    /**
     * 加密函数
     * 
     * @param data
     *            加密数据
     * @param key
     *            密钥
     * @return 返回加密后的数据
     */
    public static byte[] ECBencrypt(byte[] data, byte[] key) {

        try {

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance(EN_MODE);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，加密数据出错!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密函数
     * 
     * @param data
     *            解密数据
     * @param key
     *            密钥
     * @return 返回解密后的数据
     */
    public static byte[] ECBdecrypt(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // byte rawKeyData[] = /* 用某种方法获取原始密匙数据 */;

            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance(EN_MODE);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，解密出错。");
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        // /61522CC42A2438E4
        try {
            byte[] buf = Converts.hexStringToBytes("31ECB658B9E657D1");
            // byte[] buf = Converts.StringToHexString(source).getBytes();
            byte[] key = Converts.hexStringToBytes("101adf10234ltnd0101adf10234ltnd0");

            System.out.println("---:" + key.length);
            // byte[] iv = "22222222".getBytes();
            byte[] encode = encrypt3DESofdouble(buf, key);
            System.out.println("加密后:" + Converts.bytesToHexString(encode));

            System.out.println("原     始:"
                               + Converts.bytesToHexString(decrypt3DESofdouble(encode, key)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //3DES加密
    public static byte[] encrypt3DESofdouble(byte[] buf, byte[] key) {
        byte[] leftkey = new byte[8];
        byte[] rightkey = new byte[8];
        System.arraycopy(key, 0, leftkey, 0, 8);
        System.arraycopy(key, 8, rightkey, 0, 8);

        return ECBencrypt(DES.ECBdecrypt(DES.ECBencrypt(buf, leftkey), rightkey), leftkey);
    }

    //3DES解密
    public static byte[] decrypt3DESofdouble(byte[] buf, byte[] key) {
        byte[] leftkey = new byte[8];
        byte[] rightkey = new byte[8];
        System.arraycopy(key, 0, leftkey, 0, 8);
        System.arraycopy(key, 8, rightkey, 0, 8);
        return ECBdecrypt(DES.ECBencrypt(DES.ECBdecrypt(buf, leftkey), rightkey), leftkey);
    }
}