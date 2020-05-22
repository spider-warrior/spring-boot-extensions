package cn.t.extension.springboot.starters.trace;

import cn.t.common.trace.generic.TraceIdGenerator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.t.common.trace.generic.TraceConstants.TRACE_ID_HEADER_NAME;
import static cn.t.common.trace.generic.TraceConstants.TRACE_ID_LOG_NAME;


/**
 * 控制器日志拦截器(没想好如何使用)
 *
 * @author yj
 * @since 2020-04-13 11:52
 **/
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private String applicationName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId = request.getHeader(TRACE_ID_HEADER_NAME);
        if(StringUtils.isEmpty(traceId)) {
            traceId = TraceIdGenerator.generateTraceId(applicationName);
        }
        MDC.put(TRACE_ID_LOG_NAME, traceId);
        chain.doFilter(request, response);
    }

    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
