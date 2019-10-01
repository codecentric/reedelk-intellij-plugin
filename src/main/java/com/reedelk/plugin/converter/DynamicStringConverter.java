package com.reedelk.plugin.converter;

public class DynamicStringConverter extends AbstractDynamicConverter<String> {

    private StringConverter delegate = new StringConverter();

    @Override
    protected ValueConverter<String> delegate() {
        return delegate;
    }
}
