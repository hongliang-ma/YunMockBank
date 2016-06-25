package com.mock.core.service.transaction.filestory.util;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

/**
 * 这个是key的工具类，用来管理我们的key
 * @author sheng.liuzs
 *
 */
public class KeyUtil {

    private static final Log logger = LogFactory.getLog(KeyUtil.class);

    /**
     * 
     * 这个类用来得到支付宝的公钥，返回一个PublicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getAlipayPubKey() throws Exception {
        KeyReader keyReader = new KeyReader();
        // 注意这里java在读取文件路径中有空格的情况下是会抛异常的。
        String filename = keyReader.getClass().getResource("Katong.cer").getFile();
        PublicKey pubKey = (PublicKey) keyReader.fromCerStoredFile(filename);
        return pubKey;
    }

    /**
     * 
     * 这个类用来得到模拟银行的公钥，返回一个PublicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getMOCKPubKey() throws Exception {
        KeyReader keyReader = new KeyReader();
        // 注意这里java在读取文件路径中有空格的情况下是会抛异常的。
        String filename = keyReader.getClass().getResource("mock.cer").getFile();
        PublicKey pubKey = (PublicKey) keyReader.fromCerStoredFile(filename);
        return pubKey;
    }

    /**
     * 
     * 这个类用来得到模拟银行的私钥，返回一个PrivateKey
     * @return
     * @throws Exception 
     * @throws Exception
     */
    public static PrivateKey getMOCKPriKey(Resource priiKey) {
        KeyReader keyReader = new KeyReader();
        // 注意这里java在读取文件路径中有空格的情况下是会抛异常的。
        PrivateKey priKey = null;
        try {
            File file = priiKey.getFile();
            priKey = keyReader.readPrivateKeyfromPKCS12StoredFile(file, "12345678");
        } catch (Exception e) {
            logger.error("couldn't get MOCKPrivatekey", e);
        }
        return priKey;
    }

    /**
     * 获取公钥信息
     * @param args
     */
    public static void main(String[] args) {

    }
}
