package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

public class AsDynamicDouble extends AbstractDynamicValueConverter<Double> {

    private final AsDoubleObject delegate = new AsDoubleObject();

    @Override
    protected ValueConverter<Double> delegate() {
        return delegate;
    }
}
