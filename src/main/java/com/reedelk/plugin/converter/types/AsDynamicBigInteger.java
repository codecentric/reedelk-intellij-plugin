package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

import java.math.BigInteger;

public class AsDynamicBigInteger extends AbstractDynamicValueConverter<BigInteger> {

    private final AsBigInteger delegate = new AsBigInteger();

    @Override
    protected ValueConverter<BigInteger> delegate() {
        return delegate;
    }
}