package com.reedelk.plugin.graph.deserializer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;

public class DeserializerContext {

    private final Module module;

    public DeserializerContext(Module module) {
        this.module = module;
    }

    public <T extends GraphNode> T instantiateGraphNode(String componentName) {
        return GraphNodeFactory.get(module, componentName);
    }
}
