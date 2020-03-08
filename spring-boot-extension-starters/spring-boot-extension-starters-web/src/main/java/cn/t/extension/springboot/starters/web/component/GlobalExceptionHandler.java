package cn.t.extension.springboot.starters.web.component;

import cn.t.base.common.service.ErrorInfo;
import cn.t.base.common.service.ServiceException;
import cn.t.extension.springboot.starters.web.vo.ResultVo;
import org.hibernate.validator.engine.HibernateConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResultVoWrapper resultVoWrapper;

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVo duplicateKeyException(DuplicateKeyException e) {
        logger.error("cat a DuplicateKeyException", e);
        return resultVoWrapper.buildFail();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVo noHandlerFound(ConstraintViolationException e) {
        logger.error("cat a ConstraintViolationException", e);
        Set<ConstraintViolation<?>> errors = e.getConstraintViolations();
        if(!CollectionUtils.isEmpty(errors)) {
            Object defaultError = errors.toArray()[0];
            if(defaultError instanceof HibernateConstraintViolation) {
                HibernateConstraintViolation<?> validation = (HibernateConstraintViolation<?>)defaultError;
                return resultVoWrapper.buildBadParam(validation.getMessage());
            }
        }
        return resultVoWrapper.buildFail();
    }


    /**
     * 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultVo noHandlerFound(NoHandlerFoundException e) {
        logger.error("cat a NoHandlerFoundException", e);
        return resultVoWrapper.buildSourceNotFound();
    }

    /**
     * 400
     * */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo noHandlerFound(HttpMessageNotReadableException e) {
        logger.error("cat a HttpMessageNotReadableException", e);
        return resultVoWrapper.buildBadParam();
    }

    /**
     * 405
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo methodNotSupport(HttpRequestMethodNotSupportedException e) {
        logger.error("cat a HttpRequestMethodNotSupportedException", e);
        return resultVoWrapper.buildMethodNotSupport();
    }

    /**
     * 500
     */
    @ExceptionHandler(Throwable.class)
    public ResultVo exception(Throwable t) {
        if(t instanceof ServiceException) {
            ServiceException serviceException = (ServiceException)t;
            ErrorInfo errorInfo = serviceException.getErrorInfo();
            logger.warn("业务异常, code: {}, msg: {}, data: {}", errorInfo.getCode(), errorInfo.getMsg(), serviceException.getData());
            return resultVoWrapper.buildFail(errorInfo, serviceException.getData());
        } else {
            logger.error("catch a exception", t);
            return resultVoWrapper.buildFail();
        }
    }

    public GlobalExceptionHandler(ResultVoWrapper resultVoWrapper) {
        this.resultVoWrapper = resultVoWrapper;
    }
}
