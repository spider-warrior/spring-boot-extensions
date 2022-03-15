package cn.t.extension.springboot.starters.trace;

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


/**
 * 控制器日志拦截器(没想好如何使用)
 *
 * @author yj
 * @since 2020-04-13 11:52
 **/
public class WebTraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        String traceId = request.getHeader(TRACE_ID_HEADER_NAME);
        if(StringUtils.hasText(traceId)) {
            MDC.put(TRACE_ID_NAME, traceId);
        }
        String clientId = request.getHeader(TRACE_CLIENT_ID_HEADER_NAME);
        if(StringUtils.hasText(clientId)) {
            MDC.put(CLIENT_ID_NAME, clientId);
        }
        String userId = request.getHeader(TRACE_USER_ID_HEADER_NAME);
        if(StringUtils.hasText(clientId)) {
            MDC.put(USER_ID_NAME, userId);
        }
        String spanId = request.getHeader(TRACE_SPAN_ID_HEADER_NAME);
        if(StringUtils.hasText(spanId)) {
            MDC.put(SPAN_ID_NAME, spanId);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_NAME);
            MDC.remove(CLIENT_ID_NAME);
            MDC.remove(USER_ID_NAME);
            MDC.remove(SPAN_ID_NAME);
        }
    }

}
