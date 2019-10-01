package com.reedelk.plugin.converter;

import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.type.unknown.UnknownPropertyType;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicByteArray;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicInteger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.component.domain.TypeComboDescriptor.TypeCombo;
import static com.reedelk.plugin.component.domain.TypeFileDescriptor.TypeFile;
import static java.lang.String.format;

public class ValueConverterFactory {

    private static final ValueConverter<Object> UNKNOWN_TYPE_CONVERTER = new UnknownTypeConverter();

    private static final Map<Class<?>, ValueConverter<?>> CONVERTER;

    static {
        Map<Class<?>, ValueConverter<?>> tmp = new HashMap<>();
        tmp.put(int.class, new IntegerConverter());
        tmp.put(Integer.class, new IntegerConverter());
        tmp.put(long.class, new LongConverter());
        tmp.put(Long.class, new LongConverter());
        tmp.put(float.class, new FloatConverter());
        tmp.put(Float.class, new FloatConverter());
        tmp.put(double.class, new DoubleConverter());
        tmp.put(Double.class, new DoubleConverter());
        tmp.put(boolean.class, new BooleanConverter());
        tmp.put(Boolean.class, new BooleanConverter());
        tmp.put(Enum.class, new EnumConverter());
        tmp.put(String.class, new StringConverter());
        tmp.put(BigInteger.class, new BigIntegerConverter());
        tmp.put(BigDecimal.class, new BigDecimalConverter());
        tmp.put(TypeFile.class, new FileConverter());
        tmp.put(TypeCombo.class, new ComboConverter());
        tmp.put(Map.class, new MapConverter());
        tmp.put(Script.class, new ScriptConverter());
        tmp.put(DynamicByteArray.class, new DynamicByteArrayConverter());
        tmp.put(DynamicInteger.class, new DynamicIntegerConverter());
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
    private static <T> ValueConverter<T> forType(Class<T> typeClazz) {
        if (CONVERTER.containsKey(typeClazz)) {
            return (ValueConverter<T>) CONVERTER.get(typeClazz);
        }
        throw new IllegalStateException(
                format("Input Type '%s' does not have suitable converter", typeClazz.getName()));
    }
}
