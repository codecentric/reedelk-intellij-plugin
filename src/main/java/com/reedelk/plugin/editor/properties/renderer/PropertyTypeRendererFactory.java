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
import com.reedelk.plugin.editor.properties.renderer.typefile.FilePropertyRenderer;
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
import com.reedelk.plugin.editor.properties.renderer.typescript.ScriptPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typestring.DynamicStringPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typestring.StringPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typeunknown.UnknownPropertyTypeRenderer;
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
import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.domain.TypePasswordDescriptor.TypePassword;
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

        tmp.put(Enum.class, new EnumPropertyRenderer());
        tmp.put(String.class, new StringPropertyRenderer());
        tmp.put(Map.class, new MapPropertyRenderer());
        tmp.put(Script.class, new ScriptPropertyRenderer());
        tmp.put(TypeFile.class, new FilePropertyRenderer());
        tmp.put(TypeCombo.class, new ComboPropertyRenderer());
        tmp.put(TypePassword.class, new PasswordPropertyRenderer());

        tmp.put(TypeObject.class, new ObjectPropertyRenderer());
        tmp.put(UnknownType.class, new UnknownPropertyTypeRenderer());

        // Dynamic value types
        tmp.put(DynamicBigDecimal.class, new DynamicBigDecimalPropertyRenderer());
        tmp.put(DynamicBigInteger.class, new DynamicBigIntegerPropertyRenderer());
        tmp.put(DynamicBoolean.class, new DynamicBooleanPropertyRenderer());
        tmp.put(DynamicByteArray.class, new DynamicByteArrayPropertyRenderer());
        tmp.put(DynamicDouble.class, new DynamicDoublePropertyRenderer());
        tmp.put(DynamicFloat.class, new DynamicFloatPropertyRenderer());
        tmp.put(DynamicInteger.class, new DynamicIntegerPropertyRenderer());
        tmp.put(DynamicLong.class, new DynamicLongPropertyRenderer());
        tmp.put(DynamicObject.class, new DynamicObjectPropertyRenderer());
        tmp.put(DynamicString.class, new DynamicStringPropertyRenderer());

        // Dynamic map types
        tmp.put(DynamicStringMap.class, new DynamicStringMapPropertyRenderer());

        RENDERER = Collections.unmodifiableMap(tmp);
    }

    public static PropertyTypeRendererFactory get() {
        return new PropertyTypeRendererFactory();
    }

    public PropertyTypeRenderer from(TypeDescriptor propertyType) {
        if (RENDERER.containsKey(propertyType.type())) {
            return RENDERER.get(propertyType.type());
        }
        throw new IllegalArgumentException("Could not find renderer for type: " + propertyType.type());
    }
}
