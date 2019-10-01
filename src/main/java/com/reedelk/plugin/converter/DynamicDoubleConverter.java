package com.reedelk.plugin.converter;

public class DynamicDoubleConverter extends AbstractDynamicConverter<Double> {

    private DoubleConverter delegate = new DoubleConverter();

    @Override
    protected ValueConverter<Double> delegate() {
        return delegate;
    }
}
