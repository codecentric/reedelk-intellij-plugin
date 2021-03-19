package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

public class AsDynamicBoolean extends AbstractDynamicValueConverter<Boolean> {

    private final AsBooleanObject delegate = new AsBooleanObject();

    @Override
    protected ValueConverter<Boolean> delegate() {
        return delegate;
    }
}

