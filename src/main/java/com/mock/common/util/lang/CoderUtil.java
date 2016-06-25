/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util.lang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

/**
 * 字符串编码工具类
 * 
 * @author peng.lanqp
 * @author zhao.xiong
 * @version $Id: CoderUtil.java, v 0.1 2011-4-22 上午09:24:44 peng.lanqp Exp $
 */
public class CoderUtil {

    /** 默认字符集 */
    private final static String CHARSET = "UTF-8";

    /**
     * URL解码，目前在Velocity模板中使用
     * 
     * @param message
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String urlDecode(String message) throws UnsupportedEncodingException {
        return urlDecode(message, CHARSET);
    }

    /**
     * URL编码，目前在Velocity模板中使用
     * 
     * @param message
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String urlEncode(String message) throws UnsupportedEncodingException {
        return urlEncode(message, CHARSET);
    }

    /**
     * BASE64 编码
     * 
     * @param message
     * @return
     */
    public static String base64Encode(byte[] message) {
        return new String(Base64.encodeBase64(message));
    }

    /**
     * BASE64 解码
     * 
     * @param message
     * @return
     */
    public static byte[] base64DecodeToByte(String message) {
        return Base64.decodeBase64(message.getBytes());
    }

    /**
     * 
     * URL解码，目前在Velocity模板中使用
     * @param message
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String urlDecode(String message, String charset)
                                                                  throws UnsupportedEncodingException {
        if (StringUtil.isEmpty(message)) {
            return StringUtil.EMPTY_STRING;
        }
        return URLDecoder.decode(message, charset);
    }

    /**
     * URL编码，目前在Velocity模板中使用
     * 
     * @param message
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String urlEncode(String message, String charset)
                                                                  throws UnsupportedEncodingException {
        if (StringUtil.isEmpty(message)) {
            return StringUtil.EMPTY_STRING;
        }
        return URLEncoder.encode(message, charset);
    }

    /**
     * 用于解码不确定类型的报文，目前在Groovy模板中使用<br>
     * 不确定outTransCode的情况下，无法知道报文格式<br>
     * 在groovy脚本中指定报文格式，然后利用此工具类将字节流转换为相应的对象。<br>
     * 
     * @param messageFormat 由于依赖问题，没有使用MessageFormat枚举,传入text,byte,map类型
     *                      其实map类型是反序列化对象类型，也可以是其它类型，默认为字符串
     * @param inputCharset 字符串类型的编码，如果为空，默认为gbk
     * @param originalData 字节流数据
     * @return 
     * @throws IOException 
     * @throws ClassNotFoundException
     * @see com.mock.core.model.communication.message.enums.MessageFormat 
     */
    public static Object decodeMessage(String messageFormat, String inputCharset,
                                       byte[] originalData) throws IOException,
                                                           ClassNotFoundException {

        Assert.notNull(messageFormat, "消息类型传入为空");

        // 无输入的情况下默认字符集为gbk
        final String charset = (StringUtil.isEmpty(inputCharset)) ? "gbk" : inputCharset;

        if ("text".equalsIgnoreCase(messageFormat)) {

            return new String(originalData, charset);

        } else if ("byte".equalsIgnoreCase(messageFormat)) {

            return originalData;

        } else if ("map".equalsIgnoreCase(messageFormat)) {

            return readObject(originalData);

        }

        // 默认情况下输出字符串
        return new String(originalData, charset);
    }

    /**
     * 将序列化为Byte数组的Map还原
     * @param originalData
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object readObject(byte[] originalData) throws IOException,
                                                         ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(
            originalData));
        Object result = null;
        try {
            result = objectInputStream.readObject();
        } finally {
            IOUtils.closeQuietly(objectInputStream);
        }

        return result;
    }

    /**
     * BASE64 编码
     * 
     * @param message
     * @return
     */
    public static String base64Encode(String message) {
        if (StringUtil.isEmpty(message)) {
            return StringUtil.EMPTY_STRING;
        }
        return new String(Base64.encodeBase64(message.getBytes()));
    }

    /**
     * BASE64 解码
     * 
     * @param message
     * @return
     */
    public static String base64Decode(String message) {
        if (StringUtil.isEmpty(message)) {
            return StringUtil.EMPTY_STRING;
        }
        return new String(Base64.decodeBase64(message.getBytes()));
    }

    /**
     * 解密密文
     * @param data
     * @param password
     * @param alorigthm
     * @return
     * @throws Exception
     */
    public static String decryptData(String data, String password, String alorigthm)
                                                                                    throws Exception {
        byte[] content = Base64.decodeBase64(data.getBytes());
        byte[] pass = Base64.decodeBase64(password.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pass);
        PrivateKey key = KeyFactory.getInstance(alorigthm).generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance(alorigthm);// 创建密码器   
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化   
        byte[] result = cipher.doFinal(content);
        return new String(result); // 解密   
    }

    /**
     * 加密明文
     *
     * @param content
     * @param password
     * @param alorigthm 
     * @return
     */
    public static String encryptData(final String content, final String password,
                                     final String alorigthm) throws Exception {
        byte[] pass = Base64.decodeBase64(password.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pass);
        PublicKey key = KeyFactory.getInstance(alorigthm).generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance(alorigthm);// 创建密码器  
        byte[] byteContent = content.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
        byte[] result = cipher.doFinal(byteContent);
        return new String(Base64.encodeBase64(result)); // 加密   

    }

}
