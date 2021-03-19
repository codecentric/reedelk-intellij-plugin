package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.runtime.converter.RuntimeConverters;
import de.codecentric.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

public class AsBoolean implements ValueConverter<Boolean> {

    // boolean is never null in the Plugin
    @Override
    public String toText(Object value) {
        return value == null ? Boolean.FALSE.toString() :
                RuntimeConverters.getInstance().convert(value, String.class);
    }

    @Override
    public Boolean from(String value) {
        return RuntimeConverters.getInstance().convert(value, boolean.class);
    }

    @Override
    public Boolean from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(boolean.class, object, propertyName);
    }
}
