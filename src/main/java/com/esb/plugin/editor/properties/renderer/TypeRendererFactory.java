package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeFileDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.component.domain.TypeScriptDescriptor;
import com.esb.plugin.component.type.unknown.UnknownPropertyType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TypeRendererFactory {

    private static final Map<Class<?>, TypePropertyRenderer> RENDERER;
    static {
        Map<Class<?>, TypePropertyRenderer> tmp = new HashMap<>();

        tmp.put(int.class, new IntegerPropertyRenderer());
        tmp.put(Integer.class, new IntegerPropertyRenderer());
        tmp.put(long.class, new LongPropertyRenderer());
        tmp.put(Long.class, new LongPropertyRenderer());
        tmp.put(float.class, new FloatPropertyRenderer());
        tmp.put(Float.class, new FloatPropertyRenderer());
        tmp.put(double.class, new DoublePropertyRenderer());
        tmp.put(Double.class, new DoublePropertyRenderer());
        tmp.put(boolean.class, new BooleanPropertyRenderer());
        tmp.put(Boolean.class, new BooleanPropertyRenderer());

        tmp.put(Enum.class, new EnumPropertyRenderer());
        tmp.put(String.class, new StringPropertyRenderer());
        tmp.put(BigInteger.class, new BigIntegerPropertyRenderer());
        tmp.put(BigDecimal.class, new BigDecimalPropertyRenderer());

        tmp.put(TypeFileDescriptor.TypeFile.class, new TypeFilePropertyRenderer());
        tmp.put(TypeObjectDescriptor.TypeObject.class, new TypeObjectPropertyRenderer());
        tmp.put(TypeScriptDescriptor.TypeScript.class, new TypeScriptPropertyRenderer());
        tmp.put(UnknownPropertyType.UnknownType.class, new UnknownPropertyRenderer());

        RENDERER = tmp;
    }

    public static TypeRendererFactory get() {
        return new TypeRendererFactory();
    }

    public TypePropertyRenderer from(TypeDescriptor propertyType) {
        if (RENDERER.containsKey(propertyType.type())) {
            return RENDERER.get(propertyType.type());
        } else {
            throw new IllegalArgumentException("Could not find renderer for type: " + propertyType);
        }
    }
}
