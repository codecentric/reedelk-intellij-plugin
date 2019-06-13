package com.esb.plugin.component.scanner;

import com.esb.api.component.AbstractInbound;
import com.esb.api.component.Component;
import com.esb.api.component.Inbound;
import com.esb.api.component.Processor;
import com.esb.plugin.component.domain.ComponentClass;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class ComponentClassAnalyzer {

    private final ClassInfo classInfo;

    ComponentClassAnalyzer(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public ComponentClass analyze() {
        if (isInbound(classInfo)) {
            return ComponentClass.INBOUND;
        } else if (isProcessor(classInfo)) {
            return ComponentClass.PROCESSOR;
        } else if (isComponent(classInfo)) {
            return ComponentClass.COMPONENT;
        } else {
            return ComponentClass.UNKNOWN;
        }
    }

    /**
     * A component is inbound if implements either the AbstractInbound abstract class
     * or Inbound interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes an Inbound component, false otherwise.
     */
    private boolean isInbound(ClassInfo componentClassInfo) {
        ClassInfoList superclasses = componentClassInfo.getSuperclasses();
        boolean implementsAbstractInbound = superclasses.stream().anyMatch(classInfo ->
                classInfo.getName().equals(AbstractInbound.class.getName()));
        boolean implementsInboundInterface = componentClassInfo.getInterfaces().stream().anyMatch(classInfo ->
                classInfo.getName().equals(Inbound.class.getName()));
        return implementsAbstractInbound || implementsInboundInterface;
    }

    /**
     * A component is a processor if implements the Processor interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes a Processor component, false otherwise.
     */
    private boolean isProcessor(ClassInfo componentClassInfo) {
        return implementsInterface(componentClassInfo, Processor.class);
    }

    /**
     * A component is just a component if implements the Component interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor describes a Component, false otherwise.
     */
    private boolean isComponent(ClassInfo componentClassInfo) {
        return implementsInterface(componentClassInfo, Component.class);
    }

    private static boolean implementsInterface(ClassInfo componentClassInfo, Class target) {
        return componentClassInfo.getInterfaces().stream().anyMatch(classInfo ->
                classInfo.getName().equals(target.getName()));
    }
}
