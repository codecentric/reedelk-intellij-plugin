package com.reedelk.plugin.converter;

import com.reedelk.plugin.converter.types.*;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.Password;
import com.reedelk.runtime.api.commons.ConfigurationPropertyUtils;
import com.reedelk.runtime.api.resource.DynamicResource;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.resource.ResourceText;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicBooleanMap;
import com.reedelk.runtime.api.script.dynamicmap.DynamicObjectMap;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.*;
import com.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.lang.String.format;

public class ConfigPropertyAwareConverters implements Converter {

    private static final ConfigPropertyAwareConverters INSTANCE = new ConfigPropertyAwareConverters();

    private static final Map<Class<?>, ValueConverter<?>> CONFIG_PROPERTY_AWARE_CONVERTERS;
    static {
        Map<Class<?>, ValueConverter<?>> tmp = new HashMap<>();
        tmp.put(Boolean.class, new ConfigPropertyAwareConverter<>(new AsBooleanObject()));
        tmp.put(boolean.class, new ConfigPropertyAwareConverter<>(new AsBoolean()));
        tmp.put(Double.class, new ConfigPropertyAwareConverter<>(new AsDoubleObject()));
        tmp.put(double.class, new ConfigPropertyAwareConverter<>(new AsDouble()));
        tmp.put(Float.class, new ConfigPropertyAwareConverter<>(new AsFloatObject()));
        tmp.put(float.class, new ConfigPropertyAwareConverter<>(new AsFloat()));
        tmp.put(Integer.class, new ConfigPropertyAwareConverter<>(new AsIntegerObject()));
        tmp.put(int.class, new ConfigPropertyAwareConverter<>(new AsInteger()));
        tmp.put(Long.class, new ConfigPropertyAwareConverter<>(new AsLongObject()));
        tmp.put(long.class, new ConfigPropertyAwareConverter<>(new AsLong()));
        tmp.put(String.class, new ConfigPropertyAwareConverter<>(new AsString()));
        tmp.put(BigInteger.class, new ConfigPropertyAwareConverter<>(new AsBigInteger()));
        tmp.put(BigDecimal.class, new ConfigPropertyAwareConverter<>(new AsBigDecimal()));

        tmp.put(Script.class, new ConfigPropertyAwareConverter<>(new AsScript()));
        tmp.put(Password.class, new ConfigPropertyAwareConverter<>(new AsPassword()));

        // Dynamic types
        tmp.put(DynamicLong.class, new ConfigPropertyAwareConverter<>(new AsDynamicLong()));
        tmp.put(DynamicFloat.class, new ConfigPropertyAwareConverter<>(new AsDynamicFloat()));
        tmp.put(DynamicDouble.class, new ConfigPropertyAwareConverter<>(new AsDynamicDouble()));
        tmp.put(DynamicObject.class, new ConfigPropertyAwareConverter<>(new AsDynamicObject()));
        tmp.put(DynamicString.class, new ConfigPropertyAwareConverter<>(new AsDynamicString()));
        tmp.put(DynamicBoolean.class, new ConfigPropertyAwareConverter<>(new AsDynamicBoolean()));
        tmp.put(DynamicInteger.class, new ConfigPropertyAwareConverter<>(new AsDynamicInteger()));
        tmp.put(DynamicResource.class, new ConfigPropertyAwareConverter<>(new AsDynamicResource()));
        tmp.put(DynamicByteArray.class, new ConfigPropertyAwareConverter<>(new AsDynamicByteArray()));
        tmp.put(DynamicBigInteger.class, new ConfigPropertyAwareConverter<>(new AsDynamicBigInteger()));
        tmp.put(DynamicBigDecimal.class, new ConfigPropertyAwareConverter<>(new AsDynamicBigDecimal()));

        // The following types Cannot be specified with a config property.
        tmp.put(Map.class, new AsMap());
        tmp.put(List.class, new AsList());
        tmp.put(Enum.class, new AsEnum());
        tmp.put(Combo.class, new AsCombo());
        tmp.put(char.class, new AsChar());
        tmp.put(Character.class, new AsCharObject());
        tmp.put(ResourceText.class, new AsResourceText());
        tmp.put(ResourceBinary.class, new AsResourceBinary());

        tmp.put(DynamicBooleanMap.class, new AsDynamicBooleanMap());
        tmp.put(DynamicStringMap.class, new AsDynamicStringMap());
        tmp.put(DynamicObjectMap.class, new AsDynamicObjectMap());

        CONFIG_PROPERTY_AWARE_CONVERTERS = Collections.unmodifiableMap(tmp);
    }

    private ConfigPropertyAwareConverters() {
    }

    public static Converter getInstance() {
        return INSTANCE;
    }

    static int size() {
        return CONFIG_PROPERTY_AWARE_CONVERTERS.size();
    }

    static Set<Class<?>> supportedConverters() {
        return CONFIG_PROPERTY_AWARE_CONVERTERS.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ValueConverter<T> forType(Class<T> typeClazz) {
        if (CONFIG_PROPERTY_AWARE_CONVERTERS.containsKey(typeClazz)) {
            return (ValueConverter<T>) CONFIG_PROPERTY_AWARE_CONVERTERS.get(typeClazz);
        }
        throw new IllegalStateException(format("Input Type '%s' does not have suitable converter", typeClazz.getName()));
    }

    /**
     * A decorator for which does not convert the property value if
     * it is a config property e.g ${config.property.key}. If it is not a config
     * property, then the original value converter is used to convert to the
     * correct target type.
     * The decorator likewise does not convert during deserialization a string value
     * to the target type if it is already a string.
     */
    private static class ConfigPropertyAwareConverter<T> implements ValueConverter<Object> {

        private ValueConverter<T> delegate;

        ConfigPropertyAwareConverter(ValueConverter<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public String toText(Object value) {
            return delegate.toText(value);
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
            Object value = JsonObjectConverter.getInstance().convert(Object.class, object, propertyName);
            if (ConfigurationPropertyUtils.isConfigProperty(value)) {
                return value;
            }
            return delegate.from(propertyName, object);
        }
    }
}
