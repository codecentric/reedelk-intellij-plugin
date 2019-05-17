package com.esb.plugin.designer.properties.renderer.type;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.TypeDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TypeRendererFactory {

    private static final Map<Class<?>, Class<? extends TypeRenderer>> RENDERER;

    static {
        Map<Class<?>, Class<? extends TypeRenderer>> tmp = new HashMap<>();

        tmp.put(int.class, IntegerRenderer.class);
        tmp.put(Integer.class, IntegerRenderer.class);

        tmp.put(long.class, LongRenderer.class);
        tmp.put(Long.class, LongRenderer.class);

        tmp.put(float.class, FloatRenderer.class);
        tmp.put(Float.class, FloatRenderer.class);

        tmp.put(double.class, DoubleRenderer.class);
        tmp.put(Double.class, DoubleRenderer.class);

        tmp.put(boolean.class, BooleanRenderer.class);
        tmp.put(Boolean.class, BooleanRenderer.class);

        tmp.put(String.class, StringRenderer.class);
        tmp.put(BigInteger.class, BigIntegerRenderer.class);
        tmp.put(BigDecimal.class, BigDecimalRenderer.class);

        tmp.put(Enum.class, EnumRenderer.class);

        RENDERER = tmp;
    }

    public static TypeRendererFactory get() {
        return new TypeRendererFactory();
    }

    public TypeRenderer from(TypeDescriptor propertyType) {
        Class<? extends TypeRenderer> rendererClazz = RENDERER.get(propertyType.type());
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
