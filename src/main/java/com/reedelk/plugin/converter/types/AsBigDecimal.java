package com.reedelk.plugin.converter.types;

import java.math.BigDecimal;

public class AsBigDecimal extends AbstractNumericValueConverter<BigDecimal> {

    @Override
    Class<BigDecimal> getClazz() {
        return BigDecimal.class;
    }
}
