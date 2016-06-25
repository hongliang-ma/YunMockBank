
package com.mock.core.model.shared.exception;

import com.mock.core.model.shared.exception.code.ErrorCode;

/**
 * anymock整个系统的异常基类
 * 
 * @author hongliang.ma
 * @version $Id: AnymockException.java, v 0.1 2012-6-14 下午4:12:32 hongliang.ma Exp $
 */
public class AnymockException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -8615103443717498766L;

    /** 异常代码 */
    private final ErrorCode   resultCode;

    /**
     * 构造函数
     * @param resultCode
     */
    public AnymockException(ErrorCode resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     * @param resultCode
     * @param message
     */
    public AnymockException(ErrorCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     * @param message
     * @param e
     */
    public AnymockException(ErrorCode resultCode, Throwable e) {
        super(e);
        this.resultCode = resultCode;
    }

    /**
     * 构造函数
     * @param resultCode
     * @param message
     * @param e
     */
    public AnymockException(ErrorCode resultCode, String message, Throwable e) {
        super(message, e);
        this.resultCode = resultCode;
    }

    /**
     * 获取异常类型
     * @return
     */
    public ExceptionType getType() {
        return resultCode.getType();
    }

    /**
     * 获取异常代码
     * 
     * @return property value of resultCode
     */
    public ErrorCode getErrorCode() {
        return resultCode;
    }

    /** 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (null == super.getMessage()) {
            return this.resultCode.getDescription();
        } else {
            return super.getMessage();
        }
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder(" AnymockException[");
        if (resultCode != null) {
            retValue.append("type=").append(resultCode.getType()).append(',');
            retValue.append("code=").append(resultCode.getCode()).append(',');
            retValue.append("description=").append(resultCode.getDescription()).append(',');
        }
        retValue.append("extraMessage=").append(getMessage());
        retValue.append(']');
        return retValue.toString();
    }
}