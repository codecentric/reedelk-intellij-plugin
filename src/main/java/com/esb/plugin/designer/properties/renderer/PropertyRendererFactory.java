package com.esb.plugin.designer.properties.renderer;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.component.PropertyTypeDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PropertyRendererFactory {

    private static final Map<Class<? extends PropertyTypeDescriptor>, Class<? extends PropertyRenderer>> RENDERER;

    static {
        Map<Class<? extends PropertyTypeDescriptor>, Class<? extends PropertyRenderer>> tmp = new HashMap<>();
        tmp.put(PrimitiveTypeDescriptor.class, PrimitiveTypePropertyRenderer.class);
        tmp.put(EnumTypeDescriptor.class, EnumTypePropertyRenderer.class);
        RENDERER = tmp;
    }

    public static PropertyRendererFactory get() {
        return new PropertyRendererFactory();
    }

    public PropertyRenderer from(PropertyTypeDescriptor propertyType) {
        Class<? extends PropertyRenderer> rendererClazz = RENDERER.get(propertyType.getClass());
        return instantiateRenderer(rendererClazz);
    }

    private PropertyRenderer instantiateRenderer(Class<? extends PropertyRenderer> rendererClazz) {
        try {
            return rendererClazz.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
