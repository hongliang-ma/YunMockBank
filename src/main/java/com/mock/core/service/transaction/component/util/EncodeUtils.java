/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.util;

import java.security.MessageDigest;

/**
 * 编码解码工具类. 
 * @author xi.chen
 * @version $Id: EncodeUtils.java, v 0.1 2011-4-20 下午04:51:40 xi.chen Exp $
 */
public class EncodeUtils {
    
    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'    };

    /**
     * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
     * 
     * @param data
     *            a byte[] to convert to Hex characters
     * @return A String containing hexadecimal characters
     * @since 1.4
     */
    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     * 
     * @param data
     *            a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     * 
     * @param data
     *            a byte[] to convert to Hex characters
     * @param toLowerCase
     *            <code>true</code> converts to lowercase, <code>false</code> to uppercase
     * @return A char[] containing hexadecimal characters
     * @since 1.4
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, DIGITS_LOWER);
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     * 
     * @param data
     *            a byte[] to convert to Hex characters
     * @param toDigits
     *            the output alphabet
     * @return A char[] containing hexadecimal characters
     * @since 1.4
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
    
    
    /**
     * 对输入字符串进行sha1散列, 返回Hex编码的结果.
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static String sha1ToHex(String input,String encoding) throws Exception {
        byte[] digestResult = digest(input, "sha-1",encoding);
        return encodeHexString(digestResult);
    }
    
    /**
     * 
     * 
     * @param input
     * @param encoding
     * @return
     * @throws Exception
     */
    public static byte[] sha1(String input,String encoding )throws Exception {
        return digest(input, "sha-1",encoding);
    }
    
    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     * 
     * @param input
     * @param algorithm
     * @return
     * @throws Exception
     */
    private static byte[] digest(String input, String algorithm,String encoding) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        return messageDigest.digest(input.getBytes(encoding));
    }
    
    /**
     * 
     * 
     * @param abyte0
     * @param i
     * @return
     */
    public static byte[] fromHexString(byte abyte0[], int i) {
        int j = 0;
        if (abyte0[0] == 48 && (abyte0[1] == 120 || abyte0[1] == 88)) {
            j += 2;
            i -= 2;
        }
        int k = i / 2;
        byte abyte1[] = new byte[k];
        for (int l = 0; l < k;) {
            abyte1[l] = (byte) ((hexValueOf(abyte0[j]) << 4 | hexValueOf(abyte0[j + 1])) & 0xff);
            l++;
            j += 2;
        }

        return abyte1;
    }
    
    /**
     * 
     * 
     * @param i
     * @return
     */
    public static int hexValueOf(int i) {
        if (i >= 48 && i <= 57)
            return i - 48;
        if (i >= 97 && i <= 102)
            return (i - 97) + 10;
        if (i >= 65 && i <= 70)
            return (i - 65) + 10;
        else
            return 0;
    }
}
