package com.esb.plugin.component.type.fork;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;

public class ForkConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        GraphNode placeholder = GraphNodeFactory.get(module, Placeholder.class.getName());

        ForkNode fork = ((ForkNode) componentToAdd);
        fork.addToScope(placeholder);

        FlowSubGraph scopeInitialSubGraph = new FlowSubGraph();
        scopeInitialSubGraph.root(fork);
        scopeInitialSubGraph.add(componentToAdd, placeholder);

        return new ScopedNodeConnector(graph, scopeInitialSubGraph);
    }
}
