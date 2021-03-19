package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.runtime.api.commons.ScriptUtils;
import de.codecentric.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

abstract class AbstractDynamicValueConverter<T> implements ValueConverter<Object> {

    @Override
    public String toText(Object value) {
        // A dynamic script is already a string.
        // Otherwise we return the text value of the type.
        return value instanceof String ? (String) value : delegate().toText(value);
    }

    @Override
    public Object from(String value) {
        // If the value is a script, we return the script, otherwise its value with the correct type.
        return ScriptUtils.isScript(value) ? value : delegate().from(value);
    }

    @Override
    public Object from(String propertyName, JSONObject object) {
        Object value = JsonObjectConverter.getInstance().convert(Object.class, object, propertyName);
        // If the json value is a script, we return it, otherwise we convert it  to the correct type.
        return ScriptUtils.isScript(value) ? value : delegate().from(propertyName, object);
    }

    protected abstract ValueConverter<T> delegate();

}
