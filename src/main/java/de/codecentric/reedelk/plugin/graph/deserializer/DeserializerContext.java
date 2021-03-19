package de.codecentric.reedelk.plugin.graph.deserializer;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

public class DeserializerContext {

    private final Module module;

    public DeserializerContext(Module module) {
        this.module = module;
    }

    public <T extends GraphNode> T instantiateGraphNode(String componentName) {
        return GraphNodeFactory.get(module, componentName);
    }
}
