package cn.t.extension.springboot.starters.web;

import cn.t.common.response.ResultVo;
import cn.t.common.service.ErrorInfoEnum;
import cn.t.common.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        TreeMap<String, String> fieldErrorMap = buildErrorField(bindingResult);
        ResultVo vo;
        if(fieldErrorMap.size() > 0 && fieldErrorMap.firstEntry() != null) {
            vo = ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo.getCode(), fieldErrorMap.firstEntry().getValue(), fieldErrorMap);
        } else {
            vo = ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo, buildErrorField(bindingResult));
        }
        logger.warn("cat a MethodArgumentNotValidException, uri: "+ request.getRequestURI() +", {}", vo);
        return vo;
    }

    @ExceptionHandler(BindException.class)
    public ResultVo methodArgumentNotValid(BindException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        ResultVo vo = ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo, buildErrorField(bindingResult));
        logger.warn("cat a BindException, uri: "+ request.getRequestURI() +", {}", vo);
        return vo;
    }

    private TreeMap<String, String> buildErrorField(BindingResult bindingResult) {
        List<ObjectError> objectErrorList =  bindingResult.getAllErrors();
        TreeMap<String, String> errorFieldMap = new TreeMap<>();
        objectErrorList.forEach(error -> {
            if(error instanceof FieldError) {
                errorFieldMap.put(((FieldError)error).getField(), error.getDefaultMessage());
            }
        });
        return errorFieldMap;
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultVo noHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        logger.error("cat a NoHandlerFoundException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.SOURCE_NOT_FOUND.errorInfo);
    }

    // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo messageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        logger.error("cat a HttpMessageNotReadableException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo);
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo methodNotSupport(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        logger.error("cat a HttpRequestMethodNotSupportedException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.METHOD_NOT_SUPPORT.errorInfo);
    }

    // 415
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResultVo mediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e, HttpServletRequest request) {
        logger.error("cat a HttpMediaTypeNotAcceptableException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.MEDIA_TYPE_NOT_SUPPORT.errorInfo);
    }

    @ExceptionHandler(ServiceException.class)
    public ResultVo exception(ServiceException e, HttpServletRequest request) {
        if(!StringUtils.hasText(e.getCode())) {
            return ResultVo.buildFail();
        } else {
            logger.warn("业务异常, uri: {}, code: {}, msg: {}, data: {}", request.getRequestURI(), e.getCode(), e.getMessage(), e.getData());
            if("employee_no_authority".equals(e.getCode())) {
                return ResultVo.buildFail(e.getCode(), "无操作权限: " + e.getData());
            } else {
                return ResultVo.buildFail(e.getCode(), e.getMessage(), e.getData());
            }
        }
    }

    // 500
    @ExceptionHandler(Throwable.class)
    public ResultVo exception(Throwable t, HttpServletRequest request) {
        logger.error("catch a exception: " + request.getRequestURI(), t);
        return ResultVo.buildFail();
    }

}
