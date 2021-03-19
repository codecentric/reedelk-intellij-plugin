package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;

import java.math.BigInteger;

public class AsDynamicBigInteger extends AbstractDynamicValueConverter<BigInteger> {

    private final AsBigInteger delegate = new AsBigInteger();

    @Override
    protected ValueConverter<BigInteger> delegate() {
        return delegate;
    }
}