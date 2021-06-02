package cn.t.extension.springboot.starters.web;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;

/**
 * ResourceHttpRequestHandler返回值:
 * 当status为400时  404.html
 * 当status为500时  500.html
 * 由org.apache.catalina.core.StandardHostValve#status方法找到errorPage渲染
 * 如果未配置则forward到/error
 */
public class AppErrorPageRegistrar implements ErrorPageRegistrar {

    private final ServerProperties properties;
    private final DispatcherServletPath dispatcherServletPath;

    @Override
    public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
        ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, this.dispatcherServletPath
                .getRelativePath("404.html"));
        ErrorPage page500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, this.dispatcherServletPath
                .getRelativePath("500.html"));
        errorPageRegistry.addErrorPages(page404, page500);
    }

    public AppErrorPageRegistrar(ServerProperties properties,
                                 DispatcherServletPath dispatcherServletPath) {
        this.properties = properties;
        this.dispatcherServletPath = dispatcherServletPath;
    }

}
