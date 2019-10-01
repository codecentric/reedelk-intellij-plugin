package com.reedelk.plugin.converter;

public class DynamicIntegerConverter extends AbstractDynamicConverter<Integer> {

    private IntegerConverter delegate = new IntegerConverter();

    @Override
    protected ValueConverter<Integer> delegate() {
        return delegate;
    }
}
