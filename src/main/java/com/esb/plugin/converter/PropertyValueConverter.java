package com.esb.plugin.converter;

public interface PropertyValueConverter<ConvertedType> {

    String to(Object value);

    ConvertedType from(String value);

}
