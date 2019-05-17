package com.esb.plugin.designer.properties.renderer.type;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.PropertyTypeDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class TypeRendererFactory {

    private static final Map<Class<?>, Class<? extends TypeRenderer>> RENDERER;

    static {
        Map<Class<?>, Class<? extends TypeRenderer>> tmp = new HashMap<>();
        tmp.put(String.class, StringRenderer.class);
        tmp.put(Integer.class, IntegerRenderer.class);
        tmp.put(int.class, IntegerRenderer.class);
        tmp.put(Enum.class, EnumRenderer.class);
        RENDERER = tmp;
    }

    public static TypeRendererFactory get() {
        return new TypeRendererFactory();
    }

    public TypeRenderer from(PropertyTypeDescriptor propertyType) {
        Class<? extends TypeRenderer> rendererClazz = RENDERER.get(propertyType.getClass());
        return instantiateRenderer(rendererClazz);
    }

    private TypeRenderer instantiateRenderer(Class<? extends TypeRenderer> rendererClazz) {
        try {
            return rendererClazz.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
