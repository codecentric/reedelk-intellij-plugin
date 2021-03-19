package de.codecentric.reedelk.plugin.converter;

import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;

public class ValueConverterProvider {

    private ValueConverterProvider() {
    }

    public static ValueConverter<?> forType(PropertyTypeDescriptor typeDescriptor) {
        return forType(typeDescriptor.getType());
    }

    public static <T> ValueConverter<T> forType(Class<T> typeClazz) {
        return ConfigPropertyAwareConverters.getInstance().forType(typeClazz);
    }

    public static Converter forDefaults() {
        return DefaultConverters.getInstance();
    }
}
