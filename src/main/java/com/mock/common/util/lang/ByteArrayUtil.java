package com.mock.common.util.lang;

/**
 * <pre>
 * Byte数组操作工具类
 * </pre>
 *
 * @author peng.lanqp
 * @version $Id: ByteArrayUtil.java, v 0.1 2010-5-12 下午05:09:02 peng.lanqp Exp $
 */
public class ByteArrayUtil {

    /**
     * <pre>
     * 字符串Byte数组转java字节码
     * 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
     * 
     * "600900".getBytes()
     * 转化之后的效果相当于 new byte[] {(byte)0x60,(byte)0x09,(byte)0x00} 
     * </pre>
     *
     * @param strByteArray
     * @return
     */
    public static byte[] hex2byte(byte[] strByteArray) {
        if ((strByteArray.length % 2) != 0) {
            throw new IllegalArgumentException("需要处理的参数长度不是偶数");
        }

        byte[] hexByteArray = new byte[strByteArray.length / 2];
        for (int n = 0; n < strByteArray.length; n += 2) {
            String item = new String(strByteArray, n, 2);
            hexByteArray[n / 2] = (byte) Integer.parseInt(item, 16);
        }

        return hexByteArray;
    }

    /**
     * <pre>
     * 字符串转java字节码
     * 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
     * 
     * "600900"
     * 转化之后的效果相当于 new byte[] {(byte)0x60,(byte)0x09,(byte)0x00} 
     * </pre>
     *
     * @param str 
     * @return
     */
    public static byte[] str2ByteArrary(String str) {
        return hex2byte(str.getBytes());
    }

}


