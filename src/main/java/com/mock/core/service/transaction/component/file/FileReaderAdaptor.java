
package com.mock.core.service.transaction.component.file;

import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.filestory.util.FileUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 文件读取
 * @author jun.qi
 * @version $Id: FileReaderAdaptor.java, v 0.1 2012-7-3 下午02:53:50 jun.qi Exp $
 */
public class FileReaderAdaptor extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        //硬盘目录
        String path = (String) localTransferData.getObject("FileReaderAdaptor" + "path");
        try {
            String content = FileUtil.readFile(path, false);
            LoggerUtil.info(logger, "文件读取为[", content, "]");
            data.getProperties().put(DataMapDict.SERVER_FORWARD_CONTENT, content);
        } catch (Exception e) {
            LoggerUtil.info(logger, "文件读取异常");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}