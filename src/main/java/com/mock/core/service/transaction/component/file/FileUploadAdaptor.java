
package com.mock.core.service.transaction.component.file;

import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.filestory.util.FileUtil;
import com.mock.common.util.ByteUtil;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 文件上传
 * @author jun.qi
 * @version $Id: FileUploadAdaptor.java, v 0.1 2012-7-3 下午04:21:54 jun.qi Exp $
 */
public class FileUploadAdaptor extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        //文件的临时保存路径
        String filePath = (String) localTransferData.getObject("FileUploadAdaptor" + "filePath");
        String sendPath = (String) localTransferData.getObject("FileUploadAdaptor" + "path");
        //请求类型
        String msgType = (String) localTransferData.getObject("FileUploadAdaptor" + "msgType");

        try {
            byte[] fileCount = FileUtil.readFile(filePath, false).getBytes();
            if (StringUtil.equalsIgnoreCase(msgType, "1")) {
                data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, fileCount);
                LoggerUtil.info(logger, "发送文件：", "长度:", fileCount.length, "内容：",
                    ByteUtil.formatByte(fileCount));
            } else if (StringUtil.equalsIgnoreCase(msgType, "2") && StringUtil.isNotEmpty(sendPath)) {
                FileUtil.copyFile(filePath, sendPath);
                data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, "Fileip OK");
            } else {
                LoggerUtil.warn(logger, "msgType的值错误，msgType=", msgType);
                throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
            }
        } catch (Exception e) {
            if (e instanceof AnymockException) {
                return;
            }
            ExceptionUtil.caught(e, "普通文件上传出现异常,filePath=", filePath, "msgType=", msgType);
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {

    }
}