package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

public class AsDynamicDouble extends AbstractDynamicValueConverter<Double> {

    private final AsDoubleObject delegate = new AsDoubleObject();

    @Override
    protected ValueConverter<Double> delegate() {
        return delegate;
    }
}
