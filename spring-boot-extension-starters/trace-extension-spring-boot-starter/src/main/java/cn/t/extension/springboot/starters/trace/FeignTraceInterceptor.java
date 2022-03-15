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
        Map<String, Collection<String>> headers = template.headers();
        //traceId
        if(CollectionUtils.isEmpty(headers.get(TRACE_ID_HEADER_NAME))) {
            String traceId = MDC.get(TRACE_ID_NAME);
            if(!StringUtil.isEmpty(traceId)) {
                template.header(TRACE_ID_HEADER_NAME, traceId);
            }

        }
        //clientId
        if(CollectionUtils.isEmpty(headers.get(TRACE_CLIENT_ID_HEADER_NAME))) {
            String clientId = MDC.get(CLIENT_ID_NAME);
            if(!StringUtil.isEmpty(clientId)) {
                template.header(TRACE_CLIENT_ID_HEADER_NAME, clientId);
            }
        }
        //userId
        if(CollectionUtils.isEmpty(headers.get(TRACE_USER_ID_HEADER_NAME))) {
            String userId = MDC.get(USER_ID_NAME);
            if(!StringUtil.isEmpty(userId)) {
                template.header(TRACE_USER_ID_HEADER_NAME, userId);
            }
        }
        //spanId
        if(CollectionUtils.isEmpty(headers.get(TRACE_SPAN_ID_HEADER_NAME))) {
            String spanId = MDC.get(SPAN_ID_NAME);
            if(!StringUtil.isEmpty(spanId)) {
                template.header(TRACE_SPAN_ID_HEADER_NAME, spanId);
            }
        }
    }
}
