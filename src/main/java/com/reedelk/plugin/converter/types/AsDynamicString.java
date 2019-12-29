package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

public class AsDynamicString extends AbstractDynamicValueConverter<String> {

    private final AsString delegate = new AsString();

    @Override
    protected ValueConverter<String> delegate() {
        return delegate;
    }
}
