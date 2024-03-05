package com.xuecheng.base.exception;

public class ErrorResponse {
    private Long errCode;
    private String errMessage;

    // Constructors
    public ErrorResponse() {
    }

    public ErrorResponse(Long errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    // Getters and setters
    public Long getErrCode() {
        return errCode;
    }

    public void setErrCode(Long errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    // toString method
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errCode='" + errCode + '\'' +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }
}
