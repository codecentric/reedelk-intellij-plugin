package com.reedelk.plugin.converter;

import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.type.unknown.UnknownPropertyType;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.component.domain.TypeComboDescriptor.TypeCombo;
import static com.reedelk.plugin.component.domain.TypeFileDescriptor.TypeFile;
import static java.lang.String.format;

public class ValueConverterFactory {

    private ValueConverterFactory() {
    }

    private static final ValueConverter<Object> UNKNOWN_TYPE_CONVERTER = new UnknownTypeConverter();

    private static final Map<Class<?>, ValueConverter<?>> CONVERTER;

    static {
        Map<Class<?>, ValueConverter<?>> tmp = new HashMap<>();

        tmp.put(int.class, new ConfigPropertyAwareConverter(new IntegerConverter()));
        tmp.put(Integer.class, new ConfigPropertyAwareConverter(new IntegerConverter()));
        tmp.put(long.class, new ConfigPropertyAwareConverter(new LongConverter()));
        tmp.put(Long.class, new ConfigPropertyAwareConverter(new LongConverter()));
        tmp.put(float.class, new ConfigPropertyAwareConverter(new FloatConverter()));
        tmp.put(Float.class, new ConfigPropertyAwareConverter(new FloatConverter()));
        tmp.put(double.class, new ConfigPropertyAwareConverter(new DoubleConverter()));
        tmp.put(Double.class, new ConfigPropertyAwareConverter(new DoubleConverter()));
        tmp.put(BigInteger.class, new ConfigPropertyAwareConverter(new BigIntegerConverter()));
        tmp.put(BigDecimal.class, new ConfigPropertyAwareConverter(new BigDecimalConverter()));

        tmp.put(boolean.class, new BooleanConverter());
        tmp.put(Boolean.class, new BooleanConverter());
        tmp.put(Enum.class, new EnumConverter());
        tmp.put(String.class, new StringConverter());
        tmp.put(TypeFile.class, new FileConverter());
        tmp.put(TypeCombo.class, new ComboConverter());
        tmp.put(Map.class, new MapConverter());
        tmp.put(Script.class, new ScriptConverter());

        // Dynamic value types
        tmp.put(DynamicBigDecimal.class, new DynamicBigDecimalConverter());
        tmp.put(DynamicBigInteger.class, new DynamicBigIntegerConverter());
        tmp.put(DynamicBoolean.class, new DynamicBooleanConverter());
        tmp.put(DynamicByteArray.class, new DynamicByteArrayConverter());
        tmp.put(DynamicDouble.class, new DynamicDoubleConverter());
        tmp.put(DynamicFloat.class, new DynamicFloatConverter());
        tmp.put(DynamicInteger.class, new DynamicIntegerConverter());
        tmp.put(DynamicLong.class, new DynamicLongConverter());
        tmp.put(DynamicObject.class, new DynamicObjectConverter());
        tmp.put(DynamicString.class, new DynamicStringConverter());

        // Dynamic map types
        tmp.put(DynamicStringMap.class, new DynamicStringMapConverter());

        CONVERTER = Collections.unmodifiableMap(tmp);
    }

    public static ValueConverter<?> forType(TypeDescriptor typeDescriptor) {
        if (typeDescriptor instanceof UnknownPropertyType) {
            return UNKNOWN_TYPE_CONVERTER;
        } else {
            return forType(typeDescriptor.type());
        }
    }

    public static boolean isKnownType(String clazzFullyQualifiedName) {
        return CONVERTER.keySet()
                .stream()
                .anyMatch(aClass -> aClass.getName().equals(clazzFullyQualifiedName));
    }

    @SuppressWarnings("unchecked")
    public static <T> ValueConverter<T> forType(Class<T> typeClazz) {
        if (CONVERTER.containsKey(typeClazz)) {
            return (ValueConverter<T>) CONVERTER.get(typeClazz);
        }
        throw new IllegalStateException(
                format("Input Type '%s' does not have suitable converter", typeClazz.getName()));
    }
}
