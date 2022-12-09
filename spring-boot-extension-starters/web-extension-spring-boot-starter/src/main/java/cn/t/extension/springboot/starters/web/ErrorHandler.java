package cn.t.extension.springboot.starters.web;

import cn.t.common.response.ResultVo;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ErrorHandler extends Ordered {
    ResultVo handle(Throwable t, HttpServletRequest request, HttpServletResponse response);
    @Override
    default int getOrder() {
        return 0;
    }
}
