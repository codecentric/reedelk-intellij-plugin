package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.type.flowreference.FlowReferenceNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.FlowReference;
import com.intellij.openapi.module.Module;

public class ForkConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        FlowSubGraph scopeSubGraph = new FlowSubGraph();

        scopeSubGraph.root(componentToAdd);

        // TODO: Fixme... this should not use flow reference and also should use the factory
        FlowReferenceNode placeholder = GraphNodeFactory.get(module, FlowReference.class.getName());
        placeholder.componentData().set("ref", "123");

        scopeSubGraph.add(componentToAdd, placeholder);

        ((ForkNode) componentToAdd).addToScope(placeholder);

        return new ScopedNodeConnector(graph, scopeSubGraph);
    }
}