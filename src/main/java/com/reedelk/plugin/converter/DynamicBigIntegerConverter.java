package com.reedelk.plugin.converter;

import java.math.BigInteger;

public class DynamicBigIntegerConverter extends AbstractDynamicConverter<BigInteger> {

    private BigIntegerConverter delegate = new BigIntegerConverter();

    @Override
    protected ValueConverter<BigInteger> delegate() {
        return delegate;
    }
}
