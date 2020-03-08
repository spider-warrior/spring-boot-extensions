package cn.t.extension.springboot.starters.web.component;


import cn.t.base.common.service.ErrorInfo;
import cn.t.extension.springboot.starters.web.vo.ResultVo;
import org.springframework.stereotype.Component;

@Component
public class ResultVoWrapper {

    private static final String SUCCESS = "200";
    private static final String BAD_PARAM = "400";
    private static final String SOURCE_NOT_FOUND = "404";
    private static final String METHOD_NOT_SUPPORT = "405";
    private static final String INTERNAL_ERROR = "500";


    /**
     * 成功
     */
    public ResultVo buildSuccess() {
        return buildSuccess(null, null);
    }

    public ResultVo buildSuccess(Object data) {
        return buildSuccess(null, data);
    }

    public ResultVo buildSuccess(String message) {
        return buildSuccess(message, null);
    }

    public ResultVo buildSuccess(String message, Object data) {
        return doBuild(SUCCESS, message, data);
    }

    /**
     * 400
     */

    public ResultVo buildBadParam() {
        return buildBadParam(null);
    }

    public ResultVo buildBadParam(String msg) {
        return buildBadParam(msg, null);
    }

    public ResultVo buildBadParam(Object data) {
        return buildBadParam(null, data);
    }

    public ResultVo buildBadParam(String msg, Object data) {
        return buildFail(BAD_PARAM, (msg == null ? "参数异常" : msg), data);
    }

    /**
     * 404
     */
    public ResultVo buildSourceNotFound() {
        return buildFail(SOURCE_NOT_FOUND, "资源未找到");
    }

    /**
     * 405
     */
    public ResultVo buildMethodNotSupport() {
        return buildFail(METHOD_NOT_SUPPORT, "不支持的请求方法");
    }

    /**
     * 失败
     */
    public ResultVo buildFail() {
        return buildFail(INTERNAL_ERROR, "服务器内部异常");
    }

    public ResultVo buildFail(ErrorInfo errorInfo) {
        return buildFail(errorInfo.getCode(), errorInfo.getMsg());
    }

    public ResultVo buildFail(ErrorInfo errorInfo, Object data) {
        return buildFail(errorInfo.getCode(), errorInfo.getMsg(), data);
    }

    public ResultVo buildFail(String errorCode, String msg) {
        return buildFail(errorCode, msg, null);
    }

    public ResultVo buildFail(String errorCode, Object data) {
        return buildFail(errorCode, null, data);
    }

    public ResultVo buildFail(String errorCode, String message, Object data) {
        return doBuild(errorCode, message, data);
    }

    private ResultVo doBuild(String code, String message, Object data) {
        ResultVo vo = new ResultVo();
        vo.setCode(code);
        vo.setMessage(message);
        vo.setData(data);
        return vo;
    }

}
