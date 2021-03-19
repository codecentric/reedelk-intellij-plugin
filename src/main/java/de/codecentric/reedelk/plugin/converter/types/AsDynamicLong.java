package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

public class AsDynamicLong extends AbstractDynamicValueConverter<Long> {

    private final AsLongObject delegate = new AsLongObject();

    @Override
    protected ValueConverter<Long> delegate() {
        return delegate;
    }
}
