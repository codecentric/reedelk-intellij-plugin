package com.esb.plugin.converter;

import org.json.JSONObject;

public interface PropertyValueConverter<ConvertedType> {

    String to(Object value);

    ConvertedType from(String value);

    ConvertedType from(String key, JSONObject object);

}
