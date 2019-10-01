package com.reedelk.plugin.converter;

public class DynamicObjectConverter extends AbstractDynamicConverter<String> {

    private StringConverter delegate = new StringConverter();

    @Override
    protected ValueConverter<String> delegate() {
        return delegate;
    }
}
