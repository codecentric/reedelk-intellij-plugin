package com.reedelk.plugin.converter;

public interface Converter {

    <T> ValueConverter<T> forType(Class<T> typeClazz);
}
