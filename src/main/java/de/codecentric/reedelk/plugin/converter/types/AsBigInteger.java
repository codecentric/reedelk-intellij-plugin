package de.codecentric.reedelk.plugin.converter.types;

import java.math.BigInteger;

public class AsBigInteger extends AbstractNumericValueConverter<BigInteger> {

    @Override
    Class<BigInteger> getClazz() {
        return BigInteger.class;
    }
}
