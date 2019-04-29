package com.esb.plugin.component.flowreference;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.DrawableConnector;
import com.intellij.openapi.module.Module;

public class FlowReferenceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        return new DrawableConnector(graph, componentToAdd);
    }
}
