package de.codecentric.reedelk.plugin.converter;

import org.json.JSONObject;

public interface ValueConverter<T> {

    String toText(Object value);

    T from(String value);

    T from(String propertyName, JSONObject object);

}
