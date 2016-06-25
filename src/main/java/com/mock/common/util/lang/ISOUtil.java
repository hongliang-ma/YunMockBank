/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util.lang;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import com.mock.common.util.ExceptionUtil;

/**
 * ISO-8583 工具类
 * 
 * @author peng.lanqp
 * @author 松雪
 * @version $Id: ISOUtil.java, v 0.1 2011-8-12 下午7:14:46 peng.lanqp Exp $
 */
public class ISOUtil {

    /** ISO编码字符集 */
    public static final String ENCODING = "ISO8859_1";

    /**
     * converts to BCD
     * 
     * @param s - the number
     * @param padLeft - flag indicating left/right padding
     * @param d The byte array to copy into.
     * @param offset Where to start copying into.
     * @return BCD representation of the number
     */
    public static byte[] number2bcd(String s, boolean padLeft, byte[] d, int offset) {
        int len = s.length();
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            d[offset + (i >> 1)] |= (s.charAt(i - start) - '0') << ((i & 1) == 1 ? 0 : 4);
        }

        return d;
    }

    /**
     * converts to BCD
     * 
     * @param s - the number
     * @param padLeft - flag indicating left/right padding
     * @return BCD representation of the number
     */
    public static byte[] number2bcd(String s, boolean padLeft) {
        int len = s.length();
        byte[] data = new byte[(len + 1) >> 1];
        return number2bcd(s, padLeft, data, 0);
    }

    /**
     * 字符串可是是非数字（比如十六进制的数据） 转 BCD
     * 
     * @param src
     * @param padLeft
     * @param data
     * @param offset
     * @return BCD representation of the string
     */
    public static byte[] string2bcd(String src, boolean padLeft, byte[] data, int offset) {
        String temp = src;
        int len = src.length();
        if (len % 2 != 0) {
            temp = padLeft ? ('0' + src) : (src + '0');
            len = temp.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        abt = temp.getBytes();
        int j, k;

        for (int p = 0; p < temp.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            data[p + offset] = b;
        }

        return data;

    }

    /**
     * 字符串可是是非数字（比如十六进制的数据） 转 BCD
     * @param src
     * @param padLeft true左补‘0’ false右补
     * @return
     */
    public static byte[] string2bcd(String src, boolean padLeft) {
        int len = src.length();
        byte[] data = new byte[(len + 1) >> 1];
        return string2bcd(src, padLeft, data, 0);
    }

    /**
     * converts a BCD representation of a number to a String
     * 
     * @param b - BCD representation
     * @param offset - starting offset
     * @param len - BCD field len
     * @param padLeft - was padLeft packed?
     * @return the String representation of the number
     */
    public static String bcd2number(byte[] b, int offset, int len, boolean padLeft) {
        StringBuilder d = new StringBuilder(len);
        int start = (((len & 1) == 1) && padLeft) ? 1 : 0;
        for (int i = start; i < len + start; i++) {
            int shift = ((i & 1) == 1 ? 0 : 4);
            char c = Character.forDigit(((b[offset + (i >> 1)] >> shift) & 0x0F), 16);
            if (c == 'd') {
                c = '=';
            }

            d.append(Character.toUpperCase(c));
        }
        return d.toString();
    }

    /**
     * 支持非数字的BCD编码数组转化成String
     * 
     * @param rawData
     * @param offset
     * @param length
     * @param padLeft
     * @return
     */
    public static String bcd2string(byte[] rawData, int offset, int length, boolean padLeft) {

        if (length == 0) {
            return StringUtil.EMPTY_STRING;
        }

        StringBuilder builder = new StringBuilder();
        if (padLeft) {
            if (length % 2 == 0) {
                builder.append(ByteConstant.ASCII[(rawData[offset] & 0xf0) >> 4]);
                builder.append(ByteConstant.ASCII[rawData[offset] & 0x0f]);
            } else {
                builder.append(ByteConstant.ASCII[rawData[offset] & 0x0f]);
            }

            for (int i = 1; i < (length + 1) / 2; i++) {
                builder.append(ByteConstant.ASCII[(rawData[offset + i] & 0xf0) >> 4]);
                builder.append(ByteConstant.ASCII[rawData[offset + i] & 0x0f]);
            }

            return builder.toString();
        }

        // 右补零的处理方式
        for (int i = 0; i < (length - 1) / 2; i++) {
            builder.append(ByteConstant.ASCII[(rawData[offset + i] & 0xf0) >> 4]);
            builder.append(ByteConstant.ASCII[rawData[offset + i] & 0x0f]);
        }

        if (length % 2 == 0) {
            builder.append(ByteConstant.ASCII[(rawData[offset + (length - 1) / 2] & 0xf0) >> 4]);
            builder.append(ByteConstant.ASCII[rawData[offset + (length - 1) / 2] & 0x0f]);
        } else {
            builder.append(ByteConstant.ASCII[(rawData[offset + (length - 1) / 2] & 0xf0) >> 4]);
        }

        return builder.toString();
    }

    /**
     * Converts a binary representation of a Bitmap field into a Java BitSet
     * 
     * @param b - binary representation
     * @param offset - staring offset
     * @param maxBits - max number of bits (supports 64,128 or 192)
     * @return java BitSet object
     */
    public static BitSet byte2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? ((b[offset] & 0x80) == 0x80 ? 128 : 64) : maxBits;
        if (maxBits > 128 && b.length > offset + 8 && (b[offset + 8] & 0x80) == 0x80) {
            len = 192;
        }

        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++) {
            if (((b[offset + (i >> 3)]) & (0x80 >> (i % 8))) > 0) {
                bmap.set(i + 1);
            }
        }
        return bmap;
    }

    /**
     * converts a BitSet into a binary field used in pack routines
     * 
     * @param b - the BitSet
     * @param bytes - number of bytes to return
     * @return binary representation
     */
    public static byte[] bitSet2byte(BitSet b, int bytes) {
        int len = bytes * 8;

        byte[] d = new byte[bytes];
        for (int i = 0; i < len; i++) {
            if (b.get(i + 1)) {
                d[i >> 3] |= (0x80 >> (i % 8));
            }
        }

        if (len > 64) {
            d[0] |= 0x80;
        }
        if (len > 128) {
            d[8] |= 0x80;
        }

        return d;
    }

    /**
     * converts a BitSet into a binary field
     * used in pack routines
     * @param b - the BitSet
     * @return binary representation
     */
    public static byte[] bitSet2byte(BitSet b) {
        int len = (((b.length() + 62) >> 6) << 6);
        byte[] d = new byte[len >> 3];
        for (int i = 0; i < len; i++) {
            if (b.get(i + 1)) {
                d[i >> 3] |= (0x80 >> (i % 8));
            }
        }

        if (len > 64) {
            d[0] |= 0x80;
        }
        if (len > 128) {
            d[8] |= 0x80;
        }

        return d;
    }

    /**
     * converts a byte array to hex string 
     * (suitable for dumps and ASCII packaging of Binary fields
     * 
     * @param b - byte array
     * @return String representation
     */
    public static String hexString(byte[] b) {
        StringBuilder d = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            char hi = Character.forDigit((aB >> 4) & 0x0F, 16);
            char lo = Character.forDigit(aB & 0x0F, 16);
            d.append(Character.toUpperCase(hi));
            d.append(Character.toUpperCase(lo));
        }
        return d.toString();
    }

    /**
     * converts a byte array to hex string 
     * (suitable for dumps and ASCII packaging of Binary fields
     * @param b - byte array
     * @param offset  - starting position
     * @param len the length
     * @return String representation
     */
    public static String hexString(byte[] b, int offset, int len) {
        StringBuilder d = new StringBuilder(len * 2);
        int offsetLength = len + offset;
        for (int i = offset; i < offsetLength; i++) {
            char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
            char lo = Character.forDigit(b[i] & 0x0F, 16);
            d.append(Character.toUpperCase(hi));
            d.append(Character.toUpperCase(lo));
        }
        return d.toString();
    }

    /**
     * @param s source string (with Hex representation)
     * @return byte array
     */
    public static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }

    /**
     * @param   b       source byte array
     * @param   offset  starting offset
     * @param   len     number of bytes in destination (processes len*2)
     * @return  byte[len]
     */
    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            int shift = i % 2 == 1 ? 0 : 4;
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }

    /**
     * Converts an ASCII representation of a Bitmap field into a Java BitSet
     * 
     * @param b - hex representation
     * @param offset - starting offset
     * @param maxBits - max number of bits (supports 8, 16, 24, 32, 48, 52, 64,.. 128 or 192)
     * @return java BitSet object
     */
    public static BitSet hex2BitSet(byte[] b, int offset, int maxBits) {
        int len = maxBits > 64 ? ((Character.digit((char) b[offset], 16) & 0x08) == 8 ? 128 : 64)
            : maxBits;
        BitSet bmap = new BitSet(len);
        for (int i = 0; i < len; i++) {
            int digit = Character.digit((char) b[offset + (i >> 2)], 16);
            if ((digit & (0x08 >> (i % 4))) > 0) {
                bmap.set(i + 1);
                if (i == 65 && maxBits > 128) {
                    len = 192;
                }
            }
        }
        return bmap;
    }

    /**
     * 
     * @param s
     * @param e
     * @param offset
     */
    public static void asciiToEbcdic(String s, byte[] e, int offset) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            e[offset + i] = ByteConstant.ASCII2EBCDIC[s.charAt(i) & 0xFF];
        }
    }

    /**
     * 
     * @param e
     * @param offset
     * @param len
     * @return
     */
    public static String ebcdicToAscii(byte[] e, int offset, int len) {
        try {
            return new String(ebcdicToAsciiBytes(e, offset, len), ENCODING);
        } catch (UnsupportedEncodingException ex) {
            ExceptionUtil.caught(ex, "ignore Encoding Exception");
        }

        return StringUtil.EMPTY_STRING;
    }

    /**
     * 
     * @param e
     * @param offset
     * @param len
     * @return
     */
    public static byte[] ebcdicToAsciiBytes(byte[] e, int offset, int len) {
        byte[] a = new byte[len];
        for (int i = 0; i < len; i++) {
            a[i] = ByteConstant.EBCDIC2ASCII[e[offset + i] & 0xFF];
        }
        return a;
    }

    /**
     * @param b a byte[] buffer
     * @return hexdump
     */
    public static String hexdump(byte[] b) {
        return hexdump(b, 0, b.length);
    }

    /**
     * @param b a byte[] buffer
     * @param offset starting offset
     * @param len the Length
     * @return hexdump
     */
    public static String hexdump(byte[] b, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        StringBuilder hex = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        String sep = "  ";
        String lineSep = System.getProperty("line.separator");

        for (int i = offset; i < len; i++) {
            char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
            char lo = Character.forDigit(b[i] & 0x0F, 16);
            hex.append(Character.toUpperCase(hi));
            hex.append(Character.toUpperCase(lo));
            hex.append(' ');
            char c = (char) b[i];
            ascii.append((c >= 32 && c < 127) ? c : '.');

            int j = i % 16;
            switch (j) {
                case 7: {
                    hex.append(' ');
                    break;
                }

                case 15: {
                    sb.append(hexOffset(i));
                    sb.append(sep);
                    sb.append(hex.toString());
                    sb.append(' ');
                    sb.append(ascii.toString());
                    sb.append(lineSep);
                    hex = new StringBuilder();
                    ascii = new StringBuilder();
                    break;
                }

                default: {
                    break;
                }
            }
        }
        if (hex.length() > 0) {
            while (hex.length() < 49) {
                hex.append(' ');
            }

            sb.append(hexOffset(len));
            sb.append(sep);
            sb.append(hex.toString());
            sb.append(' ');
            sb.append(ascii.toString());
            sb.append(lineSep);
        }
        return sb.toString();
    }

    private static String hexOffset(int i) {
        int temp = (i >> 4) << 4;
        int w = temp > 0xFFFF ? 8 : 4;
        return zeropad(Integer.toString(temp, 16), w);
    }

    /**
     * left pad with '0'
     * @param s - original string
     * @param len - desired len
     * @return zero padded string
     * @throws ISOException if string's length greater than len
     */
    public static String zeropad(String s, int len) {
        return padleft(s, len, '0');
    }

    /**
     * zeropads a long (performs modulus operation)
     *
     * @param l the long
     * @param len the length
     * @return zeropadded value
     */
    public static String zeropad(long l, int len) {
        return padleft(Long.toString((long) (l % Math.pow(10, len))), len, '0');
    }

    /**
     * pad to the left
     * @param s - original string
     * @param len - desired len
     * @param c - padding char
     * @return padded string
     */
    public static String padleft(String s, int len, char c) {
        String trimedString = s.trim();
        int trimedLength = trimedString.length();
        if (trimedLength > len) {
            throw new IllegalArgumentException("invalid len " + trimedLength + "/" + len);
        }

        StringBuilder d = new StringBuilder(len);
        int fill = len - trimedLength;
        while (fill-- > 0) {
            d.append(c);
        }

        d.append(trimedString);
        return d.toString();
    }
}
