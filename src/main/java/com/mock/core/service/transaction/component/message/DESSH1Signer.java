/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.mock.core.service.transaction.component.message;

import org.apache.commons.codec.binary.Base64;
import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.util.EncodeUtils;
import com.mock.core.service.transaction.util.DES;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 3Des加密
 * @author jun.qi
 * @version $Id: DESSH1Signer.java, v 0.1 2012-7-3 下午01:15:31 jun.qi Exp $
 */
public final class DESSH1Signer extends ComponetHandler {

    /**
     * 获取实际的工作密钥
     * @param comm
     * @return
     */
    private byte[] getRealKey(TransferData localTransferData) throws Exception {
        byte[] rv = null;

        String baseKey = (String) localTransferData.getObject("DESSH1Signer" + "basekey");
        String workKey = (String) localTransferData.getObject("DESSH1Signer" + "workkey");

        if (StringUtil.isEmpty(baseKey) || StringUtil.isEmpty(workKey)) {
            logger.error("参数为空（basekey或者workkey）！无法获取实际密钥。");
            throw new AnymockException(CommunicationErrorCode.DECODE_IO_EXCEPTION);
        } else {
            byte[] base = EncodeUtils.fromHexString(baseKey.getBytes(), baseKey.length());
            byte[] work = EncodeUtils.fromHexString(workKey.getBytes(), workKey.length());

            rv = DES.decrypt3DESofdouble(work, base);
            LoggerUtil.info(logger, "开始打印实际的工作密钥: ", printHex(rv));
        }

        return rv;
    }

