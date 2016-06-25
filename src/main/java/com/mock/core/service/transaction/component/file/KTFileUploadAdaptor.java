
package com.mock.core.service.transaction.component.file;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetFactory;
import com.mock.core.service.transaction.component.ComponetHandler;

/**
 * 卡通文件上传
 * @author jun.qi
 * @version $Id: KTFileUploadAdaptor.java, v 0.1 2012-7-3 下午04:36:12 jun.qi Exp $
 */
public class KTFileUploadAdaptor extends ComponetHandler {

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {

        ComponetFactory.componetSubHandler("PostFileAdaptor", data, localTransferData);

    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }

}
