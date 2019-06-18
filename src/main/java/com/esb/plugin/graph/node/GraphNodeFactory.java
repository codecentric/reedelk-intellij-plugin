package com.esb.plugin.graph.node;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDescriptionDecorator;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.type.flowreference.FlowReferenceNode;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.component.type.generic.GenericComponentNode;
import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.component.type.router.RouterNode;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.component.type.unknown.UnknownNode;
import com.esb.plugin.service.module.ComponentService;
import com.esb.system.component.*;
import com.intellij.openapi.module.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GraphNodeFactory {

    private static final Class<? extends GraphNode> DEFAULT = GenericComponentNode.class;

    private static final Map<String, Class<? extends GraphNode>> COMPONENT_DRAWABLE_MAP;

    static {
        Map<String, Class<? extends GraphNode>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopNode.class);
        tmp.put(Fork.class.getName(), ForkNode.class);
        tmp.put(Router.class.getName(), RouterNode.class);
        tmp.put(Unknown.class.getName(), UnknownNode.class);
        tmp.put(Placeholder.class.getName(), PlaceholderNode.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceNode.class);
        COMPONENT_DRAWABLE_MAP = tmp;
    }


    public static <T extends GraphNode> T get(Module module, String componentName) {
        ComponentDescriptor componentDescriptor =
                ComponentService
                        .getInstance(module)
                        .componentDescriptorByName(componentName);
        return GraphNodeFactory.get(componentDescriptor);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GraphNode> T get(ComponentDescriptor descriptor) {
        ComponentDescriptionDecorator componentDescriptionDecorator = new ComponentDescriptionDecorator(descriptor);

        ComponentData componentData = new ComponentData(componentDescriptionDecorator);

        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        Class<? extends GraphNode> componentNodeClazz =
                COMPONENT_DRAWABLE_MAP.getOrDefault(componentFullyQualifiedName, DEFAULT);
        try {
            return (T) componentNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
