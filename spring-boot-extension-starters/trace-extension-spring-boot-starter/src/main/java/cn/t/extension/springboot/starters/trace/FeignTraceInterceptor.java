package cn.t.extension.springboot.starters.trace;

import cn.t.util.common.StringUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

import static cn.t.common.trace.generic.TraceConstants.*;


/**
 * 控制器日志拦截器(没想好如何使用)
 *
 * @author yj
 * @since 2020-04-13 11:52
 **/
public class FeignTraceInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //traceId
        configHeader(template, TRACE_ID_HEADER_NAME, TRACE_ID_NAME);
        //clientId
        configHeader(template, TRACE_CLIENT_ID_HEADER_NAME, CLIENT_ID_NAME);
        //userId
        configHeader(template, TRACE_USER_ID_HEADER_NAME, USER_ID_NAME);
        //pSpanId
        configHeader(template, TRACE_P_SPAN_ID_HEADER_NAME, P_SPAN_ID_NAME);
        //spanId
        configHeader(template, TRACE_SPAN_ID_HEADER_NAME, SPAN_ID_NAME);
    }
    private void configHeader(RequestTemplate template, String headerName, String logContextProperty) {
        if(CollectionUtils.isEmpty(template.headers().get(headerName))) {
            String value = MDC.get(logContextProperty);
            if(!StringUtil.isEmpty(value)) {
                template.header(headerName, value);
            }
        }
    }
}
