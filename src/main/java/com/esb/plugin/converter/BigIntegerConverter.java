package com.esb.plugin.converter;

import org.json.JSONObject;

import java.math.BigInteger;

public class BigIntegerConverter implements ValueConverter<BigInteger> {

    @Override
    public String toString(Object value) {
        BigInteger realValue = (BigInteger) value;
        return realValue.toString();
    }

    @Override
    public BigInteger from(String value) {
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            return BigInteger.ZERO;
        }
    }

    @Override
    public BigInteger from(String key, JSONObject object) {
        return object.getBigInteger(key);
    }
}
