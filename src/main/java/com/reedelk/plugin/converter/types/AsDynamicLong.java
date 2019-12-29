package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

public class AsDynamicLong extends AbstractDynamicValueConverter<Long> {

    private final AsLongObject delegate = new AsLongObject();

    @Override
    protected ValueConverter<Long> delegate() {
        return delegate;
    }
}
