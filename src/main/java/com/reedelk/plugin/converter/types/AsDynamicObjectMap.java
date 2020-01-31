package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

import java.util.Map;

public class AsDynamicObjectMap extends AbstractDynamicValueConverter<Map<String, Object>> {

    private final AsMap delegate = new AsMap();

    @Override
    protected ValueConverter<Map<String, Object>> delegate() {
        return delegate;
    }
}