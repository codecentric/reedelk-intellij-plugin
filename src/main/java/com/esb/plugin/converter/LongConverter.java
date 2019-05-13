package com.esb.plugin.converter;

public class LongConverter implements PropertyValueConverter<Long> {

    @Override
    public String to(Object value) {
        Long realValue = (Long) value;
        return String.valueOf(realValue);
    }

    @Override
    public Long from(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
