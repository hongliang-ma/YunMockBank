package com.mock.core.service.transaction.filestory.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * 这个类用来测试文件的加解密和压缩。
 * 标准卡通文档规定文件必须先压缩，再加密。
 * 加密算法是3DES
 */
public class FileEncrypt {
    private static final String TEXT_TYPE   = ".txt";

    private static String       threeDesKey = "123456788765432112345678";

    /**
     * 该方法接受一个文件名（一般是txt的文件）。
     * 然后生成一个同名的加过密的zip文件。
     * 
     * 例如该方法接受scf20070203.txt
     * 那么将在c盘下生成scf20070203.zip文件。
     * @param filename
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static byte[] makeZipfile(InputStream fileStream) throws FileNotFoundException,
                                                            IOException {

        FileEncrypt fileEncryptDemo = new FileEncrypt();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        //生成的文件在C盘下filename.zip,zip包里面的文件是hello.txt,内容是filename.txt的内容。
        OutputStream out = fileEncryptDemo
            .alipayUploadOuputStream(threeDesKey, byteStream, "hello");
        IOUtils.copy(fileStream, out);
        out.close();

        return byteStream.toByteArray();
    }

    private OutputStream alipayUploadOuputStream(String threeDesKey, OutputStream fileOuputStream,
                                                 String zipEntryName) throws IOException {
        // 设置流以加密模式输出
        OutputStream encryptOutPutStream = Des3Tool.encryptMode(threeDesKey, fileOuputStream);
        // 设置流以压缩模式输出
        ZipOutputStream zipOutPutStream = new ZipOutputStream(encryptOutPutStream);
        zipOutPutStream.putNextEntry(new ZipEntry(zipEntryName + TEXT_TYPE));
        return zipOutPutStream;
    }

}
