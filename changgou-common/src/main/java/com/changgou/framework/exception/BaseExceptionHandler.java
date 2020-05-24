package com.changgou.framework.exception;

import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/21 18:21
 * @Description:公共异常处理
 */
@ControllerAdvice
public class BaseExceptionHandler {
    /**
     * 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }
}
