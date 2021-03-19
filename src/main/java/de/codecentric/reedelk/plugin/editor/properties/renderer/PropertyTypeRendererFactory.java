package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.component.type.unknown.UnknownPropertyType;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typebigdecimal.BigDecimalPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typebigdecimal.DynamicBigDecimalPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typebiginteger.BigIntegerPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typebiginteger.DynamicBigIntegerPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeboolean.BooleanPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeboolean.DynamicBooleanPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typebytearray.DynamicByteArrayPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typechar.CharPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typecombo.ComboPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typedouble.DoublePropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typedouble.DynamicDoublePropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeenum.EnumPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typefloat.DynamicFloatPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typefloat.FloatPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeinteger.DynamicIntegerPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeinteger.IntegerPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.ListPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typelong.DynamicLongPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typelong.LongPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.MapPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.dynamic.DynamicMapPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeobject.DynamicObjectPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeobject.ObjectPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typepassword.PasswordPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeresource.ResourceDynamicPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeresource.ResourcePropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typescript.ScriptPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typestring.DynamicStringPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typestring.StringPropertyRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeunknown.UnknownPropertyTypeRenderer;
import de.codecentric.reedelk.runtime.api.annotation.Combo;
import de.codecentric.reedelk.runtime.api.annotation.Password;
import de.codecentric.reedelk.runtime.api.resource.DynamicResource;
import de.codecentric.reedelk.runtime.api.resource.ResourceBinary;
import de.codecentric.reedelk.runtime.api.resource.ResourceText;
import de.codecentric.reedelk.runtime.api.script.Script;
import de.codecentric.reedelk.runtime.api.script.dynamicmap.DynamicBooleanMap;
import de.codecentric.reedelk.runtime.api.script.dynamicmap.DynamicObjectMap;
import de.codecentric.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
        tmp.put(char.class, new CharPropertyRenderer());
        tmp.put(Character.class, new CharPropertyRenderer());
        tmp.put(BigInteger.class, new BigIntegerPropertyRenderer());
        tmp.put(BigDecimal.class, new BigDecimalPropertyRenderer());

        // Other known types
        tmp.put(Map.class, new MapPropertyRenderer());
        tmp.put(List.class, new ListPropertyRenderer());
        tmp.put(Enum.class, new EnumPropertyRenderer());
        tmp.put(Script.class, new ScriptPropertyRenderer());
        tmp.put(String.class, new StringPropertyRenderer());
        tmp.put(ResourceText.class, new ResourcePropertyRenderer());
        tmp.put(ResourceBinary.class, new ResourcePropertyRenderer());
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
        tmp.put(DynamicBooleanMap.class, new DynamicMapPropertyRenderer());
        tmp.put(DynamicStringMap.class, new DynamicMapPropertyRenderer());
        tmp.put(DynamicObjectMap.class, new DynamicMapPropertyRenderer());

        // NOT platform types (internal use only)
        tmp.put(ObjectDescriptor.TypeObject.class, new ObjectPropertyRenderer());
        tmp.put(UnknownPropertyType.UnknownType.class, new UnknownPropertyTypeRenderer());

        RENDERER = Collections.unmodifiableMap(tmp);
    }

    public static PropertyTypeRendererFactory get() {
        return new PropertyTypeRendererFactory();
    }

    public static int size() {
        return RENDERER.size();
    }

    public static Set<Class<?>> supportedConverters() {
        return RENDERER.keySet();
    }

    public PropertyTypeRenderer from(PropertyTypeDescriptor propertyType) {
        if (RENDERER.containsKey(propertyType.getType())) {
            return RENDERER.get(propertyType.getType());
        }
        throw new IllegalArgumentException("Could not find renderer for type: " + propertyType.getType());
    }
}
