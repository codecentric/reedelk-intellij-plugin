package com.esb.plugin.graph.node;

import com.esb.component.*;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentDescriptorDecorator;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.component.fork.ForkNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.component.unknown.UnknownNode;
import com.esb.plugin.service.module.ComponentService;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.module.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class GraphNodeFactory {

    private static final Class<? extends GraphNode> DEFAULT = GenericComponentNode.class;

    private static final Map<String, Class<? extends GraphNode>> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            Stop.class.getName(), StopNode.class,
            Fork.class.getName(), ForkNode.class,
            Choice.class.getName(), ChoiceNode.class,
            Unknown.class.getName(), UnknownNode.class,
            FlowReference.class.getName(), FlowReferenceNode.class);


    public static <T extends GraphNode> T get(Module module, String componentName) {
        ComponentDescriptor componentDescriptor =
                ComponentService
                        .getInstance(module)
                        .componentDescriptorByName(componentName);
        return GraphNodeFactory.get(componentDescriptor);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GraphNode> T get(ComponentDescriptor descriptor) {
        ComponentDescriptorDecorator decorator = new ComponentDescriptorDecorator(descriptor);
        ComponentData data = new ComponentData(decorator);
        fillDefaultDescriptorValues(decorator, data);

        String componentFullyQualifiedName = data.getFullyQualifiedName();

        Class<? extends GraphNode> componentDrawableClazz =
                COMPONENT_DRAWABLE_MAP.getOrDefault(componentFullyQualifiedName, DEFAULT);
        try {
            return (T) componentDrawableClazz.getConstructor(ComponentData.class).newInstance(data);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void fillDefaultDescriptorValues(ComponentDescriptor componentDescriptor, ComponentData componentData) {
        componentData.getDataProperties().forEach(propertyName ->
                componentDescriptor.getPropertyDescriptor(propertyName).ifPresent(propertyDescriptor -> {
                    Object defaultValue = propertyDescriptor.getPropertyType().defaultValue();
                    componentData.set(propertyName, defaultValue);
                }));
    }
}
