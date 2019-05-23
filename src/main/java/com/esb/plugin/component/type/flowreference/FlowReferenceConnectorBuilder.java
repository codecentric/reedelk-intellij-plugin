package com.esb.plugin.component.type.flowreference;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.DefaultNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

public class FlowReferenceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        return new DefaultNodeConnector(graph, componentToAdd);
    }
}
