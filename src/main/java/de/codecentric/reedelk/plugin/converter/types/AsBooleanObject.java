package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.runtime.converter.RuntimeConverters;
import de.codecentric.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

public class AsBooleanObject implements ValueConverter<Boolean> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }

    @Override
    public Boolean from(String value) {
        return RuntimeConverters.getInstance().convert(value, Boolean.class);
    }

    @Override
    public Boolean from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(Boolean.class, object, propertyName);
    }
}
