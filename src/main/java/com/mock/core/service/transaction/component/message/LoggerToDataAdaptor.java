
package com.mock.core.service.transaction.component.message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import com.alibaba.common.lang.StringUtil;
import com.mock.core.model.shared.enums.DataMapDict;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.TransactionErrorCode;
import com.mock.core.model.transaction.detail.TransferData;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.common.util.ExceptionUtil;

/**
 * 独立Logger写入 
 * @author jun.qi
 * @version $Id: LoggerToDataAdaptor.java, v 0.1 2012-7-3 下午12:48:00 jun.qi Exp $
 */

public final class LoggerToDataAdaptor extends ComponetHandler {

    /**
     * 写入文件
     * @param directory  文件目录
     * @param content    文件内容
     * @throws Exception
     */
    public void createFile(String directory, String content) throws Exception {
        String s = new String();
        StringBuilder s1 = new StringBuilder();
        File f = new File(directory);
        f.createNewFile();
        BufferedReader input = new BufferedReader(new FileReader(f));
        while ((s = input.readLine()) != null) {
            s1.append(s).append("\n");
        }
        input.close();
        s1.append(content);
        BufferedWriter output = new BufferedWriter(new FileWriter(f));
        output.write(s1.toString());
        output.close();
    }

    /**
     * 写入文件
     * @param directory   文件目录
     * @param content     文件内容
     * @throws IOException
     */
    public void creatFileFromByte(String directory, byte[] content) throws IOException {

        File f = new File(directory);
        if (!f.exists()) {
            f.createNewFile();
        }
        //文件流写入
        InputStream is = new FileInputStream(f);
        long length = f.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + f.getName());
        }
        is.close();

        byte[] rs = new byte[bytes.length + content.length];
        System.arraycopy(bytes, 0, rs, 0, bytes.length);
        System.arraycopy(content, 0, rs, bytes.length, content.length);

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(rs);
        fos.close();
    }

    @Override
    protected void process(TransferData data, TransferData localTransferData)
                                                                             throws AnymockException {
        String content = (String) data.getProperties().get(DataMapDict.SERVER_FORWARD_CONTENT);
        /**文件路径 */
        String directory = (String) localTransferData
            .getObject("LoggerToDataAdaptor" + "directory");
        /**消息传递类型 */
        String msgType = (String) localTransferData.getObject("LoggerToDataAdaptor" + "msgType");
        if (StringUtil.isBlank(msgType)) {
            msgType = "String";
        }
        try {
            if (msgType != null && msgType.equals("String")) {
                createFile(directory, content);
            } else {
                byte[] cont = (byte[]) data.getProperties().get(DataMapDict.MSGBODY);
                creatFileFromByte(directory, cont);
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "写入文件出错!");
            throw new AnymockException(TransactionErrorCode.COMPONENT_HANDLE_ERROR);
        }
    }

    @Override
    protected void processInner(TransferData data, TransferData localTransferData)
                                                                                  throws AnymockException {
    }
}