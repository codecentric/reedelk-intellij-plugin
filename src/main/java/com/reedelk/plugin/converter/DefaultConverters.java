package com.reedelk.plugin.converter;

import com.reedelk.plugin.converter.types.*;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.Password;
import com.reedelk.runtime.api.resource.DynamicResource;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.resource.ResourceText;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicBooleanMap;
import com.reedelk.runtime.api.script.dynamicmap.DynamicObjectMap;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.lang.String.format;

public class DefaultConverters implements Converter {

    private static final DefaultConverters INSTANCE = new DefaultConverters();

    private static final Map<Class<?>, ValueConverter<?>> DEFAULTS_CONVERTERS;
    static {
        Map<Class<?>, ValueConverter<?>> tmp = new HashMap<>();
        tmp.put(Boolean.class, new AsBooleanObject());
        tmp.put(boolean.class, new AsBoolean());
        tmp.put(Double.class, new AsDoubleObject());
        tmp.put(double.class, new AsDouble());
        tmp.put(Float.class, new AsFloatObject());
        tmp.put(float.class, new AsFloat());
        tmp.put(Integer.class, new AsIntegerObject());
        tmp.put(int.class, new AsInteger());
        tmp.put(Long.class, new AsLongObject());
        tmp.put(long.class, new AsLong());
        tmp.put(Character.class, new AsCharObject());
        tmp.put(char.class, new AsChar());
        tmp.put(String.class, new AsString());
        tmp.put(BigInteger.class, new AsBigInteger());
        tmp.put(BigDecimal.class, new AsBigDecimal());

        tmp.put(Map.class, new AsMap());
        tmp.put(List.class, new AsList());
        tmp.put(Enum.class, new AsEnum());
        tmp.put(Script.class, new AsScript());
        tmp.put(Combo.class, new AsCombo());
        tmp.put(Password.class, new AsPassword());
        tmp.put(ResourceText.class, new AsResourceText());
        tmp.put(ResourceBinary.class, new AsResourceBinary());

        // Dynamic types
        tmp.put(DynamicLong.class, new AsDynamicLong());
        tmp.put(DynamicFloat.class, new AsDynamicFloat());
        tmp.put(DynamicDouble.class, new AsDynamicDouble());
        tmp.put(DynamicObject.class, new AsDynamicObject());
        tmp.put(DynamicString.class, new AsDynamicString());
        tmp.put(DynamicBoolean.class, new AsDynamicBoolean());
        tmp.put(DynamicInteger.class, new AsDynamicInteger());
        tmp.put(DynamicResource.class, new AsDynamicResource());
        tmp.put(DynamicByteArray.class, new AsDynamicByteArray());
        tmp.put(DynamicBigInteger.class, new AsDynamicBigInteger());
        tmp.put(DynamicBigDecimal.class, new AsDynamicBigDecimal());

        // Dynamic map types
        tmp.put(DynamicBooleanMap.class, new AsDynamicBooleanMap());
        tmp.put(DynamicStringMap.class, new AsDynamicStringMap());
        tmp.put(DynamicObjectMap.class, new AsDynamicObjectMap());

        DEFAULTS_CONVERTERS = Collections.unmodifiableMap(tmp);
    }

    private DefaultConverters() {
    }

    public static Converter getInstance() {
        return INSTANCE;
    }

    static int size() {
        return DEFAULTS_CONVERTERS.size();
    }

    static Set<Class<?>> supportedConverters() {
        return DEFAULTS_CONVERTERS.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ValueConverter<T> forType(Class<T> typeClazz) {
        if (DEFAULTS_CONVERTERS.containsKey(typeClazz)) {
            return (ValueConverter<T>) DEFAULTS_CONVERTERS.get(typeClazz);
        }
        throw new IllegalStateException(format("Input Type '%s' does not have suitable converter", typeClazz.getName()));
    }
}
