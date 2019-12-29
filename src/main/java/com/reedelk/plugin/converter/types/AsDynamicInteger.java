package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

public class AsDynamicInteger extends AbstractDynamicValueConverter<Integer> {

    private final AsIntegerObject delegate = new AsIntegerObject();

    @Override
    protected ValueConverter<Integer> delegate() {
        return delegate;
    }
}