package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.editor.properties.renderer.typebigdecimal.BigDecimalPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typebigdecimal.DynamicBigDecimalPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typebiginteger.BigIntegerPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typebiginteger.DynamicBigIntegerPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeboolean.BooleanPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeboolean.DynamicBooleanPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typebytearray.DynamicByteArrayPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typecombo.ComboPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typedouble.DoublePropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typedouble.DynamicDoublePropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeenum.EnumPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typefloat.DynamicFloatPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typefloat.FloatPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeinteger.DynamicIntegerPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeinteger.IntegerPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typelong.DynamicLongPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typelong.LongPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.DynamicStringMapPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeobject.DynamicObjectPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeobject.ObjectPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typepassword.PasswordPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeresource.ResourceDynamicPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeresource.ResourcePropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typescript.ScriptPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typestring.DynamicStringPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typestring.StringPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeunknown.UnknownPropertyTypeRenderer;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.Password;
import com.reedelk.runtime.api.resource.DynamicResource;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.resource.ResourceText;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.type.unknown.UnknownPropertyType.UnknownType;

public class PropertyTypeRendererFactory {

    private static final Map<Class<?>, PropertyTypeRenderer> RENDERER;

    static {
        Map<Class<?>, PropertyTypeRenderer> tmp = new HashMap<>();
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
        tmp.put(BigInteger.class, new BigIntegerPropertyRenderer());
        tmp.put(BigDecimal.class, new BigDecimalPropertyRenderer());

        // Other known types
        tmp.put(Map.class, new MapPropertyRenderer());
        tmp.put(Enum.class, new EnumPropertyRenderer());
        tmp.put(Script.class, new ScriptPropertyRenderer());
        tmp.put(String.class, new StringPropertyRenderer());
        tmp.put(ResourceText.class, new ResourcePropertyRenderer());
        tmp.put(ResourceBinary.class, new ResourcePropertyRenderer());
        tmp.put(TypeObject.class, new ObjectPropertyRenderer());
        tmp.put(UnknownType.class, new UnknownPropertyTypeRenderer());
        tmp.put(Combo.class, new ComboPropertyRenderer());
        tmp.put(Password.class, new PasswordPropertyRenderer());

        // Dynamic value types
        tmp.put(DynamicLong.class, new DynamicLongPropertyRenderer());
        tmp.put(DynamicFloat.class, new DynamicFloatPropertyRenderer());
        tmp.put(DynamicDouble.class, new DynamicDoublePropertyRenderer());
        tmp.put(DynamicObject.class, new DynamicObjectPropertyRenderer());
        tmp.put(DynamicString.class, new DynamicStringPropertyRenderer());
        tmp.put(DynamicInteger.class, new DynamicIntegerPropertyRenderer());
        tmp.put(DynamicBoolean.class, new DynamicBooleanPropertyRenderer());
        tmp.put(DynamicResource.class, new ResourceDynamicPropertyRenderer());
        tmp.put(DynamicByteArray.class, new DynamicByteArrayPropertyRenderer());
        tmp.put(DynamicBigInteger.class, new DynamicBigIntegerPropertyRenderer());
        tmp.put(DynamicBigDecimal.class, new DynamicBigDecimalPropertyRenderer());

        // Dynamic map types
        tmp.put(DynamicStringMap.class, new DynamicStringMapPropertyRenderer());

        RENDERER = Collections.unmodifiableMap(tmp);
    }

    public static PropertyTypeRendererFactory get() {
        return new PropertyTypeRendererFactory();
    }

    public PropertyTypeRenderer from(TypeDescriptor propertyType) {
        if (RENDERER.containsKey(propertyType.getType())) {
            return RENDERER.get(propertyType.getType());
        }
        throw new IllegalArgumentException("Could not find renderer for type: " + propertyType.getType());
    }
}
