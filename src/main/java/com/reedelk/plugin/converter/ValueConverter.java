package com.reedelk.plugin.converter;

import org.json.JSONObject;

public interface ValueConverter<ConvertedType> {

    String toText(Object value);

    ConvertedType from(String value);

    ConvertedType from(String propertyName, JSONObject object);

}
