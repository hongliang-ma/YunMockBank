package com.mock.core.service.transaction.component.file;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.filestory.enums.MessageTypeEnum;
import com.mock.core.service.transaction.filestory.util.BRRNotify;
import com.mock.core.service.transaction.filestory.util.CCNotify;
import com.mock.core.service.transaction.filestory.util.CastorMessageBinder;
import com.mock.core.service.transaction.filestory.util.ECCNotify;
import com.mock.core.service.transaction.filestory.util.Extension;
import com.mock.core.service.transaction.filestory.util.FileEncrypt;
import com.mock.core.service.transaction.filestory.util.FileManager;
import com.mock.core.service.transaction.filestory.util.KeyUtil;
import com.mock.core.service.transaction.filestory.util.Notify;
import com.mock.core.service.transaction.filestory.util.SCNotify;
import com.mock.core.service.transaction.filestory.util.SignUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;
import com.mock.common.util.lang.StringUtil;

/**
 *  用来将文件发送到某一个机器的适配器
 * 
 * @author 马洪良
 * @version $Id: PostFileAdaptor.java, v 0.1 2012-12-14 上午10:32:46 马洪良 Exp $
 */
public class PostFileAdaptor extends ComponetHandler {

    public static final String TEMP_INNER = "hello";
    public static final String TEMP_NAME  = "SCF_20080415_01.zip";

    /**
     * 这段程序模拟银行上传文件到支付宝。
     * 如果上传成功，则发一个通知给支付宝。
     * 如果通知成功，则能收到NotifyAccept的报文。
     * 如果Message里面不含签名，则报一个或者多个域不符合格式
     * 摘要是对zip文件签名，不是对txt文件签名的。
     * 
     * 这里bankName选MOCK的时候，发送通知报文的时候，报"银行标识不正确"
     * 估计后台有处理这个逻辑，过滤掉了MOCK
     * 换成WZCB后就好了。
     * @param args
     * @throws Exception 
     * @throws Exception 
     */
    public String postFile(final TransferData localTransferData) throws Exception {

        //貌似这个文件名必须以zip结尾，否则会传不成功。
        String fileType = (String) localTransferData.getObject("KTFileUploadAdaptorfileType");
        String certId = (String) localTransferData.getObject("KTFileUploadAdaptorcertId");
        String host = (String) localTransferData.getObject("KTFileUploadAdaptorhost");
        String instId = (String) localTransferData.getObject("KTFileUploadAdaptorinstId");

        String operateDate = (String) localTransferData.getObject("KTFileUploadAdaptoroperateDate");
        String extension = (String) localTransferData.getObject("KTFileUploadAdaptorextension");

        String fileName = fileType + "_" + operateDate + "_" + new Random().nextInt(100) + ".zip";

        String urlRt = "http://" + host + "/file";
        String sUrl = "/upload/" + instId + "/" + operateDate + "/" + fileName;

        //首先根据txt文件生成加密后的zip文件。注意是先zip在加密。
        String filePath = (String) localTransferData.getObject("KTFileUploadAdaptorfilePath");
        byte[] buffer = FileEncrypt.makeZipfile(new FileInputStream(filePath));
        LoggerUtil.info(logger, "文件压缩: ", fileName);

        //对生成的zip文件做摘要
        InputStream ins = new ByteArrayInputStream(buffer);
        String resultXml = "";
        String digest = FileManager.digest(ins);
        LoggerUtil.info(logger, "文件摘要: ", digest);
        //上传文件
        Resource priKey = new FileSystemResource(
            "file:/home/admin/build/app/core/service/transaction/src/main/resources/META-INF/spring/mock.pfx");
        boolean flag = FileManager.upload(urlRt, sUrl, buffer, fileName, priKey);
        if (flag) {
            //发送通知给支付宝
            Notify notify = getType(instId, certId, operateDate, fileName, digest, extension);

            CastorMessageBinder binder = new CastorMessageBinder();

            resultXml = binder.bindMessage(notify);

            PrivateKey priiKey = KeyUtil.getMOCKPriKey(priKey);
            resultXml = SignUtil.sign(resultXml, priiKey, getId(fileType));
            LoggerUtil.info(logger, "签名后的结果是: ", resultXml);

        }
        return resultXml;

    }

