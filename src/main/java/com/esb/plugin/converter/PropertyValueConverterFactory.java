package com.esb.plugin.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class PropertyValueConverterFactory {

    private static final Map<Class<?>, PropertyValueConverter<?>> CONVERTER;

    static {
        Map<Class<?>, PropertyValueConverter<?>> tmp = new HashMap<>();

        tmp.put(int.class, new IntConverter());
        tmp.put(Integer.class, new IntConverter());
        tmp.put(long.class, new LongConverter());
        tmp.put(Long.class, new LongConverter());
        tmp.put(float.class, new FloatConverter());
        tmp.put(Float.class, new FloatConverter());
        tmp.put(double.class, new DoubleConverter());
        tmp.put(Double.class, new DoubleConverter());
        tmp.put(boolean.class, new BooleanConverter());
        tmp.put(Boolean.class, new BooleanConverter());

        tmp.put(String.class, new StringConverter());
        tmp.put(BigInteger.class, new BigIntegerConverter());
        tmp.put(BigDecimal.class, new BigDecimalConverter());

        // Enum

        CONVERTER = Collections.unmodifiableMap(tmp);
    }

    public static PropertyValueConverter<?> forType(Class<?> inputType) {
        if (CONVERTER.containsKey(inputType)) {
            return CONVERTER.get(inputType);
        }
        throw new IllegalStateException(
                format("Input Type '%s' does not have suitable converter",
                        inputType.getName()));
    }
}
