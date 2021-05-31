package cn.t.extension.springboot.starters.trace;

import cn.t.common.trace.generic.TraceIdGenerator;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.t.common.trace.generic.TraceConstants.*;
import static cn.t.common.trace.generic.TraceConstants.TRACE_ID_HEADER_NAME;


/**
 * 控制器日志拦截器(没想好如何使用)
 *
 * @author yj
 * @since 2020-04-13 11:52
 **/
public class WebTraceFilter extends OncePerRequestFilter {

    private final String applicationName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        String traceId = request.getHeader(TRACE_ID_HEADER_NAME);
        if(StringUtils.isEmpty(traceId)) {
            traceId = TraceIdGenerator.generateTraceId(applicationName, request.getRemoteAddr());
        }
        MDC.put(TRACE_ID_NAME, traceId);
        MDC.put(CLIENT_ID_NAME, request.getHeader(TRACE_CLIENT_ID_HEADER_NAME));
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_NAME);
            MDC.remove(CLIENT_ID_NAME);
        }
    }

    public WebTraceFilter(String applicationName) {
        this.applicationName = applicationName;
    }
}
