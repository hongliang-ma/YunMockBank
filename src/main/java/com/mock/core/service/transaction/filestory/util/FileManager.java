package com.mock.core.service.transaction.filestory.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    /**
     * 
     * 文件上传完成后，文件上传者发送文件上传通知报文给对方。
     * 在发送文件上传通知报文前，
     * 文件上传方可以再次发送相同文件名的文件覆盖原来的文件。
     * 文件上传方一旦发送了文件上传通知报文，文件系统不允许再次上传该文件。
     * 
     * 
     * 银行把文件上传成功到支付宝之后，支付宝会返回一个FileAccept报文。
     * 银行收到FileAccept报文后可以发一个XXXNotify报文来通知支付宝去下载这个文件。
     * filedate的格式为YYYYMMDD
     * 为了简单起见，我们规定c盘下必须有这个以fileName命名的文件。
     * @throws IOException 
     */

    public static boolean upload(String urlRoot, String urlContext, byte[] buffer, String fileName,
                                 Resource priKey) throws IOException {
        String sign = SignUtil.sign(urlContext, priKey);
        sign = URLEncoder.encode(sign, "UTF-8"); // url编码

        // upload File here!
        String targetURL = urlRoot + urlContext;
        HttpClient client = new HttpClient();
        PostMethod filePost = new PostMethod(targetURL);
        LoggerUtil.info(logger, "Target Url: ", targetURL);

        Part[] parts = { new FilePart(fileName, new ByteArrayPartSource(fileName, buffer)) };
        System.err.println(new String(buffer));
        filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

        // queryString != parameters
        String queryString = "certId=0001&sign=" + sign;
        filePost.setQueryString(queryString);
        LoggerUtil.info(logger, "Query String: ", queryString);

        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        int status = client.executeMethod(filePost);
        if (status == HttpStatus.SC_OK) {
            LoggerUtil.info(logger, "文件发送成功：", HttpStatus.SC_OK);
            String res = filePost.getResponseBodyAsString();
            //如果收到FileAccept的报文，则表示上传成功
            LoggerUtil.info(logger, "文件上传返回报文:　\n", res);
            if (StringUtil.contains(res, "FileAccept")) {
                return true;
            }
        }
        LoggerUtil.info(logger, "未收到成功返回报文:", status);
        return false;

    }

    /**
     * 对一个输入流做摘要，返回摘要。
     * @param in
     * @return 
     * @throws IOException 
     */
    public static String digest(InputStream in) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        IOUtils.copy(in, byteStream);
        return digest(byteStream.toByteArray());
    }

    /**
     * 计算文件摘要
     * 
     * @param contents
     * @return
     */
    public static String digest(byte[] contents) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(contents);
            return new String(Hex.encodeHex(messageDigest.digest())).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.info(logger, "digest异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

}
