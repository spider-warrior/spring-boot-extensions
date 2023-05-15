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
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final List<ErrorHandler> errorHandlerList;

    public GlobalExceptionHandler(List<ErrorHandler> errorHandlerList) {
        this.errorHandlerList = errorHandlerList;
        if(errorHandlerList != null) {
            errorHandlerList.sort(Comparator.comparingInt(ErrorHandler::getOrder));
        }
    }

    /**
     * 400
     * */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo messageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        logger.error("cat a HttpMessageNotReadableException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo);
    }

    /**
     * 400
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
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

    /**
     * 400
     * */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVo constraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> constraintViolationSet = e.getConstraintViolations();
        String msg = null;
        for (ConstraintViolation<?> constraintViolation : constraintViolationSet) {
            msg = constraintViolation.getMessage();
            if(StringUtils.hasText(msg)) {
                break;
            }
        }
        logger.error("cat a ConstraintViolationException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo.getCode(), msg);
    }

    /**
     * 400
     * */
    @ExceptionHandler(BindException.class)
    public ResultVo bindException(BindException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        ResultVo vo = ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo, buildErrorField(bindingResult));
        logger.warn("cat a BindException, uri: "+ request.getRequestURI() +", {}", vo);
        return vo;
    }

    /**
     * 400
     * */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo missingServletRequestParameterException(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        return ResultVo.buildFail(ErrorInfoEnum.BAD_PARAM.errorInfo, paramName);
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

    /**
     * 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultVo noHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        logger.error("cat a NoHandlerFoundException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.SOURCE_NOT_FOUND.errorInfo);
    }

    /**
     * 405
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo methodNotSupport(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        logger.error("cat a HttpRequestMethodNotSupportedException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.METHOD_NOT_SUPPORT.errorInfo);
    }

    /**
     * 415
     * */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResultVo mediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e, HttpServletRequest request) {
        logger.error("cat a HttpMediaTypeNotAcceptableException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.MEDIA_TYPE_NOT_SUPPORT.errorInfo);
    }

    /**
     * 415
     * */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultVo mediaTypeNotSupport(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        logger.error("cat a HttpMediaTypeNotSupportedException: " + request.getRequestURI(), e);
        return ResultVo.buildFail(ErrorInfoEnum.MEDIA_TYPE_NOT_SUPPORT.errorInfo);
    }

    /**
     * service exception
     */
    @ExceptionHandler(ServiceException.class)
    public ResultVo serviceException(ServiceException e, HttpServletRequest request) {
        if(!StringUtils.hasText(e.getCode())) {
            return ResultVo.buildFail();
        } else {
            Throwable throwable = e.getCause();
            if(throwable == null) {
                logger.warn("业务异常, uri: {}, code: {}, msg: {}, data: {}", request.getRequestURI(), e.getCode(), e.getMessage(), e.getData());
            } else {
                logger.warn("业务异常, uri: {}, code: {}, msg: {}, data: {}, error: {}", request.getRequestURI(), e.getCode(), e.getMessage(), e.getData(), throwable.getMessage());
            }
            if("employee_no_authority".equals(e.getCode())) {
                if(e.getData() == null) {
                    if(e.getMessage() == null) {
                        return ResultVo.buildFail(e.getCode(), "无操作权限");
                    } else {
                        return ResultVo.buildFail(e.getCode(), e.getMessage());
                    }
                } else {
                    return ResultVo.buildFail(e.getCode(), "无操作权限: " + e.getData());
                }
            } else {
                return ResultVo.buildFail(e.getCode(), e.getMessage(), e.getData());
            }
        }
    }

    /**
     * ResponseStatus exception
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResultVo responseStatusException(ResponseStatusException e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("cat a ResponseStatusException: " + request.getRequestURI(), e);
        response.setStatus(e.getStatus().value());
        return ResultVo.buildFail(String.valueOf(e.getStatus().value()), e.getReason());
    }

    /**
     * 500
     */
    @ExceptionHandler(Throwable.class)
    public ResultVo exception(Throwable t, HttpServletRequest request, HttpServletResponse response) {
        logger.error("catch a exception: " + request.getRequestURI(), t);
        for (ErrorHandler errorHandler : errorHandlerList) {
            try {
                ResultVo resultVo = errorHandler.handle(t, request, response);
                if(resultVo != null) {
                    return resultVo;
                }
            } catch (Exception e) {
                logger.error("ErrorHandler异常", e);
            }
        }
        return ResultVo.buildFail();
    }

}
