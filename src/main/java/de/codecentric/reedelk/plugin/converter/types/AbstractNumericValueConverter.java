package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.runtime.api.commons.DefaultValues;
import de.codecentric.reedelk.runtime.converter.RuntimeConverters;
import de.codecentric.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

abstract class AbstractNumericValueConverter<T> extends AbstractValueConverter<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T from(String value) {
        if (value == null) {
            return (T) DefaultValues.defaultValue(getClazz());
        } else {
            try {
                return RuntimeConverters.getInstance().convert(value, getClazz());
            } catch (NumberFormatException e) {
                return (T) DefaultValues.defaultValue(getClazz());
            }
        }
    }

    @Override
    public T from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(getClazz(), object, propertyName);
    }

    abstract Class<T> getClazz();
}
