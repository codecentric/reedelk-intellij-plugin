package com.reedelk.plugin.converter;

import com.reedelk.runtime.api.commons.ConfigurationPropertyUtils;
import org.json.JSONObject;

/**
 * A decorator for non-string fields which does not convert the value if
 * it is a config property e.g ${config.property.key}. If it is not a config
 * property, then the original value converter is used to convert to the
 * correct target type.
 * The decorator likewise does not convert during deserialization a string value
 * to the target type if it is already a string.
 */
public class ConfigPropertyAwareConverter implements ValueConverter<Object> {

    private ValueConverter<?> delegate;

    ConfigPropertyAwareConverter(ValueConverter<?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String toText(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else {
            return delegate.toText(value);
        }
    }

    @Override
    public Object from(String value) {
        // If it is a config property e.g: '${property.key}'
        if (ConfigurationPropertyUtils.isConfigProperty(value)) {
            return value;
        } else {
            return delegate.from(value);
        }
    }

    @Override
    public Object from(String propertyName, JSONObject object) {
        if (object.isNull(propertyName)) {
            return null;
        }

        Object value = object.get(propertyName);
        if (ConfigurationPropertyUtils.isConfigProperty(value)) {
            return value;
        }

        return delegate.from(propertyName, object);
    }
}
