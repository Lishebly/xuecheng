package com.xuecheng.base.exception;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/3:54 PM
 * @Version: 1.0
 */
public class DeleteException extends RuntimeException{
    private String errMessage;
    private Long errorCode;

    public DeleteException(String errMessage, Long errorCode)
    {
        super(errMessage);
        this.errMessage = errMessage;
        this.errorCode = errorCode;
    }

    public DeleteException()
    {
        super();
    }

    public String getErrMessage()
    {
        return errMessage;
    }

    public Long getErrorCode()
    {
        return errorCode;
    }

    public static void cast(String errMessage,Long errorCode) {
        throw new DeleteException(errMessage,errorCode);
    }

}
