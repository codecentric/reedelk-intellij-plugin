package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.commons.GetAnnotationValue;
import com.reedelk.plugin.commons.MapUtils;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.component.scanner.UnsupportedType;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.DisplayName;
import com.reedelk.runtime.api.annotation.File;
import com.reedelk.runtime.api.annotation.TabGroup;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.script.DynamicMap;
import com.reedelk.runtime.api.script.Script;
import io.github.classgraph.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.reedelk.plugin.component.scanner.property.PropertyScannerUtils.*;
import static com.reedelk.plugin.converter.ValueConverterFactory.isDynamicValue;
import static com.reedelk.plugin.converter.ValueConverterFactory.isKnownType;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class TypeHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        TypeSignature typeSignature = propertyInfo.getTypeDescriptor();

        if (typeSignature instanceof BaseTypeSignature) {
            TypeDescriptor typeDescriptor = processKnownType(((BaseTypeSignature) typeSignature).getType(), propertyInfo);
            builder.type(typeDescriptor);
        } else if (typeSignature instanceof ClassRefTypeSignature) {
            ClassRefTypeSignature classRef = (ClassRefTypeSignature) typeSignature;
            TypeDescriptor typeDescriptor = processClassRefType(classRef, propertyInfo, context);
            builder.type(typeDescriptor);
        } else {
            throw new UnsupportedType(typeSignature.getClass());
        }
    }

    private TypeDescriptor processKnownType(Class<?> clazz, FieldInfo fieldInfo) {
        if (isScript(clazz)) {
            return new TypeScriptDescriptor();
        } else if (isDynamicMap(clazz)) {
            String tabGroup = getAnnotationValueOrDefault(fieldInfo, TabGroup.class, null);
            return new TypeDynamicMapDescriptor(tabGroup);
        } else if (isFile(fieldInfo, clazz)) {
            return new TypeFileDescriptor();
        } else if (isCombo(fieldInfo, clazz)) {
            boolean editable = getAnnotationParameterValueOrDefault(fieldInfo, Combo.class, "editable", false);
            Object[] comboValues = getAnnotationParameterValueOrDefault(fieldInfo, Combo.class, "comboValues", new String[]{});
            List<String> items = stream(comboValues).map(value -> (String) value).collect(toList());
            return new TypeComboDescriptor(editable, items.toArray(new String[]{}));
        } else if (isMap(clazz)) {
            String tabGroup = getAnnotationValueOrDefault(fieldInfo, TabGroup.class, null);
            return new TypeMapDescriptor(tabGroup);
        } else {
            return new TypePrimitiveDescriptor(clazz);
        }
    }

    private TypeDescriptor processClassRefType(ClassRefTypeSignature typeSignature, FieldInfo fieldInfo, ComponentAnalyzerContext context) {
        String fullyQualifiedClassName = typeSignature.getFullyQualifiedClassName();
        if (isKnownType(fullyQualifiedClassName)) {
            try {
                Class<?> typeClazz = Class.forName(fullyQualifiedClassName);
                return processKnownType(typeClazz, fieldInfo);
            } catch (ClassNotFoundException e) {
                // if it is a known type, then the class must be resolvable.
                // Otherwise the @PropertyValueConverterFactory class would not even compile.
                throw new UnsupportedType(fullyQualifiedClassName);
            }
        } else if (isDynamicValue(fullyQualifiedClassName)) {
            return new TypeDynamicValueDescriptor();
        } else if (isEnumeration(fullyQualifiedClassName, context)) {
            return processEnumType(typeSignature, context);
        } else {
            // We check that we can resolve class info. If we can, then
            ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
            if (classInfo == null) {
                throw new UnsupportedType(typeSignature.getClass());
            }
            Shared shared = isShareable(classInfo);
            Collapsible collapsible = isCollapsible(classInfo);
            ComponentPropertyAnalyzer propertyAnalyzer = new ComponentPropertyAnalyzer(context);

            List<ComponentPropertyDescriptor> allProperties = classInfo
                    .getFieldInfo()
                    .stream()
                    .map(propertyAnalyzer::analyze)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());
            return new TypeObjectDescriptor(fullyQualifiedClassName, allProperties, shared, collapsible);
        }
    }

    private TypeEnumDescriptor processEnumType(ClassRefTypeSignature enumRefType, ComponentAnalyzerContext context) {
        String enumFullyQualifiedClassName = enumRefType.getFullyQualifiedClassName();
        ClassInfo enumClassInfo = context.getClassInfo(enumFullyQualifiedClassName);
        FieldInfoList declaredFieldInfo = enumClassInfo.getDeclaredFieldInfo();
        Map<String, String> nameAndDisplayName = declaredFieldInfo
                .stream()
                .filter(filterByFullyQualifiedClassNameType(enumFullyQualifiedClassName))
                .collect(toMap(FieldInfo::getName, fieldInfo ->
                        GetAnnotationValue.getOrDefault(fieldInfo, DisplayName.class, fieldInfo.getName())));

        // Default enum value is the first key. Its default value can be overridden
        // on the property definition with the @Default annotation.
        String defaultEnumValue = MapUtils.getFirstKeyOrDefault(nameAndDisplayName, StringUtils.EMPTY);

        return new TypeEnumDescriptor(nameAndDisplayName, defaultEnumValue);
    }

    private boolean isFile(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(File.class.getName()) &&
                String.class.equals(clazz);
    }

    private boolean isCombo(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(Combo.class.getName()) &&
                String.class.equals(clazz);
    }

    private boolean isMap(Class<?> clazz) {
        return Map.class.equals(clazz);
    }

    private boolean isScript(Class<?> clazz) {
        return Script.class.equals(clazz);
    }

    private boolean isDynamicMap(Class<?> clazz) {
        return DynamicMap.class.equals(clazz);
    }

    private Shared isShareable(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Shared.class.getName()) ?
                Shared.YES : Shared.NO;
    }

    private Collapsible isCollapsible(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Collapsible.class.getName()) ?
                Collapsible.YES : Collapsible.NO;
    }
}
