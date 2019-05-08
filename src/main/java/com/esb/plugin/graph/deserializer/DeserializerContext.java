package com.esb.plugin.graph.deserializer;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class DeserializerContext {

    private final Module module;

    public DeserializerContext(Module module) {
        this.module = module;
    }

    public <T extends GraphNode> T instantiateGraphNode(String componentName) {
        ComponentDescriptor componentDescriptor =
                ComponentService
                        .getInstance(module)
                        .componentDescriptorByName(componentName);

        return GraphNodeFactory.get(componentDescriptor);
    }
}
