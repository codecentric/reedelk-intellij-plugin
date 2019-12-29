package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

public class AsDynamicFloat extends AbstractDynamicValueConverter<Float> {

    private final AsFloatObject delegate = new AsFloatObject();

    @Override
    protected ValueConverter<Float> delegate() {
        return delegate;
    }
}
