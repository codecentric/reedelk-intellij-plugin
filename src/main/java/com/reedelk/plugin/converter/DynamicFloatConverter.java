package com.reedelk.plugin.converter;

public class DynamicFloatConverter extends AbstractDynamicConverter<Float> {

    private FloatConverter delegate = new FloatConverter();

    @Override
    protected ValueConverter<Float> delegate() {
        return delegate;
    }
}
