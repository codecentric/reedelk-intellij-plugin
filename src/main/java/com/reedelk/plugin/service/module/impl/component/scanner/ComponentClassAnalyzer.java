package com.reedelk.plugin.service.module.impl.component.scanner;

import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.runtime.api.component.*;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

class ComponentClassAnalyzer {

    private final ClassInfo classInfo;

    ComponentClassAnalyzer(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    ComponentClass analyze() {
        if (isInbound(classInfo)) {
            return ComponentClass.INBOUND;
        } else if (isProcessor(classInfo)) {
            return ComponentClass.PROCESSOR;
        } else if (isComponent(classInfo)) {
            return ComponentClass.COMPONENT;
        } else if (isJoin(classInfo)) {
            return ComponentClass.JOIN;
        } else {
            return ComponentClass.UNKNOWN;
        }
    }

    /**
     * A component is inbound if it implements either the AbstractInbound abstract class
     * or Inbound interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes an Inbound component, false otherwise.
     */
    private boolean isInbound(ClassInfo componentClassInfo) {
        ClassInfoList superclasses = componentClassInfo.getSuperclasses();
        boolean implementsAbstractInbound = superclasses.stream().anyMatch(superClassInfo ->
                superClassInfo.getName().equals(AbstractInbound.class.getName()));
        boolean implementsInboundInterface = componentClassInfo.getInterfaces().stream().anyMatch(interfaceClassInfo ->
                interfaceClassInfo.getName().equals(Inbound.class.getName()));
        return implementsAbstractInbound || implementsInboundInterface;
    }

    /**
     * A component is a processor if it implements the ProcessorSync or ProcessorAsync interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes a Processor component, false otherwise.
     */
    private boolean isProcessor(ClassInfo componentClassInfo) {
        return implementsInterface(componentClassInfo, ProcessorSync.class) ||
                implementsInterface(componentClassInfo, ProcessorAsync.class);
    }

    /**
     * A component is just a component if it implements the Component interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes a Component, false otherwise.
     */
    private boolean isComponent(ClassInfo componentClassInfo) {
        return implementsInterface(componentClassInfo, Component.class);
    }

    /**
     * A component is class join if it implements the Join interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes a Join, false otherwise.
     */
    private boolean isJoin(ClassInfo componentClassInfo) {
        return implementsInterface(componentClassInfo, Join.class);
    }

    private static boolean implementsInterface(ClassInfo componentClassInfo, Class target) {
        return componentClassInfo.getInterfaces().stream().anyMatch(classInfo ->
                classInfo.getName().equals(target.getName()));
    }
}
