/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2010 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.common.util.DateUtil;
import com.mock.common.util.LoggerUtil;

/**
 * 文件处理的工具类
 * 
 * @author 马洪良
 * @version $Id: FileUtil.java, v 0.1 2012-12-13 下午4:52:02 马洪良 Exp $
 */
public class FileUtil {

    private static final Logger log      = LoggerFactory.getLogger(FileUtil.class);

    /** 共享文件保存路径 */
    private String              fileDir;

    /** 文件共享根路径 */
    private String              rootDir;

    /** 目录分隔符 */
    public static final String  SEP      = "/";

    /** 模板文件生成目录 */
    public static final String  GENERATE = "generate";

    /** 配置变更目录 */
    public static final String  ALTER    = "alter";

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @param isRelative 是否为相对路径
     * @return
     */
    public static String readFile(String filePath, boolean isRelative) {
        try {
            InputStream in = null;
            if (isRelative) {
                in = FileUtil.class.getResourceAsStream(filePath);
            } else {
                in = getInputStream(filePath);
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            log.error("文件读取异常", e);
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
    }

    /**
     * 复制单个文件
     * 
     * @param oldPathFile
     *            准备复制的文件源
     * @param newPathFile
     *            拷贝到新绝对路径带文件名
     * @return
     */
    public static void copyFile(String oldPathFile, String newPathFile) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            log.error("文件读取异常", e);
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
    }

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @param isRelative 是否为相对路径
     * @return
     */
    public String readSharedateFile(String filePath, boolean isRelative) {
        try {
            String absolutePath = filePath;
            if (isRelative) {
                absolutePath = buildAbsolutePath(filePath);
            }

            InputStream in = getInputStream(absolutePath);
            return IOUtils.toString(in);
        } catch (IOException e) {
            log.error("文件读取异常", e);
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
    }

    /**
     * 获取文件流
     * 
     * @param fileAbsolutePath
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStream(String fileAbsolutePath) {
        return getInputStream(new File(fileAbsolutePath));
    }

    /**
     * 获取文件流
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ff) {
            LoggerUtil.warn(log, "文件未找到:", file.getAbsoluteFile());
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, ff);
        }
    }

    /**
     * 读取文本文件内容
     * 
     * @param filePathAndName
     *            带有完整绝对路径的文件名
     * @param encoding
     *            文本文件打开的编码方式
     * @return 返回文本文件的内容
     */
    public static String readTxt(String filePathAndName, String encoding) throws IOException {
        encoding = encoding.trim();
        StringBuffer str = new StringBuffer("");
        try {
            FileInputStream fs = new FileInputStream(filePathAndName);
            InputStreamReader isr;
            if (encoding.equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding);
            }
            BufferedReader br = new BufferedReader(isr);
            try {
                String data = "";
                while ((data = br.readLine()) != null) {
                    str.append(data + " ");
                }
            } catch (Exception e) {
                str.append(e.toString());
            }
        } catch (IOException es) {
            LoggerUtil.warn(log, " 读取文本文件内容异常:");
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, es);
        }
        return str.toString();
    }

