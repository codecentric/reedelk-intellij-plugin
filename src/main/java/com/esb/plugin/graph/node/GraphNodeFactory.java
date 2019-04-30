package com.esb.plugin.graph.node;

import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceGraphNode;
import com.esb.plugin.component.flowreference.FlowReferenceGraphNode;
import com.esb.plugin.component.forkjoin.ForkJoinGraphNode;
import com.esb.plugin.component.generic.GenericComponentGraphNode;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class GraphNodeFactory {

    private static final Class<? extends GraphNode> DEFAULT = GenericComponentGraphNode.class;

    private static final Map<String, Class<? extends GraphNode>> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            Choice.class.getName(), ChoiceGraphNode.class,
            Fork.class.getName(), ForkJoinGraphNode.class,
            FlowReference.class.getName(), FlowReferenceGraphNode.class);


    public static GraphNode get(ComponentDescriptor descriptor) {

        Component component = new Component(descriptor);

        String componentFullyQualifiedName = component.getFullyQualifiedName();

        Class<? extends GraphNode> componentDrawableClazz = COMPONENT_DRAWABLE_MAP.getOrDefault(componentFullyQualifiedName, DEFAULT);

        try {
            return componentDrawableClazz.getConstructor(Component.class).newInstance(component);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
