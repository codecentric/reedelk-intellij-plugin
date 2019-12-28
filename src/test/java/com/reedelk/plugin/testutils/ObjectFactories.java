package com.reedelk.plugin.testutils;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;

import java.util.List;
import java.util.Map;

public class ObjectFactories {


    public static TypeObjectDescriptor createTypeObjectDescriptor(String fullyQualifiedName, List<ComponentPropertyDescriptor> propertiesDescriptor, Shared shared) {
        TypeObjectDescriptor descriptor = new TypeObjectDescriptor();
        descriptor.setTypeFullyQualifiedName(fullyQualifiedName);
        descriptor.setCollapsible(Collapsible.NO);
        descriptor.setShared(shared);
        descriptor.setObjectProperties(propertiesDescriptor);
        return descriptor;
    }

    public static TypeObjectDescriptor createTypeObjectDescriptor(String fullyQualifiedName, List<ComponentPropertyDescriptor> propertiesDescriptor) {
        return createTypeObjectDescriptor(fullyQualifiedName, propertiesDescriptor, Shared.NO);
    }

    public static TypePrimitiveDescriptor createTypePrimitiveDescriptor(Class<?> clazzType) {
        TypePrimitiveDescriptor descriptor = new TypePrimitiveDescriptor();
        descriptor.setType(clazzType);
        return descriptor;
    }

    public static <T extends DynamicValue<?>> TypeDynamicValueDescriptor<T> createTypeDynamicValueDescriptor(Class<T> dynamicClazzType) {
        TypeDynamicValueDescriptor<T> descriptor = new TypeDynamicValueDescriptor<>();
        descriptor.setType(dynamicClazzType);
        return descriptor;
    }

    public static TypeEnumDescriptor createTypeEnumDescriptor(Map<String,String> valueAndDisplayMap) {
        TypeEnumDescriptor descriptor = new TypeEnumDescriptor();
        descriptor.setNameAndDisplayNameMap(valueAndDisplayMap);
        return descriptor;
    }

    public static TypeComboDescriptor createTypeComboDescriptor(boolean editable, String[] comboValues, String prototype) {
        TypeComboDescriptor descriptor = new TypeComboDescriptor();
        descriptor.setComboValues(comboValues);
        descriptor.setEditable(editable);
        descriptor.setPrototype(prototype);
        return descriptor;
    }

    public static TypeMapDescriptor createTypeMapDescriptor(String tabGroup) {
        TypeMapDescriptor descriptor = new TypeMapDescriptor();
        descriptor.setTabGroup(tabGroup);
        return descriptor;
    }

    public static ScriptSignatureDefinition createScriptSignatureDefinition(List<String> arguments) {
        ScriptSignatureDefinition definition = new ScriptSignatureDefinition();
        definition.setArguments(arguments);
        return definition;
    }
}
