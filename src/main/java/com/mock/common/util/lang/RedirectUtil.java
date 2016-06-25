/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.common.util.lang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.alibaba.common.lang.StringEscapeUtil;
import com.mock.common.util.ExceptionUtil;

/**
 * 重定向签名跳转url生成工具类
 * 生成对应的url,参数用supergwMessage和签名，会加入对应的种子，网银跳转用<code>RedirectUtil.EBANK_KEY作为加密种子</code>
 * 比如<pre>
 *  http://cashier.sht15.alipay.net/standard/result/redirectPaymentResult.htm?depositId=21414&isSettleSuccess=Y&settleStatus=N&signData=12342323<br>
 * 其它应用可以使用<code>signURLwithSeed</code>
 * 
 * @author zhao.xiong
 * @author 松雪
 * @version $Id: RedirectUtil.java, v 0.2 2012-4-25 上午10:13:58 hao.zhang Exp $
 */
public class RedirectUtil {

    /** 网银的重定向签名需要的常量 **/
    public static final String  EBANK_KEY    = "KN92cdZUzGrdIU2DxPt05yI0gu1jpWRfZbb4ruBGfQGfdmz2t73GTirONvAAvjM";

    /** URL编码字符集 */
    private static final String URL_ENCODING = "GBK";

    /**
     * 通过url域名和query参数来生成url,如类注释所示，种子为网银跳转种子<br>
     * 如url 为http://cashier.sht15.alipay.net/standard/result/redirectPaymentResult.htm?depositId=21414&isSettleSuccess=Y&settleStatus=N<br>
     * 签名后http://cashier.sht15.alipay.net/standard/result/redirectPaymentResult.htm?depositId=21414&isSettleSuccess=Y&settleStatus=N&signData=12342323<br>
     * 其中的paramValues参数自已选择
     * 签名采用md5算法 
     * 
     * @param url
     * @param paramValues
     * @return
     */
    public String signMultiEbankURL(String url, String... paramValue) {

        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("传入的url不能为空");
        }

        StringBuilder result = new StringBuilder(url);
        try {
            StringBuilder sb = new StringBuilder();
            for (String param : paramValue) {
                sb.append(param);
            }
            sb.append(EBANK_KEY);
            String signData = StringEscapeUtil.escapeURL(DigestUtils.md5Hex(sb.toString()),
                URL_ENCODING);
            result.append("&signData=").append(signData);
        } catch (UnsupportedEncodingException e) {
            ExceptionUtil.caught(e, "生成签名数据异常:url=", url);
        }

        return result.toString();
    }

    /**
     * 通过url域名和query参数来生成url,如类注释所示，种子为网银跳转种子<br>
     * 如url 为http://cashier.sht15.alipay.net/standard/result/redirectPaymentResult.htm?depositId=21414&isSettleSuccess=Y&settleStatus=N<br>
     * 签名后http://cashier.sht15.alipay.net/standard/result/redirectPaymentResult.htm?depositId=21414&isSettleSuccess=Y&settleStatus=N&signData=12342323<br>
     * 其中的paramValues参数自已选择 ，velocity里面不支持java的多参数
     * 签名采用md5算法
     * 
     * @param url
     * @param paramValues
     * @return
     * @throws UnsupportedEncodingException
     */
    public String signEbankURL(String url, String paramValues) {
        return signMultiEbankURL(url, paramValues);
    }

    /**
     * 通过url域名和query参数和keySeed来生成url,其中keySeed为外部系统的跳转种子<br>
     * 签名为md5算法
     * 
     * @param url
     * @param keySeed
     * @param paramValues
     * @return
     */
    public String signURLwithSeed(String url, String keySeed, String... paramValues) {
        StringBuilder result = new StringBuilder(url);
        try {
            StringBuilder sb = new StringBuilder();
            for (String param : paramValues) {
                sb.append(param);
            }
            sb.append(keySeed);
            String signData = StringEscapeUtil.escapeURL(DigestUtils.md5Hex(sb.toString()),
                URL_ENCODING);
            result.append("&signData=").append(signData);
        } catch (UnsupportedEncodingException e) {
            ExceptionUtil.caught(e, "生成签名数据异常:url=", url);
        }

        return result.toString();
    }

    /**
     * 访问指定的url，如果访问成功，返回页面的response，否则抛出AnymockException
     * 
     * @param url
     * @param charset 访问页面编码字符集
     * @return
     * @throws IOException 
     */
    public String getHttpResponse(String url, String charset) throws IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                // http状态异常时抛出异常
                throw new IllegalStateException("http返回码异常:url=" + url + ",statusCode="
                                                + statusCode);
            }
            return new String(method.getResponseBody(), charset);
        } finally {
            method.releaseConnection();
        }
    }
}
