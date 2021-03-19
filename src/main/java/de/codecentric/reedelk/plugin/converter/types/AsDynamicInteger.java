package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

public class AsDynamicInteger extends AbstractDynamicValueConverter<Integer> {

    private final AsIntegerObject delegate = new AsIntegerObject();

    @Override
    protected ValueConverter<Integer> delegate() {
        return delegate;
    }
}