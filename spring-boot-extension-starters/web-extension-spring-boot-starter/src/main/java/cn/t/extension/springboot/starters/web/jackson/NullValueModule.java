package cn.t.extension.springboot.starters.web.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * NullValueModule
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2022-01-05 14:26
 **/
public class NullValueModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new NullValueBeanSerializerModifier());
        super.setupModule(context);
    }
}
