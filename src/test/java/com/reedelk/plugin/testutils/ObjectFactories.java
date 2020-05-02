package com.reedelk.plugin.testutils;

import com.reedelk.module.descriptor.model.property.*;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;

import java.util.List;
import java.util.Map;

public class ObjectFactories {


    public static ObjectDescriptor createTypeObjectDescriptor(String fullyQualifiedName, List<PropertyDescriptor> propertiesDescriptor, Shared shared) {
        ObjectDescriptor descriptor = new ObjectDescriptor();
        descriptor.setTypeFullyQualifiedName(fullyQualifiedName);
        descriptor.setCollapsible(Collapsible.NO);
        descriptor.setShared(shared);
        descriptor.setObjectProperties(propertiesDescriptor);
        return descriptor;
    }

    public static ObjectDescriptor createTypeObjectDescriptor(String fullyQualifiedName, List<PropertyDescriptor> propertiesDescriptor) {
        return createTypeObjectDescriptor(fullyQualifiedName, propertiesDescriptor, Shared.NO);
    }

    public static PrimitiveDescriptor createTypePrimitiveDescriptor(Class<?> clazzType) {
        PrimitiveDescriptor descriptor = new PrimitiveDescriptor();
        descriptor.setType(clazzType);
        return descriptor;
    }

    public static <T extends DynamicValue<?>> DynamicValueDescriptor createTypeDynamicValueDescriptor(Class<T> dynamicClazzType) {
        DynamicValueDescriptor descriptor = new DynamicValueDescriptor();
        descriptor.setType(dynamicClazzType);
        return descriptor;
    }

    public static EnumDescriptor createTypeEnumDescriptor(Map<String,String> valueAndDisplayMap) {
        EnumDescriptor descriptor = new EnumDescriptor();
        descriptor.setNameAndDisplayNameMap(valueAndDisplayMap);
        return descriptor;
    }

    public static ComboDescriptor createTypeComboDescriptor(boolean editable, String[] comboValues, String prototype) {
        ComboDescriptor descriptor = new ComboDescriptor();
        descriptor.setComboValues(comboValues);
        descriptor.setEditable(editable);
        descriptor.setPrototype(prototype);
        return descriptor;
    }

    public static MapDescriptor createTypeMapDescriptor(String tabGroup, PropertyTypeDescriptor valueType) {
        MapDescriptor descriptor = new MapDescriptor();
        descriptor.setValueType(valueType);
        descriptor.setTabGroup(tabGroup);
        return descriptor;
    }

    public static ListDescriptor createTypeListDescriptor(String tabGroup, PropertyTypeDescriptor valueType) {
        ListDescriptor descriptor = new ListDescriptor();
        descriptor.setValueType(valueType);
        descriptor.setTabGroup(tabGroup);
        return descriptor;
    }

    public static ScriptSignatureDescriptor createScriptSignatureDefinition(List<ScriptSignatureArgument> arguments) {
        ScriptSignatureDescriptor definition = new ScriptSignatureDescriptor();
        definition.setArguments(arguments);
        return definition;
    }
}
