package com.mock.core.service.transaction.filestory.util;

/**
 * 文件上传通知基类。
 * 
 */
public abstract class FileUploadNotify extends Notify {

    /**  */
    private static final long serialVersionUID = 1L;

    /** 文件名称*/
    protected String          fileName;

    /** 文件日期*/
    protected String          fileDate;

    /** 文件摘要*/
    protected String          digest;

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Returns the fileDate.
     */
    public String getFileDate() {
        return this.fileDate;
    }

    /**
     * @param fileDate The fileDate to set.
     */
    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    /**
     * @return Returns the digest.
     */
    public String getDigest() {
        return digest;
    }

    /**
     * @param digest The digest to set.
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }

}