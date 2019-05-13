package com.esb.plugin.service.module.impl;

import io.github.classgraph.*;

public class SetterPropertyDefinitionMapper {

    // Method starts with "setXYZ"
    public PropertyDefinition map(MethodInfo methodInfo) {
        String propertyName = methodInfo.getName().substring(3);

        MethodParameterInfo[] parameterInfo = methodInfo.getParameterInfo();

        MethodParameterInfo paramInfo = parameterInfo[0];

        TypeSignature typeDescriptor = paramInfo.getTypeDescriptor();

        if (typeDescriptor instanceof BaseTypeSignature) {

            BaseTypeSignature baseTYpe = (BaseTypeSignature) typeDescriptor;

            Class<?> actualType = baseTYpe.getType();

            return new PropertyDefinition(propertyName.toLowerCase(), actualType);

        } else if (typeDescriptor instanceof ClassRefTypeSignature) {

            ClassRefTypeSignature refTypeSignature = (ClassRefTypeSignature) typeDescriptor;

            String baseClassName = refTypeSignature.getBaseClassName();

            // ignore method (but consider List<> of type and so on...
            try {

                return new PropertyDefinition(propertyName.toLowerCase(), Class.forName(baseClassName));

            } catch (ClassNotFoundException e) {

                e.printStackTrace();

                return new PropertyDefinition(propertyName.toLowerCase(), String.class);
            }
        }

        throw new IllegalStateException("Class Not found");

    }
}
