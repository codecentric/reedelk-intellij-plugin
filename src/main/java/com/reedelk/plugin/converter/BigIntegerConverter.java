package com.reedelk.plugin.converter;

import org.json.JSONObject;

import java.math.BigInteger;

public class BigIntegerConverter implements ValueConverter<BigInteger> {

    @Override
    public String toText(Object value) {
        BigInteger realValue = (BigInteger) value;
        return realValue == null ?
                null :
                realValue.toString();
    }

    @Override
    public BigInteger from(String value) {
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            // Default value for BigInteger is null
            return null;
        }
    }

    @Override
    public BigInteger from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getBigInteger(propertyName);
    }
}
