package com.esb.plugin.converter;

import org.json.JSONObject;

public interface ValueConverter<ConvertedType> {

    String asString(Object value);

    ConvertedType from(String value);

    ConvertedType from(String key, JSONObject object);

}
