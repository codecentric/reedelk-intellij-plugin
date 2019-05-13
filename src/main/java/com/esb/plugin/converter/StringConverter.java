package com.esb.plugin.converter;

public class StringConverter implements PropertyValueConverter<String> {

    @Override
    public String to(Object value) {
        String realValue = (String) value;
        return realValue;
    }

    @Override
    public String from(String value) {
        return value;
    }
}