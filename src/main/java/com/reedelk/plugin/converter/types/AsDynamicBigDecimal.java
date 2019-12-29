package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;

import java.math.BigDecimal;

public class AsDynamicBigDecimal extends AbstractDynamicValueConverter<BigDecimal> {

    private final AsBigDecimal delegate = new AsBigDecimal();

    @Override
    protected ValueConverter<BigDecimal> delegate() {
        return delegate;
    }
}