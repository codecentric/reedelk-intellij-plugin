package com.esb.plugin.service.module;

import com.esb.component.SystemComponent;
import com.esb.plugin.service.module.impl.ComponentServiceImpl;
import io.github.classgraph.*;
import org.junit.jupiter.api.Test;

public class ClassGraphTest {

    @Test
    void shouldDoSomething() {
        ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .whitelistPackages(ComponentServiceImpl.SYSTEM_COMPONENTS_BASE_PACKAGE)
                .scan();

        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(SystemComponent.class.getName());

        // Then
        ClassInfo forkClassInfo = classesWithAnnotation.get("com.esb.component.Fork");
        FieldInfoList declaredFieldInfo = forkClassInfo.getDeclaredFieldInfo();

        MethodInfoList setThreadPoolSize = forkClassInfo.getMethodInfo("setThreadPoolSize");
        MethodInfo setThreadPoolMethod = setThreadPoolSize.getSingleMethod("setThreadPoolSize");
        MethodParameterInfo[] parameterInfo = setThreadPoolMethod.getParameterInfo();
        MethodParameterInfo paramInfo = parameterInfo[0];
        TypeSignature typeDescriptor = paramInfo.getTypeDescriptor();
        if (typeDescriptor instanceof BaseTypeSignature) {
            BaseTypeSignature baseTYpe = (BaseTypeSignature) typeDescriptor;
            Class<?> actualType = baseTYpe.getType();
        }
        System.out.println("df");

    }
}
