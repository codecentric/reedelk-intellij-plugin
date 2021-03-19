package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.runtime.converter.RuntimeConverters;
import de.codecentric.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

public class AsChar implements ValueConverter<String> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }

    @Override
    public String from(String value) {
        return value;
    }

    @Override
    public String from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(String.class, object, propertyName);
    }
}
