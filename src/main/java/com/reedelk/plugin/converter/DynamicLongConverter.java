package com.reedelk.plugin.converter;

public class DynamicLongConverter extends AbstractDynamicConverter<Long> {

    private LongConverter delegate = new LongConverter();

    @Override
    protected ValueConverter<Long> delegate() {
        return delegate;
    }
}
