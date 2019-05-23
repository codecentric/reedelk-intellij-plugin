package com.esb.plugin.editor.properties.renderer.type;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.domain.TypeDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TypeRendererFactory {

    private static final Map<Class<?>, Class<? extends TypePropertyRenderer>> RENDERER;

    static {
        Map<Class<?>, Class<? extends TypePropertyRenderer>> tmp = new HashMap<>();

        tmp.put(int.class, IntegerPropertyRenderer.class);
        tmp.put(Integer.class, IntegerPropertyRenderer.class);

        tmp.put(long.class, LongPropertyRenderer.class);
        tmp.put(Long.class, LongPropertyRenderer.class);

        tmp.put(float.class, FloatPropertyRenderer.class);
        tmp.put(Float.class, FloatPropertyRenderer.class);

        tmp.put(double.class, DoublePropertyRenderer.class);
        tmp.put(Double.class, DoublePropertyRenderer.class);

        tmp.put(boolean.class, BooleanPropertyRenderer.class);
        tmp.put(Boolean.class, BooleanPropertyRenderer.class);

        tmp.put(String.class, StringPropertyRenderer.class);
        tmp.put(BigInteger.class, BigIntegerPropertyRenderer.class);
        tmp.put(BigDecimal.class, BigDecimalPropertyRenderer.class);

        tmp.put(Enum.class, EnumPropertyRenderer.class);

        RENDERER = tmp;
    }

    public static TypeRendererFactory get() {
        return new TypeRendererFactory();
    }

    public TypePropertyRenderer from(TypeDescriptor propertyType) {
        Class<? extends TypePropertyRenderer> rendererClazz = RENDERER.get(propertyType.type());
        return instantiateRenderer(rendererClazz);
    }

    private TypePropertyRenderer instantiateRenderer(Class<? extends TypePropertyRenderer> rendererClazz) {
        try {
            return rendererClazz.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
