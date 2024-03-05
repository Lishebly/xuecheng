package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Lishebly
 * @Date: 2024/3/5/24/8:32 AM
 * @Version: 1.0
 */
@Slf4j
@ControllerAdvice
//@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常捕获
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e){
        //解析出错误信息
        String errMessage = e.getErrMessage();
        log.error("自定义异常捕获：" + errMessage);
        return new RestErrorResponse(errMessage);
    }


    /**
     * 全局异常捕获
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){
        //解析出错误信息
        String errMessage = e.getMessage();
        log.error("全局异常捕获：" + errMessage);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }


    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<String> errs = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item -> {
            log.error("全局异常捕获：" + item.getField() + "：" + item.getDefaultMessage());
            errs.add(item.getDefaultMessage());
        });
        //解析出错误信息
        String errMessage = StringUtils.join(errs, ",");
        log.error("全局异常捕获：" + errMessage);
        return new RestErrorResponse(errMessage);
    }

    @ResponseBody
    @ExceptionHandler(DeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exception(DeleteException e){
        //解析出错误信息
        String errMessage = e.getMessage();
        Long errorCode = e.getErrorCode();
        log.error("全局异常捕获：" + errMessage);
        return new ErrorResponse(errorCode,errMessage);
    }


}
