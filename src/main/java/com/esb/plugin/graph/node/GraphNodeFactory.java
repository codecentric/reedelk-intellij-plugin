package com.esb.plugin.graph.node;

import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.component.forkjoin.ForkJoinNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.service.module.ComponentService;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.module.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class GraphNodeFactory {

    private static final Class<? extends GraphNode> DEFAULT = GenericComponentNode.class;

    private static final Map<String, Class<? extends GraphNode>> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            Stop.class.getName(), StopNode.class,
            Choice.class.getName(), ChoiceNode.class,
            Fork.class.getName(), ForkJoinNode.class,
            FlowReference.class.getName(), FlowReferenceNode.class);


    public static <T extends GraphNode> T get(Module module, String componentName) {
        ComponentDescriptor componentDescriptor = ComponentService.getInstance(module)
                .componentDescriptorByName(componentName);
        return GraphNodeFactory.get(componentDescriptor);
    }

    public static <T extends GraphNode> T get(ComponentDescriptor descriptor) {
        ComponentData componentData = new ComponentData(descriptor);
        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        Class<? extends GraphNode> componentDrawableClazz = COMPONENT_DRAWABLE_MAP
                .getOrDefault(componentFullyQualifiedName, DEFAULT);
        try {
            return (T) componentDrawableClazz.getConstructor(ComponentData.class)
                    .newInstance(componentData);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
