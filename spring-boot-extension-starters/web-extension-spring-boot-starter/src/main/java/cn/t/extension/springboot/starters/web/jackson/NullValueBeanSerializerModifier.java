package cn.t.extension.springboot.starters.web.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.Collection;
import java.util.List;

/**
 * NullValueBeanSerializerModifier
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2022-01-05 14:30
 **/
public class NullValueBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        beanProperties = super.changeProperties(config, beanDesc, beanProperties);
        for (BeanPropertyWriter beanProperty : beanProperties) {
            if(isArrayType(beanProperty)) {
                beanProperty.assignNullSerializer(new NullArraySerializer());
            }/* else if(isStringType(beanProperty)) {
                beanProperty.assignNullSerializer(new NullStringSerializer());
            }*/
        }
        return beanProperties;
    }

    private boolean isArrayType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    private boolean isStringType(BeanPropertyWriter writer) {
        Class<?> clazz = writer.getType().getRawClass();
        return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
    }
}