    /**
     * 有编码方式的文件创建
     * 
     * @param filePathAndName
     *            文本文件完整绝对路径及文件名
     * @param fileContent
     *            文本文件内容
     * @param encoding
     *            编码方式 例如 GBK 或者 UTF-8
     * @return
     */
    public static void createFile(String filePathAndName, String fileContent, String encoding) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            PrintWriter myFile = new PrintWriter(myFilePath, encoding);
            String strContent = fileContent;
            myFile.println(strContent);
            myFile.close();
        } catch (Exception e) {
            LoggerUtil.warn(log, "创建文件操作出错");
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
    }

    /**
     * 文件拷贝  
     * @param context 文件内容 
     * @param targetFilePath
     * @return
     */
    public boolean genFile(String context, String targetFilePath) {
        ByteArrayInputStream bis = new ByteArrayInputStream(context.getBytes());
        return genFile(bis, targetFilePath);
    }

    /**
     * 文件拷贝  
     * @param srcFilePath 
     * @param targetFilePath
     * @return
     */
    public boolean genFile(InputStream inputStream, String targetFilePath) {

        OutputStream out = null;
        try {
            File f = new File(targetFilePath);
            if (!f.exists()) {
                f.createNewFile();
            }
            out = new FileOutputStream(f);
            IOUtils.copy(inputStream, out);
        } catch (FileNotFoundException e) {
            log.error("[FileOperator]-copyFile-FileNotFoundException:", e);
            return false;
        } catch (IOException e) {
            log.error("[FileOperator]-copyFile-IOException:" + targetFilePath, e);
            return false;
        } catch (Exception e) {
            log.error("[FileOperator]-copyFile-Exception::", e);
            return false;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(out);
        }
        return true;
    }

    /**
     * 新建目录
     * 
     * @param folderPath
     *            目录
     * @return 返回创建结果
     */
    public static Boolean createFolder(String folderPath) {
        Boolean bCreate = false;
        try {
            java.io.File myFilePath = new java.io.File(folderPath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
            bCreate = true;
            LoggerUtil.info(log, "创建目录操作OK");
        } catch (Exception e) {
            LoggerUtil.warn(log, "创建目录操作出错");
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
        return bCreate;
    }

    /**
     * 删除文件夹
     * 
     * @param folderPath
     *            文件夹完整绝对路径
     * @return
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            LoggerUtil.warn(log, "删除文件夹操作出错");
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }
    }

    /**
     * 删除指定文件夹下所有文件
     * 
     * @param path
     *            文件夹完整绝对路径
     * @return
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean bDelResult = false;
        File file = new File(path);
        if (!file.exists()) {
            LoggerUtil.warn(log, "文件不存在!");
            return true;
        }
        if (!file.isDirectory()) {
            LoggerUtil.warn(log, "不是文件夹目录!");
            return bDelResult;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                bDelResult = true;
                LoggerUtil.info(log, "删除成功!");
            }
        }
        return bDelResult;
    }

    /**
     * 获取当前时间毫秒数数创建文件名称
     * 
     * @return
     */
    public String getFilePath(String dir, boolean isTemplateFile) {
        StringBuilder sf = new StringBuilder();
        if (isTemplateFile) {
            sf.append(dir).append(SEP);
            sf.append("template").append(".xml");
        } else {
            sf.append(fileDir);
            sf.append("alter").append(SEP);
            sf.append("alter_confg_log_");
            sf.append(System.currentTimeMillis());
            sf.append(".log");
        }
        return sf.toString();
    }

    /**
     * 对于模板文件内容数据比较多
     * 把查询内容保存到文件中返回给prodmng系统
     * fileParam 文件名称字段 各个字段之间以-分隔
     *@param dir 上一级目录名称
     *@param isGenerateTemplate 是否为文件模板生成
     * @return
     */
    public String genFileDir(String dir, boolean isGenerateTemplate) {
        StringBuilder sf = new StringBuilder();
        sf.append(fileDir);

        //新增当前的年月文件夹来管理
        String yyyyMM = DateUtil.formatMonth(new Date());

        if (isGenerateTemplate) {
            sf.append(GENERATE).append(SEP).append(yyyyMM).append(SEP);
        } else {
            sf.append(ALTER).append(SEP).append(yyyyMM).append(SEP);
        }
        sf.append(dir).append(SEP);

        File dirPath = new File(sf.toString());
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }

        return dirPath.getAbsolutePath();
    }

    /**
     * 获取文件名称
     * 
     * @param fileNames
     * @return
     */
    public String getFileName(Object... fileNames) {
        StringBuilder sf = new StringBuilder();
        for (Object param : fileNames) {
            sf.append(param).append("-");
        }
        sf = sf.deleteCharAt(sf.length() - 1);
        sf.append(".txt");

        return sf.toString();
    }

    /**
     * 拼接文件路径
     * relativePath 相对路径值
     * @return
     */
    public String buildAbsolutePath(String relativePath) {
        StringBuilder sf = new StringBuilder();
        sf.append(rootDir);
        sf.append(relativePath);
        return sf.toString();
    }

    /**
     * 获取文件sharedata相对路径
     *  如果文件不在sharedata下返回原路径
     * @param absolutePath
     * @return
     */
    public String getRelativePath(String absolutePath) {
        if (!StringUtil.contains(absolutePath, getRootDir())) {
            return absolutePath;
        }
        return StringUtil.substringAfter(absolutePath, getRootDir());
    }

    /**
     * Getter method for property <tt>fileDir</tt>.
     * 
     * @return property value of fileDir
     */
    public String getFileDir() {
        return fileDir;
    }

    /**
     * Getter method for property <tt>rootDir</tt>.
     * 
     * @return property value of rootDir
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Setter method for property <tt>fileDir</tt>.
     * 
     * @param fileDir value to be assigned to property fileDir
     */
    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Setter method for property <tt>rootDir</tt>.
     * 
     * @param rootDir value to be assigned to property rootDir
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

}
