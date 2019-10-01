package com.reedelk.plugin.converter;

public class DynamicBooleanConverter extends AbstractDynamicConverter<Boolean> {

    private BooleanConverter delegate = new BooleanConverter();

    @Override
    protected ValueConverter<Boolean> delegate() {
        return delegate;
    }
}