    public static String printHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String hex = null;
        for (int i = 0, iCount = b.length; i < iCount; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 签名
     * 
     * @param source
     * @param comm
     * @param realKey
     * @return
     */
    private String sign(String source, TransferData localTransferData, byte[] realKey)
                                                                                      throws Exception {

        //需要算摘要的标签
        String shaTarget = (String) localTransferData.getObject("DESSH1Signer" + "shaTarget");
        //编码格式
        String encoding = (String) localTransferData.getObject("DESSH1Signer" + "encoding");
        //签名是否做base64编码,默认为true，如果不需要base请填false
        String base64Flag = (String) localTransferData.getObject("DESSH1Signer" + "base64Flag");

        //签名xml标签名
        String signTagName = (String) localTransferData.getObject("DESSH1Signer" + "signTagName");
        //签名父标签
        String signParentTagName = (String) localTransferData.getObject("DESSH1Signer"
                                                                        + "signParentTagName");

        if (StringUtil.isEmpty(shaTarget) || StringUtil.isEmpty(encoding)
            || StringUtil.isEmpty(signTagName) || StringUtil.isEmpty(signParentTagName)) {
            logger.error("参数为空！无法进行签名。");
            return null;
        }

        //计算摘要
        byte[] sha1Val = sha1(source, shaTarget, encoding);
        if (sha1Val == null) {
            logger.error("摘要为null，无法进行签名!");
            return null;
        }

        //加密摘要
        byte[] sha1ValAfEncrypt = DES.encrypt3DESofdouble(sha1Val, realKey);

        if (sha1ValAfEncrypt == null) {
            logger.error("加密失败");
            return null;
        }

        //编码
        boolean isBase64 = false;

        if (base64Flag == null || !StringUtil.equalsIgnoreCase(base64Flag.trim(), "false")) {
            isBase64 = true;
        }
        //base64_ischunk,Base64编码后是否分（76字符一段），默认false（都在一行）
        String chunkFlag = (String) localTransferData.getObject("DESSH1Signer" + "chunkFlag");
        boolean isChunk = true;
        if (chunkFlag == null || !chunkFlag.trim().equals("true")) {
            isChunk = false;
        }

        String sha1ValAfEncryptAndEncoding = null;
        if (isBase64) {
            sha1ValAfEncryptAndEncoding = base64(sha1ValAfEncrypt, isChunk);
        } else {
            sha1ValAfEncryptAndEncoding = EncodeUtils.encodeHexString(sha1ValAfEncrypt);
        }

        //将签名写入原报文
        int pos = source.indexOf("</" + signParentTagName + ">");
        if (pos == -1) {
            logger.error("在返回报文模板中不存在" + signParentTagName + "标签，请检查 ！");
            return null;
        }

        String prec = source.substring(0, pos);
        String endc = source.substring(pos);

        StringBuffer rvSb = new StringBuffer();
        rvSb.append(prec);
        rvSb.append("<").append(signTagName).append(">");
        rvSb.append(sha1ValAfEncryptAndEncoding);
        rvSb.append("</").append(signTagName).append(">");
        rvSb.append(endc);

        return rvSb.toString();
    }

    /**
     * 计算摘要，使用sha-1算法
     * 
     * @param content
     * @param shaTarget
     * @param encoding
     * @return
     */
    private byte[] sha1(String content, String shaTarget, String encoding) {
        byte[] rv = null;

        if (StringUtil.isEmpty(content) || StringUtil.isEmpty(shaTarget)) {
            logger.error("参数为空（返回模板或者需要算摘要的标签）！无法计算摘要。");
        } else {
            String needShaContent = getNeedShaContent(content, shaTarget);
            if (needShaContent != null) {
                try {
                    rv = EncodeUtils.sha1(needShaContent, encoding);
                } catch (Exception e) {
                    logger.error("计算摘要失败。", e);
                }
            }
        }

        return rv;
    }

    /**
     * base64编码
     * 
     * @param content
     * @param shaTarget
     * @param encoding
     * @return
     */
    private String base64(byte[] content, boolean ischunk) {
        String rv = null;
        if (content != null) {
            rv = new String(Base64.encodeBase64(content, ischunk));
        } else {
            logger.error("content为null，不做base64编码。");
        }
        return rv;
    }

    /**
     * 获取需要计算摘要的内容
     * 
     * @param content
     * @param cfgdir
     * @return
     */
    private String getNeedShaContent(String content, String shaTarget) {

        String needShaContent = null;
        String startTag = "<" + shaTarget + ">";
        String endTag = "</" + shaTarget + ">";
        int pos1 = content.indexOf(startTag);
        int pos2 = content.indexOf(endTag);
        if (pos1 == -1 || pos2 == -1) {
            logger.error("不存在需要运算摘要的标签，请检查配置");
        } else {
            needShaContent = content.substring(pos1, pos2 + endTag.length());
        }
        return needShaContent;
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        //从上一个组件获得的数据
        String source = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        try {
            byte[] realKey = getRealKey(localTransferData);
            if (StringUtil.isBlank(source)) {
                logger.error("无法获取上一步的报文");
                return;
            }
            if (realKey == null) {
                logger.error("无法获取实际的密钥，请检查basekey和workkey参数");
                return;
            }
            //签名
            String retr = source;

            //是否签名,默认false
            String signFlag = (String) localTransferData.getObject("DESSH1Signer" + "signFlag");
            if (signFlag != null && signFlag.trim().equals("true")) {
                retr = sign(source, data, realKey);
            }
            //全文加密,是否全文加密
            String encryptWholeMsg = (String) localTransferData.getObject("DESSH1Signer"
                                                                          + "encryptWholeMsg");
            if (encryptWholeMsg != null && encryptWholeMsg.trim().equals("true")) {

                String encoding = (String) localTransferData.getObject("DESSH1Signer" + "encoding");
                String base64Flag = (String) localTransferData.getObject("DESSH1Signer"
                                                                         + "base64Flag");
                String chunkFlag = (String) localTransferData.getObject("DESSH1Signer"
                                                                        + "chunkFlag");
                boolean isBase64 = false;
                if (base64Flag == null || !base64Flag.trim().equals("false")) {
                    isBase64 = true;
                }

                boolean isChunk = true;
                if (chunkFlag == null || !chunkFlag.trim().equals("true")) {
                    isChunk = false;
                }
                byte[] retBytes = retr.getBytes(encoding);
                int totalLen = retBytes.length;
                int tmp1 = totalLen % 8;
                int tmp2 = 8 - tmp1;
                String paddingChar = (String) localTransferData.getObject("DESSH1Signer"
                                                                          + "paddingChar");
                byte paddingByte = " ".getBytes(encoding)[0];
                if (paddingChar != null && paddingChar.length() == 1
                    && paddingChar.getBytes(encoding).length == 1) {
                    paddingByte = paddingChar.getBytes(encoding)[0];
                }
                byte[] retBytesAfPadding = new byte[totalLen + tmp2];
                System.arraycopy(retBytes, 0, retBytesAfPadding, 0, totalLen);
                for (int i = totalLen; i < totalLen + tmp2; i++) {
                    retBytesAfPadding[i] = paddingByte;
                }

                byte[] tmp = DES.encrypt3DESofdouble(retBytesAfPadding, realKey);
                if (isBase64) {
                    retr = base64(tmp, isChunk);
                } else {
                    retr = EncodeUtils.encodeHexString(tmp);
                }
            }
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, retr);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "全文加密失败");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}