package com.reedelk.plugin.converter;

import java.math.BigDecimal;

public class DynamicBigDecimalConverter extends AbstractDynamicConverter<BigDecimal> {

    private BigDecimalConverter delegate = new BigDecimalConverter();

    @Override
    protected ValueConverter<BigDecimal> delegate() {
        return delegate;
    }
}
