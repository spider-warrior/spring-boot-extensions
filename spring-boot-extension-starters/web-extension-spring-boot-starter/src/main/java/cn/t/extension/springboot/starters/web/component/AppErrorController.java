package cn.t.extension.springboot.starters.web.component;

import cn.t.base.common.response.ResultVoWrapper;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class AppErrorController implements ErrorController {

    private final ServerProperties serverProperties;
    private final ResultVoWrapper resultVoWrapper;

    @RequestMapping
    public Object error() {
        return resultVoWrapper.buildSourceNotFound();
    }

    @Override
    public String getErrorPath() {
        return serverProperties.getError().getPath();
    }

    public AppErrorController(ServerProperties serverProperties, ResultVoWrapper resultVoWrapper) {
        this.serverProperties = serverProperties;
        this.resultVoWrapper = resultVoWrapper;
    }
}