    private static Notify getType(String instId, String certId, String filedate, String fileName,
                                  String digest, String extension) {
        String type = getId(fileName);
        if (type.equals("SCNotify")) {
            SCNotify notify = getNotify(instId, certId, filedate, fileName, digest);
            return notify;
        } else if (type.equals("CCNotify")) {
            CCNotify notify = getCCNotify(instId, certId, filedate, fileName, digest, extension);
            return notify;
        } else if (type.equals("BRRNotify")) {
            BRRNotify notify = getBRRNotify(instId, certId, filedate, fileName, digest, extension);
            return notify;
        } else if (type.equals("ECCNotify")) {
            ECCNotify notify = getECCNotify(instId, certId, filedate, fileName, digest);
            return notify;
        }
        return null;
    }

    private static SCNotify getNotify(String instId, String certId, String filedate,
                                      String fileName, String digest) {
        SCNotify notify = new SCNotify();
        notify.setMsgId("1234678");
        notify.setMsgCertId(certId);
        notify.setMsgSrc(instId);
        notify.setMsgDest("ALIPAY");
        notify.setMsgVer("1.0.1");
        notify.setFileDate(filedate);
        notify.setFileName(fileName);
        notify.setDigest(digest);
        notify.setMsgTime(new Date());
        notify.setSerialNo("12345678");
        notify.setMsgType(MessageTypeEnum.SIGN_CHECK_NOTIFY);
        return notify;
    }

    private static CCNotify getCCNotify(String instId, String certId, String filedate,
                                        String fileName, String digest, String exten) {
        CCNotify notify = new CCNotify();
        if (StringUtil.isNotBlank(exten)) {
            List<Extension> extensions = new ArrayList<Extension>();
            Extension extension = new Extension();
            extension.setName("limitType");
            extension.setValue(exten);
            extensions.add(extension);
            notify.setExtensions(extensions);
        }
        notify.setMsgId("1234678");
        notify.setMsgCertId("0001");
        notify.setMsgSrc(instId);
        notify.setMsgDest("ALIPAY");
        notify.setMsgVer("1.0.1");
        notify.setFileDate(filedate);
        notify.setFileName(fileName);
        notify.setDigest(digest);
        notify.setMsgTime(new Date());
        notify.setSerialNo("12345678");
        notify.setMsgType(MessageTypeEnum.CLEARING_CHECK_NOTIFY);
        return notify;
    }

    private static BRRNotify getBRRNotify(String instId, String certId, String filedate,
                                          String fileName, String digest, String exten) {
        BRRNotify notify = new BRRNotify();
        if (StringUtil.isNotBlank(exten)) {
            List<Extension> extensions = new ArrayList<Extension>();
            Extension extension = new Extension();
            extension.setName("limitType");
            extension.setValue(exten);
            extensions.add(extension);
            notify.setExtensions(extensions);
        }
        notify.setMsgId("1234678");
        notify.setMsgCertId("0001");
        notify.setMsgSrc(instId);
        notify.setMsgDest("ALIPAY");
        notify.setMsgVer("1.0.1");
        notify.setFileDate(filedate);
        notify.setFileName(fileName);
        notify.setDigest(digest);
        notify.setMsgTime(new Date());
        notify.setSerialNo("12345678");
        notify.setOrginalSerialNo("11111");
        notify.setOriginalDate(new Date());
        notify.setMsgType(MessageTypeEnum.BATCH_REFUND_RESULT_NOTIFY);
        return notify;
    }

    private static ECCNotify getECCNotify(String instId, String certId, String filedate,
                                          String fileName, String digest) {
        ECCNotify notify = new ECCNotify();
        notify.setMsgId("1234678");
        notify.setMsgCertId("0001");
        notify.setMsgSrc(instId);
        notify.setMsgDest("ALIPAY");
        notify.setMsgVer("2.1.1");
        notify.setFileDate(filedate);
        notify.setFileName(fileName);
        notify.setDigest(digest);
        notify.setMsgTime(new Date());
        notify.setSerialNo("12345678");
        notify.setMsgType(MessageTypeEnum.EBANK_CLEARING_CHECK_NOTIFY);
        return notify;
    }

    private static String getId(String fileName) {
        if (StringUtil.contains(fileName, "BRR") || StringUtil.contains(fileName, "ECC")) {
            String idprex = fileName.substring(0, 3);
            return idprex + "Notify";
        }
        String idprex = fileName.substring(0, 2);
        return idprex + "Notify";
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
        try {
            String strReturnMsg = postFile(localTransferData);
            LoggerUtil.info(logger, "文件发送：", strReturnMsg);
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, strReturnMsg);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "上传文件失败!");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {

    }
}