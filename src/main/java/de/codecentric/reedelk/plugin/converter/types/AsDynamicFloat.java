package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

public class AsDynamicFloat extends AbstractDynamicValueConverter<Float> {

    private final AsFloatObject delegate = new AsFloatObject();

    @Override
    protected ValueConverter<Float> delegate() {
        return delegate;
    }
}
