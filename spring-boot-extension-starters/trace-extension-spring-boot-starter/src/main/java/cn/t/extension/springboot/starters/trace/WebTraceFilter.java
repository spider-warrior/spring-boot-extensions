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
        configMdcProperty(request, TRACE_ID_HEADER_NAME, TRACE_ID_NAME);
        configMdcProperty(request, TRACE_CLIENT_ID_HEADER_NAME, CLIENT_ID_NAME);
        configMdcProperty(request, TRACE_USER_ID_HEADER_NAME, USER_ID_NAME);
        configMdcProperty(request, TRACE_P_SPAN_ID_HEADER_NAME, P_SPAN_ID_NAME);
        configMdcProperty(request, TRACE_SPAN_ID_HEADER_NAME, SPAN_ID_NAME);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_NAME);
            MDC.remove(CLIENT_ID_NAME);
            MDC.remove(USER_ID_NAME);
            MDC.remove(P_SPAN_ID_NAME);
            MDC.remove(SPAN_ID_NAME);
        }
    }

    private void configMdcProperty(HttpServletRequest request, String header, String logContextProperty) {
        String value = request.getHeader(header);
        if(StringUtils.hasText(value)) {
            MDC.put(logContextProperty, value);
        }
    }

}
