package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

public class PlaceholderConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        PlaceholderNode placeholder = (PlaceholderNode) componentToAdd;

        FlowSubGraph scopeInitialSubGraph = new FlowSubGraph();
        scopeInitialSubGraph.root(placeholder);

        return new ScopedNodeConnector(graph, scopeInitialSubGraph);
    }
}
