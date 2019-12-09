package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.commons.MapUtils;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.service.module.impl.commons.ScannerUtil;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.service.module.impl.component.scanner.UnsupportedType;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.DisplayName;
import com.reedelk.runtime.api.annotation.MimeTypeCombo;
import com.reedelk.runtime.api.annotation.TabGroup;
import com.reedelk.runtime.api.message.content.MimeType;
import io.github.classgraph.*;

import java.util.*;

import static com.reedelk.plugin.converter.ValueConverterFactory.isKnownType;
import static com.reedelk.plugin.service.module.impl.commons.ScannerUtil.*;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class TypeFieldInfoAnalyzer implements FieldInfoAnalyzer {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        TypeSignature typeSignature = propertyInfo.getTypeDescriptor();

        // Primitive
        if (typeSignature instanceof BaseTypeSignature) {
            Class<?> clazz = ((BaseTypeSignature) typeSignature).getType();
            TypeDescriptor typeDescriptor = new TypePrimitiveDescriptor(clazz);
            builder.type(typeDescriptor);

            // Non primitive: String, BigDecimal, DynamicString, ...
        } else if (typeSignature instanceof ClassRefTypeSignature) {
            ClassRefTypeSignature classRef = (ClassRefTypeSignature) typeSignature;
            TypeDescriptor typeDescriptor = processClassRefType(classRef, propertyInfo, context);
            builder.type(typeDescriptor);

        } else {
            throw new UnsupportedType(typeSignature.getClass());
        }
    }

    private TypeDescriptor processClassRefType(ClassRefTypeSignature typeSignature, FieldInfo fieldInfo, ComponentAnalyzerContext context) {
        String fullyQualifiedClassName = typeSignature.getFullyQualifiedClassName();

        if (isEnumeration(fullyQualifiedClassName, context)) {
            return processEnumType(typeSignature, context);

            // For example: String, Integer, Float, DynamicString ...
        } else if (isKnownType(fullyQualifiedClassName)) {
            Class<?> clazz = clazzByFullyQualifiedName(fullyQualifiedClassName);
            return processKnownType(clazz, fieldInfo);

        } else {
            // We check that it is a user defined object type (with Implementor).
            // We check that we can resolve class info. If we can, then ..
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
                        ScannerUtil.annotationValueOrDefaultFrom(fieldInfo, DisplayName.class, fieldInfo.getName())));

        // Default enum value is the first key. Its default value can be overridden
        // on the property definition with the @Default annotation.
        String defaultEnumValue = MapUtils.getFirstKeyOrDefault(nameAndDisplayName, EMPTY);

        return new TypeEnumDescriptor(nameAndDisplayName, defaultEnumValue);
    }

    @SuppressWarnings("unchecked")
    private TypeDescriptor processKnownType(Class<?> clazz, FieldInfo fieldInfo) {
        if (isDynamicValue(clazz)) {
            return new TypeDynamicValueDescriptor(clazz);

        } else if (isDynamicMap(clazz)) {
            String tabGroup = annotationValueOrDefaultFrom(fieldInfo, TabGroup.class, null);
            return new TypeDynamicMapDescriptor(clazz, tabGroup);

        } else if (isScript(clazz)) {
            return new TypeScriptDescriptor();

        } else if (isPassword(fieldInfo, clazz)) {
            return new TypePasswordDescriptor();

        } else if (isFile(fieldInfo, clazz)) {
            return new TypeFileDescriptor();

        } else if (isCombo(fieldInfo, clazz)) {
            boolean editable = annotationParameterValueOrDefaultFrom(fieldInfo, Combo.class, "editable", false);
            Object[] comboValues = annotationParameterValueOrDefaultFrom(fieldInfo, Combo.class, "comboValues", new String[]{});
            String[] items = stream(comboValues).map(value -> (String) value).toArray(String[]::new);
            return new TypeComboDescriptor(editable, items);

        } else if (isMimeTypeCombo(fieldInfo, clazz)) {
            List<String> predefinedMimeTypes = Arrays.asList(MimeType.ALL_MIME_TYPES);
            String additionalMimeTypes = annotationParameterValueOrDefaultFrom(fieldInfo, MimeTypeCombo.class, "additionalTypes", EMPTY);
            if (isNotBlank(additionalMimeTypes)) {
                String[] additionalTypes = additionalMimeTypes.split(",");
                predefinedMimeTypes = new ArrayList<>(predefinedMimeTypes);
                predefinedMimeTypes.addAll(Arrays.asList(additionalTypes));
            }
            String[] comboMimeTypesArray = predefinedMimeTypes.toArray(new String[]{});
            return new TypeComboDescriptor(true, comboMimeTypesArray, MimeType.MIME_TYPE_PROTOTYPE);

        } else if (isMap(clazz)) {
            String tabGroup = annotationValueOrDefaultFrom(fieldInfo, TabGroup.class, null);
            return new TypeMapDescriptor(tabGroup);

        } else {
            return new TypePrimitiveDescriptor(clazz);
        }
    }
}
