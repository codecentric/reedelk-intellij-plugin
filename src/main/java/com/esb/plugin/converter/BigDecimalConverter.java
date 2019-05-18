package com.esb.plugin.converter;

import org.json.JSONObject;

import java.math.BigDecimal;

public class BigDecimalConverter implements ValueConverter<BigDecimal> {

    @Override
    public String toText(Object value) {
        BigDecimal realValue = (BigDecimal) value;
        return realValue.toPlainString();
    }

    @Override
    public BigDecimal from(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal from(String key, JSONObject object) {
        return object.getBigDecimal(key);
    }
}
