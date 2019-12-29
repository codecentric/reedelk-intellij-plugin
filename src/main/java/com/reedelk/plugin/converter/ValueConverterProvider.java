package com.reedelk.plugin.converter;

import com.reedelk.plugin.component.domain.TypeDescriptor;

public class ValueConverterProvider {

    private ValueConverterProvider() {
    }

    public static ValueConverter<?> forType(TypeDescriptor typeDescriptor) {
        return forType(typeDescriptor.getType());
    }

    public static <T> ValueConverter<T> forType(Class<T> typeClazz) {
        return ConfigPropertyAwareConverters.getInstance().forType(typeClazz);
    }

    public static Converter forDefaults() {
        return DefaultConverters.getInstance();
    }
}
