package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.runtime.converter.PluginConverters;
import com.reedelk.runtime.converter.PluginValueConverter;

public class PluginValueConverterProvider {

    private PluginValueConverterProvider() {
    }

    public static PluginValueConverter<?> forType(TypeDescriptor typeDescriptor) {
        return forType(typeDescriptor.type());
    }

    public static boolean isKnownType(String clazzFullyQualifiedName) {
        return PluginConverters.forConfigPropertyAware()
                .isKnownType(clazzFullyQualifiedName);
    }

    public static <T> PluginValueConverter<T> forType(Class<T> typeClazz) {
        return PluginConverters.forConfigPropertyAware()
                .forType(typeClazz);
    }
}
