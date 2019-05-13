package com.esb.plugin.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class PropertyValueConverterFactory {

    private static final Map<Class<?>, PropertyValueConverter<?>> CONVERTER;

    static {
        Map<Class<?>, PropertyValueConverter<?>> tmp = new HashMap<>();
        tmp.put(String.class, new StringConverter());
        tmp.put(Integer.class, new IntConverter());
        tmp.put(int.class, new LongConverter());
        tmp.put(Long.class, new LongConverter());
        tmp.put(long.class, new LongConverter());
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
